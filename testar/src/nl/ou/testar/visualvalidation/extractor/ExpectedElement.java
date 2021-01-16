package nl.ou.testar.visualvalidation.extractor;

import nl.ou.testar.visualvalidation.TextElement;

import java.awt.Rectangle;

public class ExpectedElement extends TextElement {

    /**
     * Constructor.
     *
     * @param location The relative location of the text inside the application.
     * @param text     The text.
     */
    public ExpectedElement(Rectangle location, String text) {
        super(location, text);
    }
}
