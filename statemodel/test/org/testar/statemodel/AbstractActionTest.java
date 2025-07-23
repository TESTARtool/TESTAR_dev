package org.testar.statemodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class AbstractActionTest {

    private AbstractAction action;

    @Before
    public void setUp() {
        action = new AbstractAction("AbstractAction1");
    }

    @Test
    public void testAbstractActionConstructor() {
        assertNotNull(action.getConcreteActionIds());
        assertTrue(action.getConcreteActionIds().isEmpty());
        assertEquals("AbstractAction1", action.getActionId());
    }

    @Test
    public void testAddConcreteActionId() {
        action.addConcreteActionId("concrete-1");
        Set<String> ids = action.getConcreteActionIds();
        assertEquals(1, ids.size());
        assertTrue(ids.contains("concrete-1"));
    }

    @Test
    public void testAddMultipleConcreteActionIds() {
        action.addConcreteActionId("c1");
        action.addConcreteActionId("c2");
        action.addConcreteActionId("c3");

        Set<String> ids = action.getConcreteActionIds();
        assertEquals(3, ids.size());
        assertTrue(ids.contains("c1"));
        assertTrue(ids.contains("c2"));
        assertTrue(ids.contains("c3"));
    }

    @Test
    public void testAddDuplicateConcreteActionId() {
        action.addConcreteActionId("concrete-1");
        action.addConcreteActionId("concrete-1");
        assertEquals(1, action.getConcreteActionIds().size());
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullActionId() {
        new AbstractAction(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyActionId() {
        new AbstractAction("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithBlankActionId() {
        new AbstractAction("    ");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetConcreteActionIdsIsUnmodifiable() {
        Set<String> ids = action.getConcreteActionIds();
        ids.add("concrete-action-should-fail");
    }

    @Test(expected = NullPointerException.class)
    public void testAddConcreteActionNullId() {
        action.addConcreteActionId(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddConcreteActionEmptyId() {
        action.addConcreteActionId("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddConcreteActionBlankId() {
        action.addConcreteActionId("    ");
    }

    @Test
    public void testCanBeDelayedIsFalse() {
        assertFalse(action.canBeDelayed());
    }

}
