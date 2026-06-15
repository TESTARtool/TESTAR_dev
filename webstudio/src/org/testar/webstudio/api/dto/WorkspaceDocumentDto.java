/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

import java.util.List;
import java.util.Map;

public final class WorkspaceDocumentDto {

    private final String workspaceName;
    private final String location;
    private final WorkspaceFileDto testSettings;
    private final WorkspaceFileDto compositionProperties;
    private final WorkspaceFileDto policiesProperties;
    private final List<WorkspaceFileDto> sourceFiles;
    private final Map<String, List<String>> references;
    private final List<WorkspaceSettingsGroupDto> settingsGroups;

    public WorkspaceDocumentDto(
        String workspaceName,
        String location,
        WorkspaceFileDto testSettings,
        WorkspaceFileDto compositionProperties,
        WorkspaceFileDto policiesProperties,
        List<WorkspaceFileDto> sourceFiles,
        Map<String, List<String>> references,
        List<WorkspaceSettingsGroupDto> settingsGroups
    ) {
        this.workspaceName = workspaceName;
        this.location = location;
        this.testSettings = testSettings;
        this.compositionProperties = compositionProperties;
        this.policiesProperties = policiesProperties;
        this.sourceFiles = sourceFiles;
        this.references = references;
        this.settingsGroups = settingsGroups;
    }

    public String workspaceName() {
        return workspaceName;
    }

    public String location() {
        return location;
    }

    public WorkspaceFileDto testSettings() {
        return testSettings;
    }

    public WorkspaceFileDto compositionProperties() {
        return compositionProperties;
    }

    public WorkspaceFileDto policiesProperties() {
        return policiesProperties;
    }

    public List<WorkspaceFileDto> sourceFiles() {
        return sourceFiles;
    }

    public Map<String, List<String>> references() {
        return references;
    }

    public List<WorkspaceSettingsGroupDto> settingsGroups() {
        return settingsGroups;
    }
}
