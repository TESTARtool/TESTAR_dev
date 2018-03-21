package org.fruit.alayer.linux.libwnck;

import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.ann.Library;

import java.io.IOException;


/**
 * LibWnck implementation - native method link.
 */
@Library("libwnck-3")
public class LibWnck {


    static{
        try {
            BridJ.getNativeLibrary("libwnck-3");
        } catch (IOException e) {
            e.printStackTrace();
        }
        BridJ.register();
    }



    //***           WnckScreen            ***\\


    /**
     * Gets the default WnckScreen on the default display.
     * @return Returns the default WnckScreen. The returned WnckScreen is owned by libwnck and must not be
     * referenced or unreferenced.
     */
    public static native long wnck_screen_get_default();


    /**
     * Gets the width of screen.
     * @param screenPointer Pointer to a WnckScreen object.
     * @return Returns the width of screen.
     */
    public static native int wnck_screen_get_width(long screenPointer);


    /**
     * Gets the height of screen.
     * @param screenPointer Pointer to a WnckScreen object.
     * @return Returns the height of screen.
     */
    public static native int wnck_screen_get_height(long screenPointer);


    /**
     * Synchronously and immediately updates the list of WnckWindow on screen. This bypasses the standard update
     * mechanism, where the list of WnckWindow is updated in the idle loop.
     * This is usually a bad idea for both performance and correctness reasons (to get things right,
     * you need to write model-view code that tracks changes, not get a static list of open windows). However,
     * this function can be useful for small applications that just do something and then exit.
     * @param screenPointer Pointer to a WnckScreen object.
     */
    public static native void wnck_screen_force_update(long screenPointer);


    /**
     * Gets the active WnckWindow on screen. May return NULL sometimes, since not all window managers
     * guarantee that a window is always active.
     * @param screenPointer Pointer to a WnckScreen object.
     * @return the active WnckWindow on screen, or NULL. The returned WnckWindow is owned by libwnck and must not be
     * referenced or unreferenced.
     */
    public static native long wnck_screen_get_active_window(long screenPointer);


    /**
     * Gets the (pointer to a) list of WnckWindow on screen . The list is not in a defined order, but should be
     * "stable" (windows should not be reordered in it). However, the stability of the list is
     * established by the window manager, so don't blame libwnck if it breaks down.
     * @param screenPointer Pointer to a WnckScreen object.
     * @return Returns the list of WnckWindow on screen , or NULL if there is no window on screen.
     *         The list should not be modified nor freed, as it is owned by screen.
     */
    public static native long wnck_screen_get_windows(long screenPointer);



    //***           WnckWindow            ***\\


    /**
     * Gets a preexisting WnckWindow for the X window xwindow. This will not create a WnckWindow if none exists.
     * The function is robust against bogus window IDs.
     * @param xWindowId An X window ID.
     * @return Returns the WnckWindow for xwindow . The returned WnckWindow is owned by libwnck and must
     *         not be referenced or unreferenced.
     */
    public static native long wnck_window_get(long xWindowId);


    /**
     * Checks whether or not window has a name. wnck_window_get_name() will always return some value, even if
     * window has no name set; wnck_window_has_name() can be used to tell if that name is real or not.
     * For icons titles, use wnck_window_has_icon_name() instead.
     * @param windowPointer Pointer to a WnckWindow object.
     * @return Returns TRUE if wnck_window_get_name() returns window 's name, FALSE if it returns a fallback name.
     */
    public static native boolean wnck_window_has_name(long windowPointer);


    /**
     * Gets the name of window, as it should be displayed in a pager or tasklist. Always returns some value,
     * even if window has no name set; use wnck_window_has_name() if you need to know whether the returned name is
     * "real" or not. For icons titles, use wnck_window_get_icon_name() instead.
     * @param windowPointer Pointer to a WnckWindow object.
     * @return Returns the name of window , or a fallback name if no name is available.
     */
    public static native Pointer<Byte> wnck_window_get_name(long windowPointer);


    /**
     * Gets the process ID of window.
     * @param windowPointer Pointer to a WnckWindow object.
     * @return Returns the process ID of window, or 0 if none is available.
     */
    public static native int wnck_window_get_pid(long windowPointer);


    /**
     * Asks the window manager to make window the active window. The window manager may choose to raise window along
     * with focusing it, and may decide to refuse the request (to not steal the focus if there is a more recent user
     * activity, for example).
     * This function existed before 2.10, but the timestamp argument was missing in earlier versions.
     * @param windowPointer Pointer to a WnckWindow object.
     * @param timestamp The X server timestamp of the user interaction event that caused this call to occur.
     */
    public static native void wnck_window_activate(long windowPointer, int timestamp);


    /**
     * Gets whether window is the active window on its WnckScreen.
     * @param windowPointer Pointer to a WnckWindow object.
     * @return Returns TRUE if window is the active window on its WnckScreen, FALSE otherwise.
     */
    public static native boolean wnck_window_is_active(long windowPointer);


}