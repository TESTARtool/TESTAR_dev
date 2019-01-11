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

/**
 * Represents a rectangle that needs to be drawn on screen in Spy- mode.
 */
public class DrawableRect extends DrawableObject {

    //region DrawableObject implementation

    @Override
    public void draw(Graphics2D g2d) {

        // TODO: this is a hack that could be looked into to.
        // If the color (255,255,0,x) comes through make it completely transparent:
        // this is Testar's highlight color in spy-mode. However, it will make the Java window not click-through anymore
        // Could catch the mouse-event and simulate it again but this works easier - Testar already has a border around
        // every highlighted element anyways.
        if (_pen.color().red() == 255 && _pen.color().green() == 255 && _pen.color().blue() == 0) {
            g2d.setColor(new Color(0, 0, 0, 0));
        } else {
            g2d.setColor(new Color(_pen.color().red(), _pen.color().green(), _pen.color().blue(), _pen.color().alpha()));
        }

        Double sw = _pen.strokeWidth();

        if (sw == null) {
            sw = _defaultPen.strokeWidth();
        }
        if (sw != null) {
            g2d.setStroke(new BasicStroke(sw.floatValue()));
        }

        FillPattern fp = _pen.fillPattern();

        if (fp == null) {
            fp = _defaultPen.fillPattern();
        }

        if (fp == FillPattern.Solid) {
            g2d.fillRect(_rect.x, _rect.y, _rect.width, _rect.height);
        } else {
            g2d.drawRect(_rect.x, _rect.y, _rect.width, _rect.height);
        }

    }

    //endregion

    //region Properties

    private Rectangle _rect;
    /**
     * The rectangle that will be drawn.
     * @return The rectangle that will be drawn.
     */
    public Rectangle getRectangle() {
        return _rect;
    }

    //endregion

    //region Constructors

    /**
     * Creates a new DrawableRect object.
     * @param loc The location where this object is located on screen.
     * @param p The pen with which this object will be drawn.
     * @param dp The default pen with which this object will be drawn as backup.
     * @param rect The rectangle that will be drawn.
     */
    DrawableRect(Point loc, Pen p, Pen dp, Rectangle rect) {
        super(loc, p, dp);

        _rect = rect;

    }

    //endregion

    //region Object overrides

    @Override
    public String toString() {
        return "(" + _location.x + ", " + _location.y + ") - '" + _rect.width + " x " + _rect.height + "'";
    }

    //endregion

}
