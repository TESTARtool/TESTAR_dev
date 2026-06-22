/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

import java.util.List;

public final class WorkspaceJavaCompileResultDto {

    private final boolean success;
    private final String scope;
    private final String targetName;
    private final String message;
    private final List<WorkspaceJavaCompileDiagnosticDto> diagnostics;

    public WorkspaceJavaCompileResultDto(boolean success,
                                         String scope,
                                         String targetName,
                                         String message,
                                         List<WorkspaceJavaCompileDiagnosticDto> diagnostics) {
        this.success = success;
        this.scope = scope;
        this.targetName = targetName;
        this.message = message;
        this.diagnostics = diagnostics;
    }

    public boolean success() {
        return success;
    }

    public String scope() {
        return scope;
    }

    public String targetName() {
        return targetName;
    }

    public String message() {
        return message;
    }

    public List<WorkspaceJavaCompileDiagnosticDto> diagnostics() {
        return diagnostics;
    }
}
