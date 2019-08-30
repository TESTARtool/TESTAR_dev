package org.testar.actions.checks;

import org.testar.actions.ActionBase;

public class ActionCheck extends ActionBase {
    private String id;
    private String description;
    private EventType eventType;

    /**
     * Gets the id of the filter
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id of the filter
     *
     * @param id the new id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get description of the filter
     * 
     * @return the description of the filter
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the filter
     * 
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the event type.
     *
     * @return the event type
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Sets the event type.
     *
     * @param eventType the new event type
     */
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}
