package org.testar.statemodel;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.HashSet;

public class AbstractStateTransitionTest {

    private AbstractState sourceState;
    private AbstractState targetState;
    private AbstractAction action;
    private AbstractStateTransition transition;

    @Before
    public void setUp() {
        sourceState = new AbstractState("AbstractStateSource", new HashSet<>());
        targetState = new AbstractState("AbstractStateTarget", new HashSet<>());
        action = new AbstractAction("AbstractAction1");

        transition = new AbstractStateTransition(sourceState, targetState, action);
    }

    @Test
    public void testAbstractStateTransitionConstructor() {
        assertEquals(sourceState, transition.getSourceState());
        assertEquals(targetState, transition.getTargetState());
        assertEquals(action, transition.getAction());
    }

    @Test
    public void testGetSourceStateId() {
        assertEquals("AbstractStateSource", transition.getSourceStateId());
    }

    @Test
    public void testGetTargetStateId() {
        assertEquals("AbstractStateTarget", transition.getTargetStateId());
    }

    @Test
    public void testGetActionId() {
        assertEquals("AbstractAction1", transition.getActionId());
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullSourceState() {
        new AbstractStateTransition(null, targetState, action);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullTargetState() {
        new AbstractStateTransition(sourceState, null, action);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullAction() {
        new AbstractStateTransition(sourceState, targetState, null);
    }

    @Test
    public void testCanBeDelayedIsFalse() {
        assertFalse(transition.canBeDelayed());
    }
}

