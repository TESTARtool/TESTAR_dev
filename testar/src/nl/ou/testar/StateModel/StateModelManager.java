package nl.ou.testar.StateModel;

import nl.ou.testar.StateModel.ActionSelection.ActionSelector;
import nl.ou.testar.StateModel.Exception.ActionNotFoundException;
import nl.ou.testar.StateModel.Exception.StateModelException;
import nl.ou.testar.StateModel.Exception.StateNotFoundException;
import nl.ou.testar.StateModel.Persistence.PersistenceManager;
import nl.ou.testar.StateModel.Util.ActionHelper;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import java.util.HashSet;
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

    /**
     * Constructor
     * @param abstractStateModel
     * @param actionSelector
     */
    public StateModelManager(AbstractStateModel abstractStateModel, ActionSelector actionSelector, PersistenceManager persistenceManager) {
        this.abstractStateModel = abstractStateModel;
        this.actionSelector = actionSelector;
        this.persistenceManager = persistenceManager;
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
        String abstractStateId = newState.get(Tags.AbstractIDCustom);
        AbstractState newAbstractState;
        try {
            newAbstractState = abstractStateModel.getState(abstractStateId);
        }
        catch (StateModelException ex) {
            // state wasn't found
            newAbstractState = AbstractStateFactory.createAbstractState(newState, actions);
        }
        // we want to provide the abstract state with the identifier of the concrete state
        newAbstractState.addConcreteStateId(newState.get(Tags.ConcreteIDCustom));

        // add the abstract state to the model
        try {
            abstractStateModel.addState(newAbstractState);
            if (currentAbstractState == null) {
                // it's apparantly the first state in our run
                abstractStateModel.addInitialState(newAbstractState);
            }
            else {
                // it's not the first state, so we want to add a transition
                if (actionUnderExecution == null) {
                    // this should never happen if the notification process is followed correctly
                    System.exit(-1); //@todo this needs some proper error handling
                }
                abstractStateModel.addTransition(currentAbstractState, newAbstractState, actionUnderExecution);
                actionUnderExecution = null;
            }
        } catch (StateModelException e) {
            System.out.println(this.getClass() + " : Could not add state due to invalid identifier");
        }

        currentAbstractState = newAbstractState;
    }

    /**
     * This method should be called when an action is about to be executed.
     * @param actionUnderExecution
     */
    public void notifyActionExecution(Action actionUnderExecution) {
        this.actionUnderExecution = new AbstractAction(actionUnderExecution.get(Tags.AbstractID));
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
