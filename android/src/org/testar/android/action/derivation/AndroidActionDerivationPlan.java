/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.android.action.derivation;

import java.util.Collections;
import java.util.List;

import org.testar.engine.action.TextInputProvider;
import org.testar.engine.action.derivation.ActionDerivationPlan;
import org.testar.engine.action.derivation.ActionDeriver;
import org.testar.engine.action.derivation.StateActionDeriver;

/**
 * Reusable default Android derivation plan for Appium-driven mobile testing.
 */
public final class AndroidActionDerivationPlan {

    private AndroidActionDerivationPlan() {
    }

    public static ActionDerivationPlan create(TextInputProvider textInputProvider,
                                              boolean includeSystemActions) {
        List<ActionDeriver> defaultDerivers = List.of(
                new StateActionDeriver(new AndroidWidgetActionDeriver(textInputProvider)),
                new AndroidSystemActionDeriver(includeSystemActions)
        );
        List<ActionDeriver> fallbackDerivers = Collections.singletonList(new AndroidBackFallbackActionDeriver());

        return new ActionDerivationPlan(
                Collections.emptyList(),
                defaultDerivers,
                fallbackDerivers
        );
    }
}
