/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.policy;

import java.util.Collections;
import java.util.List;

import org.testar.core.Assert;
import org.testar.core.policy.BlockedPolicy;
import org.testar.core.state.Widget;

public final class CompositeBlockedPolicy implements BlockedPolicy {

    private final List<BlockedPolicy> policies;

    public CompositeBlockedPolicy(List<BlockedPolicy> policies) {
        this.policies = Collections.unmodifiableList(Assert.notNull(policies));
    }

    public static CompositeBlockedPolicy allowNone() {
        return new CompositeBlockedPolicy(Collections.singletonList(widget -> false));
    }

    @Override
    public boolean isBlocked(Widget widget) {
        // ANY: one matching policy is enough to block the widget.
        for (BlockedPolicy policy : policies) {
            if (policy.isBlocked(widget)) {
                return true;
            }
        }
        return false;
    }
}
