/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.resolver;

import org.testar.core.Assert;
import org.testar.core.action.resolver.ActionResolver;

/**
 * Minimal composition contract for action resolution orchestration.
 */
public final class ActionResolverPlan {

    private final ActionResolver actionResolver;

    public ActionResolverPlan(ActionResolver actionResolver) {
        this.actionResolver = Assert.notNull(actionResolver);
    }

    public static ActionResolverPlan basic(ActionResolver actionResolver) {
        return new ActionResolverPlan(actionResolver);
    }

    public ActionResolver actionResolver() {
        return actionResolver;
    }
}
