package nl.ou.testar.visualvalidation.matcher;

import nl.ou.testar.visualvalidation.extractor.ExpectedElement;
import nl.ou.testar.visualvalidation.ocr.RecognizedElement;
import org.apache.logging.log4j.Level;
import org.testar.Logger;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LocationMatcher implements VisualMatcher {
    static final String TAG = "Matcher";

    @Override
    public MatcherResult Match(List<RecognizedElement> ocrResult, List<ExpectedElement> expectedText) {
        MatcherResult result = new MatcherResult();
        List<RecognizedElement> _ocrResult = new ArrayList<>(ocrResult);

        expectedText.forEach(expectedElement -> {
            final Match[] match = {null};
            Set<RecognizedElement> removeRecognizedList = new HashSet<>();
            _ocrResult.forEach(it -> {
                // TODO TM: Lookup margin via extended settings framework
                int margin = 0;
                Rectangle areaOfInterest = it._location;
                areaOfInterest.setBounds(
                        areaOfInterest.x - margin,
                        areaOfInterest.y - margin,
                        areaOfInterest.width + (2 * margin),
                        areaOfInterest.height + (2 * margin)
                );

                // If the recognized element lay inside the area of interest.
                if (areaOfInterest.intersects(expectedElement._location)) {
                    // Prepare to make a match.
                    if (match[0] == null) {
                        match[0] = new Match(expectedElement, margin);
                    }

                    // TODO TM: optimizing this is part of thesis, focus on equality without taking casing into account.
                    if (expectedElement._text.compareToIgnoreCase(it._text) == 0) {
                        removeRecognizedList.add(it);
                        // TODO TM: If all expected items are found should we break? Or what if we find other items that intersect?!
                    }
                    match[0].addRecognizedElement(it);
                }
            });

            if (match[0] != null) {
                result.addMatch(match[0]);

                // TODO TM: Find solution to enable this to speed up the matcher. Currently when the "title bar" is
                //  being matched it uses the entire window as area of interest so all OCR items are marked as
                //  interesting, but if a text in title bar is also found in a different location it will be removed
                //  for an invalid reason.
                // Remove the items from the recognized list.
                // removeRecognizedList.forEach(_ocrResult::remove);
            }
        });

        Set<RecognizedElement> removeRecognizedList = new HashSet<>();
        result.getMatches().forEach(it -> removeRecognizedList.addAll(it.recognizedElements));
        // Remove the elements which have been matched with expected elements.
        removeRecognizedList.forEach(_ocrResult::remove);

        // The remainders in OCR result can be added to no match?
        _ocrResult.forEach(result::addNoMatch);

        Logger.log(Level.INFO, TAG, "Result {}", result);
        return result;
    }

    @Override
    public void destroy() {

    }
}
