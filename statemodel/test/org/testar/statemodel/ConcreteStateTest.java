package org.testar.statemodel;

import org.junit.Before;
import org.junit.Test;
import java.util.Collections;
import static org.junit.Assert.*;

public class ConcreteStateTest {

    private ConcreteState concreteState;
    private AbstractState abstractState;

    @Before
    public void setUp() {
        abstractState = new AbstractState("AbstractState1", Collections.emptySet());
        concreteState = new ConcreteState("ConcreteState1", abstractState);
    }

    @Test
    public void testConcreteStateConstructor() {
        assertEquals("ConcreteState1", concreteState.getId());
        assertSame(abstractState, concreteState.getAbstractState());
        assertEquals(concreteState, concreteState.getRootWidget());
    }

    @Test
    public void testEmptyScreenshot() {
        assertNotNull(concreteState.getScreenshot());
    }

    @Test
    public void testSetAndGetScreenshot() {
        byte[] screenshotData = {1, 2, 3};
        concreteState.setScreenshot(screenshotData);
        assertArrayEquals(screenshotData, concreteState.getScreenshot());
    }

    @Test
    public void testCanBeDelayedIsTrue() {
        assertTrue(concreteState.canBeDelayed());
    }

}
