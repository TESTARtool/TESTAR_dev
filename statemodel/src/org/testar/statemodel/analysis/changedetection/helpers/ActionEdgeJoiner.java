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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Visual post-processing for change detection graphs.
 *
 * Join edge action of same source + same primary key. 
 *
 * In the merged visualization we collapse this pair into a single edge:
 * - keep the "added" edge element
 * - drop the "removed" edge element
 * - mark the edge status as {@code unchanged}
 * - mark the target node as {@code changed}
 */
public class ActionEdgeJoiner {

    public ActionEdgeJoiner() { }

    public void joinActionsByPrimaryKey(Map<String, Map<String, Object>> mergedNodes,
                               Map<String, Map<String, Object>> mergedEdges) {
        Map<String, List<String>> bySourceAndPk = new HashMap<String, List<String>>();
        for (Map.Entry<String, Map<String, Object>> entry : mergedEdges.entrySet()) {
            Map<String, Object> data = ElementUtils.getData(entry.getValue());
            if (data == null) {
                continue;
            }
            String src = ElementUtils.asString(data.get("source"));
            String pk = ElementUtils.asString(data.get("label"));
            if (src == null || pk == null) {
                continue;
            }
            if (ElementUtils.hasClass(entry.getValue(), "isAbstractedBy")) {
                continue;
            }
            String key = src + "|" + pk;
            if (!bySourceAndPk.containsKey(key)) {
                bySourceAndPk.put(key, new ArrayList<String>());
            }
            bySourceAndPk.get(key).add(entry.getKey());
        }

        Set<String> toRemove = new HashSet<String>();
        for (Map.Entry<String, List<String>> group : bySourceAndPk.entrySet()) {
            List<String> edgeKeys = group.getValue();
            if (edgeKeys.size() < 2) {
                continue;
            }

            List<String> validKeys = new ArrayList<String>();
            for (String ek : edgeKeys) {
                Map<String, Object> el = mergedEdges.get(ek);
                if (el != null && ElementUtils.getData(el) != null) {
                    validKeys.add(ek);
                }
            }
            if (validKeys.size() < 2) {
                continue;
            }

            boolean hasAdded = false;
            boolean hasRemoved = false;
            for (String k : validKeys) {
                String st = ElementUtils.asString(ElementUtils.getData(mergedEdges.get(k)).get("status"));
                if ("added".equals(st)) {
                    hasAdded = true;
                }
                if ("removed".equals(st)) {
                    hasRemoved = true;
                }
            }
            if (!hasAdded || !hasRemoved) {
                continue;
            }

            String addedKey = null;
            for (String k : validKeys) {
                String st = ElementUtils.asString(ElementUtils.getData(mergedEdges.get(k)).get("status"));
                if ("added".equals(st)) {
                    addedKey = k;
                    break;
                }
            }

            if (addedKey != null) {
                Map<String, Object> data = ElementUtils.getData(mergedEdges.get(addedKey));
                data.put("status", "unchanged");

                String targetId = ElementUtils.asString(data.get("target"));
                Map<String, Object> targetNode = mergedNodes.get(targetId);
                if (targetNode != null) {
                    Map<String, Object> tData = ElementUtils.getData(targetNode);
                    if (tData != null) {
                        String cur = ElementUtils.asString(tData.get("status"));
                        if (!"added".equals(cur) && !"removed".equals(cur)) {
                            tData.put("status", "changed");
                        }
                    }
                }
            }

            for (String k : validKeys) {
                String st = ElementUtils.asString(ElementUtils.getData(mergedEdges.get(k)).get("status"));
                if ("removed".equals(st)) {
                    toRemove.add(k);
                }
            }
        }

        for (String key : toRemove) {
            mergedEdges.remove(key);
        }
    }

}
