/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

public final class WorkspaceModuleDefinitionDto {

    private final String propertyKey;
    private final String label;
    private final String configuredClassName;

    public WorkspaceModuleDefinitionDto(String propertyKey, String label, String configuredClassName) {
        this.propertyKey = propertyKey;
        this.label = label;
        this.configuredClassName = configuredClassName;
    }

    public String propertyKey() {
        return propertyKey;
    }

    public String label() {
        return label;
    }

    public String configuredClassName() {
        return configuredClassName;
    }
}
