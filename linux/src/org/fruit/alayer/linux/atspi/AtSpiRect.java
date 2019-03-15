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
