/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.policy.composite;

import java.util.Collections;
import java.util.List;

import org.testar.core.Assert;
import org.testar.core.policy.TopLevelPolicy;
import org.testar.core.state.Widget;

public final class CompositeTopLevelPolicy implements TopLevelPolicy {

    private final CompositePolicySupport<TopLevelPolicy> support;

    public CompositeTopLevelPolicy(List<TopLevelPolicy> policies) {
        this.support = new CompositePolicySupport<>(
                Collections.unmodifiableList(Assert.notNull(policies)),
                CompositePolicyRule.ALL
        );
    }

    @Override
    public boolean isTopLevel(Widget widget) {
        // ALL: every policy must consider the widget top-level.
        return support.matches(policy -> policy.isTopLevel(widget));
    }
}
