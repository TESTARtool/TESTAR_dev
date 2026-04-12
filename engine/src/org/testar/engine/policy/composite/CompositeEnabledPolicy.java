/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.policy.composite;

import java.util.Collections;
import java.util.List;

import org.testar.core.Assert;
import org.testar.core.policy.EnabledPolicy;
import org.testar.core.state.Widget;

public final class CompositeEnabledPolicy implements EnabledPolicy {

    private final CompositePolicySupport<EnabledPolicy> support;

    public CompositeEnabledPolicy(List<EnabledPolicy> policies) {
        this.support = new CompositePolicySupport<>(
                Collections.unmodifiableList(Assert.notNull(policies)),
                CompositePolicyRule.ALL
        );
    }

    @Override
    public boolean isEnabled(Widget widget) {
        // ALL: every policy must allow the widget to remain enabled.
        return support.matches(policy -> policy.isEnabled(widget));
    }
}
