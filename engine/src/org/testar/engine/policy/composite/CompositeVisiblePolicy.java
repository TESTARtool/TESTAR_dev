/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.policy.composite;

import java.util.Collections;
import java.util.List;

import org.testar.core.Assert;
import org.testar.core.policy.VisiblePolicy;
import org.testar.core.state.Widget;

public final class CompositeVisiblePolicy implements VisiblePolicy {

    private final CompositePolicySupport<VisiblePolicy> support;

    public CompositeVisiblePolicy(List<VisiblePolicy> policies) {
        this.support = new CompositePolicySupport<>(
                Collections.unmodifiableList(Assert.notNull(policies)),
                CompositePolicyRule.ALL
        );
    }

    @Override
    public boolean isVisible(Widget widget) {
        // ALL: every policy must consider the widget visible.
        return support.matches(policy -> policy.isVisible(widget));
    }
}
