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


package org.fruit.alayer.linux.SpyMode;

import org.fruit.alayer.Pen;

import java.awt.*;

/**
 * Represents an image that needs to be drawn on screen in Spy- mode.
 */
public class DrawableImage extends DrawableObject {


    //region DrawableObject implementation


    @Override
    public void draw(Graphics2D g2d) {

        g2d.setColor(new Color(_pen.color().red(), _pen.color().green(), _pen.color().blue(), _pen.color().alpha()));
        throw new UnsupportedOperationException();

    }


    //endregion


    //region Properties


    private byte[] _bytes;
    /**
     * The bytes of the image that will be drawn.
     * @return The bytes of the image that will be drawn.
     */
    public byte[] getBytes() {
        return _bytes;
    }


    //endregion


    //region Constructors


    /**
     * Creates a new DrawableRect object.
     * @param loc The location where this object is located on screen.
     * @param p The pen with which this object will be drawn.
     * @param dp The default pen with which this object will be drawn as backup.
     * @param bytes Byte array of an image.
     */
    DrawableImage(Point loc, Pen p, Pen dp, byte[] bytes) {
        super(loc, p, dp);

        _bytes = bytes;

    }


    //endregion


    //region Object overrides


    @Override
    public String toString() {
        return "(" + _location.x + ", " + _location.y + ")";
    }


    //endregion


}
