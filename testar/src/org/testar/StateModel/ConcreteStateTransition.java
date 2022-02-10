package org.testar.StateModel;

import org.testar.StateModel.Persistence.Persistable;

public class ConcreteStateTransition implements Persistable {

    // a transition is a trinity consisting of two states as endpoints and an action to tie these together
    private ConcreteState sourceState;
    private ConcreteState targetState;
    private ConcreteAction action;

    /**
     * Constructor
     * @param sourceState
     * @param targetState
     * @param action
     */
    public ConcreteStateTransition(ConcreteState sourceState, ConcreteState targetState, ConcreteAction action) {
        this.sourceState = sourceState;
        this.targetState = targetState;
        this.action = action;
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
     * @param sourceState
     */
    public void setSourceState(ConcreteState sourceState) {
        this.sourceState = sourceState;
    }

    /**
     * Get the target state for this transition
     * @param targetState
     */
    public void setTargetState(ConcreteState targetState) {
        this.targetState = targetState;
    }

    /**
     * Get the action for this transition
     * @param action
     */
    public void setAction(ConcreteAction action) {
        this.action = action;
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
