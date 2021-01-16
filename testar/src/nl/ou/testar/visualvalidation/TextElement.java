package nl.ou.testar.visualvalidation;

import java.awt.Rectangle;

public class TextElement {
    public final Rectangle _location;
    public final String _text;

    /**
     * Constructor.
     *
     * @param location The relative location of the text inside the application.
     * @param text     The text.
     */
    public TextElement(Rectangle location, String text) {
        _location = location;
        _text = text;
    }

    @Override
    public String toString() {
        return "TextElement{" +
                "_location=" + _location +
                ", _text='" + _text + '\'' +
                '}';
    }
}
