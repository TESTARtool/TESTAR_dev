package org.fruit.alayer.linux.util;


import org.fruit.alayer.Rect;
import org.fruit.alayer.linux.gtk3.LibGtk3;


/**
 * Utility class to help with GDK functionality.
 */
public class GdkHelper {


    private static final String _defaultDisplay = ":0";


    /**
     * Gets the bounding box of the primary screen of the default display.
     * @return The bounding box of the primary screen of the default display.
     */
    public static Rect getScreenBoundingBox() {


        // Open the default display.
        long displayPointer = LibGtk3.gdk_display_open(BridJHelper.convertToPointer(_defaultDisplay));
        if (displayPointer <= 0) {
            return Rect.from(0, 0, 0, 0);
        }


        // Get the primary screen.
        long screenPointer = LibGtk3.gdk_display_get_default_screen(displayPointer);
        if (screenPointer <= 0) {
            return Rect.from(0, 0, 0, 0);
        }


        // This code should not be used when using GTK 3.22 or higher - it's deprecated.
        int width = LibGtk3.gdk_screen_get_width(screenPointer);
        int height = LibGtk3.gdk_screen_get_height(screenPointer);


        LibGtk3.gdk_display_close(displayPointer);


        return Rect.from(0, 0, width, height);


    }



}