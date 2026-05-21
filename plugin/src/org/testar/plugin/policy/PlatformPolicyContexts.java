/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin.policy;

import java.util.Collections;
import java.util.List;

import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.android.policy.AndroidClickablePolicy;
import org.testar.android.policy.AndroidScrollablePolicy;
import org.testar.android.policy.AndroidTypeablePolicy;
import org.testar.android.policy.ConfigurableAndroidClickableClassPolicy;
import org.testar.android.policy.ConfigurableAndroidTypeableClassPolicy;
import org.testar.core.policy.BlockedPolicy;
import org.testar.core.policy.EnabledPolicy;
import org.testar.core.policy.SelectablePolicy;
import org.testar.core.policy.TopLevelPolicy;
import org.testar.core.policy.VisiblePolicy;
import org.testar.core.policy.WidgetFilterPolicy;
import org.testar.engine.policy.ConfiguredWidgetFilterPolicy;
import org.testar.engine.policy.SessionPolicyContext;
import org.testar.engine.policy.TagBlockedPolicy;
import org.testar.engine.policy.TagEnabledPolicy;
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
import org.testar.windows.action.policy.WindowsClickablePolicy;
import org.testar.windows.action.policy.WindowsScrollablePolicy;
import org.testar.windows.action.policy.WindowsTypeablePolicy;

/**
 * Plugin-side defaults for platform session policy composition.
 */
public final class PlatformPolicyContexts {

    private PlatformPolicyContexts() {
    }

    public static SessionPolicyContext desktopDefaults(Settings settings) {
        List<EnabledPolicy> enabledPolicies = List.of(new TagEnabledPolicy());
        List<BlockedPolicy> blockedPolicies = List.of(new TagBlockedPolicy());
        List<WidgetFilterPolicy> widgetFilterPolicies = List.of(new ConfiguredWidgetFilterPolicy(settings));
        List<VisiblePolicy> visiblePolicies = Collections.singletonList(widget -> true);
        List<TopLevelPolicy> topLevelPolicies = Collections.singletonList(widget -> true);

        return new SessionPolicyContext(
                new CompositeClickablePolicy(List.of(new WindowsClickablePolicy())),
                new CompositeTypeablePolicy(List.of(new WindowsTypeablePolicy())),
                new CompositeScrollablePolicy(List.of(new WindowsScrollablePolicy())),
                new CompositeSelectablePolicy(Collections.<SelectablePolicy>emptyList()),
                new CompositeEnabledPolicy(enabledPolicies),
                new CompositeBlockedPolicy(blockedPolicies),
                new CompositeWidgetFilterPolicy(widgetFilterPolicies),
                new CompositeVisiblePolicy(visiblePolicies),
                new CompositeTopLevelPolicy(topLevelPolicies)
        );
    }

    public static SessionPolicyContext webdriverDefaults(Settings settings) {
        List<EnabledPolicy> enabledPolicies = List.of(new TagEnabledPolicy());
        List<BlockedPolicy> blockedPolicies = List.of(new TagBlockedPolicy());
        List<WidgetFilterPolicy> widgetFilterPolicies = List.of(new ConfiguredWidgetFilterPolicy(settings));
        List<VisiblePolicy> visiblePolicies = Collections.singletonList(widget -> true);
        List<TopLevelPolicy> topLevelPolicies = Collections.singletonList(widget -> true);

        return new SessionPolicyContext(
                new CompositeClickablePolicy(List.of(
                        new WebdriverClickablePolicy(),
                        new ConfigurableWebdriverClickableClassPolicy(settings.get(ConfigTags.WebClickableClasses, Collections.emptyList()))
                )),
                new CompositeTypeablePolicy(List.of(
                        new WebdriverTypeablePolicy(),
                        new ConfigurableWebdriverTypeableClassPolicy(settings.get(ConfigTags.WebTypeableClasses, Collections.emptyList()))
                )),
                new CompositeScrollablePolicy(List.of(new WebdriverScrollablePolicy())),
                new CompositeSelectablePolicy(List.of(new WebdriverSelectablePolicy())),
                new CompositeEnabledPolicy(enabledPolicies),
                new CompositeBlockedPolicy(blockedPolicies),
                new CompositeWidgetFilterPolicy(widgetFilterPolicies),
                new CompositeVisiblePolicy(visiblePolicies),
                new CompositeTopLevelPolicy(topLevelPolicies)
        );
    }

    public static SessionPolicyContext androidDefaults(Settings settings) {
        List<EnabledPolicy> enabledPolicies = List.of(new TagEnabledPolicy());
        List<BlockedPolicy> blockedPolicies = List.of(new TagBlockedPolicy());
        List<WidgetFilterPolicy> widgetFilterPolicies = List.of(new ConfiguredWidgetFilterPolicy(settings));
        List<VisiblePolicy> visiblePolicies = Collections.singletonList(widget -> true);
        List<TopLevelPolicy> topLevelPolicies = Collections.singletonList(widget -> true);

        return new SessionPolicyContext(
                new CompositeClickablePolicy(List.of(
                        new AndroidClickablePolicy(),
                        new ConfigurableAndroidClickableClassPolicy(settings.get(ConfigTags.AndroidClickableClasses, Collections.emptyList()))
                )),
                new CompositeTypeablePolicy(List.of(
                        new AndroidTypeablePolicy(),
                        new ConfigurableAndroidTypeableClassPolicy(settings.get(ConfigTags.AndroidTypeableClasses, Collections.emptyList()))
                )),
                new CompositeScrollablePolicy(List.of(new AndroidScrollablePolicy())),
                new CompositeSelectablePolicy(Collections.<SelectablePolicy>emptyList()),
                new CompositeEnabledPolicy(enabledPolicies),
                new CompositeBlockedPolicy(blockedPolicies),
                new CompositeWidgetFilterPolicy(widgetFilterPolicies),
                new CompositeVisiblePolicy(visiblePolicies),
                new CompositeTopLevelPolicy(topLevelPolicies)
        );
    }
}
