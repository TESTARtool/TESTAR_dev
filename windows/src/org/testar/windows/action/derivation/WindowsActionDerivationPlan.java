/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.action.derivation;

import java.util.Collections;
import java.util.List;

import org.testar.engine.action.TextInputProvider;
import org.testar.engine.action.derivation.ActionDerivationPlan;
import org.testar.engine.action.derivation.ActionDeriver;
import org.testar.engine.action.derivation.EscFallbackActionDeriver;
import org.testar.engine.action.derivation.ForegroundActionDeriver;
import org.testar.engine.action.derivation.KillProcessesActionDeriver;
import org.testar.engine.action.derivation.StateActionDeriver;

/**
 * Reusable default Windows derivation plan for desktop testing.
 */
public final class WindowsActionDerivationPlan {

    private WindowsActionDerivationPlan() {
    }

    public static ActionDerivationPlan create(String processesToKillRegex,
                                              TextInputProvider textInputProvider) {
        List<ActionDeriver> forcedDerivers =
                hasProcessKillRegex(processesToKillRegex)
                        ? List.of(
                                new KillProcessesActionDeriver(processesToKillRegex),
                                new ForegroundActionDeriver()
                        )
                        : List.of(new ForegroundActionDeriver());
        ActionDeriver defaultDeriver = new StateActionDeriver(
                new WindowsWidgetActionDeriver(textInputProvider)
        );
        List<ActionDeriver> defaultDerivers = Collections.singletonList(defaultDeriver);
        List<ActionDeriver> fallbackDerivers = Collections.singletonList(new EscFallbackActionDeriver());

        return new ActionDerivationPlan(
                forcedDerivers,
                defaultDerivers,
                fallbackDerivers
        );
    }

    private static boolean hasProcessKillRegex(String processKillRegex) {
        return processKillRegex != null
                && !processKillRegex.isBlank()
                && !"(?!x)x".equals(processKillRegex);
    }
}
