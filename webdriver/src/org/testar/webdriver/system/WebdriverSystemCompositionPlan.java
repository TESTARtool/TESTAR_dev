/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webdriver.system;

import org.testar.config.settings.Settings;
import org.testar.engine.system.SystemCompositionPlan;
import org.testar.webdriver.service.WebdriverSystemService;

/**
 * Reusable default system composition plan for WebDriver-driven browser testing.
 */
public final class WebdriverSystemCompositionPlan {

    private WebdriverSystemCompositionPlan() {
    }

    public static SystemCompositionPlan fromSettings(Settings settings) {
        return SystemCompositionPlan.basic(WebdriverSystemService.fromSettings(settings));
    }
}
