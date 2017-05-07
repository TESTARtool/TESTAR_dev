package org.fruit.alayer.linux.atspi;


import org.fruit.alayer.linux.atspi.enums.AtSpiComponentLayers;
import org.fruit.alayer.linux.atspi.enums.AtSpiCoordTypes;


/**
 * Java implementation of an AtSpiComponent object - An interface implemented by objects which have onscreen visual representations.
 */
public class AtSpiComponent {


    //region Properties


    private long _componentPtr;
    public long componentPtr() {
        return _componentPtr;
    }


    private AtSpiRect _extentsOnScreen;
    /**
     * The bounding box of the component relative to the screen.
     * @return The bounding box of the component relative to the screen.
     */
    public AtSpiRect extentsOnScreen() {
        return AtSpiRect.CreateInstance(LibAtSpi.atspi_component_get_extents(_componentPtr, AtSpiCoordTypes.Screen.ordinal(), 0));
    }


    private AtSpiRect _extentsOnWindow;
    /**
     * The bounding box of the component relative to the window.
     * @return The bounding box of the component relative to the window.
     */
    public AtSpiRect extentsOnWindow() {
        return AtSpiRect.CreateInstance(LibAtSpi.atspi_component_get_extents(_componentPtr, AtSpiCoordTypes.Window.ordinal(), 0));
    }


    private AtSpiPoint _positionOnScreen;
    /**
     * Gets the position of the AtspiComponent in screen coordinates.
     * @return The position of the AtspiComponent in screen coordinates.
     */
    public AtSpiPoint positionOnScreen() {
        return AtSpiPoint.CreateInstance(LibAtSpi.atspi_component_get_position(_componentPtr, AtSpiCoordTypes.Screen.ordinal(), 0));
    }


    private AtSpiPoint _positionOnWindow;
    /**
     * Gets the position of the AtspiComponent in window coordinates.
     * @return The position of the AtspiComponent in window coordinates.
     */
    public AtSpiPoint positionOnWindow() {
        return AtSpiPoint.CreateInstance(LibAtSpi.atspi_component_get_position(_componentPtr, AtSpiCoordTypes.Window.ordinal(), 0));
    }


    private AtSpiPoint _size;
    /**
     * Gets the size of the AtspiComponent.
     * @return The size of the AtspiComponent.
     */
    public AtSpiPoint size() {
        return AtSpiPoint.CreateInstance(LibAtSpi.atspi_component_get_size(_componentPtr, 0));
    }


    private AtSpiComponentLayers _layer;
    /**
     * Queries which layer the component is painted into, to help determine its visibility in terms of stacking order.
     * @return The layer the component is painted into.
     */
    public AtSpiComponentLayers layer() {

        int val = LibAtSpi.atspi_component_get_layer(_componentPtr, 0);


        // Sometimes an invalid value gets returned - link it to the Invalid enum value.
        if (val == -1) {
            val = 0;
        }

        return AtSpiComponentLayers.values()[val];

    }


    private short _mdiZOrder;
    /**
     * Queries the z stacking order of a component which is in the MDI or window layer
     * (Bigger z-order numbers mean nearer the top).
     * @return Returns a short indicating the stacking order of the component in the MDI layer,
     * or -1 if the component is not in the MDI layer.
     */
    public short mdiZOrder() {
        return LibAtSpi.atspi_component_get_mdi_z_order(_componentPtr, 0);
    }


    //endregion


    //region Constructors + Initialization


    /**
     * Default empty constructor.
     */
    private AtSpiComponent() {

    }


    /**
     * Creates a new instance of an AtSpiComponent object from a pointer.
     * @param componentPtr Pointer to the AtSpiComponent object.
     * @return A Java instance of an AtSpiComponent object.
     */
    public static AtSpiComponent CreateInstance(long componentPtr) {


        if (componentPtr == 0) {
            return null;
        }


        // Create a new instance.
        AtSpiComponent cObj = new AtSpiComponent();


        // Fill the instance's properties.
        cObj._componentPtr = componentPtr;


        return cObj;

    }


    /**
     * Fills an AtSpiComponent object's information.
     * @param componentPtr Pointer to the AtSpiComponent object.
     * @param cObj The Java instance of an AtSpiComponent object.
     */
    private static void fillInstance(long componentPtr, AtSpiComponent cObj, boolean light) {


        // Fill the properties with the information.
        //cObj._componentPtr = componentPtr;


        // These operations are quite heavy so better only do them if explicitly asked.
        if (!light) {

            cObj._extentsOnScreen = AtSpiRect.CreateInstance(LibAtSpi.atspi_component_get_extents(componentPtr, AtSpiCoordTypes.Screen.ordinal(), 0));
            cObj._extentsOnWindow = AtSpiRect.CreateInstance(LibAtSpi.atspi_component_get_extents(componentPtr, AtSpiCoordTypes.Window.ordinal(), 0));

            cObj._positionOnScreen = AtSpiPoint.CreateInstance(LibAtSpi.atspi_component_get_position(componentPtr, AtSpiCoordTypes.Screen.ordinal(), 0));
            cObj._positionOnWindow = AtSpiPoint.CreateInstance(LibAtSpi.atspi_component_get_position(componentPtr, AtSpiCoordTypes.Window.ordinal(), 0));

            cObj._size = AtSpiPoint.CreateInstance(LibAtSpi.atspi_component_get_size(componentPtr, 0));

        }


        int val = LibAtSpi.atspi_component_get_layer(componentPtr, 0);


        // Sometimes an invalid value gets returned - link it to the Invalid enum value.
        if (val == -1) {
            val = 0;
        }

        cObj._layer = AtSpiComponentLayers.values()[val];


        cObj._mdiZOrder = LibAtSpi.atspi_component_get_mdi_z_order(componentPtr, 0);


    }


    //endregion


    //region Component functionality


    /**
     * Fills the instance with data for debug purposes.
     */
    public void retrieveInformation(boolean light) {
        fillInstance(_componentPtr, this, light);
    }


    /**
     * Attempts to set the keyboard input focus to the specified AtspiComponent.
     * @return TRUE if successful, FALSE otherwise.
     */
    public boolean grabFocus() {
        return LibAtSpi.atspi_component_grab_focus(_componentPtr, 0);


    }


    //endregion


    //region Object overrides


    /**
     * Returns a string representation of an AtSpiComponent object.
     * @return Returns a string representation of an AtSpiComponent object.
     */
    @Override
    public String toString() {

        if (_layer != null) {
            return "Layer: " + _layer.toString();
        }

        return "Layer: " + layer().toString();
    }


    //endregion


}