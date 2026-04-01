/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2019-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2019-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.plugin.screenshot.json;

import org.testar.core.Pair;

import java.util.Set;

public class FullWidgetInfoJsonObject {
    Set<Pair<String, String>> widgetTreeObjects;

    public FullWidgetInfoJsonObject(Set<Pair<String, String>> widgetTreeObjects) {
        this.widgetTreeObjects = widgetTreeObjects;
    }
}