/***************************************************************************************************
*
* Copyright (c) 2017 Open Universiteit - www.ou.nl
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* 1. Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
* 2. Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* 3. Neither the name of the copyright holder nor the names of its
* contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
* ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************************************/

package org.fruit.example.tests;


import org.fruit.alayer.linux.LinuxProcess;
import org.fruit.alayer.linux.glib.GList;
import org.fruit.alayer.linux.gtk3.LibGtk3;
import org.fruit.alayer.linux.libwnck.LibWnck;
import org.fruit.alayer.linux.libwnck.WnckWindow;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for working with the LibWnck API.
 */
public class LibWnckTests {


    private static final String ApplicationPath_Galculator = "/usr/bin/galculator";

    // Id should be retrieved manually after running a galculator instance.
    private static final long GalculatorXid = 35651587;


    @Test
    public void screenMethods() {


        LibGtk3.gdk_init(0, 0);


        long screenPointer = LibWnck.wnck_screen_get_default();
        assertTrue(screenPointer > 0);


        int width = LibWnck.wnck_screen_get_width(screenPointer);
        int heigth = LibWnck.wnck_screen_get_height(screenPointer);
        assertEquals(1920, width);
        assertEquals(975, heigth);


        // Before accessing anything about windows, the screen needs to be filled with information - force_update needed.
        LibWnck.wnck_screen_force_update(screenPointer);


        long windowListPtr = LibWnck.wnck_screen_get_windows(screenPointer);
        assertTrue(windowListPtr > 0);


        GList<Long> windows = GList.CreateInstance(windowListPtr, Long.class);
        ArrayList<WnckWindow> windowsList = new ArrayList<>();


        assert windows != null;
        for (Long w : windows.elements()) {

            WnckWindow win = WnckWindow.CreateInstance(w);

            if (win != null && win.windowPtr() > 1000000) {
                win.fillDebug();
                windowsList.add(win);
            }

        }



        long activeWindowPtr = LibWnck.wnck_screen_get_active_window(screenPointer);
        System.out.println("[" + getClass().getSimpleName() + "] Active window ptr: " + activeWindowPtr);
        assertTrue(activeWindowPtr > 0);


        WnckWindow activeWindow = WnckWindow.CreateInstance(activeWindowPtr);
        assertNotNull(activeWindow);
        assert activeWindow != null;
        System.out.println("[" + getClass().getSimpleName() + "] Active window name: " + activeWindow.name());
        activeWindow.fillDebug();


        // Launch an application - which will become the active window.
        LinuxProcess calc = LinuxProcess.fromExecutable(ApplicationPath_Galculator);


        // Short pause to give the application time to start.
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Flush the gtk event loop.
        while (LibGtk3.gtk_events_pending()) {
            LibGtk3.gtk_main_iteration();
        }


        GList<Long> windows2 = GList.CreateInstance(windowListPtr, Long.class);
        ArrayList<WnckWindow> windowsList2 = new ArrayList<>();


        assert windows2 != null;
        for (Long w : windows2.elements()) {

            WnckWindow win = WnckWindow.CreateInstance(w);

            if (win != null && win.windowPtr() > 1000000) {
                win.fillDebug();
                windowsList2.add(win);
            }

        }


        // Check if the window pointer changed.
        long activeWindowPtr2 = LibWnck.wnck_screen_get_active_window(screenPointer);
        System.out.println("[" + getClass().getSimpleName() + "] Active window ptr 2: " + activeWindowPtr2);
        assertTrue(activeWindowPtr2 > 0);
        assertNotEquals(activeWindowPtr, activeWindowPtr2);


        WnckWindow activeWindow2 = WnckWindow.CreateInstance(activeWindowPtr);
        assertNotNull(activeWindow2);
        assert activeWindow2 != null;
        System.out.println("[" + getClass().getSimpleName() + "] Active window name 2: " + activeWindow2.name());
        activeWindow2.fillDebug();
        assertNotEquals(activeWindow.name(), activeWindow2.name());


        // Flush the gtk event loop.
        while (LibGtk3.gtk_events_pending()) {
            LibGtk3.gtk_main_iteration();
        }


        // Check if the window pointer changed.
        long activeWindowPtr3 = LibWnck.wnck_screen_get_active_window(screenPointer);
        System.out.println("[" + getClass().getSimpleName() + "] Active window ptr 3: " + activeWindowPtr2);
        assertTrue(activeWindowPtr3 > 0);
        assertNotEquals(activeWindowPtr2, activeWindowPtr3);


        WnckWindow activeWindow3 = WnckWindow.CreateInstance(activeWindowPtr);
        assertNotNull(activeWindow3);
        assert activeWindow3 != null;
        System.out.println("[" + getClass().getSimpleName() + "] Active window name 3: " + activeWindow3.name());
        activeWindow3.fillDebug();
        assertNotEquals(activeWindow2.name(), activeWindow3.name());


        calc.stop();


    }


    @Test
    public void windowActive() {


        LibGtk3.gdk_init(0, 0);


        long screenPointer = LibWnck.wnck_screen_get_default();
        assertTrue(screenPointer > 0);


        // Before accessing anything about windows, the screen needs to be filled with information - force_update needed.
        LibWnck.wnck_screen_force_update(screenPointer);


        long windowPointer = LibWnck.wnck_window_get(GalculatorXid);
        assertTrue(windowPointer > 0);


        WnckWindow galculator = WnckWindow.CreateInstance(windowPointer);
        assertNotNull(galculator);
        assert galculator != null;
        galculator.fillDebug();


        boolean isActive = LibWnck.wnck_window_is_active(windowPointer);
        assertFalse(isActive);


        LibWnck.wnck_window_activate(windowPointer, (int)(System.currentTimeMillis() / 1000));


        // Flush the gtk event loop.
        while (LibGtk3.gtk_events_pending()) {
            LibGtk3.gtk_main_iteration();
        }

        isActive = LibWnck.wnck_window_is_active(windowPointer);
        assertTrue(isActive);



    }


}
