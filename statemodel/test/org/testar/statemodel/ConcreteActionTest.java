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

    @Test
    public void testEmptyScreenshot() {
        assertNotNull(concreteAction.getScreenshot());
    }

    @Test
    public void testSetAndGetScreenshot() {
        byte[] screenshotData = {1, 2, 3};
        concreteAction.setScreenshot(screenshotData);
        assertArrayEquals(screenshotData, concreteAction.getScreenshot());
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

    @Test
    public void testSetNullScreenshot() {
        concreteAction.setScreenshot(null);
        assertNotNull(concreteAction.getScreenshot());
        assertEquals(0, concreteAction.getScreenshot().length);
    }

    @Test
    public void testScreenshotDefensiveCopy() {
        byte[] data = {1, 2, 3};
        concreteAction.setScreenshot(data);
        data[0] = 99;
        assertEquals(1, concreteAction.getScreenshot()[0]);
        assertNotEquals(99, concreteAction.getScreenshot()[0]);

        byte[] retrieved = concreteAction.getScreenshot();
        retrieved[1] = 88;
        assertEquals(1, concreteAction.getScreenshot()[0]);
        assertNotEquals(88, concreteAction.getScreenshot()[1]);
    }

}
