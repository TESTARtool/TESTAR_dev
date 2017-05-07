package org.fruit.alayer.linux.glib;

import org.bridj.Pointer;
import org.fruit.alayer.linux.atspi.LibAtSpi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Java implementation of the GArray object.
 */
public class GArray<T> {


    //region Properties


    private Class<T> _elementType;


    protected long _arrayPtr;
    public long arrayPtr() {
        return _arrayPtr;
    }


    /**
     * Gets the pointer to where the actual data is stored.
     * @return The pointer to where the actual data is stored.
     */
    public long dataPtr() {

        Pointer<long[]> ptr = Pointer.pointerToAddress(_arrayPtr, long[].class, null);

        // The GArray structure starts with a long value to point to the data followed by an int to
        // specify the length - so start at index 0.
        return ptr.getLongAtIndex(0);

    }


    /**
     * Gets the length of the array.
     * @return The length of the array.
     */
    public int length() {


        Pointer<int[]> ptr = Pointer.pointerToAddress(_arrayPtr, int[].class, null);

        // The GArray structure starts with a long value to point to the data followed by an int to
        // specify the length - a long is the size of two ints so start at index 2.
        return ptr.getIntAtIndex(2);


    }


    /**
     * Gets the size of the elements in the array in Bytes.
     * @return The size of the elements in the array in Bytes.
     */
    public int elementSize() {
        return LibAtSpi.g_array_get_element_size(_arrayPtr);
    }


    protected List<T> _elements;
    public List<T> elements() {


        if (Objects.equals(_elementType.getName(), Long.class.getName())) {

            //noinspection unchecked
            _elements = (List<T>) new ArrayList<Long>();

            Pointer<long[]> ptr = Pointer.pointerToAddress(dataPtr(), long[].class, null);


            // Retrieve each long value in the array.
            for (int i = 0; i < length(); i++) {
                ((ArrayList<Long>)_elements).add(ptr.getLongAtIndex(i));
            }


        } else if (Objects.equals(_elementType.getName(), Integer.class.getName())) {

            //noinspection unchecked
            _elements = (List<T>) new ArrayList<Integer>();

            Pointer<int[]> ptr = Pointer.pointerToAddress(dataPtr(), int[].class, null);


            // Retrieve each long value in the array.
            for (int i = 0; i < length(); i++) {
                ((ArrayList<Integer>)_elements).add(ptr.getIntAtIndex(i));
            }

        } else if (Objects.equals(_elementType.getName(), String.class.getName())) {


            //noinspection unchecked
            _elements = (List<T>) new ArrayList<String>();


            // Create a Pointer to point to the data in the array.
            Pointer<long[]> data = Pointer.pointerToAddress(dataPtr(), long[].class, null);

            // Retrieve each string ptr value in the array.
            for (int i = 0; i < length(); i++) {

                // Get the string pointer element at index 'i' in the array and convert it to a Pointer to a string.
                Pointer<Byte> ptr = Pointer.pointerToAddress(data.getLongAtIndex(i), Byte.class, null);


                // Retrieve the string value and add it to the elements list.
                ((ArrayList<String>)_elements).add(ptr.getCString());

            }

        }


        return _elements;

        //return _elements;

    }


    //endregion


    //region Constructors


    /**
     * Default empty constructor.
     */
    private GArray() {

    }


    /**
     * Creates a new typed instance of a GArray.
     * @param arrayPtr Pointer to a GArray structure.
     * @param elementType The type of the GArray elements.
     * @param <T> The type of the GArray elements.
     * @return An instance of a GArray object.
     */
    public static <T> GArray<T> CreateInstance(long arrayPtr, Class<T> elementType) {


        if (arrayPtr == 0) {
            return null;
        }


        // Create a new instance.
        GArray<T> aObj = new GArray<T>();


        // Fill the instance's properties.
        aObj._arrayPtr = arrayPtr;
        aObj._elementType = elementType;
        aObj._elements = aObj.elements();


        return aObj;




    }


    //endregion


    //region Object overrides


    /**
     * Returns a string representation of a GArray object.
     * @return Returns a string representation of a GArray object.
     */
    @Override
    public String toString() {
        return "Size: " + length();
    }


    //endregion


}