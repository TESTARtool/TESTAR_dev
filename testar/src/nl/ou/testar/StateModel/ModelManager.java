/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2021 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2021 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package nl.ou.testar.StateModel;

import nl.ou.testar.StateModel.ActionSelection.ActionSelector;
import nl.ou.testar.StateModel.Exception.ActionNotFoundException;
import nl.ou.testar.StateModel.Exception.StateModelException;
import nl.ou.testar.StateModel.Persistence.PersistenceManager;
import nl.ou.testar.StateModel.Sequence.SequenceError;
import nl.ou.testar.StateModel.Sequence.SequenceManager;
import nl.ou.testar.StateModel.Util.AbstractStateService;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;

import java.util.*;

public class ModelManager implements StateModelManager {

    // the abstract state model that this class is managing
    public AbstractStateModel abstractStateModel;

    // current abstract state of the SUT
    public  AbstractState currentAbstractState;

    // the action that is currently being executed, if applicable
    private AbstractAction actionUnderExecution;

    // action selector that chooses actions to execute
    public ActionSelector actionSelector;

    // persistence manager interface for persisting our model entities
    public PersistenceManager persistenceManager;

    // tags containing the attributes that were used in creating the concrete state ID
    private Set<Tag<?>> concreteStateTags;

    // current concrete state
    private ConcreteState currentConcreteState;

    // the concrete action that is being executed.
    private ConcreteAction concreteActionUnderExecution;

    // manager that is responsible for recording test sequences as they are executed
    private SequenceManager sequenceManager;

    // if there any irregularities that occur during runs, they should be appended here
    private StringJoiner errorMessages;

    // the number of actions in the model that end in more than one unique state
    // use this to monitor non-determinism in the model
    private int nrOfNonDeterministicActions;

    // should the widgets of concrete states be stored in the model?
    boolean storeWidgets;

    /**
     * Constructor
     * @param abstractStateModel
     * @param actionSelector
     */
    public ModelManager(AbstractStateModel abstractStateModel, ActionSelector actionSelector, PersistenceManager persistenceManager,
                        Set<Tag<?>> concreteStateTags, SequenceManager sequenceManager, boolean storeWidgets) {
        this.abstractStateModel = abstractStateModel;
        this.actionSelector = actionSelector;
        this.persistenceManager = persistenceManager;
        this.concreteStateTags = concreteStateTags;
        this.sequenceManager = sequenceManager;
        errorMessages = new StringJoiner(", ");
        nrOfNonDeterministicActions = 0;
        this.storeWidgets = storeWidgets;
        init();
    }

    /**
     * Initialization logic needs to go here
     */
    private void init() {
        // check if the model is deterministic
        boolean modelIsDeterministic = persistenceManager.modelIsDeterministic(abstractStateModel);
        System.out.println("Model is deterministic: " + persistenceManager.modelIsDeterministic(abstractStateModel));
        if (!modelIsDeterministic) {
            nrOfNonDeterministicActions = persistenceManager.getNrOfNondeterministicActions(abstractStateModel);
        }
    }

    /**
     * This method should be called once when a new state is reached after the execution
     * of an action or succesfully starting the SUT.
     * @param newState
     * @param actions
     */
    @Override
    public void notifyNewStateReached(State newState, Set<Action> actions) {
        // check if we are dealing with a new state or an existing one
        String abstractStateId = newState.get(Tags.AbstractIDCustom);
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

        // Here we have created the discovered abstract state in memory
        // But we did not save the executed abstract action information in the database (done in the transition)
        // For this reason we need to use the actionUnderExecution to update possible predicted actions
        if (actionUnderExecution != null && currentAbstractState != null
        		// Only if actionUnderExecution existed in previous state
        		// If not this will create predicted actions constantly
        		&& currentAbstractState.getActionIds().contains(actionUnderExecution.getActionId())) {
        	// Indicate that actionUnderExecution was visited in (previous)currentAbstractState
        	currentAbstractState.addVisitedAction(actionUnderExecution);
        	newAbstractState.updatePredictedActions(actionUnderExecution);
        }

        // add the concrete state id to the abstract state
        newAbstractState.addConcreteStateId(newState.get(Tags.ConcreteIDCustom));

        // check if an action was executed
        if (actionUnderExecution == null) {
            // no action is being executed, so we consider this an initial state
            newAbstractState.setInitial(true);
            try {
                abstractStateModel.addState(newAbstractState);
            } catch (StateModelException e) {
                e.printStackTrace();
                throw new RuntimeException("An error occurred while adding a new abstract state to the model");
            }
        }
        else {
            // an action is being executed
            // that means we need to have a current abstract state already set
            if (currentAbstractState == null) {
                throw new RuntimeException("An action was being executed without a recorded current state");
            }

            //add a transition to the statemodel
            try {
                abstractStateModel.addTransition(currentAbstractState, newAbstractState, actionUnderExecution);
            } catch (StateModelException e) {
                e.printStackTrace();
                throw new RuntimeException("Encountered a problem adding a state transition into the statemodel");
            }
            // we reset the executed action to await the next one.
            actionUnderExecution = null;
        }

        // Previous creation of an abstract state or abstract transition updates the information of visited, unvisited and predicted actions
        // Then, Iterate through the predicted actions of the current to create the predicted transitions
        for(PredictedAction predAct : newAbstractState.getPredictedActions()) {
        	try {
        		abstractStateModel.addPredictedTransition(newAbstractState, predAct);
        	} catch (StateModelException e) {
        		e.printStackTrace();
        		throw new RuntimeException("Encountered a problem adding a predicted action transition into the statemodel");
        	}
        }

        // we now store this state to be the current abstract state
        currentAbstractState = newAbstractState;

        // and then we store the concrete state and possibly the action
        ConcreteState newConcreteState = ConcreteStateFactory.createConcreteState(newState, concreteStateTags, newAbstractState, storeWidgets);
        if (concreteActionUnderExecution == null) {
            persistenceManager.persistConcreteState(newConcreteState);
        }
        else {
            ConcreteStateTransition concreteStateTransition = new ConcreteStateTransition(currentConcreteState, newConcreteState, concreteActionUnderExecution);
            persistenceManager.persistConcreteStateTransition(concreteStateTransition);
        }

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
        concreteActionUnderExecution = null;

        // temporarily output the nr of states in the model
        System.out.println(abstractStateModel.getStates().size() + " abstract states in the model");

        // temporarily output the number of unvisited actions still left
        System.out.println(abstractStateModel.getStates().stream().map(AbstractState::getUnvisitedActions).flatMap(
                Collection::stream
        ).count() + " unvisited actions left");
        System.out.println("----------------------------");
        System.out.println();
    }

