/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.policy.composite;

import java.util.Collections;
import java.util.List;

import org.testar.core.Assert;
import org.testar.core.policy.ClickablePolicy;
import org.testar.core.state.Widget;

public final class CompositeClickablePolicy implements ClickablePolicy {

    private final CompositePolicySupport<ClickablePolicy> support;

    public CompositeClickablePolicy(List<ClickablePolicy> policies) {
        this.support = new CompositePolicySupport<>(
                Collections.unmodifiableList(Assert.notNull(policies)),
                CompositePolicyRule.ANY
        );
    }

    @Override
    public boolean isClickable(Widget widget) {
        // ANY: one matching policy is enough to consider the widget clickable.
        return support.matches(policy -> policy.isClickable(widget));
    }
}
