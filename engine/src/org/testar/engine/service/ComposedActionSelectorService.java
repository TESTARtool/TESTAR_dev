/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.service;

import java.util.Set;

import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.service.ActionSelectorService;
import org.testar.core.state.State;
import org.testar.engine.action.selection.ActionSelectorPlan;

/**
 * Engine-side action selector service that composes one selector strategy with
 * a fallback selector.
 */
public final class ComposedActionSelectorService implements ActionSelectorService {

    private final ActionSelectorPlan plan;

    public ComposedActionSelectorService(ActionSelectorPlan plan) {
        this.plan = Assert.notNull(plan);
    }

    public static ComposedActionSelectorService compose(ActionSelectorPlan plan) {
        return new ComposedActionSelectorService(plan);
    }

    @Override
    public Action selectAction(State state, Set<Action> actions) {
        Action selectedAction = plan.actionSelectorService().selectAction(state, actions);
        if (selectedAction != null) {
            return selectedAction;
        }
        return plan.fallbackActionSelectorService().selectAction(state, actions);
    }

    public ActionSelectorPlan plan() {
        return plan;
    }
}
