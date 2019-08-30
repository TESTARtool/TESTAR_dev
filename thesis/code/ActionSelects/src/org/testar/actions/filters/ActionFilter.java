package org.testar.actions.filters;

import org.testar.actions.ActionBase;

public class ActionFilter extends ActionBase {
    private String id;
    private String description;

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
}
