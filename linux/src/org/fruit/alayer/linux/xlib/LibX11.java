package org.fruit.alayer.linux.xlib;

import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.ann.Library;

import java.io.IOException;


/**
 * Implementation of LibX11.
 */
@Library("libX11")
public class LibX11 {


    static{
        try {
            BridJ.getNativeLibrary("libX11");
        } catch (IOException e) {
            e.printStackTrace();
        }
        BridJ.register();
    }


    //***           libX11              ***\\


    /**
     * Opens a connection to the X server that controls a display.
     *
     * @param displayName Specifies the hardware display name, which determines the display and communications
     *                    domain to be used. On a POSIX-conformant system, if the display_name is NULL,
     *                    it defaults to the value of the DISPLAY environment variable.
     * @return  Returns a (pointer to a) Display structure that serves as the connection to the X server and that
     *          contains all the information about that X server.
     */
    public static native long XOpenDisplay(Pointer<Byte> displayName);


    /**
     * Closes the connection to the X server for the display specified in the Display structure and destroys all
     * windows, resource IDs (Window, Font, Pixmap, Colormap, Cursor, and GContext), or other resources that
     * the client has created on this display, unless the close-down mode of the resource has been changed
     * (see XSetCloseDownMode()).
     * @param displayPtr Specifies the connection to the X server in (a pointer to) a Display structure.
     */
    public static native void XCloseDisplay(long displayPtr);


    /**
     * Returns the string that was passed to XOpenDisplay() when the current display was opened.
     * @param displayPtr Specifies the connection to the X server in (a pointer to) a Display structure.
     * @return  Returns the string that was passed to XOpenDisplay() when the current display was opened.
     *          On POSIX-conformant systems, if the passed string was NULL, these return the value of
     *          the DISPLAY environment variable when the current display was opened.
     */
    public static native Pointer<Byte> XDisplayString(long displayPtr);


    /**
     * Returns an integer that describes the height of the screen in pixels.
     * @param displayPtr Specifies the connection to the X server in (a pointer to) a Display structure.
     * @param screenNumber Specifies the appropriate screen number on the host X server.
     * @return Returns an integer that describes the height of the screen in pixels.
     */
    public static native int XDisplayHeight(long displayPtr, int screenNumber);


    /**
     * Returns an integer that describes the width of the screen in pixels.
     * @param displayPtr Specifies the connection to the X server in (a pointer to) a Display structure.
     * @param screenNumber Specifies the appropriate screen number on the host X server.
     * @return Returns an integer that describes the width of the screen in pixels.
     */
    public static native int XDisplayWidth(long displayPtr, int screenNumber);


    /**
     * Returns the number of available screens.
     * @param displayPtr Specifies the connection to the X server in (a pointer to) a Display structure.
     * @return Returns the number of available screens.
     */
    public static native int XScreenCount(long displayPtr);


    /**
     * Returns the default screen number referenced by the XOpenDisplay() function.
     * @param displayPtr Specifies the connection to the X server in (a pointer to) a Display structure.
     * @return Returns the default screen number referenced by the XOpenDisplay() function.
     */
    public static native int XDefaultScreen(long displayPtr);


    /**
     * Returns the root window.
     * @param displayPtr Specifies the connection to the X server in (a pointer to) a Display structure.
     * @param screenNumber Specifies the appropriate screen number on the host X server.
     * @return Returns the root window.
     */
    public static native int XRootWindow(long displayPtr, int screenNumber);


    /**
     * The XInternAtom() function returns the atom identifier associated with the specified atom_name string. If
     * only_if_exists is False, the atom is created if it does not exist. Therefore, XInternAtom() can return None.
     * If the atom name is not in the Host Portable Character Encoding, the result is implementation dependent.
     * Uppercase and lowercase matter; the strings ``thing'', ``Thing'', and ``thinG'' all designate different atoms.
     * The atom will remain defined even after the client's connection closes. It will become undefined only when the
     * last connection to the X server closes.
     * @param displayPtr Specifies the connection to the X server in (a pointer to) a Display structure.
     * @param atomName Name of the Atom to find.
     * @param onlyIfExists Specifies a Boolean value that indicates whether the atom must be created.
     * @return The Atom requested.
     */
    public static native long XInternAtom(long displayPtr, Pointer<Byte> atomName, boolean onlyIfExists);


    /**
     * The XGetWindowProperty() function returns the actual type of the property; the actual format of the property;
     * the number of 8-bit, 16-bit, or 32-bit items transferred; the number of bytes remaining to be read in the
     * property; and a pointer to the data actually returned.
     * @param displayPtr Specifies the connection to the X server.
     * @param w Specifies the window whose property you want to obtain.
     * @param property Specifies the property name.
     * @param offset Specifies the offset in the specified property (in 32-bit quantities) where the data is to be retrieved.
     * @param length Specifies the length in 32-bit multiples of the data to be retrieved.
     * @param delete Specifies a Boolean value that determines whether the property is deleted.
     * @param reqType Specifies the atom identifier associated with the property type or AnyPropertyType.
     * @param actualTypeReturnPtr Returns the atom identifier that defines the actual type of the property.
     * @param actualFormatReturnPtr Returns the actual format of the property.
     * @param itemsReturnPtr Returns the actual number of 8-bit, 16-bit, or 32-bit items stored in the prop_return data.
     * @param bytesAfterReturnPtr Returns the number of bytes remaining to be read in the property if a partial read was performed.
     * @param propReturnPtrToPtr Returns the data in the specified format.
     * @return The function returns Success if it executes successfully.
     */
    public static native int XGetWindowProperty(long displayPtr, long w, long property, long offset, long length,
                                                boolean delete, long reqType, long actualTypeReturnPtr,
                                                int actualFormatReturnPtr, long itemsReturnPtr, long bytesAfterReturnPtr,
                                                long propReturnPtrToPtr);


    /**
     * The XRaiseWindow() function raises the specified window to the top of the stack so that no
     * sibling window obscures it.
     * @param displayPtr Specifies the connection to the X server in (a pointer to) a Display structure.
     * @param windowId Specifies the window.
     */
    public static native void XRaiseWindow(long displayPtr, long windowId);


    public static native long XSendEvent(long displaytPtr, long windowId, boolean propagate,
                                         long eventMask, long eventSend);


}