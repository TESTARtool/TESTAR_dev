package org.fruit.alayer.linux.glib;

import org.fruit.alayer.linux.atspi.LibAtSpi;


/**
 * Java implementation of the GHashTable object.
 *
 * !!! Not fully implemented - it does not retrieve any values, only the size.
 */
public class GHashTable {


    //region Properties


    private long _hastTablePtr;
    public long hashTablePtr() {
        return _hastTablePtr;
    }


    public int size() {
        return LibAtSpi.g_hash_table_size(_hastTablePtr);
    }


    //endregion


    //region Constructors


    /**
     * Default empty constructor.
     */
    private GHashTable() {

    }


    /**
     * Creates a new instance of an GHashTable object from a pointer.
     * @param hashTablePtr Pointer to the GHashTable object.
     * @return A Java instance of an GHashTable object.
     */
    public static GHashTable CreateInstance(long hashTablePtr) {


        if (hashTablePtr == 0) {
            return null;
        }


        // Create a new instance.
        GHashTable htObj = new GHashTable();


        // Fill the instance's properties.
        fillInstance(hashTablePtr, htObj);


        return htObj;

    }


    /**
     * Fills an GHashTable object's information.
     * @param hashTablePtr Pointer to the GHashTable object.
     * @param htObj The Java instance of an GHashTable object.
     */
    private static void fillInstance(long hashTablePtr, GHashTable htObj) {


        // Fill the properties with the information.
        htObj._hastTablePtr = hashTablePtr;


    }


    //endregion


    //region Object overrides


    /**
     * Returns a string representation of an GHashTable object.
     * @return Returns a string representation of an GHashTable object.
     */
    @Override
    public String toString() {
        return "Size: " + size();
    }


    //endregion


}