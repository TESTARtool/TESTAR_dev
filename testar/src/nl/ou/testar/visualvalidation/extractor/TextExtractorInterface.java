package nl.ou.testar.visualvalidation.extractor;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.fruit.alayer.State;
import org.fruit.alayer.Widget;

public interface TextExtractorInterface {
    /**
     * Extract the expected text for all the available {@link Widget}'s in the given {@link State}.
     *
     * @param state    The current state of the application under test.
     * @param widget   When set we only extract the text from this widget instead of the entire state.
     * @param callback Callback function for returning the expected text.
     */
    void ExtractExpectedText(State state, @Nullable Widget widget, ExpectedTextCallback callback);

    /**
     * Destroy the text extractor.
     */
    void Destroy();
}
