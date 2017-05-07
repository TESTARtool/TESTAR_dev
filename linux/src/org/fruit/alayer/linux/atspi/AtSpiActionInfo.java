package org.fruit.alayer.linux.atspi;

/**
 * Information on an AtSpiAction.
 */
public class AtSpiActionInfo {


    public String name;
    public String description;
    public String keyBinding;
    public int index;


    //region Object overrides


    /**
     * Returns a string representation of an AtSpiAccessible object.
     * @return Returns a string representation of an AtSpiAccessible object.
     */
    @Override
    public String toString() {
        return "Action: " + name + " - Description: " + description;
    }


    //endregion


}