package org.testar.visualvalidation;

public class TextElement implements Comparable<TextElement> {
    public final Location _location;
    public final String _text;

    /**
     * Constructor.
     *
     * @param location The relative location of the text inside the application.
     * @param text     The text.
     */
    public TextElement(Location location, String text) {
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

    @Override
    public int compareTo(TextElement other) {
        int result = -1;
        if (_text.equals(other._text) && _location.equals(other._location)) {
            result = 0;
        }
        return result;
    }
}
