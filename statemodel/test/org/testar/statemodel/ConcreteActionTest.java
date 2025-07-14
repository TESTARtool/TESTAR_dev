package org.testar.statemodel;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConcreteActionTest {

    private ConcreteAction concreteAction;
    private AbstractAction abstractAction;

    @Before
    public void setUp() {
        abstractAction = new AbstractAction("abstract-A1");
        concreteAction = new ConcreteAction("concrete-C1", abstractAction);
    }

    @Test
    public void testConcreteActionConstructor() {
        assertEquals("concrete-C1", concreteAction.getActionId());
        assertEquals(abstractAction, concreteAction.getAbstractAction());
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullActionId() {
        new ConcreteAction(null, abstractAction);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullAbstractAction() {
        new ConcreteAction("concrete-C2", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyActionId() {
        new ConcreteAction("", abstractAction);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithBlankActionId() {
        new ConcreteAction("   ", abstractAction);
    }

}
