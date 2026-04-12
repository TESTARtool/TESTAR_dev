/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.policy.composite;

import java.util.Collections;
import java.util.List;

import org.testar.core.Assert;
import org.testar.core.policy.WidgetFilterPolicy;
import org.testar.core.state.Widget;

public final class CompositeWidgetFilterPolicy implements WidgetFilterPolicy {

    private final CompositePolicySupport<WidgetFilterPolicy> support;

    public CompositeWidgetFilterPolicy(List<WidgetFilterPolicy> policies) {
        this.support = new CompositePolicySupport<>(
                Collections.unmodifiableList(Assert.notNull(policies)),
                CompositePolicyRule.ALL
        );
    }

    @Override
    public boolean allows(Widget widget) {
        // ALL: every filter must allow the widget to keep it in scope.
        return support.matches(policy -> policy.allows(widget));
    }
}
