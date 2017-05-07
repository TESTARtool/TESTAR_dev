package org.fruit.alayer.linux.atspi;

import org.bridj.Pointer;

/**
 * Java implementation of the AtSpiPoint structure.
 */
public class AtSpiPoint {



    public int x;
    public int y;


    //region Constructors


    /**
     * Default empty constructor.
     */
    public AtSpiPoint() {

    }


    /**
     * Creates a new instance of an AtSpiPoint object from a pointer.
     * @param pointPtr Pointer to the AtSpiPoint object.
     * @return A Java instance of an AtSpiPoint object.
     */
    public static AtSpiPoint CreateInstance(long pointPtr) {


        if (pointPtr == 0) {
            return null;
        }


        // Create a new instance.
        AtSpiPoint pObj = new AtSpiPoint();


        // Fill the instance's properties.
        fillInstance(pointPtr, pObj);


        return pObj;

    }


    /**
     * Fills an AtSpiPoint object's information.
     * @param pointPtr Pointer to the AtSpiPoint object.
     * @param pObj The Java instance of an AtSpiPoint object.
     */
    private static void fillInstance(long pointPtr, AtSpiPoint pObj) {


        // Fill the properties with the information.
        Pointer<int[]> ptr = Pointer.pointerToAddress(pointPtr, int[].class, null);


        pObj.x = ptr.getIntAtIndex(0);
        pObj.y = ptr.getIntAtIndex(1);


    }


    //endregion


    //region Object overrides


    /**
     * Returns a string representation of an AtSpiPoint object.
     * @return Returns a string representation of an AtSpiPoint object.
     */
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }


    //endregion


}