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