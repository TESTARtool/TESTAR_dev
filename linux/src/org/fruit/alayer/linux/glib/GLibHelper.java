/***************************************************************************************************
*
* Copyright (c) 2017 Open Universiteit - www.ou.nl
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* 1. Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
* 2. Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* 3. Neither the name of the copyright holder nor the names of its
* contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
* ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************************************/

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
