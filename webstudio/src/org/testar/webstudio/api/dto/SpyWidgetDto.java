/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

import java.util.Map;

public final class SpyWidgetDto {

    private final String id;
    private final String parentId;
    private final String label;
    private final String role;
    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private final boolean enabled;
    private final Map<String, String> properties;

    public SpyWidgetDto(
        String id,
        String parentId,
        String label,
        String role,
        double x,
        double y,
        double width,
        double height,
        boolean enabled,
        Map<String, String> properties
    ) {
        this.id = id;
        this.parentId = parentId;
        this.label = label;
        this.role = role;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.enabled = enabled;
        this.properties = properties;
    }

    public String id() {
        return id;
    }

    public String parentId() {
        return parentId;
    }

    public String label() {
        return label;
    }

    public String role() {
        return role;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double width() {
        return width;
    }

    public double height() {
        return height;
    }

    public boolean enabled() {
        return enabled;
    }

    public Map<String, String> properties() {
        return properties;
    }
}
