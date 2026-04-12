/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.service;

import org.testar.core.Assert;
import org.testar.core.CodingManager;
import org.testar.core.exceptions.StateBuildException;
import org.testar.core.service.StateService;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.engine.policy.SessionPolicyContext;
import org.testar.engine.state.StateCompositionPlan;

/**
 * Engine-side state service that composes a shared policy context with a state
 * composition plan.
 */
public final class ComposedStateService implements StateService, AutoCloseable {

    private final SessionPolicyContext context;
    private final StateCompositionPlan plan;

    public ComposedStateService(SessionPolicyContext context,
                                StateCompositionPlan plan) {
        this.context = Assert.notNull(context);
        this.plan = Assert.notNull(plan);
    }

    public static ComposedStateService compose(SessionPolicyContext context,
                                               StateCompositionPlan plan) {
        return new ComposedStateService(context, plan);
    }

    @Override
    public State getState(SUT system) throws StateBuildException {
        State capturedState = plan.stateService().getState(system);
        CodingManager.buildIDs(capturedState);
        return plan.query(capturedState, context);
    }

    public SessionPolicyContext context() {
        return context;
    }

    public StateCompositionPlan plan() {
        return plan;
    }

    @Override
    public void close() throws Exception {
        StateService delegate = plan.stateService();
        if (delegate instanceof AutoCloseable) {
            ((AutoCloseable) delegate).close();
        }
    }
}
