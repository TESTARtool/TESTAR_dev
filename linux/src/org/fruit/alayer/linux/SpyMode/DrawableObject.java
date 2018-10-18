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
 *
 */
public abstract class DrawableObject {


    //region Properties


    protected Point _location;
    /**
     * The location where this object will be drawn.
     * @return The location where this object will be drawn.
     */
    public Point getLocation() {
        return _location;
    }


    protected Pen _defaultPen;
    /**
     * The DefaultPen with which this object will be drawn as backup.
     * @return The DefaultPen with which this object will be drawn as backup.
     */
    public Pen getDefaultPen() {
        return _defaultPen;
    }


    protected Pen _pen;
    /**
     * The Pen with which this object will be drawn.
     * @return The Pen with which this object will be drawn.
     */
    public Pen getPen() {
        return _pen;
    }


    //endregion


    //region Abstract methods


    /**
     * Draws the object on the graphic context supplied.
     * @param g2d The graphics context on/ with which to paint.
     */
    public abstract void draw(Graphics2D g2d);


    //endregion


    //region Constructors


    /**
     * Creates a new DrawableObject.
     * @param loc The location where this object is located on screen.
     * @param p The pen with which this object will be drawn.
     * @param dp The default pen with which this object will be drawn as backup.
     */
    DrawableObject(Point loc, Pen p, Pen dp) {
        _location = loc;
        _pen = p;
        _defaultPen = dp;
    }


    //endregion


}
