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


package org.testar.monkey.alayer.linux.atspi;

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
