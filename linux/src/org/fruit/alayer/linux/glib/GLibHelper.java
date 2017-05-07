package org.fruit.alayer.linux.glib;

import org.bridj.Pointer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class to help with glib operations.
 */
public class GLibHelper {


    /**
     * Creates a list of elements of a certain type by reading memory in a certain location.
     * @param dataPtr Pointer to the memory the data is stored.
     * @param dataStructureLength The length of the structure in which the elements are stored.
     * @param elementType The class type of the elements.
     * @param <T> The type of the elements.
     * @return A list of typed elements read from memory.
     */
    public static <T> List<T> createTypedList(long dataPtr, int dataStructureLength, Class<T> elementType) {


        List<T> elements = null;


        if (Objects.equals(elementType.getName(), Long.class.getName())) {

            //noinspection unchecked
            elements = (List<T>) new ArrayList<Long>();

            Pointer<long[]> ptr = Pointer.pointerToAddress(dataPtr, long[].class, null);


            // Retrieve each long value in the list.
            for (int i = 0; i < dataStructureLength; i++) {
                ((ArrayList<Long>)elements).add(ptr.getLongAtIndex(i));
            }


        } else if (Objects.equals(elementType.getName(), Integer.class.getName())) {

            //noinspection unchecked
            elements = (List<T>) new ArrayList<Integer>();

            Pointer<int[]> ptr = Pointer.pointerToAddress(dataPtr, int[].class, null);


            // Retrieve each long value in the list.
            for (int i = 0; i < dataStructureLength; i++) {
                ((ArrayList<Integer>)elements).add(ptr.getIntAtIndex(i));
            }

        } else if (Objects.equals(elementType.getName(), String.class.getName())) {


            //noinspection unchecked
            elements = (List<T>) new ArrayList<String>();


            // Create a Pointer to point to the data in the list.
            Pointer<long[]> data = Pointer.pointerToAddress(dataPtr, long[].class, null);

            // Retrieve each string ptr value in the list.
            for (int i = 0; i < dataStructureLength; i++) {

                // Get the string pointer element at index 'i' in the array and convert it to a Pointer to a string.
                Pointer<Byte> ptr = Pointer.pointerToAddress(data.getLongAtIndex(i), Byte.class, null);


                // Retrieve the string value and add it to the elements list.
                ((ArrayList<String>)elements).add(ptr.getCString());

            }

        }


        return elements;


    }


}