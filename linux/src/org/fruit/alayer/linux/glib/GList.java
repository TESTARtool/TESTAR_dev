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
import org.fruit.alayer.linux.atspi.LibAtSpi;

import java.util.List;

/**
 * Java implementation of the GList object.
 */
public class GList<T> {

    //region Properties

    private Class<T> _elementType;

    protected long _listPtr;
    public long listPtr() {
        return _listPtr;
    }

    /**
     * Gets the pointer to where the actual data is stored.
     * @return The pointer to where the actual data is stored.
     */
    public long dataPtr() {

        Pointer<long[]> ptr = Pointer.pointerToAddress(_listPtr, long[].class, null);

        // The GList structure starts with a void* value to point to the data followed two pointers to
        // next and previous - so start at index 0.
        return ptr.getLongAtIndex(0);

    }

    /**
     * Gets the length of the array.
     * @return The length of the array.
     */
    public int length() {
        return LibAtSpi.g_list_length(_listPtr);
    }

    protected List<T> _elements;
    public List<T> elements() {
        return GLibHelper.createTypedList(dataPtr(), length(), _elementType);
    }

    //endregion

    //region Constructors

    /**
     * Default empty constructor.
     */
    private GList() {

    }

    /**
     * Creates a new typed instance of a GList.
     * @param listPtr Pointer to a GList structure.
     * @param elementType The type of the GList elements.
     * @param <T> The type of the GList elements.
     * @return An instance of a GList object.
     */
    public static <T> GList<T> CreateInstance(long listPtr, Class<T> elementType) {

        if (listPtr == 0) {
            return null;
        }

        // Create a new instance.
        GList<T> aObj = new GList<T>();

        // Fill the instance's properties.
        aObj._listPtr = listPtr;
        aObj._elementType = elementType;
        aObj._elements = aObj.elements();

        return aObj;

    }

    //endregion

    //region Object overrides

    /**
     * Returns a string representation of a GList object.
     * @return Returns a string representation of a GList object.
     */
    @Override
    public String toString() {
        return "Size: " + length();
    }

    //endregion

}
