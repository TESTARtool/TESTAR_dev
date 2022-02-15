package nl.ou.testar.StateModel;

import java.util.Set;

import org.fruit.alayer.Tag;

import nl.ou.testar.StateModel.Event.StateModelEvent;
import nl.ou.testar.StateModel.Event.StateModelEventListener;
import nl.ou.testar.StateModel.Event.StateModelEventType;
import nl.ou.testar.StateModel.Exception.StateModelException;

public class AbstractStateModelReinforcementLearning extends AbstractStateModel {
	
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
    public void addTransition(AbstractState sourceState, AbstractState targetState, AbstractAction executedAction) throws StateModelException{
        checkStateId(sourceState.getStateId());
        checkStateId(targetState.getStateId());

        // check if the transition already exists
        if (stateTransitionsBySource.containsKey(sourceState.getStateId())) {
            // loop through all the transitions that have the same source state and check for matches
            for(AbstractStateTransition stateTransition : stateTransitionsBySource.get(sourceState.getStateId())) {
                if (targetState.getStateId().equals(stateTransition.getTargetStateId()) && executedAction.getActionId().equals(stateTransition.getActionId())) {
                    // the transition already exists. We send an update event to deal with changes in the states and actions
                    // now we notify our listeners of the possible update
                    emitEvent(new StateModelEvent(StateModelEventType.ABSTRACT_STATE_TRANSITION_CHANGED, stateTransition));

                    //Event to update Reinforcement Learning Tags Values associated with this AbstractAction
                    emitEvent(new StateModelEvent(StateModelEventType.ABSTRACT_ACTION_ATTRIBUTE_UPDATED, stateTransition));

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

        addTransition(newStateTransition);
        addState(sourceState);
        addState(targetState);

        activateEvents();
        emitEvent(new StateModelEvent(StateModelEventType.ABSTRACT_STATE_TRANSITION_ADDED, newStateTransition));
    }
}
