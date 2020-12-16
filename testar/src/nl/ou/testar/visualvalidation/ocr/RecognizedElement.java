package nl.ou.testar.visualvalidation.ocr;

import nl.ou.testar.visualvalidation.TextElement;

/**
 * A discovered text element by an OCR engine.
 */
public class RecognizedElement extends TextElement {
    public final float _confidence;

    /**
     * Constructor.
     *
     * @param x1         The first X coordinate of the discovered text.
     * @param y1         The first Y coordinate of the discovered text.
     * @param x2         The second X coordinate of the discovered text.
     * @param y2         The second Y coordinate of the discovered text.
     * @param confidence The confidence level of the discovered text.
     * @param text       The discovered text.
     */
    public RecognizedElement(int x1, int y1, int x2, int y2, float confidence, String text) {
        super(x1, y1, x2, y2, text);
        _confidence = confidence;
    }

    @Override
    public String toString() {
        return "RecognizedElement{" +
                "_x1=" + _x1 +
                ", _y1=" + _y1 +
                ", _x2=" + _x2 +
                ", _y2=" + _y2 +
                ", _confidence=" + _confidence +
                ", _text='" + _text + '\'' +
                '}';
    }
}
