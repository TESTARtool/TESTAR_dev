package org.testar.statemodel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.statemodel.actionselector.ActionSelector;
import org.testar.statemodel.exceptions.ActionNotFoundException;
import org.testar.statemodel.exceptions.StateModelException;
import org.testar.statemodel.persistence.PersistenceManager;
import org.testar.statemodel.sequence.SequenceError;
import org.testar.statemodel.sequence.SequenceManager;
import org.testar.statemodel.util.AbstractStateService;

public class ModelManagerListeningMode extends ModelManager implements StateModelManager {

	/**
	 * Constructor
	 * @param abstractStateModel
	 * @param actionSelector
	 */
	public ModelManagerListeningMode(AbstractStateModel abstractStateModel, ActionSelector actionSelector, PersistenceManager persistenceManager,
			SequenceManager sequenceManager, boolean storeWidgets) {
		super(abstractStateModel, actionSelector, persistenceManager, sequenceManager, storeWidgets);
	}

	/**
	 * Listening mode notification to synchronize surface State and Model State
	 * 
	 * This method should be called once when a concurrence problem occurs at the surface
	 * and we need to add or update the current State of the Model that represents the
	 * State of the surface SUT.
	 * @param newState
	 * @param actions
	 */
	@Override
	public void notifyConcurrenceStateReached(State newState, Set<Action> actions, Action unknown) {

		// If we the surface State still being the Model State, no concurrence occurred, just end this
		if(currentAbstractState.getId().equals(newState.get(Tags.AbstractID)))
			return;

		// surface SUT state changes due to concurrence but no action was executed
		// also this is not an initial State
		actionUnderExecution = null;
		concreteActionUnderExecution = null;
		notifyActionExecution(unknown);

		// check if we are dealing with a new state or an existing one
		String abstractStateId = newState.get(Tags.AbstractID);
		AbstractState newAbstractState;

		// fetch or create an abstract state
		if (abstractStateModel.containsState(abstractStateId)) {
			try {
				newAbstractState = abstractStateModel.getState(abstractStateId);
				// update the abstract state
				AbstractStateService.updateAbstractStateActions(newAbstractState, actions);
			}
			catch (StateModelException ex) {
				ex.printStackTrace();
				throw new RuntimeException("An error occurred while retrieving abstract state from the state model");
			}
		} else {
			newAbstractState = AbstractStateFactory.createAbstractState(newState, actions);
		}

		// add the concrete state id to the abstract state
		newAbstractState.addConcreteStateId(newState.get(Tags.ConcreteID));

		try {
			abstractStateModel.addState(newAbstractState);
		} catch (StateModelException e) {
			e.printStackTrace();
			throw new RuntimeException("An error occurred while adding a new abstract state to the model");
		}

		// an action is being executed
		// that means we need to have a current abstract state already set
		if (currentAbstractState == null) {
			throw new RuntimeException("An action was being executed without saving current state");
		}

		//add a transition to the statemodel
		try {
			abstractStateModel.addTransition(currentAbstractState, newAbstractState, actionUnderExecution);
		} catch (StateModelException e) {
			e.printStackTrace();
			throw new RuntimeException("Encountered a problem adding a state transition into the statemodel");
		}

		// we now store this state to be the current abstract state
		currentAbstractState = newAbstractState;

		// and then we store the concrete state and possibly the action
		ConcreteState newConcreteState = ConcreteStateFactory.createConcreteState(newState, newAbstractState, storeWidgets);
		persistenceManager.persistConcreteState(newConcreteState);

		ConcreteStateTransition concreteStateTransition = new ConcreteStateTransition(currentConcreteState, newConcreteState, concreteActionUnderExecution);
		persistenceManager.persistConcreteStateTransition(concreteStateTransition);

		// check if non-determinism was introduced into the model
		int currentNrOfNonDeterministicActions = persistenceManager.getNrOfNondeterministicActions(abstractStateModel);
		if (currentNrOfNonDeterministicActions > nrOfNonDeterministicActions) {
			System.out.println("Non-deterministic action was executed!");
			sequenceManager.notifyStateReached(newConcreteState, concreteActionUnderExecution, SequenceError.NON_DETERMINISTIC_ACTION);
			nrOfNonDeterministicActions = currentNrOfNonDeterministicActions;
		}
		else {
			sequenceManager.notifyStateReached(newConcreteState, concreteActionUnderExecution);
		}

		currentConcreteState = newConcreteState;

		// temporarily output the nr of states in the model
		System.out.println(abstractStateModel.getStates().size() + " abstract states in the model");

		// temporarily output the number of unvisited actions still left
		System.out.println(abstractStateModel.getStates().stream().map(AbstractState::getUnvisitedActions).flatMap(
				Collection::stream
				).count() + " unvisited actions left");
		System.out.println("----------------------------");
		System.out.println();

		// reset actions
		actionUnderExecution = null;
		concreteActionUnderExecution = null;

	}

