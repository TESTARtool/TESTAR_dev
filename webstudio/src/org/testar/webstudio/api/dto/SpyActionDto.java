/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

public final class SpyActionDto {

    private final String id;
    private final String description;
    private final String role;
    private final String targetWidgetId;

    public SpyActionDto(String id, String description, String role, String targetWidgetId) {
        this.id = id;
        this.description = description;
        this.role = role;
        this.targetWidgetId = targetWidgetId;
    }

    public String id() {
        return id;
    }

    public String description() {
        return description;
    }

    public String role() {
        return role;
    }

    public String targetWidgetId() {
        return targetWidgetId;
    }
}
