package nl.ou.testar.StateModel;

public class AbstractStateTransition {

    // a transition is a trinity consisting of two states as endpoints and an action to tie these together
    private String sourceStateId;
    private String targetStateId;
    private String actionId;

    /**
     * Constructor
     * @param sourceStateId
     * @param targetStateId
     * @param actionId
     */
    public AbstractStateTransition(String sourceStateId, String targetStateId, String actionId) {
        this.sourceStateId = sourceStateId;
        this.targetStateId = targetStateId;
        this.actionId = actionId;
    }

    /**
     * Get the id for the source state of this transition
     * @return
     */
    public String getSourceStateId() {
        return sourceStateId;
    }

    /**
     * Get the id for the target state of this transition
     * @return
     */
    public String getTargetStateId() {
        return targetStateId;
    }

    /**
     * Get the id for the executed action in this transition
     * @return
     */
    public String getActionId() {
        return actionId;
    }
}
