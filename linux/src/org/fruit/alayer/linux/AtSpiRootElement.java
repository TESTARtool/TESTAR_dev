package org.fruit.alayer.linux;


import org.fruit.Util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;


/**
 * Represents an AT-SPI root (application) node that can be used by Testar.
 */
public class AtSpiRootElement extends AtSpiElement {


    //region Properties


    long pid, timeStamp;


    boolean isRunning, isActive, hasStandardMouse, hasStandardKeyboard;


    /**
     * Contains a mapping of AtSpiAccessible pointers and their corresponding AtSpiElements.
     */
    transient Map<Long, AtSpiElement> pointerMap;


    AtSpiElementMap topLevelContainerMap;


    //endregion


    //region Constructor


    /**
     * Default constructor.
     */
    AtSpiRootElement() {
        super(null);

        root = this;
        pointerMap = Util.newHashMap();
        topLevelContainerMap = new AtSpiElementMap();

    }


    //endregion



    //region Other needed functionality - Used by AtSpiHitTester


    /**
     * Determines whether an element encompasses a point on the screen.
     * @param element The element that might encompasses the point on the screen.
     * @param x The x-coordinate of the point.
     * @param y The y-coordiante of the point.
     * @return True if the element encompasses the point on screen; False otherwise.
     */
    boolean visibleAt(AtSpiElement element, double x, double y){


        // The element doesn't encompass the point on the screen.
        if(element.boundingBoxOnScreen == null || !element.boundingBoxOnScreen.contains(x, y) || !this.boundingBoxOnScreen.contains(x, y)) {
            return false;
        }


        // Get the top level container encompassing the hit test point.
        AtSpiElement topLevelContainer = topLevelContainerMap.at(x, y);


        // Top level containers always have z-index of 0 (I think) - checks if element is obscured by children
        // or a top level container.
        return (topLevelContainer == null || topLevelContainer.zIndex <= element.zIndex) &&
                !obscuredByChildren(element, x, y);

    }


    /**
     * Determines whether an element encompasses a point on the screen.
     * @param element The element that might encompasses the point on the screen.
     * @param x The x-coordinate of the point.
     * @param y The y-coordiante of the point.
     * @param obscuredByChildFeature A child obscures the (parent) element??
     * @return True if the element encompasses the point on screen; False otherwise.
     */
    boolean visibleAt(AtSpiElement element, double x, double y, boolean obscuredByChildFeature){


        // The element doesn't encompass the point on the screen.
        if(element.boundingBoxOnScreen == null || !element.boundingBoxOnScreen.contains(x, y) || !this.boundingBoxOnScreen.contains(x, y)) {
            return false;
        }


        // Get the top level container encompassing the hit test point.
        AtSpiElement topLevelContainer = topLevelContainerMap.at(x, y);


        // Top level containers always have z-index of 0 (I think) - checks if element is obscured by children
        // or a top level container.
        return (topLevelContainer == null || topLevelContainer.zIndex <= element.zIndex ||
                !obscuredByChildFeature || !obscuredByChildren(element, x, y));
    }


    /**
     * Determines whether an element is obscured by a child at a certain point on the screen.
     * @param element The element that might be obscured by a child.
     * @param x The x-coordinate of the point.
     * @param y The y-coordiante of the point.
     * @return True if a child element obscures the (parent) element at a certain point on the screen; False otherwise.
     */
    private boolean obscuredByChildren(AtSpiElement element, double x, double y){

        for(int i = 0; i < element.children.size(); i++){

            AtSpiElement child = element.children.get(i);

            if(child.boundingBoxOnScreen != null && child.boundingBoxOnScreen.contains(x, y) && child.zIndex >= element.zIndex) {
                return true;
            }


        }

        return false;

    }


    //endregion


    //region Serializable functionality


    // Used to determine the class during serialization.
    private static final long serialVersionUID = 456852951753L;


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
        return "PID: " + pid + " - Running: " + isRunning + " - Active: " + isActive + " - Children: " + children.size();
    }


    //endregion


}