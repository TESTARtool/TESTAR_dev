package org.testar.statemodel.changedetection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;
import org.testar.statemodel.changedetection.DeltaAction.Direction;

public class ChangeDetectionResultTest {

    @Test
    public void testChangedResultsOfAddedRemovedStates() {
        DeltaState addedState = new DeltaState("AS1", Collections.singletonList("CS1"),
                Collections.singletonList(new DeltaAction("AA1", "desc-AA1", Direction.INCOMING)),
                Collections.emptyList());

        DeltaState removedState = new DeltaState("AS2", Collections.singletonList("CS2"),
                Collections.singletonList(new DeltaAction("AA2", "desc-AA2", Direction.INCOMING)),
                Collections.emptyList());

        ChangeDetectionResult result = new ChangeDetectionResult("old", "new",
                Collections.singletonList(addedState),
                Collections.singletonList(removedState),
                Collections.emptyMap(),
                Collections.emptyMap());

        assertEquals("old", result.getOldModelIdentifier());
        assertEquals("new", result.getNewModelIdentifier());
        assertEquals(1, result.getAddedStates().size());
        assertEquals(1, result.getRemovedStates().size());
        assertTrue(result.hasDifferences());
    }

    @Test
    public void testNoDifferencesDetected() {
        ChangeDetectionResult result = new ChangeDetectionResult("old", "new",
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyMap(),
                Collections.emptyMap());
        assertFalse(result.hasDifferences());
    }

    @Test(expected = NullPointerException.class)
    public void testNullOldModelIdentifier() {
        new ChangeDetectionResult(null, "new", Collections.emptyList(), Collections.emptyList(), Collections.emptyMap(), Collections.emptyMap());
    }

    @Test(expected = NullPointerException.class)
    public void testNullNewModelIdentifier() {
        new ChangeDetectionResult("old", null, Collections.emptyList(), Collections.emptyList(), Collections.emptyMap(), Collections.emptyMap());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyOldModelIdentifier() {
        new ChangeDetectionResult(" ", "new", Collections.emptyList(), Collections.emptyList(), Collections.emptyMap(), Collections.emptyMap());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyNewModelIdentifier() {
        new ChangeDetectionResult("old", " ", Collections.emptyList(), Collections.emptyList(), Collections.emptyMap(), Collections.emptyMap());
    }

}
