package org.testar.statemodel;

import org.junit.Before;
import org.junit.Test;
import org.testar.statemodel.exceptions.ActionNotFoundException;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class AbstractStateTest {

    private AbstractAction abstractAction1;
    private AbstractAction abstractAction2;
    private AbstractState abstractState;

    @Before
    public void setUp() {
        abstractAction1 = new AbstractAction("AbstractAction1");
        abstractAction2 = new AbstractAction("AbstractAction2");
        Set<AbstractAction> actions = new HashSet<>();
        actions.add(abstractAction1);
        actions.add(abstractAction2);
        abstractState = new AbstractState("AbstractState1", actions);
    }

    @Test
    public void testAbstractStateConstructor() {
        assertEquals("AbstractState1", abstractState.getStateId());
        assertEquals(2, abstractState.getActions().size());

        assertTrue(abstractState.getUnvisitedActions().contains(abstractAction1));
        assertTrue(abstractState.getUnvisitedActions().contains(abstractAction2));

        assertTrue(abstractState.getVisitedActions().isEmpty());

        assertTrue(abstractState.getConcreteStateIds().isEmpty());

        assertFalse(abstractState.isInitial());
    }

    @Test
    public void testAddConcreteStateId() {
        abstractState.addConcreteStateId("ConcreteState1");
        assertTrue(abstractState.getConcreteStateIds().contains("ConcreteState1"));
    }

    @Test
    public void testAddVisitedAction() {
        abstractState.addVisitedAction(abstractAction1);

        assertFalse(abstractState.getUnvisitedActions().contains(abstractAction1));
        assertTrue(abstractState.getVisitedActions().contains(abstractAction1));

        assertTrue(abstractState.getUnvisitedActions().contains(abstractAction2));
        assertFalse(abstractState.getVisitedActions().contains(abstractAction2));
    }

    @Test
    public void testGetActionByIdSuccess() throws ActionNotFoundException {
        AbstractAction result = abstractState.getAction("AbstractAction1");
        assertEquals(abstractAction1, result);
    }

    @Test(expected = ActionNotFoundException.class)
    public void testGetActionByIdThrowsException() throws ActionNotFoundException {
        abstractState.getAction("non-existent");
    }

    @Test
    public void testSetAndIsInitial() {
        assertFalse(abstractState.isInitial());
        abstractState.setInitial(true);
        assertTrue(abstractState.isInitial());
    }

    @Test
    public void testSetModelIdentifierUpdatesActions() {
        abstractState.setModelIdentifier("model-123");
        assertEquals("model-123", abstractAction1.getModelIdentifier());
        assertEquals("model-123", abstractAction2.getModelIdentifier());
    }

    @Test
    public void testAddNewActionIgnoresDuplicates() {
        abstractState.addNewAction(abstractAction1); // already present
        assertEquals(2, abstractState.getActions().size());
    }

    @Test
    public void testAddNewActionAddsNewAction() {
        AbstractAction newAction = new AbstractAction("AbstractAction3");
        abstractState.addNewAction(newAction);

        assertEquals(3, abstractState.getActions().size());
        assertTrue(abstractState.getActionIds().contains("AbstractAction3"));
        assertTrue(abstractState.getUnvisitedActions().contains(newAction));
    }

    @Test
    public void testCanBeDelayedIsFalse() {
        assertFalse(abstractState.canBeDelayed());
    }
}

