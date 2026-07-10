package org.testar.serialisation;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.LinkedList;

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
    public void exit_WhenTestSequenceFolderIsNull_DoesNotThrowAndClearsSingleton() throws Exception {
        setStaticField("testSequenceFolder", null);
        setStaticField("singletonScreenshotSerialiser", newScreenshotSerialiserInstance());

        ScreenshotSerialiser.exit();

        Assert.assertNull(getStaticField("singletonScreenshotSerialiser"));
    }

    private ScreenshotSerialiser newScreenshotSerialiserInstance() throws Exception {
        Constructor<ScreenshotSerialiser> constructor = ScreenshotSerialiser.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
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
