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

        if (sw == null)
            sw = _defaultPen.strokeWidth();

        if (sw != null)
            g2d.setStroke(new BasicStroke(sw.floatValue()));


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