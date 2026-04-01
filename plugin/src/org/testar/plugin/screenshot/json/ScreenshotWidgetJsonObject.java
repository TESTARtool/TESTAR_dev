/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2019-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2019-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.plugin.screenshot.json;


import java.util.Set;

public class ScreenshotWidgetJsonObject {
    Set<WidgetJsonObject> widgetJsonObjects;
    String screenshotPath;

    public ScreenshotWidgetJsonObject(Set<WidgetJsonObject> widgetJsonObjects, String screenshotPath) {
        this.widgetJsonObjects = widgetJsonObjects;
        this.screenshotPath = screenshotPath;
    }
}
