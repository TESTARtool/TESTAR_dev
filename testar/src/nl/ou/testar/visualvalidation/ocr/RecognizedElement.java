package nl.ou.testar.visualvalidation.ocr;

/**
 * A discovered text element by an OCR engine.
 */
public class RecognizedElement {
    final int _x1;
    final int _y1;
    final int _x2;
    final int _y2;
    final float _confidence;
    final String _text;

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
        _x1 = x1;
        _y1 = y1;
        _x2 = x2;
        _y2 = y2;
        _confidence = confidence;
        _text = text;
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
