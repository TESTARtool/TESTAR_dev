package nl.ou.testar.StateModel.Event;

public abstract class StateModelEvent {

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
