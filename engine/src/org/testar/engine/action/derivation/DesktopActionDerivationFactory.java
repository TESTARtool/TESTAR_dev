/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.derivation;

import java.util.Collections;
import java.util.List;

import org.testar.core.policy.BlockedPolicy;
import org.testar.core.policy.ClickablePolicy;
import org.testar.core.policy.EnabledPolicy;
import org.testar.core.policy.ScrollablePolicy;
import org.testar.core.policy.TypeablePolicy;
import org.testar.core.policy.WidgetFilterPolicy;
import org.testar.engine.action.TextInputProvider;
import org.testar.engine.policy.TagBlockedPolicy;
import org.testar.engine.policy.TagEnabledPolicy;

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

        List<EnabledPolicy> enabledPolicies = List.of(new TagEnabledPolicy());
        List<BlockedPolicy> blockedPolicies = List.of(new TagBlockedPolicy());
        List<WidgetFilterPolicy> widgetFilters = Collections.emptyList();

        return DefaultActionDerivationService.prioritizedWithPolicies(
                Collections.singletonList(clickablePolicy),
                Collections.singletonList(typeablePolicy),
                Collections.singletonList(scrollablePolicy),
                enabledPolicies,
                blockedPolicies,
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
