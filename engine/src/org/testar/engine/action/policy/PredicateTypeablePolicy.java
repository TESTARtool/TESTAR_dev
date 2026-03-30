/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.policy;

import java.util.function.Predicate;

import org.testar.core.Assert;
import org.testar.core.action.policy.TypeablePolicy;
import org.testar.core.state.Widget;

public final class PredicateTypeablePolicy implements TypeablePolicy {

    private final Predicate<Widget> predicate;

    public PredicateTypeablePolicy(Predicate<Widget> predicate) {
        this.predicate = Assert.notNull(predicate);
    }

    @Override
    public boolean isTypeable(Widget widget) {
        return predicate.test(widget);
    }
}
