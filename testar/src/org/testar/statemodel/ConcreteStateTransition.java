package org.testar.statemodel;

import org.testar.statemodel.persistence.Persistable;

public class ConcreteStateTransition implements Persistable {

    // a transition is a trinity consisting of two states as endpoints and an action to tie these together
    private IConcreteState sourceState;
    private IConcreteState targetState;
    private IConcreteAction action;

    /**
     * Constructor
     * @param sourceState
     * @param targetState
     * @param action
     */
    public ConcreteStateTransition(IConcreteState sourceState, IConcreteState targetState, IConcreteAction action) {
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
    public IConcreteState getSourceState() {
        return sourceState;
    }

    /**
     * Get the target state for this transition
     * @return
     */
    public IConcreteState getTargetState() {
        return targetState;
    }

    /**
     * Get the executed action for this transition
     * @return
     */
    public IConcreteAction getAction() {
        return action;
    }

    @Override
    public boolean canBeDelayed() {
        return true;
    }
}
