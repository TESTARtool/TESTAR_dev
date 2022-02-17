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
