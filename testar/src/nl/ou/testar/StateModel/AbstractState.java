package nl.ou.testar.StateModel;

import nl.ou.testar.StateModel.Event.StateModelEvent;
import nl.ou.testar.StateModel.Event.StateModelEventType;
import nl.ou.testar.StateModel.Exception.ActionNotFoundException;
import nl.ou.testar.StateModel.Util.ActionHelper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AbstractState extends AbstractEntity {

    // list of possible actions that can be executed from this state
    private Map<String, AbstractAction> actions;
    // list of possible actions that have not yet been executed from this state
    private Map<String, AbstractAction> unvisitedActions;
    // a set of strings containing the concrete state ids that correspond to this abstract state
    private Set<String> concreteStateIds;
    // is this an initial state?
    private boolean isInitial = false;

    /**
     * Constructor
     * @param stateId
     * @param actions
     */
    public AbstractState(String stateId, Set<AbstractAction> actions) {
        super(stateId);
        this.actions = new HashMap<>();
        unvisitedActions = new HashMap<>();
        for(AbstractAction action:actions) {
            this.actions.put(action.getActionId(), action);
            unvisitedActions.put(action.getActionId(), action);
        }
        concreteStateIds = new HashSet<>();
    }

    /**
     * Adds a concrete state id that corresponds to this abstract state.
     * @param concreteStateId the concrete id to add
     */
    public void addConcreteStateId(String concreteStateId) {
        if (!concreteStateIds.contains(concreteStateId)) {
            this.concreteStateIds.add(concreteStateId);
            emitEvent(new StateModelEvent(StateModelEventType.ABSTRACT_STATE_CHANGED, this));
        }
    }

    /**
     * This method returns the id for this abstract state
     * @return String
     */
    public String getStateId() {
        return getId();
    }

    /**
     * This method sets the given action id to status visited
     * @param action the visited action
     */
    public void addVisitedAction(AbstractAction action) {
            unvisitedActions.remove(action.getActionId());
    }

    /**
     * This method returns all the possible actions that can be executed in this state
     * @return executable actions for this state
     */
    public Set<String> getActionIds() {
        return actions.keySet();
    }

    /**
     * This method returns all the abstract actions that are executable from this abstract state
     * @return
     */
    public Set<AbstractAction> getActions() {
        return new HashSet<>(actions.values());
    }

    /**
     * This method returns all the state's executable actions for a given set of ids
     * @param actionIds
     * @return
     */
    public Set<AbstractAction> getActions(Set<String> actionIds) {
        Set<AbstractAction> abstractActions = new HashSet<AbstractAction>();
        for (String actionId:actionIds) {
            if (actions.containsKey(actionId)) {
                abstractActions.add(actions.get(actionId));
            }
        }
        return abstractActions;
    }

    /**
     * This method returns the action for a given action identifier
     * @param actionId
     * @return
     * @throws ActionNotFoundException
     */
    public AbstractAction getAction(String actionId) throws ActionNotFoundException{
        if (!actions.containsKey(actionId)) {
            throw new ActionNotFoundException();
        }
        return actions.get(actionId);
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
    public Set<AbstractAction> getUnvisitedActions() {
        return new HashSet<>(unvisitedActions.values());
    }

    /**
     * This method returns all the actions for this abstract state that have been visited
     * @return
     */
    public Set<AbstractAction> getVisitedActions() {
        Set<AbstractAction> visitedActions = new HashSet<>();
        for (AbstractAction action : actions.values()) {
            visitedActions.add(action);
        }
        System.out.println("Values in visitedactions set: " + visitedActions.toString());
        System.out.println("Values in unvisited actions set: " + getUnvisitedActions().toString());
        visitedActions.removeAll(getUnvisitedActions());
        System.out.println("Values after removing unvisited actions: " + visitedActions.toString());
        System.out.println("Values in all actions: " + actions.values().toString());
        return visitedActions;
    }

    /**
     * Returns true if this is one of the starting states of the model. False otherwise.
     */
    public boolean isInitial() {
        return isInitial;
    }

    /**
     * Set to true if this is one of the starting states of the model
     * @param initial
     */
    public void setInitial(boolean initial) {
        isInitial = initial;
    }
}
