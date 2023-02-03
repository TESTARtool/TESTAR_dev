package org.testar.statemodel;

import org.testar.statemodel.event.StateModelEvent;
import org.testar.statemodel.event.StateModelEventListener;
import org.testar.statemodel.event.StateModelEventType;
import org.testar.statemodel.exceptions.InvalidStateIdException;
import org.testar.statemodel.exceptions.StateModelException;
import org.testar.statemodel.exceptions.StateNotFoundException;
import org.testar.monkey.alayer.Tag;

import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public class AbstractStateModel {

    // this should contain a hash to uniquely identify the elements that were `used` in the abstraction level of the model
    private String modelIdentifier;

    // the name of the application that is being modelled
    private String applicationName;

    // the version of the application that is being modelled
    private String applicationVersion;

    // a set of tags that was used to `form` the abstract state model
    private Set<Tag<?>> tags;

    private Set<AbstractStateTransition> abstractStateTransitions;

    // we store the transitions twice extra, once by the source state and once by the target state for fast bi-directional lookup
    // the extra overhead is minimal
    private Map<String, Set<AbstractStateTransition>> abstractStateTransitionsBySource;
    private Map<String, Set<AbstractStateTransition>> abstractStateTransitionsByTarget;

    // the states in the model
    private Map<String, AbstractState> abstractStates;

    // set of initial states
    private Map<String, AbstractState> initialStates;

    // a set of event listeners
    private Set<StateModelEventListener> eventListeners;

    // are we emitting events or not?
    private boolean emitEvents = true;

    /**
     * constructor
     * @param modelIdentifier
     */
    public AbstractStateModel(String modelIdentifier,
                              String applicationName,
                              String applicationVersion,
                              Set<Tag<?>> tags,
                              StateModelEventListener ...eventListeners) {
        this.modelIdentifier = modelIdentifier;
        this.applicationName = applicationName;
        this.applicationVersion = applicationVersion;
        this.tags = tags;
        // sets are empty when the model is just created
        abstractStateTransitions = new HashSet<>();
        abstractStateTransitionsBySource = new HashMap<>();
        abstractStateTransitionsByTarget = new HashMap<>();
        abstractStates = new HashMap<>();
        initialStates = new HashMap<>();
        this.eventListeners = new HashSet<>();
        for (int i = 0; i < eventListeners.length;i++) {
            this.eventListeners.add(eventListeners[i]);
        }
        initStateModel();
    }

    /**
     * initialization code for the state model should go in this method
     */
    private void initStateModel() {
        // add code here to initialize the model, such as loading a model from disk/database/external storage
        emitEvent(new StateModelEvent(StateModelEventType.ABSTRACT_STATE_MODEL_INITIALIZED, this));
    }

    /**
     * This method adds a new abstract state transition to the model
     * @param sourceState
     * @param targetState
     * @param executedAction
     * @throws StateModelException
     */
    public void addAbstractTransition(AbstractState sourceState, AbstractState targetState, AbstractAction executedAction) throws StateModelException{
        checkStateId(sourceState.getStateId());
        checkStateId(targetState.getStateId());

        // check if the transition already exists
        if (abstractStateTransitionsBySource.containsKey(sourceState.getStateId())) {
            // loop through all the transitions that have the same source state and check for matches
            for(AbstractStateTransition stateTransition : abstractStateTransitionsBySource.get(sourceState.getStateId())) {
                if (targetState.getStateId().equals(stateTransition.getTargetStateId()) && executedAction.getActionId().equals(stateTransition.getActionId())) {
                    // the transition already exists. We send an update event to deal with changes in the states and actions
                    // now we notify our listeners of the possible update
                    emitEvent(new StateModelEvent(StateModelEventType.ABSTRACT_STATE_TRANSITION_CHANGED, stateTransition));
                    return;
                }
            }
        }

        // we set the action to visited for the source state
        sourceState.addVisitedAction(executedAction);

        // new transition
        AbstractStateTransition newStateTransition = new AbstractStateTransition(sourceState, targetState, executedAction);
        // temporarily tell the state model not to emit events. We do not want to give double updates.
        deactivateEvents();

        addAbstractTransition(newStateTransition);
        addAbstractState(sourceState);
        addAbstractState(targetState);

        activateEvents();
        emitEvent(new StateModelEvent(StateModelEventType.ABSTRACT_STATE_TRANSITION_ADDED, newStateTransition));
    }

    /**
     * Helper method to add an abstract transition to several storage attributes
     * @param newTransition
     */
    private void addAbstractTransition(AbstractStateTransition newTransition) {
        abstractStateTransitions.add(newTransition);
        // add the transitions to the source map
        if (!abstractStateTransitionsBySource.containsKey(newTransition.getSourceStateId())) {
            abstractStateTransitionsBySource.put(newTransition.getSourceStateId(), new HashSet<>());
        }
        abstractStateTransitionsBySource.get(newTransition.getSourceStateId()).add(newTransition);

        // and then to the target map
        if (!abstractStateTransitionsByTarget.containsKey(newTransition.getTargetStateId())) {
            abstractStateTransitionsByTarget.put(newTransition.getTargetStateId(), new HashSet<>());
        }
        abstractStateTransitionsByTarget.get(newTransition.getTargetStateId()).add(newTransition);
    }

    /**
     * This method adds a new abstract state to the collection of states
     * @param newState
     * @throws StateModelException
     */
    public void addAbstractState(AbstractState newState) throws StateModelException {
        checkStateId(newState.getStateId());
        if (!containsAbstractState(newState.getStateId())) {
            // provide the state with this state model's abstract identifier
            newState.setModelIdentifier(modelIdentifier);
            // provide the state with the event listeners from this state model
            for (StateModelEventListener eventListener: eventListeners) {
                newState.addEventListener(eventListener);
            }
            this.abstractStates.put(newState.getStateId(), newState);
            emitEvent(new StateModelEvent(StateModelEventType.ABSTRACT_STATE_ADDED, newState));
        }
        else {
            emitEvent(new StateModelEvent(StateModelEventType.ABSTRACT_STATE_CHANGED, newState));
        }

        // check for initial state
        if (newState.isInitial()) {
            addInitialState(newState);
        }
    }

  /**
     * This method retrieves an abstract state for a given identifier, if present
     * @param abstractStateId the identifier of the state to retrieve
     * @return
     * @throws StateModelException
     */
    public AbstractState getAbstractState(String abstractStateId) throws StateModelException {
        if (containsAbstractState(abstractStateId)) {
            return abstractStates.get(abstractStateId);
        }
        throw new StateNotFoundException();
    }

    /**
     * This method returns all the abstract states in the abstract state model
     * @return
     */
    public Set<AbstractState> getAbstractStates() {
        return new HashSet<>(abstractStates.values());
    }

    /**
     * This method returns all the abstract actions in the abstract state model
     * @return
     */
    public Set<AbstractAction> getAbstractActions() {
      return abstractStateTransitions.stream().map(AbstractStateTransition::getAction).collect(Collectors.toSet());
    }

    /**
     * This method returns true if a requested abstract state is contained in this model
     * @param abstractStateId the identifier for the state
     * @return
     */
    public boolean containsAbstractState(String abstractStateId) {
        return this.abstractStates.containsKey(abstractStateId);
    }

    /**
     * This methods adds a state to the collection of initial states
     * @param initialState
     * @throws StateModelException
     */
    private void addInitialState(AbstractState initialState) throws StateModelException{
        checkStateId(initialState.getStateId());
        if (!initialStates.containsKey(initialState.getStateId())) {
            initialState.setInitial(true);
            initialStates.put(initialState.getStateId(), initialState);
        }
    }

    /**
     * This is a helper method to check if the abstract Id that is provided is valid.
     * @param stateId identifier to verify
     * @throws StateModelException
     */
    protected void checkStateId(String stateId) throws StateModelException{
        if (stateId == null || stateId.equals("")) {
            throw new InvalidStateIdException();
        }
    }

    /**
     * This method returns all the outgoing transitions for a given state.
     * @param stateId
     * @return
     */
    public Set<AbstractStateTransition> getOutgoingTransitionsForState(String stateId) {
        return abstractStateTransitionsBySource.get(stateId);
    }

    /**
     * This method returns all the incoming transitions for a given state.
     * @param stateId
     * @return
     */
    public Set<AbstractStateTransition> getIncomingTransitionsForState(String stateId) {
        return abstractStateTransitionsByTarget.get(stateId);
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
    protected void emitEvent(StateModelEvent event) {
        if (!emitEvents) return;
        for (StateModelEventListener eventListener: eventListeners) {
            eventListener.eventReceived(event);
        }
    }

    /**
     * THis method returns the unique hash that was calculated to identify this abstract state model
     * @return
     */
    public String getModelIdentifier() {
        return modelIdentifier;
    }

    /**
     * This method returns the tags that were used in determining what the unique states are for this model.
     * @return
     */
    public Set<Tag<?>> getTags() {
        return tags;
    }

    /**
     * Set the abstract state model to not emit events.
     */
    protected void deactivateEvents() {
        emitEvents = false;
    }

    /**
     * Set the abstract state model to emit events.
     */
    protected void activateEvents() {
        emitEvents = true;
    }

    /**
     * This method returns the name of the application that is being modelled.
     * @return
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * This method returns the version of the application that is being modelled.
     * @return
     */
    public String getApplicationVersion() {
        return applicationVersion;
    }

    public void onReady() {
      emitEvent(new StateModelEvent(StateModelEventType.ABSTRACT_STATE_MODEL_READY, this));
    }
}
