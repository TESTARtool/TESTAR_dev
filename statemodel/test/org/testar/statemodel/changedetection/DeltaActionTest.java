package org.testar.statemodel.changedetection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.testar.statemodel.changedetection.DeltaAction.Direction;

public class DeltaActionTest {

    @Test
    public void testOutgoingDeltaAction() {
        DeltaAction action = new DeltaAction("aa1", "click", Direction.OUTGOING);
        assertEquals("aa1", action.getActionId());
        assertEquals("click", action.getDescription());
        assertEquals(Direction.OUTGOING, action.getDirection());
    }

    @Test
    public void testIncomingDeltaAction() {
        DeltaAction action = new DeltaAction("aa2", "type", Direction.INCOMING);
        assertEquals("aa2", action.getActionId());
        assertEquals("type", action.getDescription());
        assertEquals(Direction.INCOMING, action.getDirection());
    }

    @Test
    public void testEqualsDeltaAction() {
        DeltaAction a = new DeltaAction("aa1", "click", Direction.INCOMING);
        DeltaAction b = new DeltaAction("aa1", "click", Direction.INCOMING);
        DeltaAction c = new DeltaAction("aa2", "click", Direction.INCOMING);
        DeltaAction d = new DeltaAction("aa1", "click", Direction.OUTGOING);
        DeltaAction e = new DeltaAction("aa1", "tap", Direction.INCOMING);
        assertEquals(a, b);
        assertNotEquals(a, c);
        assertNotEquals(a, d);
        assertNotEquals(a, e);
    }

    @Test(expected = NullPointerException.class)
    public void testNullActionId() {
        new DeltaAction(null, "desc", Direction.INCOMING);
    }

    @Test(expected = NullPointerException.class)
    public void testNullDescription() {
        new DeltaAction("aa1", null, Direction.INCOMING);
    }

    @Test(expected = NullPointerException.class)
    public void testNullDirection() {
        new DeltaAction("aa1", "desc", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyActionId() {
        new DeltaAction("   ", "desc", Direction.INCOMING);
    }

}
