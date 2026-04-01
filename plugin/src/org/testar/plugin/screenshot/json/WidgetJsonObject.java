/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2019-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2019-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.plugin.screenshot.json;

public class WidgetJsonObject {
    boolean enabled;
    String role;
    boolean blocked;
    BoundingPoly boundingPoly;
    String className;
    String title;
    String desc;
    String name;
    String toolTipText;
    String valuePattern;

    public WidgetJsonObject(boolean enabled, String role, boolean blocked, BoundingPoly boundingPoly, String className, String title, String desc, String name, String toolTipText, String valuePattern) {
        this.enabled = enabled;
        this.role = role;
        this.blocked = blocked;
        this.boundingPoly = boundingPoly;
        this.className = className;
        this.title = title;
        this.desc = desc;
        this.name = name;
        this.toolTipText = toolTipText;
        this.valuePattern = valuePattern;
    }
}
