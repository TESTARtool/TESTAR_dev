package nl.ou.testar.visualvalidation.matcher;

import nl.ou.testar.visualvalidation.extractor.ExpectedElement;
import nl.ou.testar.visualvalidation.ocr.RecognizedElement;
import org.apache.logging.log4j.Level;
import org.fruit.Pair;
import org.testar.Logger;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
                    // TODO TM: If all expected items are found should we break? Or what if we find other items that intersect?!
                    match[0].addRecognizedElement(it);
                }
            });

            // All the recognized elements which lay inside the area of interest are linked to this match.
            if (match[0] != null) {
                result.addMatch(match[0]);

                // Now that we have collected all the recognized elements, try to match the content.
                analyze(match[0]);

                // Remove all the recognized items that have bene linked to this match from the list with items which
                // we still need to match based on their location
                match[0].recognizedElements.forEach(_ocrResult::remove);
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

    enum RES {
        MATCHED,
        CASE_MISMATCH,
        WHITESPACE_CORRECTED,
        NO_MATCH
    }

    class Test {
        char expected;
        char found;
        RES matchRes;

        public Test(char e) {
            expected = e;
            found = Character.MIN_VALUE;
            matchRes = RES.NO_MATCH;
        }

        public String toString() {
            return "Expected :" + expected + " Found :" + found + " Matched :" + matchRes;
        }
    }

    private void analyze(Match match) {
        String expectedText = match.expectedElement._text;
        Logger.log(Level.INFO, TAG, "Trying to match {}", expectedText);
        // Try to sort the found elements based on their location,
        // map them to a grid based on coordinates starting with Upper left 0.0
        List<RecognizedElement> sorted = match.recognizedElements.stream()
                .sorted(Comparator.comparingInt(o -> o._location.x))
                .collect(Collectors.toList());

        // Create list of Found chars and matched FLAG
        List<Pair<Character, Boolean>> unMatched = sorted.stream().flatMap(
                recognizedElement -> recognizedElement._text.chars().boxed().map(it -> Character.toChars(it)[0]))
                .map(it -> Pair.from(it, false))
                .collect(Collectors.toList());


        // Try to make the longest possible match
        // For each char that matches the expected text mark as matched and move one ahead.

        // If not equal, could it be a case mismatch?
        // if they are not equal at all


        List<Test> result = new ArrayList<>();
        int unMatchedSize = unMatched.size();
        int indexCounter = 0;
        for (int i = 0; i < expectedText.length(); i++) {
            Character actual = expectedText.charAt(i);
            Test charRes = new Test(actual);

            // Iterate over the
            for (int k = indexCounter; k < unMatchedSize; k++) {
                Pair<Character, Boolean> item = unMatched.get(k);
                Character found = item.left();
                // Try to match the actual char case sensitive:
                if (found == actual && !item.right()) {
                    charRes.found = found;
                    unMatched.set(k, Pair.from(found, true));
                    charRes.matchRes = RES.MATCHED;
                    // We have found a match inside the unMatched List move one position.
                    indexCounter = k + 1;
                    break;
                }

                if (Character.isLetter(actual)) {
                    // Try to match the actual char none sensitive:
                    int CASING = 32;
                    if (Math.abs(found.compareTo(actual)) == CASING && !item.right()) {
                        charRes.found = found;
                        unMatched.set(k, Pair.from(found, true));
                        charRes.matchRes = RES.CASE_MISMATCH;
                        // We have found a match inside the unMatched List move one position.
                        indexCounter = k + 1;
                        break;
                    }
                }
            }

            result.add(i, charRes);
        }

        // Try to auto correct the missing whitespace
        // Skip the first and last since we can't match left and right element
        // TODO TM: Should we try to convert all the white spaces
        int almostLast = result.size() - 1;
        for (int i = 1; i < almostLast; i++) {

            Test res = result.get(i);
            if (res.matchRes == RES.NO_MATCH && Character.isWhitespace(res.expected)){
                // Found a candidate, try to fix it
                if (result.get(i-1).matchRes != RES.NO_MATCH && result.get(i+1).matchRes != RES.NO_MATCH ){
                    // Surroundings are matched this must be a missing whitespace not detected by OCR
                    res.matchRes = RES.WHITESPACE_CORRECTED;
                    result.set(i, res);
                    Logger.log(Level.INFO, TAG, "Auto corrected whitespace in between expected text");
                }
            }
        }

        result.forEach(i -> Logger.log(Level.INFO, TAG, "{}", i));
    }
}

//TODO see how to deal with reconginized elements which are placed/assinged to incorrect text fields
