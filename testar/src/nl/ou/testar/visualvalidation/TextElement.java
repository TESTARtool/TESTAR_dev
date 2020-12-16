package nl.ou.testar.visualvalidation;

public class TextElement {
    public final int _x1;
    public final int _y1;
    public final int _x2;
    public final int _y2;
    public final String _text;

    /**
     * Constructor.
     *
     * @param x1   The first X coordinate of the text.
     * @param y1   The first Y coordinate of the text.
     * @param x2   The second X coordinate of the text.
     * @param y2   The second Y coordinate of the text.
     * @param text The text.
     */
    public TextElement(int x1, int y1, int x2, int y2, String text) {
        _x1 = x1;
        _y1 = y1;
        _x2 = x2;
        _y2 = y2;
        _text = text;
    }

    @Override
    public String toString() {
        return "TextElement{" +
                "_x1=" + _x1 +
                ", _y1=" + _y1 +
                ", _x2=" + _x2 +
                ", _y2=" + _y2 +
                ", _text='" + _text + '\'' +
                '}';
    }
}
