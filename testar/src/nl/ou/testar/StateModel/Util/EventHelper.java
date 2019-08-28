package nl.ou.testar.StateModel.Util;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.AbstractStateModel;
import nl.ou.testar.StateModel.AbstractStateTransition;
import nl.ou.testar.StateModel.Event.StateModelEvent;
import nl.ou.testar.StateModel.Exception.InvalidEventException;
import nl.ou.testar.StateModel.Sequence.Sequence;
import nl.ou.testar.StateModel.Sequence.SequenceManager;
import nl.ou.testar.StateModel.Sequence.SequenceNode;
import nl.ou.testar.StateModel.Sequence.SequenceStep;

public class EventHelper {

    public void validateEvent(StateModelEvent event) throws InvalidEventException {
        if (event.getPayload() == null) {
            throw new InvalidEventException();
        }

        // verify that the event payload is what is expected
        switch (event.getEventType()) {
            case ABSTRACT_STATE_ADDED:
            case ABSTRACT_STATE_CHANGED:
                if (!(event.getPayload() instanceof AbstractState)) {
                    throw new InvalidEventException();
                }
                break;

            case ABSTRACT_STATE_TRANSITION_ADDED:
            case ABSTRACT_ACTION_CHANGED:
                if (!(event.getPayload() instanceof AbstractStateTransition)) {
                    throw new InvalidEventException();
                }
                break;

            case ABSTRACT_STATE_MODEL_INITIALIZED:
                if (!(event.getPayload() instanceof AbstractStateModel)) {
                    throw new InvalidEventException();
                }
                break;

            case SEQUENCE_STARTED:
            case SEQUENCE_ENDED:
                if (!(event.getPayload() instanceof Sequence)) {
                    throw new InvalidEventException();
                }
                break;

            case SEQUENCE_MANAGER_INITIALIZED:
                if (!(event.getPayload() instanceof SequenceManager)) {
                    throw new InvalidEventException();
                }
                break;

            case SEQUENCE_NODE_ADDED:
            case SEQUENCE_NODE_UPDATED:
                if (!(event.getPayload() instanceof SequenceNode)) {
                    throw new InvalidEventException();
                }
                break;

            case SEQUENCE_STEP_ADDED:
                if (!(event.getPayload() instanceof SequenceStep)) {
                    throw new InvalidEventException();
                }
                break;
        }
    }

}
