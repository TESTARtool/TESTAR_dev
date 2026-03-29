/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel;

import java.util.Objects;

import org.testar.statemodel.persistence.Persistable;

public class AbstractStateTransition implements Persistable {

    // a transition is a trinity consisting of two states as endpoints and an action to tie these together
    private final AbstractState sourceState;
    private final AbstractState targetState;
    private final AbstractAction action;

    /**
     * Constructor
     * @param sourceState
     * @param targetState
     * @param action
     */
    public AbstractStateTransition(AbstractState sourceState, AbstractState targetState, AbstractAction action) {
        this.sourceState = Objects.requireNonNull(sourceState, "Abstract source state cannot be null");
        this.targetState = Objects.requireNonNull(targetState, "Abstract target state cannot be null");
        this.action = Objects.requireNonNull(action, "AbstractAction cannot be null");
    }

    /**
     * Get the id for the source state of this transition
     * @return
     */
    public String getSourceStateId() {
        return sourceState.getStateId();
    }

    /**
     * Get the id for the target state of this transition
     * @return
     */
    public String getTargetStateId() {
        return targetState.getStateId();
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
    public AbstractState getSourceState() {
        return sourceState;
    }

    /**
     * Get the target state for this transition
     * @return
     */
    public AbstractState getTargetState() {
        return targetState;
    }

    /**
     * Get the executed action for this transition
     * @return
     */
    public AbstractAction getAction() {
        return action;
    }

    @Override
    public boolean canBeDelayed() {
        return false;
    }
}
