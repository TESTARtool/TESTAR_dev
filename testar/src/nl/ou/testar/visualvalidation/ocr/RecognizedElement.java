package nl.ou.testar.visualvalidation.ocr;

import nl.ou.testar.visualvalidation.TextElement;

import java.awt.Rectangle;

/**
 * A discovered text element by an OCR engine.
 */
public class RecognizedElement extends TextElement {
    public final float _confidence;

    /**
     * Constructor.
     *
     * @param location   The relative location of the text inside the application.
     * @param confidence The confidence level of the discovered text.
     * @param text       The discovered text.
     */
    public RecognizedElement(Rectangle location, float confidence, String text) {
        super(location, text);
        _confidence = confidence;
    }

    @Override
    public String toString() {
        return "RecognizedElement{" +
                "_location=" + _location +
                ", _confidence=" + _confidence +
                ", _text='" + _text + '\'' +
                '}';
    }
}
