/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2025 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.statemodel;

import org.testar.statemodel.actionselector.ActionSelector;
import org.testar.statemodel.exceptions.ActionNotFoundException;
import org.testar.statemodel.exceptions.StateModelException;
import org.testar.statemodel.persistence.PersistenceManager;
import org.testar.statemodel.persistence.orientdb.entity.EntityManager;
import org.testar.statemodel.sequence.SequenceError;
import org.testar.statemodel.sequence.SequenceManager;
import org.testar.statemodel.util.AbstractStateService;

import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.exceptions.NoSuchTagException;

import java.util.*;

public class ModelManager implements StateModelManager {

    // the abstract state model that this class is managing
    private final AbstractStateModel abstractStateModel;

    // current abstract state of the SUT
    private AbstractState currentAbstractState;

    // the action that is currently being executed, if applicable
    private AbstractAction actionUnderExecution;

    // action selector that chooses actions to execute
    private final ActionSelector actionSelector;

    // persistence manager interface for persisting our model entities
    private final PersistenceManager persistenceManager;

    // current concrete state
    private ConcreteState currentConcreteState;

    // the concrete action that is being executed.
    private ConcreteAction concreteActionUnderExecution;

    // manager that is responsible for recording test sequences as they are executed
    private final SequenceManager sequenceManager;

    // if there any irregularities that occur during runs, they should be appended here
    private StringJoiner errorMessages;

    // the number of actions in the model that end in more than one unique state
    // use this to monitor non-determinism in the model
    private int nrOfNonDeterministicActions;

    // should the widgets of concrete states be stored in the model?
    private final boolean storeWidgets;

    /**
     * Constructor
     * @param abstractStateModel
     * @param actionSelector
     */
    public ModelManager(AbstractStateModel abstractStateModel, ActionSelector actionSelector, PersistenceManager persistenceManager,
                        SequenceManager sequenceManager, boolean storeWidgets) {
        this.abstractStateModel = abstractStateModel;
        this.actionSelector = actionSelector;
        this.persistenceManager = persistenceManager;
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

        // we now store this state to be the current abstract state
        currentAbstractState = newAbstractState;

        // and then we store the concrete state and possibly the action
        ConcreteState newConcreteState = ConcreteStateFactory.createConcreteState(newState, newAbstractState, storeWidgets);
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
            String abstractId= actionSelector.selectAction(currentAbstractState, abstractStateModel).getActionId();
            System.out.println("Finding action with abstractId : " + abstractId);
            for(Action action : actions) {
            	try {
                    if (action.get(Tags.AbstractID).equals(abstractId)) {
                        return action;
                    }
            	} catch (NoSuchTagException e) {
                    String message = "ERROR getAbstractActionToExecute : " + action.get(Tags.Desc, "No description");
                    System.out.println(message);
                    errorMessages.add(message);
                }
            }
            System.out.println("Could not find action with abstractId : " +abstractId);
            errorMessages.add("The actions selector returned the action with abstractId: " + abstractId + " . However, TESTAR was " +
                    "unable to find the action in its executable actions");
        } catch (ActionNotFoundException e) {
            System.out.println("Could not find an action to execute for abstract state id : " + currentAbstractState.getStateId());
        }
        return null;
    }

    @Override
    public void notifyTestSequencedStarted() {
        sequenceManager.startNewSequence();
        actionSelector.notifyNewSequence();
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

    @Override
    public String getModelIdentifier() {
    	return abstractStateModel.getModelIdentifier();
    }

    @Override
    public String queryStateModel(String query, Object... params) {
    	EntityManager manager = persistenceManager.getEntityManager();

    	try (OResultSet resultSet = manager.getConnection().getDatabaseSession().query(query, params)) {
    		if (resultSet.hasNext()) {
    			OResult result = resultSet.next();
    			return result.toString();
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

    	return "Empty";
    }

}
