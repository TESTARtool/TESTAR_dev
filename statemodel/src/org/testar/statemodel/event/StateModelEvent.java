/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.event;

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
