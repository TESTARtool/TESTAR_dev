package org.fruit.alayer.linux.SpyMode;

import org.fruit.alayer.Pen;

import java.awt.*;

/**
 * Represents a line that needs to be drawn on screen in Spy- mode.
 */
public class DrawableLine extends DrawableObject {


    //region DrawableObject implementation


    @Override
    public void draw(Graphics2D g2d) {

        g2d.setColor(new Color(_pen.color().red(), _pen.color().green(), _pen.color().blue(), _pen.color().alpha()));
        g2d.drawLine(_location.x, _location.y, _target.x, _target.y);

    }


    //endregion


    //region Properties


    private Point _target;
    /**
     * The target point of the line that will be drawn.
     * @return The target point of the line that will be drawn.
     */
    public Point getTarget() {
        return _target;
    }


    //endregion


    //region Constructors


    /**
     * Creates a new DrawableRect object.
     * @param loc The location where this object is located on screen.
     * @param p The pen with which this object will be drawn.
     * @param dp The default pen with which this object will be drawn as backup.
     * @param target End point of the line to be drawn.
     */
    DrawableLine(Point loc, Pen p, Pen dp, Point target) {
        super(loc, p, dp);

        _target = target;

    }


    //endregion


    //region Object overrides


    @Override
    public String toString() {
        return "(" + _location.x + ", " + _location.y + ") to (" + _location.x + ", " + _location.y + ")";
    }


    //endregion


}