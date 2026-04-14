/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.testar.core.Assert;
import org.testar.core.policy.Policy;

/**
 * Plugin-side configuration contract that describes how a session policy
 * context should be composed.
 */
public final class SessionPolicyConfiguration {

    private final boolean includePlatformDefaults;
    private final Map<Class<? extends Policy>, List<Policy>> additionalPolicies;
    private final Map<Class<? extends Policy>, List<Policy>> replacementPolicies;

    private SessionPolicyConfiguration(boolean includePlatformDefaults,
                                       Map<Class<? extends Policy>, List<Policy>> additionalPolicies,
                                       Map<Class<? extends Policy>, List<Policy>> replacementPolicies) {
        this.includePlatformDefaults = includePlatformDefaults;
        this.additionalPolicies = copyPolicies(additionalPolicies);
        this.replacementPolicies = copyPolicies(replacementPolicies);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SessionPolicyConfiguration defaults() {
        return builder().build();
    }

    public boolean includePlatformDefaults() {
        return includePlatformDefaults;
    }

    public Map<Class<? extends Policy>, List<Policy>> additionalPolicies() {
        return additionalPolicies;
    }

    public Map<Class<? extends Policy>, List<Policy>> replacementPolicies() {
        return replacementPolicies;
    }

    @SuppressWarnings("unchecked")
    public <T extends Policy> List<T> additionalPolicies(Class<T> policyType) {
        return (List<T>) additionalPolicies.getOrDefault(policyType, Collections.emptyList());
    }

    @SuppressWarnings("unchecked")
    public <T extends Policy> List<T> replacementPolicies(Class<T> policyType) {
        return (List<T>) replacementPolicies.getOrDefault(policyType, Collections.emptyList());
    }

    private Map<Class<? extends Policy>, List<Policy>> copyPolicies(
            Map<Class<? extends Policy>, List<Policy>> policiesByType) {
        Map<Class<? extends Policy>, List<Policy>> copiedPoliciesByType = new LinkedHashMap<>();
        for (Map.Entry<Class<? extends Policy>, List<Policy>> entry : policiesByType.entrySet()) {
            copiedPoliciesByType.put(
                    Assert.notNull(entry.getKey()),
                    Collections.unmodifiableList(new ArrayList<>(Assert.notNull(entry.getValue())))
            );
        }
        return Collections.unmodifiableMap(copiedPoliciesByType);
    }

    public static final class Builder {

        private boolean includePlatformDefaults = true;
        private final Map<Class<? extends Policy>, List<Policy>> additionalPolicies = new LinkedHashMap<>();
        private final Map<Class<? extends Policy>, List<Policy>> replacementPolicies = new LinkedHashMap<>();

        private Builder() {
        }

        public Builder includePlatformDefaults(boolean includePlatformDefaults) {
            this.includePlatformDefaults = includePlatformDefaults;
            return this;
        }

        public <T extends Policy> Builder addPolicy(Class<T> policyType, T policy) {
            Assert.notNull(policyType);
            Assert.notNull(policy);
            additionalPolicies
                    .computeIfAbsent(policyType, ignored -> new ArrayList<>())
                    .add(policy);
            return this;
        }

        public <T extends Policy> Builder replacePolicies(Class<T> policyType, List<T> policies) {
            Assert.notNull(policyType);
            Assert.notNull(policies);
            replacementPolicies.put(policyType, new ArrayList<>(policies));
            return this;
        }

        public SessionPolicyConfiguration build() {
            return new SessionPolicyConfiguration(
                    includePlatformDefaults,
                    additionalPolicies,
                    replacementPolicies
            );
        }
    }
}
