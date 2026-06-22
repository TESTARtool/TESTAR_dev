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
    private final String defaultValue;
    private final boolean regexCapable;

    public WorkspaceSettingDto(String key,
                               String value,
                               String type,
                               String description,
                               List<String> options,
                               String defaultValue,
                               boolean regexCapable) {
        this.key = key;
        this.value = value;
        this.type = type;
        this.description = description;
        this.options = options;
        this.defaultValue = defaultValue;
        this.regexCapable = regexCapable;
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

    public String defaultValue() {
        return defaultValue;
    }

    public boolean regexCapable() {
        return regexCapable;
    }
}
