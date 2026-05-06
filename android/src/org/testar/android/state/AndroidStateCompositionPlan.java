/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.android.state;

import org.testar.android.service.AndroidStateService;
import org.testar.android.tag.AndroidTags;
import org.testar.engine.state.StateCompositionPlan;

/**
 * Reusable default state composition plan for Android/Appium-driven testing.
 */
public final class AndroidStateCompositionPlan {

    private AndroidStateCompositionPlan() {
    }

    public static StateCompositionPlan appium() {
        return StateCompositionPlan.fullState(AndroidStateService.appium());
    }

    public static StateCompositionPlan appium(double timeoutSeconds) {
        return StateCompositionPlan.fullState(AndroidStateService.appium(timeoutSeconds));
    }

    public static StateCompositionPlan appiumLeafWidgets(double timeoutSeconds) {
        return StateCompositionPlan.leafWidgets(AndroidStateService.appium(timeoutSeconds));
    }

    public static StateCompositionPlan appiumTextWidgets(double timeoutSeconds) {
        return StateCompositionPlan.widgetsWithText(
                AndroidStateService.appium(timeoutSeconds),
                AndroidTags.AndroidText
        );
    }

    public static StateCompositionPlan appiumSemanticWidgets(double timeoutSeconds) {
        return StateCompositionPlan.semanticWidgets(
                AndroidStateService.appium(timeoutSeconds),
                new AndroidSemanticWidgetDescriptor()
        );
    }
}
