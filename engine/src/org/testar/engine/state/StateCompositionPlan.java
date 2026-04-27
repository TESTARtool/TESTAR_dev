/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.state;

import org.testar.core.Assert;
import org.testar.core.service.StateService;
import org.testar.core.state.State;
import org.testar.core.tag.Tag;
import org.testar.engine.policy.SessionPolicyContext;

/**
 * Defines how a state service captures and projects state for a concrete state query.
 */
public final class StateCompositionPlan {

    @FunctionalInterface
    public interface StateProjection {

        State query(State state, SessionPolicyContext context);
    }

    private final StateService stateService;
    private final StateProjection projection;

    public StateCompositionPlan(StateService stateService, StateProjection projection) {
        this.stateService = Assert.notNull(stateService);
        this.projection = Assert.notNull(projection);
    }

    public static StateCompositionPlan fullState(StateService stateService) {
        return new StateCompositionPlan(
                stateService,
                (state, context) -> Assert.notNull(state)
        );
    }

    public static StateCompositionPlan leafWidgets(StateService stateService) {
        return new StateCompositionPlan(
                stateService,
                (state, context) -> StateProjectionSupport.projectWidgets(
                        state,
                        StateProjectionSupport::isLeafWidget
                )
        );
    }

    public static StateCompositionPlan widgetsWithText(StateService stateService, Tag<String> textTag) {
        Assert.notNull(textTag);
        return new StateCompositionPlan(
                stateService,
                (state, context) -> StateProjectionSupport.projectWidgets(
                        state,
                        widget -> StateProjectionSupport.hasText(widget, textTag)
                )
        );
    }

    public static StateCompositionPlan semanticWidgets(StateService stateService,
                                                       SemanticWidgetDescriptor descriptor) {
        Assert.notNull(descriptor);
        return new StateCompositionPlan(
                stateService,
                (state, context) -> StateProjectionSupport.projectSemanticWidgets(
                        state,
                        descriptor
                )
        );
    }

    public StateService stateService() {
        return stateService;
    }

    public State query(State state, SessionPolicyContext context) {
        return projection.query(state, context);
    }
}
