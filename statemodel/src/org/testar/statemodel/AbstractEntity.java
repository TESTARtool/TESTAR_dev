/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel;

import org.testar.statemodel.event.StateModelEvent;
import org.testar.statemodel.event.StateModelEventListener;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractEntity extends TaggableEntity {

    // a unique string identifier for this entity
    private final String id;

    // this should contain a hash to uniquely identify the elements that were `used` in the abstraction level of the model
    private String modelIdentifier;

    // a set of event listeners
    private Set<StateModelEventListener> eventListeners;

    public AbstractEntity(String id) {
        super();
        this.id = id;
        eventListeners = new HashSet<>();
    }

    /**
     * Returns this entity's identifier
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the abstraction level modifier of the model this entity is a part of.
     * @return
     */
    public String getModelIdentifier() {
        return modelIdentifier;
    }

    /**
     * Sets the abstraction level modifier of the model this entity is a part of.
     * @param modelIdentifier
     */
    public void setModelIdentifier(String modelIdentifier) {
        this.modelIdentifier = modelIdentifier;
    }

    /**
     * Add an event listener to this state model
     * @param eventListener
     */
    public void addEventListener(StateModelEventListener eventListener) {
        eventListeners.add(eventListener);
    }

    /**
     * Notify our listeners of emitted events
     * @param event
     */
    protected void emitEvent(StateModelEvent event) {
        for (StateModelEventListener eventListener: eventListeners) {
            eventListener.eventReceived(event);
        }
    }
}
