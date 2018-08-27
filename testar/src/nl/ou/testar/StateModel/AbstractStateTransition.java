package nl.ou.testar.StateModel;

public class AbstractStateTransition {

    // a transition is a trinity consisting of two states as endpoints and an action to tie these together
    private AbstractState sourceState;
    private AbstractState targetState;
    private AbstractAction action;

    /**
     * Constructor
     * @param sourceState
     * @param targetState
     * @param action
     */
    public AbstractStateTransition(AbstractState sourceState, AbstractState targetState, AbstractAction action) {
        this.sourceState = sourceState;
        this.targetState = targetState;
        this.action = action;
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
     * @param sourceState
     */
    public void setSourceState(AbstractState sourceState) {
        this.sourceState = sourceState;
    }

    /**
     * Get the target state for this transition
     * @param targetState
     */
    public void setTargetState(AbstractState targetState) {
        this.targetState = targetState;
    }

    /**
     * Get the action for this transition
     * @param action
     */
    public void setAction(AbstractAction action) {
        this.action = action;
    }
}
