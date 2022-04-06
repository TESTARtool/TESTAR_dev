package org.testar.visualvalidation.matcher;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.testar.visualvalidation.Location;
import org.testar.visualvalidation.extractor.ExpectedElement;
import org.testar.visualvalidation.ocr.RecognizedElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LocationMatcherTest {

    private static final RecognizedElement recognizedElementOne =
            new RecognizedElement(new Location(0, 15, 36, 1), 0, "r");
    private static final RecognizedElement recognizedElementTwo =
            new RecognizedElement(new Location(40, 16, 26, 45), 23, "df");
    private static final RecognizedElement recognizedElementThree =
            new RecognizedElement(new Location(5, 10, 26, 45), 23, "aa");
    private static final RecognizedElement recognizedElementFour =
            new RecognizedElement(new Location(12, 16, 10, 20), 23, "Open");
    private static final ExpectedElement expectedElement =
            new ExpectedElement(new Location(10, 15, 15, 23), "open");

    @Test
    public void successfulMatch() {
        // GIVEN we have one expected text element with a matching(case corrected for first character)
        // recognized element plus three additional garbage elements.
        MatcherConfiguration config = MatcherConfiguration.CreateDefault();
        LocationMatcher sut = new LocationMatcher(config);

        List<RecognizedElement> recognizedElements = Arrays.asList(
                recognizedElementOne, recognizedElementTwo,
                recognizedElementThree, recognizedElementFour);

        List<ExpectedElement> expectedElements = Collections.singletonList(expectedElement);

        // WHEN we run the match algorithm
        MatcherResult result = sut.Match(recognizedElements, expectedElements);

        // THEN The match result should be:
        // We expected that one recognized elements isn't matched based on the location.
        assertEquals(1, result.getNoLocationMatches().size());
        assertTrue(result.getNoLocationMatches().contains(recognizedElementTwo));

        // We have 3 recognized elements that intersect with the expected text.
        assertEquals(1, result.getLocationMatches().size());
        LocationMatch locationMatch = result.getLocationMatches().iterator().next();
        assertEquals(3, locationMatch.recognizedElements.size());

        // Validate that all expected characters have been matched.
        assertEquals(1, result.getResult().size());
        assertEquals(4, result.getResult().iterator().next().totalMatched);
    }

    @Test
    public void setUnmatchedElements() {
        // GIVEN we have a matched only the first recognized element.
        MatcherConfiguration config = MatcherConfiguration.CreateDefault();
        LocationMatcher sut = new LocationMatcher(config);

        MatcherResult matcherResult = Mockito.mock(MatcherResult.class);
        LocationMatch locationMatchMock = Mockito.mock(LocationMatch.class);
        locationMatchMock.recognizedElements = new HashSet<>(Collections.singletonList(recognizedElementOne));
        Mockito.when(matcherResult.getLocationMatches()).thenReturn(new HashSet<>(Collections.singletonList(locationMatchMock)));

        ArrayList<RecognizedElement> recognizedElements = new ArrayList<>(Arrays.asList(
                recognizedElementOne,
                recognizedElementTwo,
                recognizedElementThree
        ));

        // WHEN we collect the unmatched results.
        sut.setUnmatchedElements(matcherResult, recognizedElements);

        // THEN the matcher result should contain the two remaining recognized elements.
        ArgumentCaptor<RecognizedElement> arg = ArgumentCaptor.forClass(RecognizedElement.class);
        verify(matcherResult, times(2)).addNoLocationMatch(arg.capture());
        assertEquals(2, recognizedElements.size());
        assertEquals(arg.getAllValues().get(0), recognizedElementTwo);
        assertEquals(arg.getAllValues().get(1), recognizedElementThree);
    }

    @Test
    public void getIntersectedElements() {
        // GIVEN two recognized elements inside and one outside the surface area of the expected text.
        List<RecognizedElement> recognizedElements = Arrays.asList(
                recognizedElementOne,
                recognizedElementTwo,
                recognizedElementThree
        );

        MatcherConfiguration config = MatcherConfiguration.CreateDefault();
        LocationMatcher sut = new LocationMatcher(config);

        // WHEN we try find intersecting elements.
        LocationMatch result = sut.getIntersectedElements(recognizedElements, expectedElement);

        // THEN the two recognized elements inside the area mus tbe listed in the outcome.
        assertNotNull(result);
        assertEquals(expectedElement._location, result.location.location);
        assertEquals(new HashSet<>(Arrays.asList(recognizedElementOne, recognizedElementThree)), result.recognizedElements);
    }

    @Test
    public void getIntersectedElementsReturnsNull() {
        // GIVEN recognized elements outside the surface area of the expected text.
        List<RecognizedElement> recognizedElements = Collections.singletonList(recognizedElementTwo);

        MatcherConfiguration config = MatcherConfiguration.CreateDefault();
        LocationMatcher sut = new LocationMatcher(config);

        // WHEN we try find intersecting elements.
        LocationMatch result = sut.getIntersectedElements(recognizedElements, expectedElement);

        // THEN none are expected.
        assertNull(result);
    }
}
