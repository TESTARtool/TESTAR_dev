package nl.ou.testar.StateModel;

import java.util.Set;

import org.fruit.alayer.Tag;

import nl.ou.testar.StateModel.Event.StateModelEvent;
import nl.ou.testar.StateModel.Event.StateModelEventListener;
import nl.ou.testar.StateModel.Event.StateModelEventType;
import nl.ou.testar.StateModel.Exception.StateModelException;

public class AbstractStateModelReinforcementLearning extends AbstractStateModel {

    private AbstractState previousSourceState;
    private AbstractState previvousTargetState;
    private AbstractAction previousExecutedAction;

    /**
     * constructor
     */
    public AbstractStateModelReinforcementLearning(String modelIdentifier,
            String applicationName,
            String applicationVersion,
            Set<Tag<?>> tags,
            StateModelEventListener ...eventListeners) {
        super(modelIdentifier, applicationName, applicationVersion, tags, eventListeners);
    }

    @Override
    public void addTransition(AbstractState sourceState, AbstractState targetState, AbstractAction executedAction) throws StateModelException {
        this.previousSourceState = sourceState;
        this.previvousTargetState = targetState;
        this.previousExecutedAction = executedAction;
        super.addTransition(sourceState, targetState, executedAction);
    }

    /**
     * Update the previous AbstractAction of the previous AbstractTransition. 
     * This method is needed because the RL approaches are updating the AbstractAction RLTags 
     * after the different AbstractState transitions. 
     * 
     * @throws StateModelException
     */
    public void updatePreviousTransitionAction() throws StateModelException {
        // This will happen basically in the initial state
        if(previousSourceState == null || previvousTargetState == null || previousExecutedAction == null) {
            return;
        }

        checkStateId(previousSourceState.getStateId());
        checkStateId(previvousTargetState.getStateId());

        // check if the transition already exists
        if (stateTransitionsBySource.containsKey(previousSourceState.getStateId())) {
            // loop through all the transitions that have the same source state and check for matches
            for(AbstractStateTransition stateTransition : stateTransitionsBySource.get(previousSourceState.getStateId())) {
                if (previvousTargetState.getStateId().equals(stateTransition.getTargetStateId()) && previousExecutedAction.getActionId().equals(stateTransition.getActionId())) {
                    // the transition already exists. We send an update event to deal with changes in the states and actions
                    // now we notify our listeners of the possible update
                    emitEvent(new StateModelEvent(StateModelEventType.ABSTRACT_STATE_TRANSITION_CHANGED, stateTransition));

                    //Event to update Reinforcement Learning Tags Values associated with this AbstractAction
                    emitEvent(new StateModelEvent(StateModelEventType.ABSTRACT_ACTION_ATTRIBUTE_UPDATED, stateTransition));

                    return;
                }
            }
        }
    }

    /**
     * Update a specific AbstractAction of the previous AbstractTransition. 
     * This method is needed because the RL ActionGroup approach can update multiple AbstractActions 
     * that exists in the previousState. 
     * 
     * @param abstractAction
     * @throws StateModelException
     */
    public void updateSpecificActionFromLastTransition(AbstractAction abstractAction) throws StateModelException {
        // This will happen basically in the initial state
        if(previousSourceState == null || previvousTargetState == null) {
            return;
        }

        checkStateId(previousSourceState.getStateId());
        checkStateId(previvousTargetState.getStateId());

        // check if the transition already exists
        // This will prevent to update an UnvisitedAbstractAction
        if (stateTransitionsBySource.containsKey(previousSourceState.getStateId())) {
            // loop through all the transitions that have the same source state and check for matches
            for(AbstractStateTransition stateTransition : stateTransitionsBySource.get(previousSourceState.getStateId())) {
                if (previvousTargetState.getStateId().equals(stateTransition.getTargetStateId()) && abstractAction.getActionId().equals(stateTransition.getActionId())) {
                    // the transition already exists. We send an update event to deal with changes in the states and actions
                    // now we notify our listeners of the possible update
                    emitEvent(new StateModelEvent(StateModelEventType.ABSTRACT_STATE_TRANSITION_CHANGED, stateTransition));

                    //Event to update Reinforcement Learning Tags Values associated with this AbstractAction
                    emitEvent(new StateModelEvent(StateModelEventType.ABSTRACT_ACTION_ATTRIBUTE_UPDATED, stateTransition));

                    return;
                }
            }
        }
    }

}
