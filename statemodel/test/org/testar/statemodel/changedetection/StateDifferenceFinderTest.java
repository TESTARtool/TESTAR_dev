package org.testar.statemodel.changedetection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.testar.statemodel.changedetection.DeltaAction.Direction;

public class StateDifferenceFinderTest {

    private StateDifferenceFinder finder;
    private ActionDescriptionProvider descriptionProvider;

    @Before
    public void setUp() {
        finder = new StateDifferenceFinder();
        descriptionProvider = id -> "desc-" + id;
    }

    @Test
    public void testFindAddedState() {
        StateSnapshot state = new StateSnapshot(
            "AS1", 
            Arrays.asList("CS1", "CS4"), 
            Arrays.asList("AS1"), 
            Arrays.asList("AS2")
        );

        List<StateSnapshot> oldModelStates = Arrays.asList(state);
        List<StateSnapshot> newModelStates = Arrays.asList(
                state,
                new StateSnapshot(
                    "AS2", 
                    Arrays.asList("CS2", "CS3"), 
                    Arrays.asList("AS3"), 
                    Arrays.asList("AS4")
                )
            );

        List<DeltaState> addedStates = finder.findAddedStates(oldModelStates, newModelStates, descriptionProvider);

        assertEquals(1, addedStates.size());
        DeltaState deltaAddedState = addedStates.get(0);
        assertEquals("AS2", deltaAddedState.getStateId());
        assertEquals(Arrays.asList("CS2", "CS3"), deltaAddedState.getConcreteStateIds());

        assertEquals(1, deltaAddedState.getIncomingDeltaActions().size());
        assertEquals("AS3", deltaAddedState.getIncomingDeltaActions().get(0).getActionId());
        assertEquals(Direction.INCOMING, deltaAddedState.getIncomingDeltaActions().get(0).getDirection());
        assertEquals("desc-AS3", deltaAddedState.getIncomingDeltaActions().get(0).getDescription());

        assertEquals(1, deltaAddedState.getOutgoingDeltaActions().size());
        assertEquals("AS4", deltaAddedState.getOutgoingDeltaActions().get(0).getActionId());
        assertEquals(Direction.OUTGOING, deltaAddedState.getOutgoingDeltaActions().get(0).getDirection());
        assertEquals("desc-AS4", deltaAddedState.getOutgoingDeltaActions().get(0).getDescription());
    }

    @Test
    public void testFindRemovedStates() {
        StateSnapshot state = new StateSnapshot(
            "AS1", 
            Arrays.asList("CS1", "CS4"), 
            Arrays.asList("AS1"), 
            Arrays.asList("AS2")
        );

        List<StateSnapshot> oldModelStates = Arrays.asList(
                state,
                new StateSnapshot("AS2", Arrays.asList("CS2"), Arrays.asList("AS3"), Arrays.asList("AS4")));
        List<StateSnapshot> newModelStates = Collections.singletonList(state);

        List<DeltaState> removedStates = finder.findRemovedStates(oldModelStates, newModelStates, descriptionProvider);

        assertEquals(1, removedStates.size());
        DeltaState deltaRemovedState = removedStates.get(0);
        assertEquals("AS2", deltaRemovedState.getStateId());
        assertTrue(deltaRemovedState.getIncomingDeltaActions().stream().anyMatch(a -> a.getActionId().equals("AS3")));
        assertTrue(deltaRemovedState.getOutgoingDeltaActions().stream().anyMatch(a -> a.getActionId().equals("AS4")));
    }

}
