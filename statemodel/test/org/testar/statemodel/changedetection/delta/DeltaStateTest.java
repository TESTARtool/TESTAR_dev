package org.testar.statemodel.changedetection.delta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.testar.statemodel.changedetection.delta.DeltaAction.Direction;

public class DeltaStateTest {

    @Test
    public void testDeltaStateConstructor() {
        DeltaAction incomingOne = new DeltaAction("aa11", "click", Direction.INCOMING);
        DeltaAction incomingTwo = new DeltaAction("aa12", "type", Direction.INCOMING);
        DeltaAction outgoing = new DeltaAction("aa2", "click", Direction.OUTGOING);

        DeltaState deltaState = new DeltaState("as1",
                Arrays.asList("cs1", "cs2", "cs3"),
                Arrays.asList(incomingOne, incomingTwo),
                Arrays.asList(outgoing));

        assertEquals(3, deltaState.getConcreteStateIds().size());
        assertEquals(2, deltaState.getIncomingDeltaActions().size());
        assertEquals(1, deltaState.getOutgoingDeltaActions().size());

        assertTrue(deltaState.getIncomingDeltaActions().contains(incomingOne));
        assertTrue(deltaState.getIncomingDeltaActions().contains(incomingTwo));
        assertTrue(deltaState.getOutgoingDeltaActions().contains(outgoing));

        assertNotSame(deltaState.getIncomingDeltaActions(), deltaState.getOutgoingDeltaActions());
        assertNotSame(deltaState.getConcreteStateIds(), deltaState.getOutgoingDeltaActions());
    }

    @Test(expected = NullPointerException.class)
    public void testNullStateId() {
        new DeltaState(null, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    @Test
    public void testNullActionList() {
        DeltaState deltaState = new DeltaState("as1", null, null, null);
        assertTrue(deltaState.getConcreteStateIds().isEmpty());
        assertTrue(deltaState.getIncomingDeltaActions().isEmpty());
        assertTrue(deltaState.getOutgoingDeltaActions().isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyStateId() {
        new DeltaState("   ", Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    @Test
    public void testEmptyActionList() {
        DeltaState deltaState = new DeltaState("as1", Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        assertTrue(deltaState.getConcreteStateIds().isEmpty());
        assertTrue(deltaState.getIncomingDeltaActions().isEmpty());
        assertTrue(deltaState.getOutgoingDeltaActions().isEmpty());
    }

}
