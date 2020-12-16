package nl.ou.testar.visualvalidation.extractor;

import org.fruit.alayer.State;

import java.util.ArrayList;

public class DummyExtractor implements TextExtractorInterface{
    @Override
    public void ExtractExpectedText(State state, ExpectedTextCallback callback) {
        callback.ReportExtractedText(new ArrayList<>());
    }

    @Override
    public void Destroy() {

    }
}
