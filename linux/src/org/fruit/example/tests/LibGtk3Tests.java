package org.fruit.example.tests;


import org.bridj.Pointer;
import org.fruit.alayer.linux.util.BridJHelper;
import org.fruit.alayer.linux.LinuxProcess;
import org.fruit.alayer.linux.glib.GList;
import org.fruit.alayer.linux.gtk3.LibGtk3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Unit tests for working with the GTK+ 3 API.
 */
public class LibGtk3Tests {


    private final String DefaultDisplay = ":0";
    private static final String ApplicationPath_GEdit = "/usr/bin/gedit";


    @Test
    public void openDisplay() {


        long displayPointer = LibGtk3.gdk_display_get_default();
        assertEquals(0, displayPointer);


        displayPointer = LibGtk3.gdk_display_open(BridJHelper.convertToPointer(DefaultDisplay));
        assertEquals(true, displayPointer > 0);


        Pointer<Byte> displayName = LibGtk3.gdk_display_get_name(displayPointer);
        assertEquals(":0", displayName.getCString());


        boolean isClosed = LibGtk3.gdk_display_is_closed(displayPointer);
        assertEquals(false, isClosed);


        LibGtk3.gdk_display_close(displayPointer);
        //System.out.println("Display closed!");



        long displayMngrPtr = LibGtk3.gdk_display_manager_get();
        assertEquals(true, displayMngrPtr > 0);


        // This function will only retrieve the default display if it has already been opened.
        long defaultDisplayPointer = LibGtk3.gdk_display_manager_get_default_display(displayMngrPtr);
        assertEquals(0, defaultDisplayPointer);


        //Pointer<Byte> defaultDisplayName = LibGtk3.gdk_display_get_name(defaultDisplayPointer);
        //assertEquals(":0", defaultDisplayName.getCString());


    }


    @Test
    public void screensAndMonitors() {


        long displayPointer = LibGtk3.gdk_display_open(BridJHelper.convertToPointer(DefaultDisplay));
        assertEquals(true, displayPointer > 0);


        long screenPointer = LibGtk3.gdk_display_get_default_screen(displayPointer);
        assertEquals(true, screenPointer > 0);


        // This code should not be used when using GTK 3.22 or higher - it's deprecated.
        int width = LibGtk3.gdk_screen_get_width(screenPointer);
        int height = LibGtk3.gdk_screen_get_height(screenPointer);
        assertEquals(1920, width);
        assertEquals(975, height);



        // We're using 3.18.9.1 - this code should be used when using 3.22 or higher.
//        int monitorCount = LibGtk3.gdk_display_get_n_monitors(displayPointer);
//        assertEquals(1, monitorCount);
//
//
//        long monitorPtr = LibGtk3.gdk_display_get_monitor(displayPointer, monitorCount - 1);
//        assertEquals(true, monitorPtr > 0);


        LibGtk3.gdk_display_close(displayPointer);


    }


    @Test
    public void screensAndWindows() {


        long displayPointer = LibGtk3.gdk_display_open(BridJHelper.convertToPointer(DefaultDisplay));
        assertEquals(true, displayPointer > 0);


        long screenPointer = LibGtk3.gdk_display_get_default_screen(displayPointer);
        assertEquals(true, screenPointer > 0);


        // Get the root window.
        long rootWindowPtr = LibGtk3.gdk_screen_get_root_window(screenPointer);
        assertEquals(true, rootWindowPtr > 0);


        // Launch a new application.
        LinuxProcess gedit =  LinuxProcess.fromExecutable(ApplicationPath_GEdit);


        // Short pause to give the application time to start.
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Get a pointer to a GList structure containing pointers to GDKWindows.
        long listPtr = LibGtk3.gdk_screen_get_toplevel_windows(screenPointer);
        assertEquals(true, listPtr > 0);


        GList<Long> topLevelWindowPtrList = GList.CreateInstance(listPtr, Long.class);
        assertNotNull(topLevelWindowPtrList);
        assert topLevelWindowPtrList != null;
        assertEquals(true, topLevelWindowPtrList.length() > 0);


        // Close the display.
        LibGtk3.gdk_display_close(displayPointer);


    }


}