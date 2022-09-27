package org.testar.visualvalidation.extractor;

import org.testar.visualvalidation.Location;
import org.testar.visualvalidation.TextElement;

public class ExpectedElement extends TextElement {

    /**
     * Constructor.
     *
     * @param location The relative location of the text inside the application.
     * @param text     The text.
     */
    public ExpectedElement(Location location, String text) {
        super(location, text);
    }
}
