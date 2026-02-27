/***************************************************************************************************
 *
 * Copyright (c) 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2025 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.statemodel.analysis.changedetection;

import org.testar.statemodel.analysis.changedetection.helpers.ActionEdgeJoiner;
import org.testar.statemodel.analysis.changedetection.helpers.ElementUtils;
import org.testar.statemodel.analysis.changedetection.helpers.GraphElementIndexer;
import org.testar.statemodel.analysis.changedetection.helpers.MergedGraphFilter;
import org.testar.statemodel.analysis.changedetection.helpers.ScreenshotAssigner;
import org.testar.statemodel.analysis.changedetection.helpers.StatusResolver;
import org.testar.statemodel.changedetection.ChangeDetectionResult;
import org.testar.statemodel.changedetection.key.ActionPrimaryKeyProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Builds the merged graph elements used by the change detection frontend.
 */
public class ChangeDetectionGraphBuilder {

    private final GraphElementIndexer elementIndexer;
    private final ScreenshotAssigner screenshotAssigner = new ScreenshotAssigner();
    private final ActionEdgeJoiner edgeJoiner = new ActionEdgeJoiner();
    private final MergedGraphFilter graphFilter = new MergedGraphFilter();
    private final StatusResolver statusResolver = new StatusResolver();

    public ChangeDetectionGraphBuilder(ActionPrimaryKeyProvider primaryKeyProvider) {
        this.elementIndexer = new GraphElementIndexer(primaryKeyProvider);
    }

    /**
     * Build merged graph elements (nodes + edges) from old/new model elements and change detection result.
     */
    public List<Map<String, Object>> build(String oldModelId,
                                    String newModelId,
                                    List<Map<String, Object>> oldElements,
                                    List<Map<String, Object>> newElements,
                                    ChangeDetectionResult result) {

        Objects.requireNonNull(oldElements, "oldElements");
        Objects.requireNonNull(newElements, "newElements");
        Objects.requireNonNull(result, "result");

        Map<String, String> idToStateOld = new HashMap<String, String>();
        Map<String, String> idToStateNew = new HashMap<String, String>();
        Map<String, Map<String, Object>> oldNodes = elementIndexer.indexNodesByStateId(oldElements, idToStateOld);
        Map<String, Map<String, Object>> newNodes = elementIndexer.indexNodesByStateId(newElements, idToStateNew);
        Map<String, Map<String, Object>> oldEdges = elementIndexer.indexEdgesByComparableKey(oldElements, idToStateOld);
        Map<String, Map<String, Object>> newEdges = elementIndexer.indexEdgesByComparableKey(newElements, idToStateNew);

        Map<String, Map<String, Object>> mergedNodes = new LinkedHashMap<String, Map<String, Object>>();
        for (Map.Entry<String, Map<String, Object>> entry : newNodes.entrySet()) {
            mergedNodes.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Map<String, Object>> entry : oldNodes.entrySet()) {
            if (!mergedNodes.containsKey(entry.getKey())) {
                mergedNodes.put(entry.getKey(), entry.getValue());
            }
        }

        Map<String, String> statusByState = statusResolver.buildStatusByState(result);

        // apply status to nodes and keep screenshots (old/new) when available
        for (Map<String, Object> el : mergedNodes.values()) {
            Map<String, Object> data = ElementUtils.getData(el);
            if (data == null) {
                continue;
            }
            String stateKey = ElementUtils.extractStateId(data);
            String status = statusByState.containsKey(stateKey) ? statusByState.get(stateKey) : StatusResolver.UNCHANGED;
            data.put("status", status);
        }

        // Assign screenshots to abstract nodes from concrete screenshots
        screenshotAssigner.assign(mergedNodes, oldNodes, oldEdges, newNodes, newEdges, oldModelId, newModelId, result);

        Map<String, Map<String, Object>> mergedEdges = new LinkedHashMap<String, Map<String, Object>>();
        Set<String> edgeKeys = new HashSet<String>();
        edgeKeys.addAll(newEdges.keySet());
        edgeKeys.addAll(oldEdges.keySet());

        for (String key : edgeKeys) {
            Map<String, Object> newEl = newEdges.get(key);
            Map<String, Object> oldEl = oldEdges.get(key);

            String status;
            Map<String, Object> targetEl;
            if (newEl != null && oldEl != null) {
                // When the comparable key matches, the action is considered the same
                status = "unchanged";
                targetEl = newEl;
            } else if (newEl != null) {
                status = "added";
                targetEl = newEl;
            } else {
                status = "removed";
                targetEl = oldEl;
            }

            Map<String, Object> targetData = ElementUtils.getData(targetEl);
            if (targetData != null) {
                targetData.put("status", status);
            }
            mergedEdges.put(key, targetEl);
        }

        // Resolve primary keys and enforce label=primaryKey for all merged action edges.
        for (Map<String, Object> edgeEl : mergedEdges.values()) {
            Map<String, Object> data = ElementUtils.getData(edgeEl);
            if (data == null) {
                continue;
            }
            elementIndexer.ensureEdgePrimaryKeyAndLabel(data);
        }

        edgeJoiner.joinActionsByPrimaryKey(mergedNodes, mergedEdges);

        // assign parents for layer grouping
        boolean hasAbstract = false;
        for (Map<String, Object> el : mergedNodes.values()) {
            if (ElementUtils.hasClass(el, "AbstractState") || ElementUtils.hasClass(el, "BlackHole")) {
                Map<String, Object> data = ElementUtils.getData(el);
                if (data != null) {
                    data.put("parent", "abstract-layer");
                }
                hasAbstract = true;
            }
        }

        List<Map<String, Object>> merged = new ArrayList<Map<String, Object>>();
        if (hasAbstract) {
            merged.add(layerNode("abstract-layer", "AbstractLayer", "AbstractLayer"));
        }
        merged.addAll(mergedNodes.values());
        merged.addAll(mergedEdges.values());

        return graphFilter.filter(merged);
    }

    private Map<String, Object> layerNode(String id, String label, String className) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("id", id);
        data.put("label", label);
        Map<String, Object> el = new HashMap<String, Object>();
        el.put("group", "nodes");
        el.put("data", data);
        List<String> classes = new ArrayList<String>();
        classes.add("Layer");
        classes.add(className);
        el.put("classes", classes);
        return el;
    }

}
