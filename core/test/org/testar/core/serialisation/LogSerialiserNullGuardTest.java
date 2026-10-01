package org.testar.core.serialisation;

import org.junit.After;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.LinkedList;

import static org.junit.Assert.assertNull;

public class LogSerialiserNullGuardTest {

    @After
    public void cleanup() throws Exception {
        setStaticField("log", null);
        setStaticField("singletonLogSerialiser", null);
        setStaticField("alive", false);
        setStaticField("logSavingQueue", new LinkedList<>());
    }

    @Test
    public void flushIgnoresMissingLogStream() throws Exception {
        setStaticField("log", null);

        LogSerialiser.flush();
    }

    @Test
    public void exitClearsSingletonWhenLogStreamIsMissing() throws Exception {
        Constructor<LogSerialiser> constructor = LogSerialiser.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        setStaticField("log", null);
        setStaticField("singletonLogSerialiser", constructor.newInstance());

        LogSerialiser.exit();

        assertNull(getStaticField("singletonLogSerialiser"));
    }

    private Object getStaticField(String fieldName) throws Exception {
        Field field = LogSerialiser.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(null);
    }

    private void setStaticField(String fieldName, Object value) throws Exception {
        Field field = LogSerialiser.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }
}
