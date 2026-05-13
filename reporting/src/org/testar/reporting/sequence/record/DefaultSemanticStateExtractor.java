/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.reporting.sequence.record;

import java.util.LinkedHashMap;
import java.util.Map;

import org.testar.core.state.State;
import org.testar.core.tag.Tags;

public final class DefaultSemanticStateExtractor implements SemanticStateExtractor {

    @Override
    public SemanticStateRecord extract(State state) {
        if (state == null) {
            return new SemanticStateRecord(Map.of());
        }

        Map<String, String> properties = new LinkedHashMap<>();
        addProperty(properties, "title", state.get(Tags.Title, ""));
        addProperty(properties, "link", state.get(Tags.LinkReference, ""));
        addProperty(properties, "description", state.get(Tags.Desc, ""));

        return new SemanticStateRecord(properties);
    }

    private static void addProperty(Map<String, String> properties, String name, String value) {
        String normalizedValue = value == null ? "" : value.trim();
        if (!normalizedValue.isEmpty()) {
            properties.put(name, normalizedValue);
        }
    }
}
