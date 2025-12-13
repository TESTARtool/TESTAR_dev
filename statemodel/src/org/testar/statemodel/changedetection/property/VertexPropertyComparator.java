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

package org.testar.statemodel.changedetection.property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import org.testar.statemodel.changedetection.delta.DiffType;

/**
 * Compares two sets of vertex properties and classifies their differences.
 *  - properties starting with "CD_" or custom predicate are ignored
 *  - added/removed/changed properties are reported with their values
 */
public class VertexPropertyComparator {

    private static final Predicate<String> DEFAULT_FILTER = name -> !name.startsWith("CD_");

    private VertexPropertyComparator() { }

    public static VertexPropertyDiff compare(Map<String, String> oldProps, Map<String, String> newProps) {
        return compare(oldProps, newProps, DEFAULT_FILTER);
    }

    public static VertexPropertyDiff compare(Map<String, String> oldProps,
                                             Map<String, String> newProps,
                                             Predicate<String> includePredicate) {
        Objects.requireNonNull(oldProps, "oldProps cannot be null");
        Objects.requireNonNull(newProps, "newProps cannot be null");
        Objects.requireNonNull(includePredicate, "includePredicate cannot be null");

        List<PropertyDiff> added = new ArrayList<>();
        List<PropertyDiff> removed = new ArrayList<>();
        List<PropertyDiff> changed = new ArrayList<>();

        // evaluate new properties first (added/changed)
        for (Map.Entry<String, String> entry : newProps.entrySet()) {
            String key = entry.getKey();
            if (!includePredicate.test(key)) {
                continue;
            }
            String newValue = entry.getValue();
            if (oldProps.containsKey(key)) {
                String oldValue = oldProps.get(key);
                if (!Objects.equals(newValue, oldValue)) {
                    changed.add(new PropertyDiff(key, oldValue, newValue, DiffType.CHANGED));
                }
            } else {
                added.add(new PropertyDiff(key, null, newValue, DiffType.ADDED));
            }
        }

        // evaluate removed properties
        for (Map.Entry<String, String> entry : oldProps.entrySet()) {
            String key = entry.getKey();
            if (!includePredicate.test(key)) {
                continue;
            }
            if (!newProps.containsKey(key)) {
                removed.add(new PropertyDiff(key, entry.getValue(), null, DiffType.REMOVED));
            }
        }

        return new VertexPropertyDiff(added, removed, changed);
    }

}
