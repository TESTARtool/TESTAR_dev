package org.fruit.example.linux;


import org.fruit.alayer.linux.atspi.LibAtSpi;
import org.fruit.alayer.linux.LinuxProcess;


/**
 * Test class to test Linux functionality.
 */
public class Main {


    private static final String ApplicationPath_GEdit = "/usr/bin/gedit";


    /**
     * Entry-point for the test class.
     * @param args command-line arguments.
     */
    public static void main(String[] args) {

        //launchApplication();
        //testAtSpi();
        //testLibX11();

    }


    /**
     * Tests launching a Linux application.
     */
    private static void launchApplication(){


        LinuxProcess lp = LinuxProcess.fromExecutable(ApplicationPath_GEdit);


        if (lp == null) {
            System.out.println("Failed to launch the requested application.");
        }



    }


    /**
     * Tests AT-SPI functions.
     */
    private static void testAtSpi() {

        System.out.println("Desktop count: " +  LibAtSpi.atspi_get_desktop_count());

    }


//    /**
//     * Tests XLib functions.
//     */
//    private static void testLibX11() {
//
//
//        // First, get a pointer to a Display structure for the current machine; when null is used the
//        // environment variable DISPLAY is used which translates to ':0'.
//        long displayPointer = LibX11.XOpenDisplay(null);
//
//
//        if (displayPointer == 0) {
//            System.out.println("Cannot open the display!");
//            return;
//        }
//
//
//        // Get the string used to retrieve the display.
//        Pointer<Byte> displayStringPtr = LibX11.XDisplayString(displayPointer);
//        System.out.println("Display opened with string: '"  + displayStringPtr.getCString() + "'.");
//
//
//        // Get the default screen number of the current display.
//        int defaultScreenNumber = LibX11.XDefaultScreen(displayPointer);
//        System.out.println("Default display screen number: '"  + defaultScreenNumber + "'.");
//
//
//        // Get the dimensions of the display.
//        int displayHeighth = LibX11.XDisplayHeight(displayPointer, defaultScreenNumber);
//        int displayWidth = LibX11.XDisplayWidth(displayPointer, defaultScreenNumber);
//        System.out.println("Display dimensions: '"  + displayWidth + "x" + displayHeighth + "'.");
//
//
//        // Get the number of screens connected to the display adapter.
//        int screenCount = LibX11.XScreenCount(displayPointer);
//        System.out.println("Number of screens connected to the display adapter: '" + screenCount + "'.");
//
//
//
//        // Now get the RootWindow.
//        // TODO: get/create a Window object or check if the returned pointer can be used somehow.
//        long rootWindow = LibX11.XRootWindow(displayPointer, defaultScreenNumber);
//
//
//        if (rootWindow == 0) {
//
//        }
//
//
//        // TODO: Find the Window for a given PID.
//
//
//
//
//        // TODO: Bring the Window to the foreground - most likely an XSendEvent is necessary, raising doesn't give focus or unhide from mimimize.
//        // Bring the window given its WindowId to the foreground.
//        //LibX11.XRaiseWindow(displayPointer, 65011747); // 67108874
//
//
//
//        // Close connection to the opened display.
//        LibX11.XCloseDisplay(displayPointer);
//        System.out.println("Display closed!");
//
//
//    }



}