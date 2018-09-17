package nl.ou.testar.StateModel.Util;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.AbstractStateTransition;
import nl.ou.testar.StateModel.Event.StateModelEvent;
import nl.ou.testar.StateModel.Exception.InvalidEventException;

public class EventHelper {

    public void validateEvent(StateModelEvent event) throws InvalidEventException {
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
        }
    }

}
