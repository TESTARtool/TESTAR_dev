package org.fruit.example.tests;

import org.bridj.Pointer;
import org.fruit.alayer.linux.gdiplus.LibGdiPlus;
import org.junit.jupiter.api.Test;


import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;



/**
 * Unit tests for working with Mono's implementation of the GDI+ API.
 */
public class LibGdiPlusTests {



    @Test
    public void MonitorMethods() {


        // ULong_Ptrs are defined as uint32.
        Pointer<Integer> token = Pointer.allocateInt();
        Pointer<Integer> input = Pointer.allocateInt();
        Pointer<Long> output = Pointer.allocateLong();


        // Get a handle to the primary monitor.
        long pmHnd = LibGdiPlus.GdiplusStartup(token, input, output);
        System.out.println("Token: " + token.getInt());
        System.out.println("Input: " + input.getInt());
        System.out.println("Output: " + output.getLong());
        assertTrue(pmHnd == 0);

        if (pmHnd == 0) {

        }





    }



}