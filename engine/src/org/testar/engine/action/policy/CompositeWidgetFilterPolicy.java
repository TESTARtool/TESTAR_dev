/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.policy;

import java.util.Collections;
import java.util.List;

import org.testar.core.Assert;
import org.testar.core.action.policy.WidgetFilterPolicy;
import org.testar.core.state.Widget;

public final class CompositeWidgetFilterPolicy implements WidgetFilterPolicy {

    private final List<WidgetFilterPolicy> policies;

    public CompositeWidgetFilterPolicy(List<WidgetFilterPolicy> policies) {
        this.policies = Collections.unmodifiableList(Assert.notNull(policies));
    }

    public static CompositeWidgetFilterPolicy allowAll() {
        return new CompositeWidgetFilterPolicy(Collections.singletonList(widget -> true));
    }

    @Override
    public boolean allows(Widget widget) {
        for (WidgetFilterPolicy policy : policies) {
            if (!policy.allows(widget)) {
                return false;
            }
        }
        return true;
    }
}
