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