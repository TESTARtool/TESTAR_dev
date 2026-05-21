/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin;

import java.util.Set;

import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.action.Observation;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;

final class StateModelSessionFlow {

    private static final String OBSERVATION_ABSTRACT_ACTION_ID = "AA_OBSERVATION";

    private final PlatformServices services;
    private final SUT system;
    private State lastObservedState;
    private Action pendingExecutedAction;

    StateModelSessionFlow(PlatformServices services, SUT system) {
        this.services = Assert.notNull(services);
        this.system = Assert.notNull(system);
    }

    void observeState(State state, Set<Action> actions) {
        Assert.notNull(state);
        Assert.notNull(actions);
        notifyStateModelObservation(state, actions);
    }

    void syncObservationBeforeExecution(State currentState) {
        if (lastObservedState == null) {
            Set<Action> actions = services.actionDerivationService().deriveActions(system, currentState);
            services.stateModelManager().notifyNewStateReached(currentState, actions);
            lastObservedState = currentState;
            return;
        }

        if (pendingExecutedAction != null) {
            return;
        }

        if (hasStateChanged(currentState)) {
            Set<Action> actions = services.actionDerivationService().deriveActions(system, currentState);
            Action observationAction = createObservationAction(lastObservedState, currentState);
            services.stateModelManager().notifyActionExecution(observationAction);
            services.stateModelManager().notifyNewStateReached(currentState, actions);
            lastObservedState = currentState;
        }
    }

    void markPendingExecutedAction(Action action) {
        pendingExecutedAction = Assert.notNull(action);
    }

    void finalizePendingObservation(State finalState, Set<Action> finalActions) {
        Assert.notNull(finalState);
        Assert.notNull(finalActions);
        observeState(finalState, finalActions);
    }

    State lastObservedState() {
        return lastObservedState;
    }

    boolean hasPendingExecutedAction() {
        return pendingExecutedAction != null;
    }

    private void notifyStateModelObservation(State state, Set<Action> actions) {
        if (lastObservedState == null) {
            services.stateModelManager().notifyNewStateReached(state, actions);
            lastObservedState = state;
            return;
        }

        if (pendingExecutedAction != null) {
            services.stateModelManager().notifyActionExecution(pendingExecutedAction);
            services.stateModelManager().notifyNewStateReached(state, actions);
            pendingExecutedAction = null;
            lastObservedState = state;
            return;
        }

        if (hasStateChanged(state)) {
            Action observationAction = createObservationAction(lastObservedState, state);
            services.stateModelManager().notifyActionExecution(observationAction);
            services.stateModelManager().notifyNewStateReached(state, actions);
            lastObservedState = state;
        }
    }

    private boolean hasStateChanged(State state) {
        String currentConcreteId = state.get(Tags.ConcreteID, "");
        String lastConcreteId = lastObservedState == null ? "" : lastObservedState.get(Tags.ConcreteID, "");
        return !currentConcreteId.equals(lastConcreteId);
    }

    private Action createObservationAction(State sourceState, State targetState) {
        Observation observationAction = new Observation();
        observationAction.set(Tags.AbstractID, OBSERVATION_ABSTRACT_ACTION_ID);
        observationAction.set(Tags.ConcreteID, createObservationConcreteActionId(sourceState, targetState));
        observationAction.mapOriginWidget(sourceState);
        return observationAction;
    }

    private String createObservationConcreteActionId(State sourceState, State targetState) {
        String sourceConcreteId = sourceState.get(Tags.ConcreteID, "unknown-concrete-source");
        String targetConcreteId = targetState.get(Tags.ConcreteID, "unknown-concrete-target");
        return "AC_OBSERVATION_" + sourceConcreteId + "_" + targetConcreteId;
    }

}
