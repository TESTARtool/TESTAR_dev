/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel;

import java.util.Objects;

import org.testar.statemodel.persistence.Persistable;

public class ConcreteStateTransition implements Persistable {

    // a transition is a trinity consisting of two states as endpoints and an action to tie these together
    private final ConcreteState sourceState;
    private final ConcreteState targetState;
    private final ConcreteAction action;

    /**
     * Constructor
     * @param sourceState
     * @param targetState
     * @param action
     */
    public ConcreteStateTransition(ConcreteState sourceState, ConcreteState targetState, ConcreteAction action) {
        this.sourceState = Objects.requireNonNull(sourceState, "Concrete source state cannot be null");
        this.targetState = Objects.requireNonNull(targetState, "Concrete target state cannot be null");
        this.action = Objects.requireNonNull(action, "ConcreteAction cannot be null");
    }

    /**
     * Get the id for the source state of this transition
     * @return
     */
    public String getSourceStateId() {
        return sourceState.getId();
    }

    /**
     * Get the id for the target state of this transition
     * @return
     */
    public String getTargetStateId() {
        return targetState.getId();
    }

    /**
     * Get the id for the executed action in this transition
     * @return
     */
    public String getActionId() {
        return action.getActionId();
    }

    /**
     * Get the source state for this transition
     * @return
     */
    public ConcreteState getSourceState() {
        return sourceState;
    }

    /**
     * Get the target state for this transition
     * @return
     */
    public ConcreteState getTargetState() {
        return targetState;
    }

    /**
     * Get the executed action for this transition
     * @return
     */
    public ConcreteAction getAction() {
        return action;
    }

    @Override
    public boolean canBeDelayed() {
        return true;
    }
}
