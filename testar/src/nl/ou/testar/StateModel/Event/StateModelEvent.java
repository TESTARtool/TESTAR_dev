package nl.ou.testar.StateModel.Event;

public class StateModelEvent {

    private StateModelEventType eventType;

    /**
     * Constructor
     * @param eventType
     */
    public StateModelEvent(StateModelEventType eventType) {
        this.eventType = eventType;
    }

    /**
     * Get the event type for this event.
     * @return
     */
    public StateModelEventType getEventType() {
        return eventType;
    }

    /**
     * Subject of the event
     */
    private Object payload;

    /**
     * Set the payload object
     * @param payload
     */
    public void setPayload(Object payload) {
        this.payload = payload;
    }

    /**
     * Return the payload object
     * @return
     */
    public Object getPayload() {
        return payload;
    }

}
