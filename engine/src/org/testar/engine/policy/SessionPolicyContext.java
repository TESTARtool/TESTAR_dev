/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.policy;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.testar.core.Assert;
import org.testar.core.policy.Policy;

/**
 * Stores effective session policies as a typed registry keyed by policy
 * interface.
 */
public final class SessionPolicyContext {

    private final Map<Class<? extends Policy>, Policy> policies;

    public SessionPolicyContext(Policy... policies) {
        this(java.util.Arrays.asList(Assert.notNull(policies)));
    }

    public SessionPolicyContext(Collection<? extends Policy> policies) {
        Assert.notNull(policies);
        Map<Class<? extends Policy>, Policy> registeredPolicies = new LinkedHashMap<>();
        for (Policy policy : policies) {
            registerPolicyInterfaces(registeredPolicies, Assert.notNull(policy), policy.getClass());
        }
        this.policies = Collections.unmodifiableMap(registeredPolicies);
    }

    public <T extends Policy> T require(Class<T> policyType) {
        Assert.notNull(policyType);
        T policy = get(policyType);
        if (policy == null) {
            throw new IllegalStateException("No policy registered for type " + policyType.getName());
        }
        return policy;
    }

    @SuppressWarnings("unchecked")
    public <T extends Policy> T get(Class<T> policyType) {
        Assert.notNull(policyType);
        return (T) policies.get(policyType);
    }

    public Map<Class<? extends Policy>, Policy> policies() {
        return policies;
    }

    private void registerPolicyInterfaces(Map<Class<? extends Policy>, Policy> registeredPolicies,
                                          Policy policy,
                                          Class<?> policyClass) {
        if (policyClass == null || policyClass == Object.class) {
            return;
        }
        for (Class<?> implementedInterface : policyClass.getInterfaces()) {
            if (implementedInterface == Policy.class) {
                continue;
            }
            if (Policy.class.isAssignableFrom(implementedInterface)) {
                @SuppressWarnings("unchecked")
                Class<? extends Policy> policyInterface = (Class<? extends Policy>) implementedInterface;
                registeredPolicies.put(policyInterface, policy);
            }
            registerPolicyInterfaces(registeredPolicies, policy, implementedInterface);
        }
        registerPolicyInterfaces(registeredPolicies, policy, policyClass.getSuperclass());
    }
}
