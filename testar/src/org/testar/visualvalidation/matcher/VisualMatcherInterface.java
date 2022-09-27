package org.testar.visualvalidation.matcher;

import org.testar.visualvalidation.extractor.ExpectedElement;
import org.testar.visualvalidation.ocr.RecognizedElement;

import java.util.List;

public interface VisualMatcherInterface {
    MatcherResult Match(List<RecognizedElement> ocrResult, List<ExpectedElement> expectedText);

    void destroy();
}
