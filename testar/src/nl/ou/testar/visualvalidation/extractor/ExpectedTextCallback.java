package nl.ou.testar.visualvalidation.extractor;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.fruit.alayer.Widget;

import java.util.List;

/**
 * Callback function for sharing the expected text for all the {@link Widget}.
 */
public interface ExpectedTextCallback {
    /**
     * Report the expected text to the caller.
     *
     * @param expectedText The expected text for all the {@link Widget}.
     */
    void ReportExtractedText(@NonNull List<ExpectedElement> expectedText);
}
