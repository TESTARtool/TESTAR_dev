/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webdriver.state;

import org.testar.engine.state.StateCompositionPlan;
import org.testar.webdriver.service.WebdriverStateService;
import org.testar.webdriver.tag.WdTags;

/**
 * Reusable default state composition plan for WebDriver-driven browser testing.
 */
public final class WebdriverStateCompositionPlan {

    private WebdriverStateCompositionPlan() {
    }

    public static StateCompositionPlan browser() {
        return StateCompositionPlan.fullState(WebdriverStateService.browser());
    }

    public static StateCompositionPlan browser(double timeoutSeconds) {
        return StateCompositionPlan.fullState(WebdriverStateService.browser(timeoutSeconds));
    }

    public static StateCompositionPlan browserLeafWidgets(double timeoutSeconds) {
        return StateCompositionPlan.leafWidgets(WebdriverStateService.browser(timeoutSeconds));
    }

    public static StateCompositionPlan browserTextWidgets(double timeoutSeconds) {
        return StateCompositionPlan.widgetsWithText(
                WebdriverStateService.browser(timeoutSeconds),
                WdTags.WebTextContent
        );
    }

    public static StateCompositionPlan browserSemanticWidgets(double timeoutSeconds) {
        return StateCompositionPlan.semanticWidgets(
                WebdriverStateService.browser(timeoutSeconds),
                new WebdriverSemanticWidgetDescriptor()
        );
    }
}
