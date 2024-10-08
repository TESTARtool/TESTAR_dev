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

package org.testar.monkey.example.tests;

import org.bridj.Pointer;
import org.testar.monkey.alayer.linux.gdiplus.LibGdiPlus;
import org.junit.jupiter.api.Test;


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
