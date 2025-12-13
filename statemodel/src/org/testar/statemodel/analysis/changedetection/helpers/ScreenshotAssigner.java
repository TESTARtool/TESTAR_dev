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

import org.testar.statemodel.analysis.AnalysisManager;
import org.testar.statemodel.changedetection.ChangeDetectionResult;
import org.testar.statemodel.changedetection.PropertyDiff;
import org.testar.statemodel.changedetection.VertexPropertyDiff;

import java.util.HashMap;
import java.util.Map;

/**
 * Assigns screenshot URLs from concrete nodes to abstract state nodes in the merged change-detection graph.
 */
public class ScreenshotAssigner {

    public ScreenshotAssigner() { }

    public void assign(Map<String, Map<String, Object>> mergedNodes,
                Map<String, Map<String, Object>> oldNodes,
                Map<String, Map<String, Object>> oldEdges,
                Map<String, Map<String, Object>> newNodes,
                Map<String, Map<String, Object>> newEdges,
                String oldModelId,
                String newModelId,
                ChangeDetectionResult result) {

        Map<String, String> oldAbstractShot = computeAbstractScreenshotMap(oldNodes, oldEdges, oldModelId);
        Map<String, String> newAbstractShot = computeAbstractScreenshotMap(newNodes, newEdges, newModelId);
        Map<String, String> newToOld = extractChangedStateMapping(result);

        for (Map.Entry<String, Map<String, Object>> entry : mergedNodes.entrySet()) {
            String stateId = entry.getKey();
            Map<String, Object> el = entry.getValue();
            if (!ElementUtils.hasClass(el, "AbstractState")) {
                continue;
            }
            Map<String, Object> data = ElementUtils.getData(el);
            if (data == null) {
                continue;
            }

            String oldShot = oldAbstractShot.get(stateId);
            String newShot = newAbstractShot.get(stateId);
            if (oldShot != null) {
                data.put("oldScreenshot", oldShot);
            }
            if (newShot != null) {
                data.put("newScreenshot", newShot);
            }

            String mappedOld = newToOld.get(stateId);
            if (mappedOld != null) {
                String mappedOldShot = oldAbstractShot.get(mappedOld);
                if (mappedOldShot != null) {
                    data.put("oldScreenshot", mappedOldShot);
                }
                data.put("oldStateId", mappedOld);
            }

            String fallback = ElementUtils.firstNonNull(
                    ElementUtils.asString(data.get("newScreenshot")),
                    ElementUtils.asString(data.get("oldScreenshot"))
            );
            if (fallback != null) {
                data.put("screenshot", fallback);
            }
        }
    }

    private Map<String, String> computeAbstractScreenshotMap(Map<String, Map<String, Object>> nodesByStateId,
                                                             Map<String, Map<String, Object>> edgesByKey,
                                                             String modelId) {
        Map<String, String> concreteShotByConcreteStateId = new HashMap<String, String>();
        for (Map<String, Object> nodeEl : nodesByStateId.values()) {
            if (!ElementUtils.hasClass(nodeEl, "ConcreteState")) {
                continue;
            }
            Map<String, Object> data = ElementUtils.getData(nodeEl);
            if (data == null) {
                continue;
            }
            String concreteStateId = ElementUtils.asString(data.get("id"));
            String path = buildScreenshotPath(modelId, data);
            if (concreteStateId != null && path != null) {
                concreteShotByConcreteStateId.put(concreteStateId, path);
            }
        }

        Map<String, String> abstractShotByAbstractStateId = new HashMap<String, String>();
        for (Map<String, Object> edgeEl : edgesByKey.values()) {
            if (!ElementUtils.hasClass(edgeEl, "isAbstractedBy")) {
                continue;
            }
            Map<String, Object> data = ElementUtils.getData(edgeEl);
            if (data == null) {
                continue;
            }
            String concreteStateId = ElementUtils.asString(data.get("source"));
            String abstractStateId = ElementUtils.asString(data.get("target"));
            if (concreteStateId == null || abstractStateId == null) {
                continue;
            }
            if (abstractShotByAbstractStateId.containsKey(abstractStateId)) {
                continue;
            }
            String shot = concreteShotByConcreteStateId.get(concreteStateId);
            if (shot != null) {
                abstractShotByAbstractStateId.put(abstractStateId, shot);
            }
        }

        return abstractShotByAbstractStateId;
    }

    private Map<String, String> extractChangedStateMapping(ChangeDetectionResult result) {
        Map<String, String> newToOld = new HashMap<String, String>();
        for (Map.Entry<String, VertexPropertyDiff> entry : result.getChangedStates().entrySet()) {
            String newId = entry.getKey();
            VertexPropertyDiff diff = entry.getValue();
            if (diff == null) {
                continue;
            }
            for (PropertyDiff pd : diff.getChanged()) {
                if (pd == null) {
                    continue;
                }
                if ("stateId".equals(pd.getPropertyName())
                        && pd.getOldValue() != null
                        && !pd.getOldValue().trim().isEmpty()) {
                    newToOld.put(newId, pd.getOldValue());
                    break;
                }
            }
        }
        return newToOld;
    }

    private String buildScreenshotPath(String modelId, Map<String, Object> data) {
        if (modelId == null) {
            return null;
        }
        Object raw = data.get("rawId");
        if (raw == null) {
            raw = data.get("id");
        }
        if (raw instanceof String && !((String) raw).isEmpty()) {
            return modelId + "/" + raw + ".png";
        }
        return null;
    }

}
