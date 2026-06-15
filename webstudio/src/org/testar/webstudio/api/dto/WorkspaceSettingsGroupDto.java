/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

import java.util.List;

public final class WorkspaceSettingsGroupDto {

    private final String id;
    private final String title;
    private final String description;
    private final List<WorkspaceSettingDto> settings;

    public WorkspaceSettingsGroupDto(String id, String title, String description, List<WorkspaceSettingDto> settings) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.settings = settings;
    }

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public List<WorkspaceSettingDto> settings() {
        return settings;
    }
}
