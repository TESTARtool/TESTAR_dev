package nl.ou.testar.visualvalidation.extractor;

import org.fruit.alayer.State;
import org.fruit.alayer.Widget;

public interface TextExtractorInterface {
    /**
     * Extract the expected text for all the available {@link Widget}'s in the given {@link State}.
     *
     * @param state    The current state of the application under test.
     * @param callback Callback function for returning the expected text.
     */
    void ExtractExpectedText(State state, ExpectedTextCallback callback);

    /**
     * Destroy the text extractor.
     */
    void Destroy();
}
