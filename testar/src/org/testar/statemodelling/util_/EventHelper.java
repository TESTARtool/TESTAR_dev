package org.testar.statemodelling.util;

import org.testar.statemodelling.AbstractState;
import org.testar.statemodelling.AbstractStateModel;
import org.testar.statemodelling.AbstractStateTransition;
import org.testar.statemodelling.event.StateModelEvent;
import org.testar.statemodelling.exception.InvalidEventException;
import org.testar.statemodelling.sequence.Sequence;
import org.testar.statemodelling.sequence.SequenceManager;
import org.testar.statemodelling.sequence.SequenceNode;
import org.testar.statemodelling.sequence.SequenceStep;

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
