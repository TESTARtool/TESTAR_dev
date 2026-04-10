/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.policy;

import java.util.Collections;
import java.util.List;

import org.testar.core.Assert;
import org.testar.core.policy.VisiblePolicy;
import org.testar.core.state.Widget;

public final class CompositeVisiblePolicy implements VisiblePolicy {

    private final List<VisiblePolicy> policies;

    public CompositeVisiblePolicy(List<VisiblePolicy> policies) {
        this.policies = Collections.unmodifiableList(Assert.notNull(policies));
    }

    public static CompositeVisiblePolicy allowAll() {
        return new CompositeVisiblePolicy(Collections.singletonList(widget -> true));
    }

    @Override
    public boolean isVisible(Widget widget) {
        // ALL: every policy must consider the widget visible.
        for (VisiblePolicy policy : policies) {
            if (!policy.isVisible(widget)) {
                return false;
            }
        }
        return true;
    }
}
