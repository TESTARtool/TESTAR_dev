package org.fruit.alayer.linux.atspi;


import org.bridj.Pointer;

/**
 * Java implementation of the AtSpiRect structure.
 */
public class AtSpiRect {


    //region Properties


    public int x = 0;
    public int y = 0;
    public int width = 0;
    public int height = 0;


    //endregion


    //region Constructors


    /**
     * Default empty constructor.
     */
    public AtSpiRect() {

    }


    /**
     * Creates a new instance of an AtSpiRect object from a pointer.
     * @param rectPtr Pointer to the AtSpiRect object.
     * @return A Java instance of an AtSpiRect object.
     */
    public static AtSpiRect CreateInstance(long rectPtr) {


        if (rectPtr == 0) {
            return null;
        }


        // Create a new instance.
        AtSpiRect rObj = new AtSpiRect();


        // Fill the instance's properties.
        fillInstance(rectPtr, rObj);


        return rObj;

    }


    /**
     * Fills an AtSpiRect object's information.
     * @param rectPtr Pointer to the AtSpiRect object.
     * @param rObj The Java instance of an AtSpiRect object.
     */
    private static void fillInstance(long rectPtr, AtSpiRect rObj) {


        // Fill the properties with the information.
        Pointer<int[]> ptr = Pointer.pointerToAddress(rectPtr, int[].class, null);


        rObj.x = ptr.getIntAtIndex(0);
        rObj.y = ptr.getIntAtIndex(1);
        rObj.width = ptr.getIntAtIndex(2);
        rObj.height = ptr.getIntAtIndex(3);

    }


    //endregion


    //region Object overrides


    /**
     * Returns a string representation of an AtSpiRect object.
     * @return Returns a string representation of an AtSpiRect object.
     */
    @Override
    public String toString() {
        return "(" + x + "," + y + ") - " + width + "x" + height;
    }


    //endregion


}