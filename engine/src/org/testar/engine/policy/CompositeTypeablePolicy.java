/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.policy;

import java.util.Collections;
import java.util.List;

import org.testar.core.Assert;
import org.testar.core.policy.TypeablePolicy;
import org.testar.core.state.Widget;

public final class CompositeTypeablePolicy implements TypeablePolicy {

    private final List<TypeablePolicy> policies;

    public CompositeTypeablePolicy(List<TypeablePolicy> policies) {
        this.policies = Collections.unmodifiableList(Assert.notNull(policies));
    }

    public static CompositeTypeablePolicy empty() {
        return new CompositeTypeablePolicy(Collections.emptyList());
    }

    @Override
    public boolean isTypeable(Widget widget) {
        // ANY: one matching policy is enough to consider the widget typeable.
        for (TypeablePolicy policy : policies) {
            if (policy.isTypeable(widget)) {
                return true;
            }
        }
        return false;
    }
}