	/**
	 * This method should be called when an action it was Listened by TESTAR.
	 * @param action
	 */
	@Override
	public void notifyListenedAction(Action action) {
		try {
			actionUnderExecution = currentAbstractState.getAction(action.get(Tags.AbstractID));
		}
		catch (ActionNotFoundException ex) {
			System.out.println("Action not found in state model");
			errorMessages.add("Action with id: " + action.get(Tags.AbstractID) + " was not found in the model.");
			actionUnderExecution = new AbstractAction(action.get(Tags.AbstractID));
			currentAbstractState.addNewAction(actionUnderExecution);
		}
		concreteActionUnderExecution = ConcreteActionFactory.createConcreteAction(action, actionUnderExecution);
		actionUnderExecution.addConcreteActionId(concreteActionUnderExecution.getActionId());

		// Add or update user Interest
		if(actionUnderExecution.getAttributes().get(StateModelTags.UserInterest, 0) == 0) {
			actionUnderExecution.addAttribute(StateModelTags.UserInterest, 1);
		} else {
			int currentInterest = actionUnderExecution.getAttributes().get(StateModelTags.UserInterest);
			int increasedInterest = currentInterest + 1;
			actionUnderExecution.addAttribute(StateModelTags.UserInterest, increasedInterest);
		}

		System.out.println("Executing action: " + action.get(Tags.Desc));
		System.out.println("NEW User Interest is: " + 
				actionUnderExecution.getAttributes().get(StateModelTags.UserInterest));
		System.out.println("----------------------------------");

		// if we have error messages, we tell the sequence manager about it now, right before we move to a new state
		if (errorMessages.length() > 0) {
			sequenceManager.notifyErrorInCurrentState(errorMessages.toString());
			errorMessages = new StringJoiner(", ");
		}
	}

	/**
	 * This method obtains the existing derived actions of the surface
	 * to compare them with those in the model and see if any is interesting
	 * @return Collection of interesting actions
	 */
	@Override
	public Set<Action> getInterestingActions(Set<Action> actions) {

		HashMap<String, Action> surfaceActions = new HashMap<>();
		actions.forEach(a -> surfaceActions.put(a.get(Tags.AbstractID), a));
		//actions.forEach(a -> surfaceActions.put(a.get(Tags.ConcreteID), a));

		Set<Action> interestingActions = new HashSet<>();

		if (currentAbstractState == null) {
			return Collections.<Action>emptySet();
		}
		try {

			for (AbstractAction modelAbstractAction : currentAbstractState.getActions()) {
				if(modelAbstractAction.getAttributes().get(StateModelTags.UserInterest, 0)>0 
						&& surfaceActions.containsKey(modelAbstractAction.getActionId())) {
					Action ia = surfaceActions.get(modelAbstractAction.getActionId());
					ia.set(StateModelTags.UserInterest, modelAbstractAction.getAttributes().get(StateModelTags.UserInterest));
					interestingActions.add(ia);
				}
			}

			/*for (AbstractAction modelAbstractAction : currentAbstractState.getActions()) {
    			if(modelAbstractAction.getUserInterest()>0) {
    				for(String modelConcreteId : modelAbstractAction.getConcreteActionIds()) {
    					if(surfaceActions.containsKey(modelConcreteId)) {
    						Action ia = surfaceActions.get(modelConcreteId);
    						ia.set(Tags.UserInterest, modelAbstractAction.getUserInterest());
    						interestingActions.add(ia);
    					}	
    				}
    			}
    		}*/

		}catch(Exception e){
			System.out.println("ERROR obtaining interesting Actions from the State Model");
		}

		return interestingActions;
	}

}