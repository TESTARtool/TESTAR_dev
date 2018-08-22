package nl.ou.testar.StateModel;

import nl.ou.testar.StateModel.Exception.StateModelException;
import nl.ou.testar.StateModel.Exception.StateNotFoundException;
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

    /**
     * Constructor
     * @param abstractStateModel
     */
    public StateModelManager(AbstractStateModel abstractStateModel) {
        this.abstractStateModel = abstractStateModel;
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
        //@todo ad: replace the abstract id tag with the custom tag from my dev branch
        String abstractStateId = newState.get(Tags.AbstractID);
        AbstractState newAbstractState;
        try {
            newAbstractState = abstractStateModel.getState(abstractStateId);
        }
        catch (StateModelException ex) {
            // state wasn't found
            newAbstractState = new AbstractState(abstractStateId, ActionHelper.getAbstractIds(actions));
        }

        // add the abstract state to the model
        try {
            abstractStateModel.addState(newAbstractState);
            if (currentAbstractState == null) {
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



}
