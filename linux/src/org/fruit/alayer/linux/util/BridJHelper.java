package org.fruit.alayer.linux.util;


import org.bridj.Pointer;


/**
 * Class to help with BridJ functionality.
 */
public class BridJHelper {


    private static final String EmptyString = "";


    /**
     * Converts a pointer to a string to a String.
     * @param stringPointer The pointer to the string.
     * @return The string the pointer points to.
     */
    public static String convertToString(Pointer<Byte> stringPointer) {

        if (stringPointer == null) {
            return EmptyString;
        }

        return stringPointer.getCString();
    }


    /**
     * Converts a String to a pointer to a string.
     * @param str The string to get a pointer to.
     * @return The pointer to a String.
     */
    public static Pointer<Byte> convertToPointer(String str) {
        return Pointer.pointerToCString(str);
    }


}