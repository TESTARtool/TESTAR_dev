/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.service;

import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.service.ActionExecutionService;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.engine.action.execution.ActionExecutionPlan;

/**
 * Engine-side action execution service that delegates to the configured executor.
 */
public final class ComposedActionExecutionService implements ActionExecutionService {

    private final ActionExecutionPlan plan;

    public ComposedActionExecutionService(ActionExecutionPlan plan) {
        this.plan = Assert.notNull(plan);
    }

    public static ComposedActionExecutionService compose(ActionExecutionPlan plan) {
        return new ComposedActionExecutionService(plan);
    }

    @Override
    public boolean executeAction(SUT system, State state, Action action) {
        return plan.actionExecutionService().executeAction(system, state, action);
    }

    public ActionExecutionPlan plan() {
        return plan;
    }
}
