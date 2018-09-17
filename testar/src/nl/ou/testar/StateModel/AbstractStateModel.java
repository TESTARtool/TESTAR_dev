package nl.ou.testar.StateModel;

import nl.ou.testar.StateModel.Event.StateModelEvent;
import nl.ou.testar.StateModel.Event.StateModelEventListener;
import nl.ou.testar.StateModel.Event.StateModelEventType;
import nl.ou.testar.StateModel.Exception.ElementAlreadyExistsException;
import nl.ou.testar.StateModel.Exception.InvalidStateIdException;
import nl.ou.testar.StateModel.Exception.StateModelException;
import nl.ou.testar.StateModel.Exception.StateNotFoundException;
import org.fruit.alayer.Tag;

import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class AbstractStateModel {

    // this should contain a hash to uniquely identify the elements that were `used` in the abstraction level of the model
    private String abstractionLevelIdentifier;

    // a set of tags that was used to `form` the abstract state model
    private Set<Tag<?>> tags;

    private Set<AbstractStateTransition> stateTransitions;
    // we store the transitions twice extra, once by the source state and once by the target state for fast bi-directional lookup
    // the extra overhead is minimal
    private Map<String, Set<AbstractStateTransition>> stateTransitionsBySource;
    private Map<String, Set<AbstractStateTransition>> stateTransitionsByTarget;

    // the states in the model
    private Map<String, AbstractState> states;

    // set of initial states
    private Map<String, AbstractState> initialStates;

    // set of executed actions
    private Map<String, AbstractAction> executedActions;

    // a set of event listeners
    private Set<StateModelEventListener> eventListeners;

    /**
     * constructor
     * @param abstractionLevelIdentifier
     */
    public AbstractStateModel(String abstractionLevelIdentifier, Set<Tag<?>> tags) {
        this.abstractionLevelIdentifier = abstractionLevelIdentifier;
        this.tags = tags;
        // sets are empty when the model is just created
        stateTransitions = new HashSet<>();
        stateTransitionsBySource = new HashMap<>();
        stateTransitionsByTarget = new HashMap<>();
        states = new HashMap<>();
        initialStates = new HashMap<>();
        executedActions = new HashMap<>();
        eventListeners = new HashSet<>();
        initStateModel();
    }

    /**
     * initialization code for the state model should go in this method
     */
    private void initStateModel() {
        // add code here to initialize the model, such as loading a model from disk/database/external storage
    }

    /**
     * This method adds a new state transition to the model
     * @param sourceState
     * @param targetState
     * @param executedAction
     * @throws StateModelException
     */
    public void addTransition(AbstractState sourceState, AbstractState targetState, AbstractAction executedAction) throws StateModelException{
        checkStateId(sourceState.getStateId());
        checkStateId(targetState.getStateId());

        // check if the transition already exists
        if (stateTransitionsBySource.containsKey(sourceState.getStateId())) {
            // loop through all the transitions that have the same source state and check for matches
            for(AbstractStateTransition stateTransition : stateTransitionsBySource.get(sourceState.getStateId())) {
                if (targetState.getStateId().equals(stateTransition.getTargetStateId()) && executedAction.getActionId().equals(stateTransition.getActionId())) {
                    // the transition already exists. What we want to do is update the action to reflect that there may
                    // be an extra concrete action.
                    for(String concreteActionId : executedAction.getConcreteActionIds()) {
                        stateTransition.getAction().addConcreteActionId(concreteActionId);
                    }
                    // now we notify our listeners of the possible update
                    emitEvent(new StateModelEvent(StateModelEventType.ABSTRACT_ACTION_CHANGED, stateTransition));
                }
            }
        }

        // new transition
        AbstractStateTransition newStateTransition = new AbstractStateTransition(sourceState, targetState, executedAction);
        addTransition(newStateTransition);
        addState(sourceState);
        addState(targetState);
        addAction(executedAction);
        // we also set the action to visited for the source state
        sourceState.addVisitedAction(executedAction.getActionId());
        emitEvent(new StateModelEvent(StateModelEventType.ABSTRACT_STATE_TRANSITION_ADDED, newStateTransition));
    }

    /**
     * Helper method to add a transition to several storage attributes
     * @param newTransition
     */
    private void addTransition(AbstractStateTransition newTransition) {
        stateTransitions.add(newTransition);
        // add the transitions to the source map
        if (!stateTransitionsBySource.containsKey(newTransition.getSourceStateId())) {
            stateTransitionsBySource.put(newTransition.getSourceStateId(), new HashSet<>());
        }
        stateTransitionsBySource.get(newTransition.getSourceStateId()).add(newTransition);

        // and then to the target map
        if (!stateTransitionsByTarget.containsKey(newTransition.getTargetStateId())) {
            stateTransitionsByTarget.put(newTransition.getTargetStateId(), new HashSet<>());
        }
        stateTransitionsByTarget.get(newTransition.getTargetStateId()).add(newTransition);
    }

    /**
     * This method adds a new state to the collection of states
     * @param newState
     * @throws StateModelException
     */
    public void addState(AbstractState newState) throws StateModelException {
        checkStateId(newState.getStateId());
        if (!containsState(newState.getStateId())) {
            // provide the state with the event listeners from this state model
            for (StateModelEventListener eventListener: eventListeners) {
                newState.addEventListener(eventListener);
            }
            this.states.put(newState.getStateId(), newState);
            emitEvent(new StateModelEvent(StateModelEventType.ABSTRACT_STATE_ADDED, newState));
        }
    }

    /**
     * This method retrieves a state for a given identifier, if present
     * @param abstractStateId the identifier of the state to retrieve
     * @return
     * @throws StateModelException
     */
    public AbstractState getState(String abstractStateId) throws StateModelException {
        if (containsState(abstractStateId)) {
            return states.get(abstractStateId);
        }
        throw new StateNotFoundException();
    }

    /**
     * This method returns true if a requested state is contained in this model
     * @param abstractStateId the identifier for the state
     * @return
     */
    public boolean containsState(String abstractStateId) {
        return this.states.containsKey(abstractStateId);
    }

    /**
     * This methods adds a state to the collection of initial states
     * @param initialState
     * @throws StateModelException
     */
    public void addInitialState(AbstractState initialState) throws StateModelException{
        checkStateId(initialState.getStateId());
        if (!initialStates.containsKey(initialState.getStateId())) {
            initialStates.put(initialState.getStateId(), initialState);
        }
    }

    /**
     * This method adds an action to the list of executed actions
     * @param executedAction
     */
    public void addAction(AbstractAction executedAction) {
        if (!executedActions.containsKey(executedAction.getActionId())) {
            executedActions.put(executedAction.getActionId(), executedAction);
        }
    }

    /**
     * This is a helper method to check if the abstract Id that is provided is valid.
     * @param abstractStateId identifier to verify
     * @throws StateModelException
     */
    private void checkStateId(String abstractStateId) throws StateModelException{
        if (abstractStateId == null || abstractStateId.equals("")) {
            throw new InvalidStateIdException();
        }
    }

    /**
     * This method returns all the outgoing transitions for a given state.
     * @param stateId
     * @return
     */
    public Set<AbstractStateTransition> getOutgoingTransitionsForState(String stateId) {
        return stateTransitionsBySource.get(stateId);
    }

    /**
     * This method returns all the incoming transitions for a given state.
     * @param stateId
     * @return
     */
    public Set<AbstractStateTransition> getIncomingTransitionsForState(String stateId) {
        return stateTransitionsByTarget.get(stateId);
    }

    /**
     * Add an event listener to this state model
     * @param eventListener
     */
    public void addEventListener(StateModelEventListener eventListener) {
        eventListeners.add(eventListener);
    }

    /**
     * Notify our listeners of emitted events
     * @param event
     */
    private void emitEvent(StateModelEvent event) {
        for (StateModelEventListener eventListener: eventListeners) {
            eventListener.eventReceived(event);
        }
    }


}
