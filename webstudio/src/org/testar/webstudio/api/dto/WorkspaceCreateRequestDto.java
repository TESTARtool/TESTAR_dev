/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

public final class WorkspaceCreateRequestDto {

    private String name;
    private String baseWorkspace;
    private Boolean copyTestGoals;

    public String name() {
        return name == null ? "" : name;
    }

    public String baseWorkspace() {
        return baseWorkspace == null ? "" : baseWorkspace;
    }

    public boolean copyTestGoals() {
        return copyTestGoals == null || copyTestGoals;
    }
}
