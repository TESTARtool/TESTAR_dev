package org.testar.statemodel.util;

import org.testar.statemodel.AbstractState;
import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.AbstractStateTransition;
import org.testar.statemodel.event.StateModelEvent;
import org.testar.statemodel.exceptions.InvalidEventException;
import org.testar.statemodel.sequence.Sequence;
import org.testar.statemodel.sequence.SequenceManager;
import org.testar.statemodel.sequence.SequenceNode;
import org.testar.statemodel.sequence.SequenceStep;

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
