/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.policy.composite;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.testar.core.Assert;

final class CompositePolicySupport<T> {

    private final List<T> policies;
    private final CompositePolicyRule rule;

    CompositePolicySupport(List<T> policies, CompositePolicyRule rule) {
        this.policies = Collections.unmodifiableList(Assert.notNull(policies));
        this.rule = Assert.notNull(rule);
    }

    boolean matches(Predicate<T> predicate) {
        Assert.notNull(predicate);
        if (rule == CompositePolicyRule.ANY) {
            for (T policy : policies) {
                if (predicate.test(policy)) {
                    return true;
                }
            }
            return false;
        }

        for (T policy : policies) {
            if (!predicate.test(policy)) {
                return false;
            }
        }
        return true;
    }
}
