package nl.ou.testar.visualvalidation.matcher;

import nl.ou.testar.visualvalidation.Location;
import nl.ou.testar.visualvalidation.extractor.ExpectedElement;
import nl.ou.testar.visualvalidation.ocr.RecognizedElement;
import org.junit.Test;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class ContentMatcherTest {

    private static final RecognizedElement firstLine = new RecognizedElement(new Location(0, 0, 20, 10), 100, "1");
    private static final RecognizedElement secondLine = new RecognizedElement(new Location(0, 10, 20, 10), 100, "2W");
    private static final RecognizedElement thirdLineFirst = new RecognizedElement(new Location(0, 20, 20, 10), 100, "3 ");
    private static final RecognizedElement thirdLineSecond = new RecognizedElement(new Location(10, 25, 20, 5), 100, "?");
    private static final ExpectedElement expectedElement = new ExpectedElement(new Location(0, 0, 20, 30), "1K\n\r2w\n\r3");

    private static final MatcherConfiguration config = MatcherConfiguration.CreateDefault();

    private LocationMatch prepareExpectedTextWith3Lines() {
        LocationMatch locationMatch = new LocationMatch(expectedElement, 0);
        // Shuffled input so we can test the algorithm.
        locationMatch.addRecognizedElement(firstLine);
        locationMatch.addRecognizedElement(thirdLineFirst);
        locationMatch.addRecognizedElement(secondLine);
        locationMatch.addRecognizedElement(thirdLineSecond);

        return locationMatch;
    }

    @Test
    public void match() {
        // GIVEN we have an expected text containing three text lines.
        LocationMatch input = prepareExpectedTextWith3Lines();

        // WHEN we run the algorithm.
        ContentMatchResult result = ContentMatcher.Match(input, config);

        // THEN the result must be:
        assertEquals(9, result.totalExpected);
        assertEquals(8, result.totalMatched);

        assertSame('1', result.expectedResult.expectedText.get(0).character.character);
        assertEquals(CharacterMatchResult.MATCHED, result.expectedResult.expectedText.get(0).result);
        assertSame('1', result.expectedResult.expectedText.get(0).character.match.character);

        assertSame('K', result.expectedResult.expectedText.get(1).character.character);
        assertEquals(CharacterMatchResult.NO_MATCH, result.expectedResult.expectedText.get(1).result);
        assertNull(result.expectedResult.expectedText.get(1).character.match);

        assertSame('\n', result.expectedResult.expectedText.get(2).character.character);
        assertEquals(CharacterMatchResult.WHITESPACE_CORRECTED, result.expectedResult.expectedText.get(2).result);
        assertNull(result.expectedResult.expectedText.get(1).character.match);

        assertSame('\r', result.expectedResult.expectedText.get(3).character.character);
        assertEquals(CharacterMatchResult.WHITESPACE_CORRECTED, result.expectedResult.expectedText.get(3).result);
        assertNull(result.expectedResult.expectedText.get(3).character.match);

        assertSame('2', result.expectedResult.expectedText.get(4).character.character);
        assertEquals(CharacterMatchResult.MATCHED, result.expectedResult.expectedText.get(4).result);
        assertSame('2', result.expectedResult.expectedText.get(4).character.match.character);

        assertSame('w', result.expectedResult.expectedText.get(5).character.character);
        assertEquals(CharacterMatchResult.CASE_MISMATCH, result.expectedResult.expectedText.get(5).result);
        assertSame('W', result.expectedResult.expectedText.get(5).character.match.character);

        assertSame('\n', result.expectedResult.expectedText.get(6).character.character);
        assertEquals(CharacterMatchResult.WHITESPACE_CORRECTED, result.expectedResult.expectedText.get(6).result);
        assertNull(result.expectedResult.expectedText.get(6).character.match);

        assertSame('\r', result.expectedResult.expectedText.get(7).character.character);
        assertEquals(CharacterMatchResult.WHITESPACE_CORRECTED, result.expectedResult.expectedText.get(7).result);
        assertNull(result.expectedResult.expectedText.get(7).character.match);

        assertSame('3', result.expectedResult.expectedText.get(8).character.character);
        assertEquals(CharacterMatchResult.MATCHED, result.expectedResult.expectedText.get(8).result);
        assertSame('3', result.expectedResult.expectedText.get(8).character.match.character);

        assertSame('?', result.recognizedResult.recognized.get(5).character);
        assertNull(result.recognizedResult.recognized.get(5).match);
    }

    @Test
    public void sortRecognizedElements() {
        // GIVEN we have an expected text containing three text lines.
        LocationMatch input = prepareExpectedTextWith3Lines();

        // WHEN we sort the recognized elements.
        List<RecognizedElement> result = ContentMatcher.sortRecognizedElements(input, config);

        // THEN the recognized elements should be sorted correctly.
        assertEquals(Arrays.asList(firstLine, secondLine, thirdLineFirst, thirdLineSecond), result);
    }

    @Test
    public void sortRecognizedElementsPerTextLine() {
        // GIVEN we have an expected text containing three text lines.
        LocationMatch input = prepareExpectedTextWith3Lines();

        // WHEN we sort the recognized elements only on their y coordinate.
        Map<Integer, List<RecognizedElement>> result = ContentMatcher.sortRecognizedElementsPerTextLine(input, config);

        // THEN the recognized elements should be sorted correctly.
        Map<Integer, List<RecognizedElement>> expectedResult = Stream.of(
                new AbstractMap.SimpleEntry<>(5, Collections.singletonList(firstLine)),
                new AbstractMap.SimpleEntry<>(15, Collections.singletonList(secondLine)),
                new AbstractMap.SimpleEntry<>(27, Arrays.asList(thirdLineFirst, thirdLineSecond)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        expectedResult.keySet().forEach(expectedKey ->
                {
                    assertEquals(expectedResult.get(expectedKey).size(), result.get(expectedKey).size());
                    expectedResult.get(expectedKey).forEach(it -> assertTrue(result.get(expectedKey).contains(it)));
                }
        );
    }

    @Test
    public void correctAllNoneMatchedWhitespaces() {
        // GIVEN a prepared result containing whitespace characters which are not marked as matched.
        ExpectedTextMatchResult input = new ExpectedTextMatchResult(" T\te\fs\nt\r.\u000B?");
        Arrays.asList(1, 3, 5, 7, 9, 11).forEach(it ->
                input.expectedText.get(it).result = CharacterMatchResult.MATCHED
        );

        // WHEN the correction mechanism is applied
        ContentMatcher.correctWhitespaces(input);

        // THEN all the whitespaces should be corrected.
        for (int i = 0; i < input.expectedText.size(); i++) {
            assertSame(i % 2 == 0 ? CharacterMatchResult.WHITESPACE_CORRECTED : CharacterMatchResult.MATCHED,
                    input.expectedText.get(i).result);
        }
    }

    @Test
    public void notCorrectMatchedWhitespaces() {
        // GIVEN a prepared result containing whitespace characters which are matched.
        ExpectedTextMatchResult input = new ExpectedTextMatchResult(" T");
        input.expectedText.forEach(it -> it.result = CharacterMatchResult.MATCHED);

        // WHEN the correction mechanism is applied
        ContentMatcher.correctWhitespaces(input);

        // THEN all the whitespaces should be corrected.
        input.expectedText.forEach(it -> assertSame(CharacterMatchResult.MATCHED, it.result));
    }
}
