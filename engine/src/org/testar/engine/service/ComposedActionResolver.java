/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.service;

import java.util.List;

import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.action.resolver.ActionResolver;
import org.testar.core.action.resolver.ResolvedAction;
import org.testar.engine.action.resolver.ActionResolverPlan;

/**
 * Engine-side action resolver that composes one resolver strategy through an
 * explicit resolution plan.
 */
public final class ComposedActionResolver implements ActionResolver {

    private final ActionResolverPlan plan;

    public ComposedActionResolver(ActionResolverPlan plan) {
        this.plan = Assert.notNull(plan);
    }

    public static ComposedActionResolver compose(ActionResolverPlan plan) {
        return new ComposedActionResolver(plan);
    }

    @Override
    public ResolvedAction resolve(Iterable<Action> actions, List<String> arguments) {
        return plan.actionResolver().resolve(actions, arguments);
    }

    public ActionResolverPlan plan() {
        return plan;
    }
}
