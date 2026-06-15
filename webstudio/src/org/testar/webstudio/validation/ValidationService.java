/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.validation;

import java.nio.file.Files;
import java.nio.file.Path;

import org.testar.webstudio.api.dto.ValidationResultDto;
import org.testar.webstudio.workspace.WorkspaceService;

public final class ValidationService {

    private final WorkspaceService workspaceService;

    public ValidationService(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    public ValidationResultDto validateWorkspace(String workspaceName) {
        Path workspace = workspaceService.settingsRoot().resolve(workspaceName);
        if (!Files.isDirectory(workspace)) {
            return new ValidationResultDto(false, "Workspace not found: " + workspaceName);
        }

        return new ValidationResultDto(true, "Workspace is present: " + workspace);
    }
}
