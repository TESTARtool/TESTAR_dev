package org.testar.visualvalidation.matcher;

import org.apache.logging.log4j.Level;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.testar.Logger;
import org.testar.visualvalidation.extractor.ExpectedElement;
import org.testar.visualvalidation.ocr.RecognizedElement;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class LocationMatcher implements VisualMatcherInterface {
    static final String TAG = "Matcher";
    private final MatcherConfiguration config;

    public LocationMatcher(MatcherConfiguration setting) {
        config = setting;
    }

    @Override
    public void destroy() {

    }

    /**
     * Match the recognized text elements with the expected text elements based on their location. For each expected
     * text element we first link the related recognized text elements based on their location. This process is executed
     * based on the surface area of the expected text in the order from small to large. This prevents that we don't
     * accidentally map a recognized text element to a different expected element that may overlap the actual expected
     * text element. Once we have finalized the linking we start the matching of the actual content.
     *
     * @param recognizedElements A list of recognized elements.
     * @param expectedElements   A list of expected text elements.
     * @return The result of the matching algorithm.
     */
    @Override
    public MatcherResult Match(List<RecognizedElement> recognizedElements, List<ExpectedElement> expectedElements) {
        // 1) Sort based on the area size of the element, from small to big
        expectedElements.sort((element1, element2) -> {
            Dimension element1Size = element1._location.getSize();
            Dimension element2Size = element2._location.getSize();
            return ((element1Size.width * element1Size.height) - (element2Size.width * element2Size.height));
        });

        MatcherResult _matchResult = new MatcherResult();
        List<RecognizedElement> _recognizedElements = new ArrayList<>(recognizedElements);
        // 2) Match the OCR results based on their location.
        expectedElements.forEach(expectedElement -> {
            final LocationMatch locationMatch = getIntersectedElements(_recognizedElements, expectedElement);

            // All the recognized elements which lay inside the area of interest are linked to this match.
            if (locationMatch != null) {
                _matchResult.addLocationMatch(locationMatch);

                // 3) Now that we have collected all the recognized elements, try to match the content.
                _matchResult.addContentMatchResult(ContentMatcher.Match(locationMatch, config));

                // Remove all the recognized items that have been linked to this match from the list with items which
                // we still need to match based on their location
                locationMatch.recognizedElements.forEach(_recognizedElements::remove);
            } else {
                _matchResult.addNoLocationMatch(expectedElement);
            }
        });

        // Compose the final matching result.
        setUnmatchedElements(_matchResult, _recognizedElements);
        return _matchResult;
    }

    /**
     * Find all unmatched elements and update the matcher result.
     */
    void setUnmatchedElements(MatcherResult matcherResult, List<RecognizedElement> recognizedElements) {
        Set<RecognizedElement> removeRecognizedList = new TreeSet<>();
        matcherResult.getLocationMatches().forEach(it -> removeRecognizedList.addAll(it.recognizedElements));

        // Remove the elements which have been matched with expected elements.
        removeRecognizedList.forEach(recognizedElements::remove);

        // The remainder of the recognized elements can be considered to be unmatched.
        recognizedElements.forEach(matcherResult::addNoLocationMatch);

        if (config.loggingEnabled) {
            Logger.log(Level.INFO, TAG, "No location match for the following detected text elements:\n {}", matcherResult.getNoLocationMatches().stream()
                    .map(e -> e._text + " " + e._location + "\n")
                    .collect(Collectors.joining()));
        }
    }

    /**
     * Find all recognized text elements which intersect with the expected text element.
     */
    @Nullable
    LocationMatch getIntersectedElements(List<RecognizedElement> recognizedElements, ExpectedElement expectedElement) {
        final LocationMatch[] locationMatches = {null};
        recognizedElements.forEach(it -> {
            final int margin = config.locationMatchMargin;
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
                if (locationMatches[0] == null) {
                    locationMatches[0] = new LocationMatch(expectedElement, margin);
                }
                locationMatches[0].addRecognizedElement(it);
            }
        });
        return locationMatches[0];
    }
}
