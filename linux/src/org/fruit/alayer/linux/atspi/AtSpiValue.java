package org.fruit.alayer.linux.atspi;


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