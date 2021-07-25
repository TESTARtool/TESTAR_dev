package nl.ou.testar.visualvalidation.extractor;

import nl.ou.testar.visualvalidation.Location;
import nl.ou.testar.visualvalidation.TextElement;

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
