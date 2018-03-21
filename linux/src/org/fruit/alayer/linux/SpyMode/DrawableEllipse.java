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