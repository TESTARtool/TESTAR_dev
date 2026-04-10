/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.policy;

import java.util.Collections;
import java.util.List;

import org.testar.core.Assert;
import org.testar.core.policy.ClickablePolicy;
import org.testar.core.state.Widget;

public final class CompositeClickablePolicy implements ClickablePolicy {

    private final List<ClickablePolicy> policies;

    public CompositeClickablePolicy(List<ClickablePolicy> policies) {
        this.policies = Collections.unmodifiableList(Assert.notNull(policies));
    }

    public static CompositeClickablePolicy empty() {
        return new CompositeClickablePolicy(Collections.emptyList());
    }

    @Override
    public boolean isClickable(Widget widget) {
        // ANY: one matching policy is enough to consider the widget clickable.
        for (ClickablePolicy policy : policies) {
            if (policy.isClickable(widget)) {
                return true;
            }
        }
        return false;
    }
}
