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
