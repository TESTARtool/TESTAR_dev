package org.fruit.example.tests;


import org.bridj.Pointer;
import org.fruit.alayer.linux.util.BridJHelper;
import org.fruit.alayer.linux.xlib.LibX11;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for working with the LibX11 API.
 */
public class LibX11Tests {


    /**
     * Tests XLib functions.
     */
    @Test
    public void testLibX11() {


        // First, get a pointer to a Display structure for the current machine; when null is used the
        // environment variable DISPLAY is used which translates to ':0'.
        long displayPointer = LibX11.XOpenDisplay(null);


        if (displayPointer == 0) {
            System.out.println("Cannot open the display!");
            return;
        }
        assertEquals(true, displayPointer > 0);


        // Get the string used to retrieve the display.
        Pointer<Byte> displayStringPtr = LibX11.XDisplayString(displayPointer);
        System.out.println("Display opened with string: '"  + displayStringPtr.getCString() + "'.");
        assertEquals(":0", displayStringPtr.getCString());


        // Get the default screen number of the current display.
        int defaultScreenNumber = LibX11.XDefaultScreen(displayPointer);
        System.out.println("Default display screen number: '"  + defaultScreenNumber + "'.");
        assertEquals(0, defaultScreenNumber);


        // Get the dimensions of the display.
        int displayHeighth = LibX11.XDisplayHeight(displayPointer, defaultScreenNumber);
        int displayWidth = LibX11.XDisplayWidth(displayPointer, defaultScreenNumber);
        System.out.println("Display dimensions: '"  + displayWidth + "x" + displayHeighth + "'.");
        assertEquals(1920, displayWidth);


        // Get the number of screens connected to the display adapter.
        int screenCount = LibX11.XScreenCount(displayPointer);
        System.out.println("Number of screens connected to the display adapter: '" + screenCount + "'.");
        assertEquals(1, screenCount);


        // Now get the RootWindow.
        // TODO: get/create a Window object or check if the returned pointer can be used somehow.
        long rootWindow = LibX11.XRootWindow(displayPointer, defaultScreenNumber);


        if (rootWindow == 0) {

        }


        // Find the Atom specifying the PID.
        long netWmPid = LibX11.XInternAtom(displayPointer, BridJHelper.convertToPointer("_NET_WM_PID"), true);
        System.out.println("_NET_WM_PID: '" + netWmPid + "'.");
        assertEquals(true, netWmPid > 0);


        // !!! Does not work!!!
//        long XA_CARDINAL = 6;
//        long type = 0;
//        int format = 0;
//        long nItems = 0;
//        long bytesAfter = 0;
//        long propPID = 0;
//
//
//        int returnVal = LibX11.XGetWindowProperty(displayPointer, rootWindow, netWmPid, 0, 1, false, XA_CARDINAL,
//                type, format, nItems, bytesAfter, propPID);


        // TODO: Find the Window for a given PID.



        // TODO: Bring the Window to the foreground - most likely an XSendEvent is necessary, raising doesn't give focus or unhide from mimimize.
        // Bring the window given its WindowId to the foreground.
        //LibX11.XRaiseWindow(displayPointer, 65011747); // 67108874



        // Close connection to the opened display.
        LibX11.XCloseDisplay(displayPointer);
        System.out.println("Display closed!");


    }


}