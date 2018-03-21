package org.fruit.alayer.linux.libwnck;


import org.fruit.alayer.linux.util.BridJHelper;


/**
 * Java implementation of an WnckWindow object.
 */
public class WnckWindow {


    //region Properties


    private long _windowPtr;
    public long windowPtr() {
        return _windowPtr;
    }


    private boolean _hasName;
    public boolean hasName() { return LibWnck.wnck_window_has_name(_windowPtr); }


    private String _name;
    public String name() {
        return BridJHelper.convertToString(LibWnck.wnck_window_get_name(_windowPtr));
    }


    private int _pid;
    public int pid() { return LibWnck.wnck_window_get_pid(_windowPtr); }


    //endregion


    //region Constructors


    /**
     * Default empty constructor.
     */
    private WnckWindow() {

    }


    /**
     * Creates a new instance of an WnckWindow object from a pointer.
     * @param wndwPtr Pointer to the WnckWindow object.
     * @return A Java instance of an WnckWindow object.
     */
    public static WnckWindow CreateInstance(long wndwPtr) {


        if (wndwPtr == 0) {
            return null;
        }


        // Create a new instance.
        WnckWindow wObj = new WnckWindow();


        // Fill the instance's properties.
        //fillInstance(windowPtr, wObj,);
        wObj._windowPtr = wndwPtr;


        return wObj;

    }


    /**
     * Fills an WnckWindow object's information.
     * @param wndwPtr Pointer to the WnckWindow object.
     * @param wObj The Java instance of an WnckWindow object.
     */
    private static void fillInstance(long wndwPtr, WnckWindow wObj) {


        // Fill the properties with the information.
        wObj._windowPtr = wndwPtr;
        wObj._hasName = LibWnck.wnck_window_has_name(wndwPtr);
        wObj._name = BridJHelper.convertToString(LibWnck.wnck_window_get_name(wndwPtr));
        wObj._pid = LibWnck.wnck_window_get_pid(wndwPtr);


    }


    //endregion


    /**
     * Fills the information for this object.
     */
    public void fillDebug() {
        fillInstance(_windowPtr, this);
    }


    //region Object overrides


    /**
     * Returns a string representation of an WnckWindow object.
     * @return Returns a string representation of an WnckWindow object.
     */
    @Override
    public String toString() {
        return "Name: " + _name;
    }


    //endregion


}