    /**
     * This method should be called when an action is about to be executed.
     * @param action
     */
    @Override
    public void notifyActionExecution(Action action) {
        // the action that is executed should always be traceable to an action on the current abstract state
        // in other words, we should be able to find the action on the current abstract state
        try {
            actionUnderExecution = currentAbstractState.getAction(action.get(Tags.AbstractIDCustom));
        }
        catch (ActionNotFoundException ex) {
            System.out.println("Action not found in state model");
            errorMessages.add("Action with id: " + action.get(Tags.AbstractIDCustom) + " was not found in the model.");
            actionUnderExecution = new AbstractAction(action.get(Tags.AbstractIDCustom));
            currentAbstractState.addNewAction(actionUnderExecution);
        }
        concreteActionUnderExecution = ConcreteActionFactory.createConcreteAction(action, actionUnderExecution);
        actionUnderExecution.addConcreteActionId(concreteActionUnderExecution.getActionId());
        System.out.println("Executing action: " + action.get(Tags.Desc));
        System.out.println("----------------------------------");

        // if we have error messages, we tell the sequence manager about it now, right before we move to a new state
        if (errorMessages.length() > 0) {
            sequenceManager.notifyErrorInCurrentState(errorMessages.toString());
            errorMessages = new StringJoiner(", ");
        }
    }

    @Override
    public void notifyTestingEnded() {
        persistenceManager.shutdown();
    }

    /**
     * This method uses the abstract state model to return the abstract id of an action to execute
     * @return
     */
    @Override
    public Action getAbstractActionToExecute(Set<Action> actions) {
        if (currentAbstractState == null) {
            return null;
        }
        try {
            String abstractIdCustom = actionSelector.selectAction(currentAbstractState, abstractStateModel).getActionId();
            System.out.println("Finding action with abstractIdCustom : " + abstractIdCustom);            
            for(Action action : actions) {
            	try {
                if (action.get(Tags.AbstractIDCustom).equals(abstractIdCustom)) {
                    return action;
                }
            	}catch (Exception e) {
            	    String message = "ERROR getAbstractActionToExecute : " + action.get(Tags.Desc, "No description");
            		System.out.println(message);
            		errorMessages.add(message);
            	}
            }
            System.out.println("Could not find action with abstractIdCustom : " +abstractIdCustom);
            errorMessages.add("The actions selector returned the action with abstractIdCustom: " + abstractIdCustom + " . However, TESTAR was " +
                    "unable to find the action in its executable actions");
        } catch (ActionNotFoundException e) {
            System.out.println("Could not find an action to execute for abstract state id : " + currentAbstractState.getStateId());
        }
        return null;
    }

    @Override
    public void notifyTestSequencedStarted() {
        sequenceManager.startNewSequence();
    }

    @Override
    public void notifyTestSequenceStopped() {
        currentAbstractState = null;
        currentConcreteState = null;
        actionUnderExecution = null;
        concreteActionUnderExecution = null;
        sequenceManager.stopSequence();
    }

    @Override
    public void notifyTestSequenceInterruptedByUser() {
        sequenceManager.notifyInterruptionByUser();
    }

    @Override
    public void notifyTestSequenceInterruptedBySystem(String message) {
        sequenceManager.notifyInterruptionBySystem(message);
    }

}
