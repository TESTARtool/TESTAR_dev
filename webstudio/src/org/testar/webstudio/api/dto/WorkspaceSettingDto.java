/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

import java.util.List;

public final class WorkspaceSettingDto {

    private final String key;
    private final String value;
    private final String type;
    private final String description;
    private final List<String> options;

    public WorkspaceSettingDto(String key, String value, String type, String description, List<String> options) {
        this.key = key;
        this.value = value;
        this.type = type;
        this.description = description;
        this.options = options;
    }

    public String key() {
        return key;
    }

    public String value() {
        return value;
    }

    public String type() {
        return type;
    }

    public String description() {
        return description;
    }

    public List<String> options() {
        return options;
    }
}
