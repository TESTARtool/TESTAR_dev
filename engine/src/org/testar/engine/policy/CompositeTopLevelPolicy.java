/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.policy;

import java.util.Collections;
import java.util.List;

import org.testar.core.Assert;
import org.testar.core.policy.TopLevelPolicy;
import org.testar.core.state.Widget;

public final class CompositeTopLevelPolicy implements TopLevelPolicy {

    private final List<TopLevelPolicy> policies;

    public CompositeTopLevelPolicy(List<TopLevelPolicy> policies) {
        this.policies = Collections.unmodifiableList(Assert.notNull(policies));
    }

    public static CompositeTopLevelPolicy allowAll() {
        return new CompositeTopLevelPolicy(Collections.singletonList(widget -> true));
    }

    @Override
    public boolean isTopLevel(Widget widget) {
        // ALL: every policy must consider the widget top-level.
        for (TopLevelPolicy policy : policies) {
            if (!policy.isTopLevel(widget)) {
                return false;
            }
        }
        return true;
    }
}
