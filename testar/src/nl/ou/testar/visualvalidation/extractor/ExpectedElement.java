package nl.ou.testar.visualvalidation.extractor;

import nl.ou.testar.visualvalidation.TextElement;

public class ExpectedElement extends TextElement {

    /**
     * Constructor.
     *
     * @param x1   The first X coordinate of the text.
     * @param y1   The first Y coordinate of the text.
     * @param x2   The second X coordinate of the text.
     * @param y2   The second Y coordinate of the text.
     * @param text The text.
     */
    public ExpectedElement(int x1, int y1, int x2, int y2, String text) {
        super(x1, y1, x2, y2, text);
    }
}
