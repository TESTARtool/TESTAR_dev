package org.testar.statemodel.util;

import org.testar.monkey.alayer.Tag;
import org.testar.statemodel.*;
import org.testar.statemodel.event.StateModelEvent;
import org.testar.statemodel.event.StateModelEventListener;
import org.testar.statemodel.event.StateModelEventType;
import org.testar.statemodel.exceptions.StateModelException;
import org.testar.statemodel.exceptions.StateNotFoundException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ExtendedStateModel extends AbstractStateModel {

  private Map<String, ConcreteStateTransition> concreteStateTransitions;

  private Map<String, Set<ConcreteStateTransition>> concreteStateTransitionsBySource;
  private Map<String, Set<ConcreteStateTransition>> concreteStateTransitionsByTarget;

  private Map<String, IConcreteState> concreteStates;

  /**
   * constructor
   *
   * @param modelIdentifier
   * @param applicationName
   * @param applicationVersion
   * @param tags
   * @param eventListeners
   */
  public ExtendedStateModel(String modelIdentifier, String applicationName, String applicationVersion, Set<Tag<?>> tags, StateModelEventListener... eventListeners) {
    super(modelIdentifier, applicationName, applicationVersion, tags, eventListeners);
    concreteStateTransitions = new HashMap<>();
    concreteStateTransitionsBySource = new HashMap<>();
    concreteStateTransitionsByTarget = new HashMap<>();
    concreteStates = new HashMap<>();
  }

  /**
   * This method adds a new concrete state transition to the model
   * @param sourceState
   * @param targetState
   * @param executedAction
   * @throws StateModelException
   */
  public void addConcreteTransition(ConcreteState sourceState, ConcreteState targetState, ConcreteAction executedAction) throws StateModelException{
    checkStateId(sourceState.getId());
    checkStateId(targetState.getId());

    // check if the transition already exists
    if (concreteStateTransitionsBySource.containsKey(sourceState.getId())) {
      // loop through all the transitions that have the same source state and check for matches
      for(ConcreteStateTransition stateTransition : concreteStateTransitionsBySource.get(sourceState.getId())) {
        if (targetState.getId().equals(stateTransition.getTargetStateId()) && executedAction.getActionId().equals(stateTransition.getActionId())) {
          // the transition already exists. We send an update event to deal with changes in the states and actions
          // now we notify our listeners of the possible update
//          emitEvent(new StateModelEvent(StateModelEventType.CONCRETE_STATE_TRANSITION_CHANGED, stateTransition));
          return;
        }
      }
    }

    // TODO: mark action as "visited" (if needed)

    // new transition
    ConcreteStateTransition newStateTransition = new ConcreteStateTransition(sourceState, targetState, executedAction);
    // temporarily tell the state model not to emit events. We do not want to give double updates.
    deactivateEvents();

    addConcreteTransition(newStateTransition);
    addConcreteState(sourceState);
    addConcreteState(targetState);

    activateEvents();
//    emitEvent(new StateModelEvent(StateModelEventType.CONCRETE_STATE_TRANSITION_ADDED, newStateTransition));
  }

  /**
   * Helper method to add a concrete transition to several storage attributes
   * @param newTransition
   */
  private void addConcreteTransition(ConcreteStateTransition newTransition) {
    concreteStateTransitions.put(newTransition.getAction().getActionId(),  newTransition);
    // add the transitions to the source map
    if (!concreteStateTransitionsBySource.containsKey(newTransition.getSourceStateId())) {
      concreteStateTransitionsBySource.put(newTransition.getSourceStateId(), new HashSet<>());
    }
    concreteStateTransitionsBySource.get(newTransition.getSourceStateId()).add(newTransition);

    // and then to the target map
    if (!concreteStateTransitionsByTarget.containsKey(newTransition.getTargetStateId())) {
      concreteStateTransitionsByTarget.put(newTransition.getTargetStateId(), new HashSet<>());
    }
    concreteStateTransitionsByTarget.get(newTransition.getTargetStateId()).add(newTransition);
  }

  /**
   * This method adds a new concrete state to the collection of states
   * @param newState
   * @throws StateModelException
   */
  public void addConcreteState(ConcreteState newState) throws StateModelException {
    checkStateId(newState.getId());
    if (!containsAbstractState(newState.getId())) {

      //TODO: add extras if needed

      this.concreteStates.put(newState.getId(), newState);
//      emitEvent(new StateModelEvent(StateModelEventType.CONCRETE_STATE_ADDED, newState));
    }
    else {
//      emitEvent(new StateModelEvent(StateModelEventType.CONCRETE_STATE_CHANGED, newState));
    }
  }

  /**
   * This method retrieves a concrete state for a given identifier, if present
   * @param concreteStateId the identifier of the state to retrieve
   * @return
   * @throws StateModelException
   */
  public IConcreteState getConcreteState(String concreteStateId) throws StateModelException {
    if (containsAbstractState(concreteStateId)) {
      return concreteStates.get(concreteStateId);
    }
    throw new StateNotFoundException();
  }

  /**
   * This method returns all the concrete states in the abstract state model
   * @return
   */
  public Set<IConcreteState> getConcreteStates() {
    return new HashSet<>(concreteStates.values());
  }

  /**
   * This method returns a concrete action for a given identifier, if present
   * @return
   */
  public IConcreteAction getConcreteAction(String actionId) {
    return concreteStateTransitions.get(actionId).getAction();
  }

  /**
   * This method returns all the concrete actions in the abstract state model
   * @return
   */
  public Set<IConcreteAction> getConcreteActions() {
    return concreteStateTransitions.values().stream().map(ConcreteStateTransition::getAction).collect(Collectors.toSet());
  }

  /**
   * This method returns true if a requested concrete state is contained in this model
   * @param concreteStateId the identifier for the state
   * @return
   */
  public boolean containsConcreteState(String concreteStateId) {
    return this.concreteStates.containsKey(concreteStateId);
  }
}
