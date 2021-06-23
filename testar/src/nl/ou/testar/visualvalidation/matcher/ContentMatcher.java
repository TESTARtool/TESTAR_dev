package nl.ou.testar.visualvalidation.matcher;

import nl.ou.testar.visualvalidation.ocr.RecognizedElement;
import org.apache.logging.log4j.Level;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.fruit.Pair;
import org.testar.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ContentMatcher {
    static final String TAG = "ContentMatcher";

    /**
     * Tries to match characters from the recognized text with the expected text characters. Recognized text elements
     * are first sorted based on their location. By calculating the line height of text the recognized text elements are
     * first grouped per text line. After that they are sorted based on their x coordinate. The result of this sorting
     * mechanism is a grid from left to right and top to bottom. The algorithm tries to find a matching character from
     * the grid, once found the index is stored and will be used as the starting position for the next iteration.
     */
    public static ContentMatchResult Match(LocationMatch locationMatch) {
        String expectedText = locationMatch.expectedElement._text;
        ExpectedTextMatchResult expectedResult = new ExpectedTextMatchResult(expectedText);
        Logger.log(Level.INFO, TAG, "Expected text @{} : {}", locationMatch.expectedElement._location, expectedResult);

        // Sort the recognized elements.
        List<RecognizedElement> sorted = sortRecognizedElements(locationMatch);
        RecognizedTextMatchResult recognizedResult = new RecognizedTextMatchResult(sorted);
        Logger.log(Level.INFO, TAG, "Recognized text: {}", recognizedResult);

        // Iterate over the expected text and try to match with a recognized element character.
        int unMatchedSize = recognizedResult.recognized.size();
        int indexCounter = 0;
        for (CharacterMatch expectedChar : expectedResult.expectedText) {
            char actual = expectedChar.character.character;

            // Iterate over the recognized characters.
            for (int k = indexCounter; k < unMatchedSize; k++) {
                CharacterMatchEntry item = recognizedResult.recognized.get(k);

                // Try to match the actual char case sensitive:
                if (item.character == actual && item.isNotMatched()) {
                    expectedChar.character.Match(item);
                    expectedChar.result = CharacterMatchResult.MATCHED;
                    // We have found a match move the index counter.
                    indexCounter = k + 1;
                    break;
                }

                // Try to match the actual char as case insensitive:
                if (Character.isLetter(actual)) {
                    int CASING = 32;
                    if (Math.abs(item.character.compareTo(actual)) == CASING && item.isNotMatched()) {
                        expectedChar.character.Match(item);
                        expectedChar.result = CharacterMatchResult.CASE_MISMATCH;
                        // We have found a match inside the unMatched List move one position.
                        indexCounter = k + 1;
                        break;
                    }
                }
            }
        }

        correctWhitespaces(expectedResult);

        return new ContentMatchResult(expectedResult, recognizedResult);
    }

    @NonNull
    private static List<RecognizedElement> sortRecognizedElements(LocationMatch locationMatch) {
        Map<Integer, List<RecognizedElement>> lineBucket = sortRecognizedElementsPerTextLine(locationMatch);

        // Get the sorted Y-axe coordinates for the identified lines.
        List<Integer> bucketSorted = lineBucket.keySet().stream().sorted().collect(Collectors.toList());

        // For each line sort the elements based on their X-axis coordinate and add the to the result.
        List<RecognizedElement> sorted = new ArrayList<>();
        bucketSorted.forEach(line ->
                {
                    List<RecognizedElement> sort = lineBucket.get(line).stream()
                            .sorted(Comparator.comparingInt(o -> o._location.x))
                            .collect(Collectors.toList());
                    sorted.addAll(sort);
                }
        );
        return sorted;
    }

    @NonNull
    private static Map<Integer, List<RecognizedElement>> sortRecognizedElementsPerTextLine(LocationMatch locationMatch) {
        // Determine the average height of the recognized text elements.
        double lineHeight = locationMatch.recognizedElements.stream()
                .mapToInt(e -> e._location.height)
                .average()
                .orElse(Double.NaN);

        // Create a overview of pairs reflecting the center line of the text and the recognized element.
        List<Pair<Integer, RecognizedElement>> lines = locationMatch.recognizedElements.stream()
                .map(e -> new Pair<>(e._location.y + (e._location.height / 2), e))
                .collect(Collectors.toList());

        final int margin = (int) Math.round(lineHeight / 2);
        Map<Integer, List<RecognizedElement>> lineBucket = new HashMap<>();

        // Find a matching line within the margins of the center line, if not found create a new line.
        for (Pair<Integer, RecognizedElement> line : lines) {
            boolean foundBucket = false;
            for (Integer c : lineBucket.keySet()) {
                if (IntStream.rangeClosed(c - margin, c + margin)
                        .boxed()
                        .collect(Collectors.toList())
                        .contains(line.left()))
                {
                    Logger.log(Level.DEBUG, TAG, "Line {} {} in range of bucket {}", line.left(), line.right(), c);
                    lineBucket.get(c).add(line.right());
                    foundBucket = true;
                    break;
                }
            }
            if (!foundBucket) {
                Logger.log(Level.DEBUG, TAG, "No bucket found creating new one for {} {}", line.left(), line.right());
                lineBucket.put(line.left(), new ArrayList<>());
                lineBucket.get(line.left()).add(line.right());
            }
        }
        return lineBucket;
    }

    private static void correctWhitespaces(ExpectedTextMatchResult expectedResult) {
        // Whitespaces are automatically corrected.
        for (CharacterMatch res : expectedResult.expectedText) {
            if (res.result == CharacterMatchResult.NO_MATCH && Character.isWhitespace(res.character.character)) {
                res.result = CharacterMatchResult.WHITESPACE_CORRECTED;
            }
        }
    }
}
