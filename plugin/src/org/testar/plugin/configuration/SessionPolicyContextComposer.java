/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin.configuration;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.testar.core.Assert;
import org.testar.core.policy.BlockedPolicy;
import org.testar.core.policy.ClickablePolicy;
import org.testar.core.policy.EnabledPolicy;
import org.testar.core.policy.Policy;
import org.testar.core.policy.ScrollablePolicy;
import org.testar.core.policy.SelectablePolicy;
import org.testar.core.policy.TopLevelPolicy;
import org.testar.core.policy.TypeablePolicy;
import org.testar.core.policy.VisiblePolicy;
import org.testar.core.policy.WidgetFilterPolicy;
import org.testar.engine.policy.SessionPolicyContext;
import org.testar.engine.policy.composite.CompositeBlockedPolicy;
import org.testar.engine.policy.composite.CompositeClickablePolicy;
import org.testar.engine.policy.composite.CompositeEnabledPolicy;
import org.testar.engine.policy.composite.CompositeSelectablePolicy;
import org.testar.engine.policy.composite.CompositeScrollablePolicy;
import org.testar.engine.policy.composite.CompositeTopLevelPolicy;
import org.testar.engine.policy.composite.CompositeTypeablePolicy;
import org.testar.engine.policy.composite.CompositeVisiblePolicy;
import org.testar.engine.policy.composite.CompositeWidgetFilterPolicy;

/**
 * Builds a final session policy context from platform defaults plus plugin-side
 * configuration overrides or extensions.
 */
public final class SessionPolicyContextComposer {

    private SessionPolicyContextComposer() {
    }

    public static SessionPolicyContext compose(SessionPolicyContext platformDefaults,
                                               PolicySessionConfiguration configuration) {
        Assert.notNull(platformDefaults);
        Assert.notNull(configuration);

        List<Policy> effectivePolicies = new ArrayList<>();
        for (Class<? extends Policy> policyType : collectPolicyTypes(platformDefaults, configuration)) {
            Policy effectivePolicy = composePolicy(policyType, platformDefaults, configuration);
            if (effectivePolicy != null) {
                effectivePolicies.add(effectivePolicy);
            }
        }

        return new SessionPolicyContext(effectivePolicies);
    }

    private static Set<Class<? extends Policy>> collectPolicyTypes(SessionPolicyContext platformDefaults,
                                                                   PolicySessionConfiguration configuration) {
        // Consider every policy family mentioned either by platform defaults or by the session-specific configuration.
        Set<Class<? extends Policy>> policyTypes = new LinkedHashSet<>();
        policyTypes.addAll(platformDefaults.policies().keySet());
        policyTypes.addAll(configuration.additionalPolicies().keySet());
        policyTypes.addAll(configuration.replacementPolicies().keySet());
        return policyTypes;
    }

    private static Policy composePolicy(Class<? extends Policy> policyType,
                                        SessionPolicyContext platformDefaults,
                                        PolicySessionConfiguration configuration) {
        List<? extends Policy> effectivePolicies = effectivePoliciesFor(policyType, platformDefaults, configuration);
        if (effectivePolicies.isEmpty()) {
            return null;
        }

        return composePolicies(policyType, effectivePolicies);
    }

    private static List<? extends Policy> effectivePoliciesFor(Class<? extends Policy> policyType,
                                                               SessionPolicyContext platformDefaults,
                                                               PolicySessionConfiguration configuration) {
        // Replacement policies fully own one family and bypass platform defaults plus additive policies.
        List<? extends Policy> replacementPolicies = configuration.replacementPolicies(policyType);
        if (!replacementPolicies.isEmpty()) {
            return replacementPolicies;
        }

        // Otherwise extend the platform baseline with any additional session-specific policies.
        List<Policy> policies = new ArrayList<>();
        if (configuration.includePlatformDefaults()) {
            Policy defaultPolicy = platformDefaults.get(policyType);
            if (defaultPolicy != null) {
                policies.add(defaultPolicy);
            }
        }
        policies.addAll(configuration.additionalPolicies(policyType));
        return policies;
    }

    @SuppressWarnings("unchecked")
    private static Policy composePolicies(Class<? extends Policy> policyType,
                                          List<? extends Policy> policies) {
        // One policy instance needs no composite. Multiple instances require the family-specific composite wrapper.
        if (policies.size() == 1) {
            return policies.get(0);
        }
        if (policyType == ClickablePolicy.class) {
            return new CompositeClickablePolicy((List<ClickablePolicy>) policies);
        }
        if (policyType == TypeablePolicy.class) {
            return new CompositeTypeablePolicy((List<TypeablePolicy>) policies);
        }
        if (policyType == ScrollablePolicy.class) {
            return new CompositeScrollablePolicy((List<ScrollablePolicy>) policies);
        }
        if (policyType == SelectablePolicy.class) {
            return new CompositeSelectablePolicy((List<SelectablePolicy>) policies);
        }
        if (policyType == EnabledPolicy.class) {
            return new CompositeEnabledPolicy((List<EnabledPolicy>) policies);
        }
        if (policyType == BlockedPolicy.class) {
            return new CompositeBlockedPolicy((List<BlockedPolicy>) policies);
        }
        if (policyType == WidgetFilterPolicy.class) {
            return new CompositeWidgetFilterPolicy((List<WidgetFilterPolicy>) policies);
        }
        if (policyType == VisiblePolicy.class) {
            return new CompositeVisiblePolicy((List<VisiblePolicy>) policies);
        }
        if (policyType == TopLevelPolicy.class) {
            return new CompositeTopLevelPolicy((List<TopLevelPolicy>) policies);
        }

        throw new IllegalArgumentException(
                "Cannot compose multiple policies for unsupported type " + policyType.getName()
        );
    }
}
