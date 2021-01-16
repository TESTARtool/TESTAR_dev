package nl.ou.testar.visualvalidation.matcher;

import nl.ou.testar.visualvalidation.extractor.ExpectedElement;
import nl.ou.testar.visualvalidation.ocr.RecognizedElement;

import java.util.List;

public class VisualDummyMatcher implements VisualMatcher {
    @Override
    public MatcherResult Match(List<RecognizedElement> ocrResult, List<ExpectedElement> expectedText) {
        return new MatcherResult();
    }

    @Override
    public void destroy() {

    }
}
