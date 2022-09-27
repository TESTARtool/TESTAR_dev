package org.testar.visualvalidation.matcher;

import org.testar.visualvalidation.extractor.ExpectedElement;
import org.testar.visualvalidation.ocr.RecognizedElement;

import java.util.List;

public class VisualDummyMatcher implements VisualMatcherInterface {
    @Override
    public MatcherResult Match(List<RecognizedElement> ocrResult, List<ExpectedElement> expectedText) {
        return new MatcherResult();
    }

    @Override
    public void destroy() {

    }
}
