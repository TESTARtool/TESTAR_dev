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


/**
 * Java implementation of an AtSpiValue object - An interface supporting a one-dimensional scalar to be modified,
 * or which reflects its value. If STATE_EDITABLE is not present, the value is treated as "read only".
 */
public class AtSpiValue {


    //region Properties


    private long _valuePtr;
    public long valuePtr() {
        return _valuePtr;
    }


    private double _minimumValue;
    public double minimumValue() { return LibAtSpi.atspi_value_get_minimum_value(_valuePtr, 0); }


    private double _currentValue;
    public double currentValue() { return LibAtSpi.atspi_value_get_current_value(_valuePtr, 0); }


    private double _maximumValue;
    public double maximumValue() { return LibAtSpi.atspi_value_get_maximum_value(_valuePtr, 0); }


    //endregion


    //region Constructors + Initialization


    /**
     * Default empty constructor.
     */
    private AtSpiValue() {

    }


    /**
     * Creates a new instance of an AtSpiValue object from a pointer.
     * @param valuePtr Pointer to the AtSpiValue object.
     * @return A Java instance of an AtSpiValue object.
     */
    public static AtSpiValue CreateInstance(long valuePtr) {


        if (valuePtr == 0) {
            return null;
        }


        // Create a new instance.
        AtSpiValue cObj = new AtSpiValue();


        // Fill the instance's properties.
        cObj._valuePtr = valuePtr;


        return cObj;

    }


    /**
     * Fills an AtSpiValue object's information.
     * @param valuePtr Pointer to the AtSpiValue object.
     * @param vObj The Java instance of an AtSpiValue object.
     */
    private static void fillInstance(long valuePtr, AtSpiValue vObj) {


        // Fill the properties with the information.
        vObj._minimumValue = LibAtSpi.atspi_value_get_minimum_value(valuePtr, 0);
        vObj._currentValue = LibAtSpi.atspi_value_get_current_value(valuePtr, 0);
        vObj._maximumValue = LibAtSpi.atspi_value_get_maximum_value(valuePtr, 0);


    }


    //endregion


    //region Value functionality


    /**
     * Fills the instance with data for debug purposes.
     */
    public void retrieveInformation() {
        fillInstance(_valuePtr, this);
    }


    //endregion


    //region Object overrides


    /**
     * Returns a string representation of an AtSpiValue object.
     * @return Returns a string representation of an AtSpiValue object.
     */
    @Override
    public String toString() {
        return "Value: " + currentValue() + " - MinValue: " + minimumValue() + " - MaxValue: " + maximumValue();
    }


    //endregion


}
