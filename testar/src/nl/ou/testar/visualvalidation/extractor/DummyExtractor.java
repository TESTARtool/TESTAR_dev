package nl.ou.testar.visualvalidation.extractor;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.fruit.alayer.State;
import org.fruit.alayer.Widget;

import java.util.ArrayList;

public class DummyExtractor implements TextExtractorInterface{
    @Override
    public void ExtractExpectedText(State state, @Nullable Widget widget, ExpectedTextCallback callback) {

    }

    @Override
    public void Destroy() {

    }
}
