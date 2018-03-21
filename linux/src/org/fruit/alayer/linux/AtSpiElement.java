package org.fruit.alayer.linux;


import org.fruit.alayer.Rect;
import org.fruit.alayer.linux.atspi.enums.AtSpiRoles;
import org.fruit.alayer.linux.enums.AtSpiElementOrientations;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents an AT-SPI node that can be used by Testar.
 */
public class AtSpiElement implements Serializable {


    //region Properties


    public AtSpiElement parent;
    List<AtSpiElement> children = new ArrayList<>();


    public AtSpiRootElement root;


    /**
     * Reference to the AtSpiWidget this AtSpiElement is linked to in the State (widget tree) object created by AtSpiStateFetcher.
     */
    AtSpiWidget backRef;


    //region General AT-SPI information


    long accessiblePtr;
    public String name, description, toolkitName;
    public AtSpiRoles role;
    Rect boundingBoxOnScreen;


    //endregion


    //region AT-SPI State information


    boolean isEnabled, hasFocus, isFocusable, isModal, isBlocked;
    AtSpiElementOrientations orientation;


    //endregion


    //region Inferred information


    boolean canScrollHorizontally, canScrollVertically, isTopLevelContainer, canScroll;
    double zIndex, hScrollViewSizePercentage, vScrollViewSizePercentage, hScrollPercentage, vScrollPercentage;


    //endregion


    //region Miscellaneous information


    /**
     * Specifies that this element should be ignored since it contains invalid information or
     * does not hold enough information for Testar to operate successfully.
     */
    boolean ignore;


    //endregion


    //endregion


    //region Constructors


    /**
     * Default constructor with a parent specified.
     * @param parent The parent of this AT-SPI element.
     */
    AtSpiElement(AtSpiElement parent) {

        this.parent = parent;

        if (parent != null) {
            root = parent.root;
        }

    }


    //endregion


    //region Serializable functionality


    // Used to determine the class during serialization.
    private static final long serialVersionUID = 159753654852L;


    // Most likely used to serialize and deserialize an instance of this class - don't know if this is used by Testar though.



    /**
     * Serialize an instance of this object.
     * @param oos The outputstream to write to.
     * @throws IOException An IO error occurred.
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }


    /**
     * Deserialize an instance of this object.
     * @param ois The inputstream to write to.
     * @throws IOException An IO error occurred.
     * @throws ClassNotFoundException Class could not be found.
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
        ois.defaultReadObject();
    }


    //endregion


    //region Object overrides


    @Override
    public String toString() {
        if (role != null) {
            return "Name: " + name + " - Role: " + role.toString() + " - Children: " + children.size();
        } else {
            return "Name: " + name + " - Children: " + children.size();
        }

    }


    //endregion


}