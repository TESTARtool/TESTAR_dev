/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.policy;

import java.util.Collections;
import java.util.List;

import org.testar.core.Assert;
import org.testar.core.policy.ScrollablePolicy;
import org.testar.core.state.Widget;

public final class CompositeScrollablePolicy implements ScrollablePolicy {

    private final List<ScrollablePolicy> policies;

    public CompositeScrollablePolicy(List<ScrollablePolicy> policies) {
        this.policies = Collections.unmodifiableList(Assert.notNull(policies));
    }

    public static CompositeScrollablePolicy empty() {
        return new CompositeScrollablePolicy(Collections.emptyList());
    }

    @Override
    public boolean isScrollable(Widget widget) {
        // ANY: one matching policy is enough to consider the widget scrollable.
        for (ScrollablePolicy policy : policies) {
            if (policy.isScrollable(widget)) {
                return true;
            }
        }
        return false;
    }
}
