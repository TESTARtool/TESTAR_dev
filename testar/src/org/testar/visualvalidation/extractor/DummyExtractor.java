package org.testar.visualvalidation.extractor;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Widget;

public class DummyExtractor implements TextExtractorInterface {
    @Override
    public void ExtractExpectedText(State state, @Nullable Widget widget, ExpectedTextCallback callback) {

    }

    @Override
    public void Destroy() {

    }
}
