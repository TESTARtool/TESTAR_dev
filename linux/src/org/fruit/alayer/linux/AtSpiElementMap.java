package org.fruit.alayer.linux;

import org.fruit.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Data structure representing a mapping of AtSpiElements to be something - only used for AtSpiElements
 * that are TopLevelContainers.
 *
 * This map could (perhaps should) be made generic...
 */
public class AtSpiElementMap implements Serializable {


    // Used to determine the class during serialization.
    private static final long serialVersionUID = 444555666888222999L;


    //region Properties


    private final List<AtSpiElement> elements = new ArrayList<>();


    //endregion


    //region Constructors


    /**
     * Default empty constructor.
     */
    AtSpiElementMap(){

    }


    //endregion


    //region Map functionality - This used to be in the ElementMapBuilder


    /**
     * Tries to add an element to the map.
     * @param element The element to add to the map.
     */
    void addElement(AtSpiElement element) {

        Assert.notNull(element);

        if (element.boundingBoxOnScreen != null) {
            elements.add(element);
        }

    }


    /**
     * Sorts the elements in the map.
     */
    void sort() {
        elements.sort(new AtSpiElementComparer());
    }


    //endregion


    //region Functionality used by AtSpiRootElement -> AtSpiHitTester


    /**
     * Gets the first top level container element that encompasses a certain point on the screen.
     * @param x The x-coordinate of the point to encompass.
     * @param y The y-coordinate of the point to encompass.
     * @return The first top level container element that encompasses a certain point on the screen.
     */
    public AtSpiElement at(double x, double y){
        for(AtSpiElement element : elements){
            if(element.boundingBoxOnScreen.contains(x, y))
                return element;
        }
        return null;
    }


    //endregion


}