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
