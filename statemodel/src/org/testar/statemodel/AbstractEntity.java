/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2025 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.statemodel;

import org.testar.statemodel.event.StateModelEvent;
import org.testar.statemodel.event.StateModelEventListener;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.TaggableBase;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractEntity {

    // a set of tags and values, containing those tags that were used in the creation of the abstract entity
    private TaggableBase attributes;

    // a unique string identifier for this entity
    private String id;

    // this should contain a hash to uniquely identify the elements that were `used` in the abstraction level of the model
    private String modelIdentifier;

    // a set of event listeners
    private Set<StateModelEventListener> eventListeners;

    public AbstractEntity(String id) {
        this.id = id;
        attributes = new TaggableBase();
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
     * This method adds a custom attribute to the entity in the form of a tag and its value
     * @param attribute
     * @param value
     */
    public void addAttribute(Tag attribute, Object value) {
        try {
            attributes.set(attribute, value);
        } catch (Exception e) { //TODO check what kind of exceptions can happen
            System.out.println("Problem adding value for tag " + attribute.name() + " to abstract state");
        }
    }

    /**
     * This method returns the `attributes` that have been added to this abstract state
     * @return
     */
    public TaggableBase getAttributes() {
        return attributes;
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
