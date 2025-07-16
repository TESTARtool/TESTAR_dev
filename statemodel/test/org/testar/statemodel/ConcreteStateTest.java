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

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullId() {
        new ConcreteState(null, abstractState);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullAbstractState() {
        new ConcreteState("ConcreteState1", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyId() {
        new ConcreteState("", abstractState);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithBlankId() {
        new ConcreteState("   ", abstractState);
    }

    @Test
    public void testSetNullScreenshot() {
        concreteState.setScreenshot(null);
        assertNotNull(concreteState.getScreenshot());
        assertEquals(0, concreteState.getScreenshot().length);
    }

    @Test
    public void testScreenshotDefensiveCopy() {
        byte[] data = {1, 2, 3};
        concreteState.setScreenshot(data);
        data[0] = 99;
        assertEquals(1, concreteState.getScreenshot()[0]);
        assertNotEquals(99, concreteState.getScreenshot()[0]);

        byte[] retrieved = concreteState.getScreenshot();
        retrieved[1] = 88;
        assertEquals(1, concreteState.getScreenshot()[0]);
        assertNotEquals(88, concreteState.getScreenshot()[1]);
    }

    @Test
    public void testCanBeDelayedIsTrue() {
        assertTrue(concreteState.canBeDelayed());
    }

}
