package org.fruit.alayer.linux.gtk3;


import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.ann.Library;

import java.io.IOException;


/**
 * Implementation of GTK+ 3.
 */
@Library("libgtk-3")
public class LibGtk3 {


    static{
        try {
            BridJ.getNativeLibrary("libgtk-3");
        } catch (IOException e) {
            e.printStackTrace();
        }
        BridJ.register();
    }


    //***             GDK General             ***\\


    /**
     * Initializes the GDK library and connects to the windowing system. If initialization fails,
     * a warning message is output and the application terminates with a call to exit(1).
     * Any arguments used by GDK are removed from the array and argc and argv are updated accordingly.
     * GTK+ initializes GDK in gtk_init() and so this function is not usually needed by GTK+ applications.
     * @param argcPointer Pointer to the number of command line arguments.
     * @param argvPtrToPtrToPtr Pointer to pointer to pointer to the array of command line arguments.
     */
    public static native void gdk_init(long argcPointer, long argvPtrToPtrToPtr);



    //***             GTK Main             ***\\


    /**
     * Checks if any events are pending.
     * This can be used to update the GUI and invoke timeouts etc. while doing some time intensive computation.
     * Example: Updating the GUI during a long computation.
     * @return Returns true if any events are pending, false otherwise.
     */
    public static native boolean gtk_events_pending();


    /**
     * Runs a single iteration of the main loop.
     * If no events are waiting to be processed GTK+ will block until the next event is noticed. If you don't want to
     * block then pass false for blocking or check if any events are pending with pending() first.
     * @return true if quit() has been called for the innermost main loop.
     */
    public static native boolean gtk_main_iteration();



    //***             GDK Display             ***\\


    /**
     * Opens a display.
     * @param displayName The name of the display to open.
     * @return Returns a GdkDisplay, or NULL if the display could not be opened.
     */
    public static native long gdk_display_open(Pointer<Byte> displayName);


    /**
     * Gets the default GdkDisplay. This is a convenience function for:
     * gdk_display_manager_get_default_display (gdk_display_manager_get()).
     * @return Returns a GdkDisplay, or NULL if there is no default display.
     */
    public static native long gdk_display_get_default();


    /**
     * Gets the name of the display.
     * @param displayPointer A pointer to a display object.
     * @return Returns a string representing the display name. This string is owned by GDK and
     *         should not be modified or freed.
     */
    public static native Pointer<Byte> gdk_display_get_name(long displayPointer);


    /**
     * Closes the connection to the windowing system for the given display, and cleans up associated resources.
     * @param displayPointer A pointer to a display object.
     */
    public static native void gdk_display_close(long displayPointer);


    /**
     * Finds out if the display has been closed.
     * @param displayPointer A pointer to a display object.
     * @return True if the display is closed; False otherwise.
     */
    public static native boolean gdk_display_is_closed(long displayPointer);


    /**
     * Get the default GdkScreen for display.
     * @param displayPointer A pointer to a display object.
     * @return The default GdkScreen object for display.
     */
    public static native long gdk_display_get_default_screen(long displayPointer);


//    /**
//     * Gets the number of monitors that belong to display.
//     * The returned number is valid until the next emission of the "monitor-added" or "monitor-removed" signal.
//     * @param displayPointer The default GdkScreen object for display.
//     * @return Returns the number of monitors.
//     */
//    public static native int gdk_display_get_n_monitors(long displayPointer);
//
//
//    /**
//     * Gets a monitor associated with this display.
//     * @param displayPointer The default GdkScreen object for display.
//     * @param monitorIndex The index of the monitor to retrieve.
//     * @return Returns (a pointer to) the GdkMonitor, or NULL if monitor_num is not a valid monitor number.
//     */
//    public static native long gdk_display_get_monitor(long displayPointer, int monitorIndex);



    // /***             GDK DisplayManager             ***\


    /**
     * Gets the singleton GdkDisplayManager object.
     * When called for the first time, this function consults the GDK_BACKEND environment variable to find out which of
     * the supported GDK backends to use (in case GDK has been compiled with multiple backends). Applications can use
     * gdk_set_allowed_backends() to limit what backends can be used.
     * @return The global GdkDisplayManager singleton; gdk_parse_args(), gdk_init(), or gdk_init_check()
     * must have been called first.
     */
    public static native long gdk_display_manager_get();


    /**
     * Gets the default GdkDisplay.
     * @param displayManagerPointer A pointer to a DisplayManager object.
     * @return Returns a GdkDisplay, or NULL if there is no default display.
     */
    public static native long gdk_display_manager_get_default_display(long displayManagerPointer);



    //***             GDK Monitor             ***\\




    // **             GDK Screen             ***\\


    /**
     * Gets the root window of screen.
     * @param screenPointer A pointer to a screen object.
     * @return Returns (a pointer to) the root window.
     */
    public static native long gdk_screen_get_root_window(long screenPointer);


    /**
     * Gets the width of screen in pixels. The returned size is in "application pixels", not in "device pixels"
     * (see gdk_screen_get_monitor_scale_factor()).
     * @param screenPointer A pointer to the screen to get the width for.
     * @return Returns the width of screen in pixels.
     */
    public static native int gdk_screen_get_width(long screenPointer);


    /**
     * Gets the height of screen in pixels. The returned size is in "application pixels", not in "device pixels"
     * (see gdk_screen_get_monitor_scale_factor()).
     * @param screenPointer A pointer to the screen to get the height for.
     * @return Returns the height of screen in pixels.
     */
    public static native int gdk_screen_get_height(long screenPointer);


    /**
     * Obtains a list of all toplevel windows known to GDK on the screen screen . A toplevel window is a child of
     * the root window (see gdk_get_default_root_window()).
     * The returned list should be freed with g_list_free(), but its elements need not be freed.
     * @param screenPointer A pointer to the screen to get the height for.
     * @return Returns a list of toplevel windows, free with g_list_free().
     */
    public static native long gdk_screen_get_toplevel_windows(long screenPointer);


}