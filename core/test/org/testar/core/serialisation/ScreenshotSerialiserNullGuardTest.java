package org.testar.core.serialisation;

import org.junit.After;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ScreenshotSerialiserNullGuardTest {

    @After
    public void cleanup() throws Exception {
        setStaticField("singletonScreenshotSerialiser", null);
        setStaticField("testSequenceFolder", null);
        setStaticField("scrshotOutputFolder", null);
        setStaticField("alive", false);
        setStaticField("scrshotSavingQueue", new LinkedList<>());
    }

    @Test
    public void exitClearsSingletonWhenSequenceFolderIsMissing() throws Exception {
        Constructor<ScreenshotSerialiser> constructor = ScreenshotSerialiser.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        setStaticField("testSequenceFolder", null);
        setStaticField("singletonScreenshotSerialiser", constructor.newInstance());

        ScreenshotSerialiser.exit();

        assertNull(getStaticField("singletonScreenshotSerialiser"));
    }

    @Test
    public void saveStateshotDoesNotQueueNullCanvas() throws Exception {
        setStaticField("alive", true);
        setStaticField("scrshotOutputFolder", "build/null-screenshot-" + System.nanoTime());
        setStaticField("testSequenceFolder", "sequence");

        ScreenshotSerialiser.saveStateshot("state-id", null);

        assertEquals(0, ScreenshotSerialiser.queueLength());
    }

    @Test
    public void saveActionshotDoesNotReturnPathForNullCanvas() throws Exception {
        setStaticField("alive", true);
        setStaticField("scrshotOutputFolder", "build/null-actionshot-" + System.nanoTime());
        setStaticField("testSequenceFolder", "sequence");

        assertEquals("", ScreenshotSerialiser.saveActionshot("state-id", "action-id", null));
        assertEquals(0, ScreenshotSerialiser.queueLength());
    }

    private Object getStaticField(String fieldName) throws Exception {
        Field field = ScreenshotSerialiser.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(null);
    }

    private void setStaticField(String fieldName, Object value) throws Exception {
        Field field = ScreenshotSerialiser.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }
}
