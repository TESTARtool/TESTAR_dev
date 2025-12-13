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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Filters the merged graph elements to keep the change-detection view usable:
 * - Black holes and connected action edges
 * - Concrete state nodes and any edges connected to them
 */
public class MergedGraphFilter {

    public MergedGraphFilter() { }

    public List<Map<String, Object>> filter(List<Map<String, Object>> merged) {
        Set<String> blackHoleIds = collectIdsByClass(merged, "BlackHole");
        Set<String> concreteIds = collectIdsByClass(merged, "ConcreteState");

        List<Map<String, Object>> filtered = new ArrayList<Map<String, Object>>();
        Set<String> connectedIds = new HashSet<String>();

        for (Map<String, Object> el : merged) {
            Object group = el.get("group");
            if ("edges".equals(group)) {
                Map<String, Object> data = ElementUtils.getData(el);
                String source = data != null ? ElementUtils.asString(data.get("source")) : null;
                String target = data != null ? ElementUtils.asString(data.get("target")) : null;
                if ((source != null && (blackHoleIds.contains(source) || concreteIds.contains(source)))
                        || (target != null && (blackHoleIds.contains(target) || concreteIds.contains(target)))) {
                    continue;
                }
                if (source != null) {
                    connectedIds.add(source);
                }
                if (target != null) {
                    connectedIds.add(target);
                }
                filtered.add(el);
            }
        }

        for (Map<String, Object> el : merged) {
            Object group = el.get("group");
            if ("nodes".equals(group)) {
                Map<String, Object> data = ElementUtils.getData(el);
                String id = data != null ? ElementUtils.asString(data.get("id")) : null;
                if (id != null && (blackHoleIds.contains(id) || concreteIds.contains(id))) {
                    continue;
                }
                String status = data != null ? ElementUtils.asString(data.get("status")) : null;
                boolean keep = (id != null && connectedIds.contains(id)) || (status != null && !"unchanged".equals(status));
                if (keep) {
                    filtered.add(el);
                }
            } else if (!"edges".equals(group)) {
                Map<String, Object> data = ElementUtils.getData(el);
                String id = data != null ? ElementUtils.asString(data.get("id")) : null;
                if ("abstract-layer".equals(id)) {
                    filtered.add(el);
                }
            }
        }

        return filtered;
    }

    private Set<String> collectIdsByClass(List<Map<String, Object>> elements, String cls) {
        Set<String> ids = new HashSet<String>();
        for (Map<String, Object> el : elements) {
            if (!"nodes".equals(el.get("group"))) {
                continue;
            }
            if (ElementUtils.hasClass(el, cls)) {
                Map<String, Object> data = ElementUtils.getData(el);
                if (data != null) {
                    String id = ElementUtils.asString(data.get("id"));
                    if (id != null) {
                        ids.add(id);
                    }
                }
            }
        }
        return ids;
    }

}
