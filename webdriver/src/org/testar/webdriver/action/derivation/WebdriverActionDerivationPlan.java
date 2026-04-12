/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webdriver.action.derivation;

import java.util.Collections;
import java.util.List;

import org.testar.engine.action.TextInputProvider;
import org.testar.engine.action.derivation.ActionDerivationPlan;
import org.testar.engine.action.derivation.ActionDeriver;
import org.testar.engine.action.derivation.EscFallbackActionDeriver;
import org.testar.engine.action.derivation.StateActionDeriver;

/**
 * Reusable default WebDriver derivation plan using remote scroll-aware web
 * actions.
 */
public final class WebdriverActionDerivationPlan {

    private WebdriverActionDerivationPlan() {
    }

    public static ActionDerivationPlan create(TextInputProvider textInputProvider) {
        ActionDeriver defaultDeriver = new StateActionDeriver(
                new WebdriverWidgetActionDeriver(textInputProvider)
        );
        List<ActionDeriver> defaultDerivers = Collections.singletonList(defaultDeriver);
        List<ActionDeriver> fallbackDerivers = Collections.singletonList(new EscFallbackActionDeriver());

        return new ActionDerivationPlan(
                Collections.emptyList(),
                defaultDerivers,
                fallbackDerivers
        );
    }
}
