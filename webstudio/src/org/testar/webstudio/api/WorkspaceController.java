/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api;

import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.testar.webstudio.api.dto.DebugFileDto;
import org.testar.webstudio.api.dto.DebugFileSummaryDto;
import org.testar.webstudio.api.dto.RegexValidationResultDto;
import org.testar.webstudio.api.dto.WorkspaceCreateRequestDto;
import org.testar.webstudio.api.dto.WorkspaceDocumentDto;
import org.testar.webstudio.api.dto.WorkspaceFileDto;
import org.testar.webstudio.api.dto.WorkspaceJavaCompileResultDto;
import org.testar.webstudio.api.dto.WorkspaceRenameRequestDto;
import org.testar.webstudio.api.dto.WorkspaceSummaryDto;
import org.testar.webstudio.workspace.WorkspaceService;

public final class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    public List<WorkspaceSummaryDto> listWorkspaces() {
        return workspaceService.listWorkspaces();
    }

    // Implements WS-FUNC-WORKSPACE-MANAGEMENT-001: exposes workspace creation and rename commands to WebStudio.
    public WorkspaceSummaryDto createWorkspace(WorkspaceCreateRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Workspace creation request is required.");
        }

        return workspaceService.createWorkspace(
            request.name(),
            request.baseWorkspace(),
            request.copyTestGoals(),
            request.copyOracles()
        );
    }

    public WorkspaceSummaryDto renameWorkspace(String workspaceName, WorkspaceRenameRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Workspace rename request is required.");
        }

        return workspaceService.renameWorkspace(workspaceName, request.name());
    }

    public Path settingsRoot() {
        return workspaceService.settingsRoot();
    }

    // Implements WS-FUNC-TEST-SETTINGS-001: exposes the selected workspace test.settings document.
    public WorkspaceDocumentDto readWorkspaceDocument(String workspaceName) {
        return workspaceService.readWorkspaceDocument(workspaceName);
    }

    // Implements WS-FUNC-DEBUG-FILES-001: exposes runtime debug log discovery.
    public List<DebugFileSummaryDto> listDebugFiles() {
        return workspaceService.listDebugFiles();
    }

    // Implements WS-FUNC-DEBUG-FILES-001: exposes selected runtime debug log content.
    public DebugFileDto readDebugFile(String fileName, String filePath) {
        return workspaceService.readDebugFile(fileName, filePath);
    }

    // Implements WS-FUNC-TEST-SETTINGS-001: persists raw or visual test.settings editor output.
    public WorkspaceFileDto saveTestSettings(String workspaceName, String content) {
        return workspaceService.saveWorkspaceFile(workspaceName, "test.settings", "settings", content);
    }

    // Implements WS-FUNC-COMPOSITION-FLOW-001: persists composition.properties from the Composition Flow page.
    public WorkspaceFileDto saveCompositionProperties(String workspaceName, String content) {
        return workspaceService.saveWorkspaceFile(workspaceName, "composition.properties", "composition", content);
    }

    // Implements WS-FUNC-POLICIES-001: persists policies.properties from the Policies page.
    public WorkspaceFileDto savePoliciesProperties(String workspaceName, String content) {
        return workspaceService.saveWorkspaceFile(workspaceName, "policies.properties", "policies", content);
    }

    // Implements WS-FUNC-WORKSPACE-SOURCE-EDITOR-001 and WS-FUNC-COMPOSITION-FLOW-001: reads workspace Java source files.
    public WorkspaceFileDto readWorkspaceSourceFile(String workspaceName, String sourceName) {
        return workspaceService.readWorkspaceSourceFile(workspaceName, sourceName);
    }

    // Implements WS-FUNC-WORKSPACE-SOURCE-EDITOR-001: persists workspace Java source editor content.
    public WorkspaceFileDto saveWorkspaceSourceFile(String workspaceName, String sourceName, String content) {
        return workspaceService.saveWorkspaceSourceFile(workspaceName, sourceName, content);
    }

    // Implements WS-FUNC-COMPOSITION-FLOW-001: creates or opens Java source for a composition module property.
    public WorkspaceFileDto createOrOpenModuleSource(String workspaceName, String propertyKey) {
        return workspaceService.createOrOpenModuleSource(workspaceName, propertyKey);
    }

    // Implements WS-FUNC-POLICIES-001: creates Java policy sources for a selected policy seam.
    public WorkspaceFileDto createOrOpenPolicySource(String workspaceName, String propertyKey) {
        return workspaceService.createOrOpenPolicySource(workspaceName, propertyKey);
    }

    // Implements WS-FUNC-WORKSPACE-SOURCE-EDITOR-001 and WS-FUNC-COMPOSITION-FLOW-001: compiles a selected Java source.
    public WorkspaceJavaCompileResultDto compileWorkspaceSource(String workspaceName, String sourceName) {
        return workspaceService.compileWorkspaceSource(workspaceName, sourceName);
    }

    // Implements WS-FUNC-WORKSPACE-SOURCE-EDITOR-001: validates all workspace Java sources for the selected profile.
    public WorkspaceJavaCompileResultDto compileWorkspaceProfile(String workspaceName) {
        return workspaceService.compileWorkspaceProfile(workspaceName);
    }

    public RegexValidationResultDto validateRegex(String value) {
        if (value == null || value.isBlank()) {
            return new RegexValidationResultDto(true, "Empty expression", null);
        }

        try {
            Pattern.compile(value);
            return new RegexValidationResultDto(true, "Valid regular expression", null);
        } catch (PatternSyntaxException exception) {
            return new RegexValidationResultDto(false, exception.getDescription(), exception.getIndex());
        }
    }
}
