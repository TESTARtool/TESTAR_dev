/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.reporting.sequence.record;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SemanticStateRecord {

    private final Map<String, String> properties;

    public SemanticStateRecord(Map<String, String> properties) {
        this.properties = Collections.unmodifiableMap(new LinkedHashMap<>(copyMap(properties)));
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    private static Map<String, String> copyMap(Map<String, String> values) {
        if (values == null) {
            return Collections.emptyMap();
        }

        LinkedHashMap<String, String> copy = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            copy.put(entry.getKey(), entry.getValue());
        }
        return copy;
    }
}
