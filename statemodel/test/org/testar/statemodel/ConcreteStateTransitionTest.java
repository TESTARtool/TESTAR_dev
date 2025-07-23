package org.testar.statemodel;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.HashSet;

public class ConcreteStateTransitionTest {

    private ConcreteState sourceState;
    private ConcreteState targetState;
    private ConcreteAction action;
    private ConcreteStateTransition transition;

    @Before
    public void setUp() {
        sourceState = new ConcreteState("ConcreteStateSource", new AbstractState("AbstractStateSource", new HashSet<>()));
        targetState = new ConcreteState("ConcreteStateTarget", new AbstractState("AbstractStateTarget", new HashSet<>()));
        action = new ConcreteAction("ConcreteAction1", new AbstractAction("AbstractAction1"));
        transition = new ConcreteStateTransition(sourceState, targetState, action);
    }

    @Test
    public void testConstructorInitializesFieldsCorrectly() {
        assertEquals(sourceState, transition.getSourceState());
        assertEquals(targetState, transition.getTargetState());
        assertEquals(action, transition.getAction());
    }

    @Test
    public void testGetSourceStateId() {
        assertEquals("ConcreteStateSource", transition.getSourceStateId());
    }

    @Test
    public void testGetTargetStateId() {
        assertEquals("ConcreteStateTarget", transition.getTargetStateId());
    }

    @Test
    public void testGetActionId() {
        assertEquals("ConcreteAction1", transition.getActionId());
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullSourceState() {
        new ConcreteStateTransition(null, targetState, action);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullTargetState() {
        new ConcreteStateTransition(sourceState, null, action);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullAction() {
        new ConcreteStateTransition(sourceState, targetState, null);
    }

    @Test
    public void testCanBeDelayedIsTrue() {
        assertTrue(transition.canBeDelayed());
    }
}
