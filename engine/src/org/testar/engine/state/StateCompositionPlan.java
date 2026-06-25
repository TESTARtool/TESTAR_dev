/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.state;

import org.testar.config.StateObservationMode;
import org.testar.core.Assert;
import org.testar.core.service.StateService;
import org.testar.core.state.State;
import org.testar.core.tag.Tag;
import org.testar.engine.policy.SessionPolicyContext;

/**
 * Orchestrates state capture and projection by pairing one state service with
 * the state-shaping rule that turns the captured state into the engine view.
 */
public final class StateCompositionPlan {

    @FunctionalInterface
    public interface StateProjection {

        State query(State state, SessionPolicyContext context);
    }

    private final StateService stateService;
    private final StateProjection projection;
    private final SemanticWidgetDescriptor semanticWidgetDescriptor;

    public StateCompositionPlan(StateService stateService, StateProjection projection) {
        this(stateService, projection, null);
    }

    private StateCompositionPlan(StateService stateService,
                                 StateProjection projection,
                                 SemanticWidgetDescriptor semanticWidgetDescriptor) {
        this.stateService = Assert.notNull(stateService);
        this.projection = Assert.notNull(projection);
        this.semanticWidgetDescriptor = semanticWidgetDescriptor;
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
                ),
                descriptor
        );
    }

    public StateService stateService() {
        return stateService;
    }

    public State query(State state, SessionPolicyContext context) {
        return projection.query(state, context);
    }

    public State projectState(State originalState,
                              StateObservationMode observationMode,
                              SessionPolicyContext policyContext) {
        Assert.notNull(originalState);
        Assert.notNull(observationMode);
        Assert.notNull(policyContext);

        switch (observationMode) {
            case FULL_STATE:
                return originalState;
            case LEAF_WIDGETS:
                return StateProjectionSupport.projectWidgets(
                        originalState,
                        StateProjectionSupport::isLeafWidget
                );
            case SEMANTIC_WIDGETS:
            case TEXTUAL_CONTEXT:
                return StateProjectionSupport.projectSemanticWidgets(
                        originalState,
                        requireSemanticWidgetDescriptor(observationMode)
                );
            case INTERACTIVE_WIDGETS:
                return StateProjectionSupport.projectInteractiveWidgets(originalState, policyContext);
            case INTERACTIVE_SEMANTIC_WIDGETS:
                return StateProjectionSupport.projectInteractiveSemanticWidgets(
                        originalState,
                        requireSemanticWidgetDescriptor(observationMode),
                        policyContext
                );
            case ACTIONABLE_WIDGETS:
                return StateProjectionSupport.projectActionableWidgets(originalState, policyContext);
            case ACTIONABLE_SEMANTIC_WIDGETS:
                return StateProjectionSupport.projectActionableSemanticWidgets(
                        originalState,
                        requireSemanticWidgetDescriptor(observationMode),
                        policyContext
                );
            default:
                throw new IllegalArgumentException("Unsupported state observation mode: " + observationMode);
        }
    }

    private SemanticWidgetDescriptor requireSemanticWidgetDescriptor(StateObservationMode observationMode) {
        if (semanticWidgetDescriptor == null) {
            throw new IllegalStateException(
                    "State observation mode requires a semantic descriptor: " + observationMode
            );
        }
        return semanticWidgetDescriptor;
    }
}
