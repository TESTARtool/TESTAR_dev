package nl.ou.testar.visualvalidation.matcher;

import nl.ou.testar.visualvalidation.extractor.ExpectedElement;
import nl.ou.testar.visualvalidation.ocr.RecognizedElement;

import java.util.List;

public interface VisualMatcher {
    MatcherResult Match(List<RecognizedElement> ocrResult, List<ExpectedElement> expectedText);
    
    void destroy();
}
