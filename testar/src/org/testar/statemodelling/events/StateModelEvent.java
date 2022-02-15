package org.testar.statemodelling.event;

public class StateModelEvent {

    /**
     * The type of event
     */
    private StateModelEventType eventType;

    /**
     * Subject of the event
     */
    private Object payload;

    /**
     * Constructor
     * @param eventType
     */
    public StateModelEvent(StateModelEventType eventType, Object payload) {
        this.eventType = eventType;
        this.payload = payload;
    }

    /**
     * Get the event type for this event.
     * @return
     */
    public StateModelEventType getEventType() {
        return eventType;
    }

    /**
     * Return the payload object
     * @return
     */
    public Object getPayload() {
        return payload;
    }

}
