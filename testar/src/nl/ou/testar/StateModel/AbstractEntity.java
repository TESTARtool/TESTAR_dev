package nl.ou.testar.StateModel;

import org.fruit.alayer.Tag;
import org.fruit.alayer.TaggableBase;

public abstract class AbstractEntity {

    // a set of tags and values, containing those tags that were used in the creation of the abstract entity
    private TaggableBase attributes;

    // a unique string identifier for this entity
    private String id;

    public AbstractEntity(String id) {
        this.id = id;
        attributes = new TaggableBase();
    }

    /**
     * Returns this entity's identifier
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * This method adds a custom attribute to the entity in the form of a tag and its value
     * @param attribute
     * @param value
     */
    public void addAttribute(Tag attribute, Object value) {
        try {
            attributes.set(attribute, value);
        } catch (Exception e) {
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
}
