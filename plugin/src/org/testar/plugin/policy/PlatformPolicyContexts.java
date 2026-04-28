/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin.policy;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.testar.core.policy.AtCanvasPolicy;
import org.testar.core.policy.BlockedPolicy;
import org.testar.core.policy.ClickablePolicy;
import org.testar.core.policy.EnabledPolicy;
import org.testar.core.policy.ScrollablePolicy;
import org.testar.core.policy.SelectablePolicy;
import org.testar.core.policy.TopLevelPolicy;
import org.testar.core.policy.TypeablePolicy;
import org.testar.core.policy.VisiblePolicy;
import org.testar.core.policy.WidgetFilterPolicy;
import org.testar.engine.policy.SessionPolicyContext;
import org.testar.engine.policy.TagBlockedPolicy;
import org.testar.engine.policy.TagEnabledPolicy;
import org.testar.engine.policy.composite.CompositeAtCanvasPolicy;
import org.testar.engine.policy.composite.CompositeBlockedPolicy;
import org.testar.engine.policy.composite.CompositeClickablePolicy;
import org.testar.engine.policy.composite.CompositeEnabledPolicy;
import org.testar.engine.policy.composite.CompositeSelectablePolicy;
import org.testar.engine.policy.composite.CompositeScrollablePolicy;
import org.testar.engine.policy.composite.CompositeTopLevelPolicy;
import org.testar.engine.policy.composite.CompositeTypeablePolicy;
import org.testar.engine.policy.composite.CompositeVisiblePolicy;
import org.testar.engine.policy.composite.CompositeWidgetFilterPolicy;
import org.testar.webdriver.policy.ConfigurableWebdriverClickableClassPolicy;
import org.testar.webdriver.policy.ConfigurableWebdriverTypeableClassPolicy;
import org.testar.webdriver.policy.WebdriverClickablePolicy;
import org.testar.webdriver.policy.WebdriverSelectablePolicy;
import org.testar.webdriver.policy.WebdriverScrollablePolicy;
import org.testar.webdriver.policy.WebdriverTypeablePolicy;

/**
 * Plugin-side defaults for platform session policy composition.
 */
public final class PlatformPolicyContexts {

    private PlatformPolicyContexts() {
    }

    public static SessionPolicyContext desktopDefaults(ClickablePolicy clickablePolicy,
                                                       TypeablePolicy typeablePolicy,
                                                       ScrollablePolicy scrollablePolicy) {
        List<EnabledPolicy> enabledPolicies = List.of(new TagEnabledPolicy());
        List<BlockedPolicy> blockedPolicies = List.of(new TagBlockedPolicy());
        List<WidgetFilterPolicy> widgetFilterPolicies = Collections.emptyList();
        List<VisiblePolicy> visiblePolicies = Collections.singletonList(widget -> true);
        List<AtCanvasPolicy> atCanvasPolicies = Collections.singletonList(widget -> true);
        List<TopLevelPolicy> topLevelPolicies = Collections.singletonList(widget -> true);

        return new SessionPolicyContext(
                new CompositeClickablePolicy(List.of(clickablePolicy)),
                new CompositeTypeablePolicy(List.of(typeablePolicy)),
                new CompositeScrollablePolicy(List.of(scrollablePolicy)),
                new CompositeSelectablePolicy(Collections.<SelectablePolicy>emptyList()),
                new CompositeEnabledPolicy(enabledPolicies),
                new CompositeBlockedPolicy(blockedPolicies),
                new CompositeWidgetFilterPolicy(widgetFilterPolicies),
                new CompositeVisiblePolicy(visiblePolicies),
                new CompositeAtCanvasPolicy(atCanvasPolicies),
                new CompositeTopLevelPolicy(topLevelPolicies)
        );
    }

    public static SessionPolicyContext webdriverDefaults(Collection<String> customClickableClasses,
                                                         Collection<String> customTypeableClasses) {
        List<EnabledPolicy> enabledPolicies = List.of(new TagEnabledPolicy());
        List<BlockedPolicy> blockedPolicies = List.of(new TagBlockedPolicy());
        List<WidgetFilterPolicy> widgetFilterPolicies = Collections.emptyList();
        List<VisiblePolicy> visiblePolicies = Collections.singletonList(widget -> true);
        List<AtCanvasPolicy> atCanvasPolicies = Collections.singletonList(widget -> true);
        List<TopLevelPolicy> topLevelPolicies = Collections.singletonList(widget -> true);

        return new SessionPolicyContext(
                new CompositeClickablePolicy(List.of(
                        new WebdriverClickablePolicy(),
                        new ConfigurableWebdriverClickableClassPolicy(customClickableClasses)
                )),
                new CompositeTypeablePolicy(List.of(
                        new WebdriverTypeablePolicy(),
                        new ConfigurableWebdriverTypeableClassPolicy(customTypeableClasses)
                )),
                new CompositeScrollablePolicy(List.of(new WebdriverScrollablePolicy())),
                new CompositeSelectablePolicy(List.of(new WebdriverSelectablePolicy())),
                new CompositeEnabledPolicy(enabledPolicies),
                new CompositeBlockedPolicy(blockedPolicies),
                new CompositeWidgetFilterPolicy(widgetFilterPolicies),
                new CompositeVisiblePolicy(visiblePolicies),
                new CompositeAtCanvasPolicy(atCanvasPolicies),
                new CompositeTopLevelPolicy(topLevelPolicies)
        );
    }
}
