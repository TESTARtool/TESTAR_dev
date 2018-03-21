package org.fruit.alayer.linux.gdiplus;


import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.ann.Library;

import java.io.IOException;


/**
 * Represents a canvas for a linux screen on which can be painted.
 */
@Library("libgdiplus")
public class LibGdiPlus {


    static{
        try {
            BridJ.getNativeLibrary("libgdiplus");
        } catch (IOException e) {
            e.printStackTrace();
        }
        BridJ.register();
    }


//    /**
//     * The MonitorFromPoint function retrieves a handle to the display monitor that contains a specified point.
//     * @param pt A POINT structure that specifies the point of interest in virtual-screen coordinates.
//     * @param dwFlags Determines the function's return value if the point is not contained within any display monitor.
//     * @return If the point is contained by a display monitor, the return value is an HMONITOR handle to that
//     *         display monitor. If the point is not contained by a display monitor, the return value depends
//     *         on the value of dwFlags.
//     */
//    public static native long MonitorFromPoint(Point pt, long dwFlags);
//
//
//    /**
//     * The CreateCompatibleDC function creates a memory device context (DC) compatible with the specified device.
//     * @param hdc A handle to an existing DC. If this handle is NULL, the function creates a memory DC compatible
//     *            with the application's current screen.
//     * @return If the function succeeds, the return value is the handle to a memory DC.
//     *         If the function fails, the return value is NULL.
//     */
//    public static native long CreateCompatibleDC(long hdc);


    public static native long GdiplusStartup(Pointer<Integer> token, Pointer<Integer> input, Pointer<Long> output);


}