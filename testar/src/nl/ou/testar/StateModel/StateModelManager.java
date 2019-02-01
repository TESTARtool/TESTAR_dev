package nl.ou.testar.StateModel;

import nl.ou.testar.StateModel.ActionSelection.ActionSelector;
import nl.ou.testar.StateModel.Exception.ActionNotFoundException;
import nl.ou.testar.StateModel.Exception.StateModelException;
import nl.ou.testar.StateModel.Persistence.PersistenceManager;
import nl.ou.testar.StateModel.Util.AbstractStateService;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;

import java.util.Set;

public class StateModelManager {

    // the abstract state model that this class is managing
    private AbstractStateModel abstractStateModel;

    // current abstract state of the SUT
    private AbstractState currentAbstractState;

    // the action that is currently being executed, if applicable
    private AbstractAction actionUnderExecution;

    // action selector that chooses actions to execute
    private ActionSelector actionSelector;

    // persistence manager interface for persisting our model entities
    private PersistenceManager persistenceManager;

    // tags containing the attributes that were used in creating the concrete state ID
    private Set<Tag<?>> concreteStateTags;

    // current concrete state
    private ConcreteState currentConcreteState;

    // the concrete action that is being executed.
    private ConcreteAction concreteActionUnderExecution;

    /**
     * Constructor
     * @param abstractStateModel
     * @param actionSelector
     */
    public StateModelManager(AbstractStateModel abstractStateModel, ActionSelector actionSelector, PersistenceManager persistenceManager, Set<Tag<?>> concreteStateTags) {
        this.abstractStateModel = abstractStateModel;
        this.actionSelector = actionSelector;
        this.persistenceManager = persistenceManager;
        this.concreteStateTags = concreteStateTags;
        init();
    }

    /**
     * Initialization logic needs to go here
     */
    public void init() {
        // initialization logic here
    }

    /**
     * This method should be called once when a new state is reached after the execution
     * of an action or succesfully starting the SUT.
     * @param newState
     * @param actions
     */
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

        // we now store this state to be the current abstract state
        currentAbstractState = newAbstractState;

        // and then we store the concrete state and possibly the action
        ConcreteState newConcreteState = ConcreteStateFactory.createConcreteState(newState, concreteStateTags, newAbstractState);
        if (concreteActionUnderExecution == null) {
            persistenceManager.persistConcreteState(newConcreteState);
        }
        else {
            ConcreteStateTransition concreteStateTransition = new ConcreteStateTransition(currentConcreteState, newConcreteState, concreteActionUnderExecution);
            persistenceManager.persistConcreteStateTransition(concreteStateTransition);
        }
        currentConcreteState = newConcreteState;
        concreteActionUnderExecution = null;
    }

    /**
     * This method should be called when an action is about to be executed.
     * @param action
     */
    public void notifyActionExecution(Action action) {
        // the action that is executed should always be traceable to an action on the current abstract state
        // in other words, we should be able to find the action on the current abstract state
        try {
            this.actionUnderExecution = currentAbstractState.getAction(action.get(Tags.AbstractID));
        }
        catch (ActionNotFoundException ex) {
            this.actionUnderExecution = new AbstractAction(action.get(Tags.AbstractID));
            currentAbstractState.addNewAction(actionUnderExecution);
        }
        concreteActionUnderExecution = ConcreteActionFactory.createConcreteAction(action, actionUnderExecution);
        this.actionUnderExecution.addConcreteActionId(concreteActionUnderExecution.getActionId());
    }

    public void notifySequenceEnded() {
        persistenceManager.persistAbstractStateModel(abstractStateModel);
    }

    /**
     * This method uses the abstract state model to return the abstract id of an action to execute
     * @return
     */
    public String getAbstractActionIdToExecute() {
        //@todo we will probably want to replace this method with one that returns the actual actions to execute

        if (currentAbstractState == null) {
            return null;
        }
        try {
            return actionSelector.selectAction(currentAbstractState, abstractStateModel).getActionId();
        } catch (ActionNotFoundException e) {
            System.out.println("Could not find an action to execute for abstract state id : " + currentAbstractState.getStateId());
        }
        return null;
    }



}
