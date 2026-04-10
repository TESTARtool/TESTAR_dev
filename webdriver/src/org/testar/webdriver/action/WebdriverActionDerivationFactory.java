/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webdriver.action;

import java.util.Collections;
import java.util.List;

import org.testar.core.policy.BlockedPolicy;
import org.testar.core.policy.ClickablePolicy;
import org.testar.core.policy.EnabledPolicy;
import org.testar.core.policy.ScrollablePolicy;
import org.testar.core.policy.TypeablePolicy;
import org.testar.core.policy.WidgetFilterPolicy;
import org.testar.engine.action.TextInputProvider;
import org.testar.engine.action.derivation.DefaultActionDerivationService;
import org.testar.engine.action.derivation.EscFallbackActionDeriver;
import org.testar.engine.action.derivation.StateActionDeriver;
import org.testar.engine.policy.TagBlockedPolicy;
import org.testar.engine.policy.TagEnabledPolicy;

/**
 * WebDriver default derive-action composition using remote scroll-aware web
 * actions.
 */
public final class WebdriverActionDerivationFactory {

    private WebdriverActionDerivationFactory() {
    }

    public static DefaultActionDerivationService create(ClickablePolicy clickablePolicy,
                                                        TypeablePolicy typeablePolicy,
                                                        ScrollablePolicy scrollablePolicy,
                                                        TextInputProvider textInputProvider) {
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
                Collections.emptyList(),
                Collections.singletonList(new StateActionDeriver(
                        new WebdriverWidgetActionDeriver(textInputProvider)
                )),
                Collections.singletonList(new EscFallbackActionDeriver())
        );
    }
}
