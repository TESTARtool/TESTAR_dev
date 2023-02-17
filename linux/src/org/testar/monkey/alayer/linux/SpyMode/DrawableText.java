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

package org.testar.monkey.alayer.linux.SpyMode;

import org.testar.monkey.alayer.Pen;
import org.testar.monkey.alayer.linux.util.JavaHelper;

import java.awt.*;


/**
 * Represents text that needs to be drawn on screen in Spy- mode.
 */
public class DrawableText extends DrawableObject {


    //region DrawableObject implementation


    @Override
    public void draw(Graphics2D g2d) {


        g2d.setColor(new Color(_pen.color().red(), _pen.color().green(), _pen.color().blue(), _pen.color().alpha()));


        // Set the stroke width.
        Double sw = _pen.strokeWidth();

        if (sw == null)
            sw = _defaultPen.strokeWidth();

        if (sw != null)
            g2d.setStroke(new BasicStroke(sw.floatValue()));

        // Create a font.
        String fontName = _pen.font();
        Double fontSize = _pen.fontSize();
        Font f;


        if (JavaHelper.isNullOrWhitespace(fontName)) {
            fontName = _defaultPen.font();
        }
        if (fontSize == null) {
            fontSize = _defaultPen.fontSize();
        }

        // Testar uses 3.0 stroke-width to signal plain and 5.0 to signal Bold.
        if (sw != null && sw == 5.0) {
            f = new Font(fontName, Font.BOLD, fontSize.intValue());
        } else {
            f = new Font(fontName, Font.PLAIN, fontSize.intValue());
        }


        g2d.setFont(f);



        // Displace the location.
        FontMetrics fMetrics = g2d.getFontMetrics(f);


        // TODO: Lets do a bit of cheating - Bold text is inside tooltip: shift it down and a bit to the right - plain text is edit box: centre it.
        // The 25 is the height of the textbox in this case - the method should actually get the height of the widget we're dealing with...
        if (sw != null && sw == 5.0) {
            g2d.drawString(_text, _location.x + 5, _location.y + 15);
        } else {
            g2d.drawString(_text, _location.x - (fMetrics.stringWidth(_text) / 2), _location.y - (fMetrics.getHeight() / 2) + (25 / 2));
        }


    }


    //endregion


    //region Properties


    private String _text;
    /**
     * The text that will be drawn.
     * @return The text that will be drawn.
     */
    public String getText() {
        return _text;
    }


    //endregion


    //region Constructors


    /**
     * Creates a new DrawableText object.
     * @param loc The location where this object is located on screen.
     * @param p The pen with which this object will be drawn.
     * @param dp The default pen with which this object will be drawn as backup.
     * @param txt The text that will be drawn.
     */
    DrawableText(Point loc, Pen p, Pen dp, String txt) {
        super(loc, p, dp);

        _text = txt;

    }


    //endregion


    //region Object overrides


    @Override
    public String toString() {
        return "(" + _location.x + ", " + _location.y + ") - '" + _text + "'";
    }


    //endregion


}
