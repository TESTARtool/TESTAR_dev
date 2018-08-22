package nl.ou.testar.StateModel;

import java.util.HashSet;
import java.util.Set;

public class AbstractState {

    // abstract state id
    private String stateId;
    // list of possible actions that can be executed from this state
    private Set<String> actionIds;
    // list of possible actions that have not yet been executed from this state
    private Set<String> unvisitedActionIds;
    // a set of strings containing the concrete state ids that correspond to this abstract state
    private Set<String> concreteStateIds;

    /**
     * Constructor
     * @param stateId
     * @param actionIds
     */
    public AbstractState(String stateId, Set<String> actionIds) {
        this.stateId = stateId;
        this.actionIds = actionIds;
        this.unvisitedActionIds = actionIds; // all are unvisited when creating
        concreteStateIds = new HashSet<>();
    }

    /**
     * Adds a concrete state id that corresponds to this abstract state.
     * @param concreteStateId the concrete id to add
     */
    public void addConcreteStateId(String concreteStateId) {
        this.concreteStateIds.add(concreteStateId);
    }

    /**
     * This method returns the id for this abstract state
     * @return String
     */
    public String getStateId() {
        return stateId;
    }

    /**
     * This method sets the given action id to status visited
     * @param actionId the id for the visited action
     */
    public void addVisitedAction(String actionId){
        unvisitedActionIds.remove(actionId);
    }

    /**
     * This method returns all the possible actions that can be executed in this state
     * @return executable actions for this state
     */
    public Set<String> getActionIds() {
        return actionIds;
    }

    /**
     * This method returns the concrete state ids that correspond to this abstract state.
     * @return
     */
    public Set<String> getConcreteStateIds() {
        return concreteStateIds;
    }

    /**
     * This method returns the actions that have not yet been visited from this state
     * @return
     */
    public Set<String> getUnvisitedActionIds() {
        return unvisitedActionIds;
    }
}
