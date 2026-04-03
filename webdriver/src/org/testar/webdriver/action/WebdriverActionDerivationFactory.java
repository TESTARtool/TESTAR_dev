/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webdriver.action;

import java.util.Collections;
import java.util.List;

import org.testar.core.action.policy.ClickablePolicy;
import org.testar.core.action.policy.ScrollablePolicy;
import org.testar.core.action.policy.TypeablePolicy;
import org.testar.core.action.policy.WidgetFilterPolicy;
import org.testar.engine.action.TextInputProvider;
import org.testar.engine.action.derivation.DefaultActionDerivationService;
import org.testar.engine.action.derivation.EscFallbackActionDeriver;
import org.testar.engine.action.derivation.StateActionDeriver;
import org.testar.engine.action.policy.EnabledWidgetFilterPolicy;
import org.testar.engine.action.policy.UnblockedWidgetFilterPolicy;

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
        List<WidgetFilterPolicy> widgetFilters = List.of(
                new EnabledWidgetFilterPolicy(),
                new UnblockedWidgetFilterPolicy()
        );

        return DefaultActionDerivationService.prioritizedWithPolicies(
                Collections.singletonList(clickablePolicy),
                Collections.singletonList(typeablePolicy),
                Collections.singletonList(scrollablePolicy),
                widgetFilters,
                Collections.emptyList(),
                Collections.singletonList(new StateActionDeriver(
                        new WebdriverWidgetActionDeriver(textInputProvider)
                )),
                Collections.singletonList(new EscFallbackActionDeriver())
        );
    }
}
