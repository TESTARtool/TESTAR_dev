/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.derivation;

import java.util.Collections;
import java.util.List;

import org.testar.core.action.policy.ClickablePolicy;
import org.testar.core.action.policy.ScrollablePolicy;
import org.testar.core.action.policy.TypeablePolicy;
import org.testar.core.action.policy.WidgetFilterPolicy;
import org.testar.engine.action.TextInputProvider;
import org.testar.engine.action.policy.EnabledWidgetFilterPolicy;
import org.testar.engine.action.policy.UnblockedWidgetFilterPolicy;

/**
 * Reusable default desktop derive-action composition.
 */
public final class DesktopActionDerivationFactory {

    private DesktopActionDerivationFactory() {
    }

    public static DefaultActionDerivationService create(ClickablePolicy clickablePolicy,
                                                        TypeablePolicy typeablePolicy,
                                                        ScrollablePolicy scrollablePolicy,
                                                        String processesToKillRegex,
                                                        TextInputProvider textInputProvider) {
        List<ActionDeriver> forcedDerivers =
                hasProcessKillRegex(processesToKillRegex)
                        ? List.of(
                                new KillProcessesActionDeriver(processesToKillRegex),
                                new ForegroundActionDeriver()
                        )
                        : List.of(new ForegroundActionDeriver());

        List<WidgetFilterPolicy> widgetFilters = List.of(
                new EnabledWidgetFilterPolicy(),
                new UnblockedWidgetFilterPolicy()
        );

        return DefaultActionDerivationService.prioritizedWithPolicies(
                Collections.singletonList(clickablePolicy),
                Collections.singletonList(typeablePolicy),
                Collections.singletonList(scrollablePolicy),
                widgetFilters,
                forcedDerivers,
                Collections.singletonList(new StateActionDeriver(
                        new DesktopWidgetActionDeriver(textInputProvider)
                )),
                Collections.singletonList(new EscFallbackActionDeriver())
        );
    }

    private static boolean hasProcessKillRegex(String processKillRegex) {
        return processKillRegex != null
                && !processKillRegex.isBlank()
                && !"(?!x)x".equals(processKillRegex);
    }
}
