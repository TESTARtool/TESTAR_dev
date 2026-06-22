/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

import java.util.List;

public final class WorkspacePolicyDefinitionDto {

    private final String propertyKey;
    private final String label;
    private final List<String> configuredClassNames;

    public WorkspacePolicyDefinitionDto(String propertyKey, String label, List<String> configuredClassNames) {
        this.propertyKey = propertyKey;
        this.label = label;
        this.configuredClassNames = configuredClassNames;
    }

    public String propertyKey() {
        return propertyKey;
    }

    public String label() {
        return label;
    }

    public List<String> configuredClassNames() {
        return configuredClassNames;
    }
}
