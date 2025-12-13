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

package org.testar.statemodel.analysis.changedetection.helpers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.testar.statemodel.changedetection.key.ActionPrimaryKeyProvider;

/**
 * Normalizes and indexes Cytoscape graph elements to support stable merging:
 * - node ids are normalized to their state id (AbstractId / ConcreteId)
 * - original node ids are stored as {@code rawId} for screenshot paths
 * - edge endpoints are normalized from raw node ids to state ids
 * - edge labels are resolved via {@link ActionPrimaryKeyProvider} (Desc preferred, fallback actionId)
 */
public class GraphElementIndexer {

    private final ActionPrimaryKeyProvider primaryKeyProvider;

    public GraphElementIndexer(ActionPrimaryKeyProvider primaryKeyProvider) {
        this.primaryKeyProvider = primaryKeyProvider;
    }

    public Map<String, Map<String, Object>> indexNodesByStateId(List<Map<String, Object>> elements, Map<String, String> rawIdToStateId) {
        Objects.requireNonNull(elements, "elements");
        Objects.requireNonNull(rawIdToStateId, "rawIdToStateId");

        Map<String, Map<String, Object>> nodes = new LinkedHashMap<String, Map<String, Object>>();
        for (Map<String, Object> el : elements) {
            if (!"nodes".equals(el.get("group"))) {
                continue;
            }
            Map<String, Object> data = ElementUtils.getData(el);
            if (data == null) {
                continue;
            }
            String stateId = ElementUtils.extractStateId(data);
            String rawId = ElementUtils.asString(data.get("id"));
            if (rawId != null && stateId != null) {
                rawIdToStateId.put(rawId, stateId);
            }
            if (stateId != null) {
                if (rawId != null) {
                    data.put("rawId", rawId);
                }
                data.put("id", stateId);
                nodes.put(stateId, el);
            }
        }
        return nodes;
    }

    public Map<String, Map<String, Object>> indexEdgesByComparableKey(List<Map<String, Object>> elements, Map<String, String> rawIdToStateId) {
        Objects.requireNonNull(elements, "elements");
        Objects.requireNonNull(rawIdToStateId, "rawIdToStateId");

        Map<String, Map<String, Object>> edges = new LinkedHashMap<String, Map<String, Object>>();
        for (Map<String, Object> el : elements) {
            if (!"edges".equals(el.get("group"))) {
                continue;
            }
            Map<String, Object> data = ElementUtils.getData(el);
            if (data == null) {
                continue;
            }

            ensureEdgeLabel(data);
            normalizeEdgeEndpoints(data, rawIdToStateId);
            String key = edgeComparableKey(data);
            if (key != null) {
                edges.put(key, el);
            }
        }
        return edges;
    }

    public void ensureEdgePrimaryKeyAndLabel(Map<String, Object> data) {
        Objects.requireNonNull(data, "data");
        String pk = resolvePrimaryKey(data);
        data.put("primaryKey", pk);
        data.put("label", pk);
    }

    private void ensureEdgeLabel(Map<String, Object> data) {
        if (ElementUtils.asString(data.get("label")) == null) {
            ensureEdgePrimaryKeyAndLabel(data);
        }
    }

    private void normalizeEdgeEndpoints(Map<String, Object> data, Map<String, String> rawIdToStateId) {
        String sourceRaw = ElementUtils.asString(data.get("source"));
        String targetRaw = ElementUtils.asString(data.get("target"));
        String sourceCanonical = canonicalStateId(sourceRaw, rawIdToStateId);
        String targetCanonical = canonicalStateId(targetRaw, rawIdToStateId);
        if (sourceCanonical != null) {
            data.put("source", sourceCanonical);
        }
        if (targetCanonical != null) {
            data.put("target", targetCanonical);
        }
    }

    private String canonicalStateId(String rawNodeId, Map<String, String> rawIdToStateId) {
        if (rawNodeId == null) {
            return null;
        }
        String state = rawIdToStateId.get(rawNodeId);
        return state != null ? state : rawNodeId;
    }

    String edgeComparableKey(Map<String, Object> data) {
        String source = ElementUtils.asString(data.get("source"));
        String target = ElementUtils.asString(data.get("target"));
        if (source == null || target == null) {
            return null;
        }
        String label = ElementUtils.asString(data.get("label"));
        return source + "|" + target + "|" + (label == null ? "" : label);
    }

    private String resolvePrimaryKey(Map<String, Object> data) {
        String actionId = ElementUtils.asString(data.get("actionId"));
        String pk = null;
        String existingText = ElementUtils.firstNonNull(
                ElementUtils.asString(data.get("primaryKey")),
                ElementUtils.asString(data.get("label"))
        );
        if (primaryKeyProvider != null && actionId != null) {
            try {
                pk = primaryKeyProvider.getPrimaryKey(actionId);
            } catch (Exception ignored) {
            }
        }
        if (pk == null) {
            pk = existingText;
        }
        if (pk == null) {
            pk = actionId;
        }
        return pk != null ? pk : "";
    }

}
