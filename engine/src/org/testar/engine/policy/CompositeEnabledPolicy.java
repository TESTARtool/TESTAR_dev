/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.policy;

import java.util.Collections;
import java.util.List;

import org.testar.core.Assert;
import org.testar.core.policy.EnabledPolicy;
import org.testar.core.state.Widget;

public final class CompositeEnabledPolicy implements EnabledPolicy {

    private final List<EnabledPolicy> policies;

    public CompositeEnabledPolicy(List<EnabledPolicy> policies) {
        this.policies = Collections.unmodifiableList(Assert.notNull(policies));
    }

    public static CompositeEnabledPolicy allowAll() {
        return new CompositeEnabledPolicy(Collections.singletonList(widget -> true));
    }

    @Override
    public boolean isEnabled(Widget widget) {
        // ALL: every policy must allow the widget to remain enabled.
        for (EnabledPolicy policy : policies) {
            if (!policy.isEnabled(widget)) {
                return false;
            }
        }
        return true;
    }
}
