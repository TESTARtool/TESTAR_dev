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

import org.fruit.alayer.FillPattern;
import org.fruit.alayer.Pen;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Represents a rectangle that needs to be drawn on screen in Spy- mode.
 */
public class DrawableEllipse extends DrawableObject {


    //region DrawableObject implementation


    @Override
    public void draw(Graphics2D g2d) {

        g2d.setColor(new Color(_pen.color().red(), _pen.color().green(), _pen.color().blue(), _pen.color().alpha()));

        FillPattern fp = _pen.fillPattern();

        if (fp == null) {
            fp = _defaultPen.fillPattern();
        }

        if (fp == FillPattern.Solid) {
            g2d.fill(new Ellipse2D.Double(_location.x, _location.y, _boundingBox.width, _boundingBox.height));
        } else {
            g2d.draw(new Ellipse2D.Double(_location.x, _location.y, _boundingBox.width, _boundingBox.height));
        }

    }


    //endregion


    //region Properties


    private Rectangle _boundingBox;
    
    /**
     * The bounding box of the ellipse that will be drawn.
     * @return The bounding box of the ellipse that will be drawn.
     */
    public Rectangle getBoundingBox() {
        return _boundingBox;
    }


    //endregion


    //region Constructors


    /**
     * Creates a new DrawableRect object.
     * @param loc The location where this object is located on screen.
     * @param p The pen with which this object will be drawn.
     * @param dp The default pen with which this object will be drawn as backup.
     * @param boundingBox The bounding box of the ellipse that will be drawn.
     */
    DrawableEllipse(Point loc, Pen p, Pen dp, Rectangle boundingBox) {
        super(loc, p, dp);

        _boundingBox = boundingBox;

    }


    //endregion


    //region Object overrides


    @Override
    public String toString() {
        return "(" + _location.x + ", " + _location.y + ") - '" + _boundingBox.width + " x " + _boundingBox.height + "'";
    }


    //endregion


}
