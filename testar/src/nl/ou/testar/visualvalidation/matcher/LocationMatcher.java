package nl.ou.testar.visualvalidation.matcher;

import nl.ou.testar.visualvalidation.extractor.ExpectedElement;
import nl.ou.testar.visualvalidation.ocr.RecognizedElement;
import org.apache.logging.log4j.Level;
import org.testar.Logger;

import java.awt.Dimension;
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

        // We first match them based on their location, if they intersect we mark them as match.
        // Because there are windows and frames included as well we need to make sure that we don't assign them to
        // these elements before we can assign them to the actual widget which is presented on the panel/window.
        expectedText.sort((element1, element2) -> {
            // Sort based on the area size of the element, from small to big
            Dimension element1Size = element1._location.getSize();
            Dimension element2Size = element2._location.getSize();
            return ((element1Size.width * element1Size.height) - (element2Size.width * element2Size.height));
        });

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

                // Remove the items from the recognized list.
                removeRecognizedList.forEach(_ocrResult::remove);
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
