package org.testar.statemodel;

import org.testar.statemodel.exceptions.ActionNotFoundException;
import org.testar.statemodel.persistence.Persistable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AbstractState extends AbstractEntity implements Persistable {

    // list of possible actions that can be executed from this state
    private Map<String, AbstractAction> actions;
    // list of possible actions that have not yet been executed from this state
    private Map<String, AbstractAction> unvisitedActions;
    // a list of actions that have been visited in this state
    private Map<String, AbstractAction> visitedActions;
    // a set of strings containing the concrete state ids that correspond to this abstract state
    private Set<String> concreteStateIds;
    // Set of text inputs for the code analysis experiments
    private Set<Map<String,String>> textInputs;
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
        visitedActions = new HashMap<>();
        if (actions != null) {
            for(AbstractAction action:actions) {
                this.actions.put(action.getActionId(), action);
                unvisitedActions.put(action.getActionId(), action);
            }
        }
        concreteStateIds = new HashSet<>();
        textInputs = new HashSet<>();
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
        return getId();
    }

    /**
     * This method sets the given action id to status visited
     * @param action the visited action
     */
    public void addVisitedAction(AbstractAction action) {
        unvisitedActions.remove(action.getActionId());
        visitedActions.put(action.getActionId(), action);
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
        return new HashSet<>(visitedActions.values());
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

    @Override
    public void setModelIdentifier(String modelIdentifier) {
        super.setModelIdentifier(modelIdentifier);
        // set the identifier on the abstract actions for this state
        for (String key : actions.keySet()) {
            actions.get(key).setModelIdentifier(modelIdentifier);
            if (unvisitedActions.containsKey(key)) {
                unvisitedActions.get(key).setModelIdentifier(modelIdentifier);
            }
        }
    }

    /**
     * Add a new abstract action to the abstract state.
     * @param action
     */
    public void addNewAction(AbstractAction action) {
        if (!this.actions.containsKey(action.getActionId())) {
            action.setModelIdentifier(this.getModelIdentifier());
            actions.put(action.getActionId(), action);
            unvisitedActions.put(action.getActionId(), action);
        }
    }

    @Override
    public boolean canBeDelayed() {
        return false;
    }

    /**
     * This method merges a given set of text inputs for the code analysis experiment into the
     * object text inputs.
     * @param newValues the new inputs to merge
     */
    public void mergeTextInputs(Set<Map<String,String>> newInputs) {
        for (Map<String,String> input : newInputs ) {
            textInputs.add(input);
        }
    }

    /**
     * This method returns a set of the current relevant text inputs for the code analysis experiment
     * stored in this action
     * @return set of text inputs
     */
    public Set<Map<String,String>> getTextInputs() {
        return textInputs;
    }

    /**
     * This method sets the current relevant text inputs for the code analysis experiment
     * stored in this action
     * @param textInputs set of text inputs
     */
    public void setTextInputs(Set<Map<String,String>> textInputs) {
        this.textInputs = textInputs;
    }
}
