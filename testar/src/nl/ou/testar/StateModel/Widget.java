package nl.ou.testar.StateModel;

import org.fruit.alayer.Tag;
import org.fruit.alayer.TaggableBase;

public class Widget {

    // a set of attributes and values
    private TaggableBase attributes;

    // a unique string identifier for this widget
    private String id;

    public Widget(String id) {
        this.id = id;
        attributes = new TaggableBase();
    }

    /**
     * This method returns the widget id.
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * This method adds a custom attribute to the concrete class in the form of a tag and its value
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
     * This method returns the `attributes` that have been added to this concrete state
     * @return
     */
    public TaggableBase getAttributes() {
        return attributes;
    }

}
