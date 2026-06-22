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
import org.testar.webstudio.api.dto.WorkspaceDocumentDto;
import org.testar.webstudio.api.dto.WorkspaceFileDto;
import org.testar.webstudio.api.dto.WorkspaceJavaCompileResultDto;
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

    public Path settingsRoot() {
        return workspaceService.settingsRoot();
    }

    public WorkspaceDocumentDto readWorkspaceDocument(String workspaceName) {
        return workspaceService.readWorkspaceDocument(workspaceName);
    }

    public List<DebugFileSummaryDto> listDebugFiles() {
        return workspaceService.listDebugFiles();
    }

    public DebugFileDto readDebugFile(String fileName, String filePath) {
        return workspaceService.readDebugFile(fileName, filePath);
    }

    public WorkspaceFileDto saveTestSettings(String workspaceName, String content) {
        return workspaceService.saveWorkspaceFile(workspaceName, "test.settings", "settings", content);
    }

    public WorkspaceFileDto saveCompositionProperties(String workspaceName, String content) {
        return workspaceService.saveWorkspaceFile(workspaceName, "composition.properties", "composition", content);
    }

    public WorkspaceFileDto savePoliciesProperties(String workspaceName, String content) {
        return workspaceService.saveWorkspaceFile(workspaceName, "policies.properties", "policies", content);
    }

    public WorkspaceFileDto readWorkspaceSourceFile(String workspaceName, String sourceName) {
        return workspaceService.readWorkspaceSourceFile(workspaceName, sourceName);
    }

    public WorkspaceFileDto saveWorkspaceSourceFile(String workspaceName, String sourceName, String content) {
        return workspaceService.saveWorkspaceSourceFile(workspaceName, sourceName, content);
    }

    public WorkspaceFileDto createOrOpenModuleSource(String workspaceName, String propertyKey) {
        return workspaceService.createOrOpenModuleSource(workspaceName, propertyKey);
    }

    public WorkspaceFileDto createOrOpenPolicySource(String workspaceName, String propertyKey) {
        return workspaceService.createOrOpenPolicySource(workspaceName, propertyKey);
    }

    public WorkspaceJavaCompileResultDto compileWorkspaceSource(String workspaceName, String sourceName) {
        return workspaceService.compileWorkspaceSource(workspaceName, sourceName);
    }

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
