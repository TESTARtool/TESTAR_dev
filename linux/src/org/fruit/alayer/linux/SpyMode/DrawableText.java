package org.fruit.alayer.linux.SpyMode;

import org.fruit.alayer.Pen;
import org.fruit.alayer.linux.util.JavaHelper;

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