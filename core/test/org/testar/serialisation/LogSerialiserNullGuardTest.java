package org.testar.serialisation;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.LinkedList;

public class LogSerialiserNullGuardTest {

    @After
    public void cleanup() throws Exception {
        setStaticField("log", null);
        setStaticField("singletonLogSerialiser", null);
        setStaticField("alive", false);
        setStaticField("logSavingQueue", new LinkedList<>());
    }

    @Test
    public void flush_WhenLogIsNull_DoesNotThrow() throws Exception {
        setStaticField("log", null);

        LogSerialiser.flush();
    }

    @Test
    public void exit_WhenLogIsNull_DoesNotThrowAndClearsSingleton() throws Exception {
        setStaticField("log", null);
        setStaticField("singletonLogSerialiser", newLogSerialiserInstance());

        LogSerialiser.exit();

        Assert.assertNull(getStaticField("singletonLogSerialiser"));
    }

    private LogSerialiser newLogSerialiserInstance() throws Exception {
        Constructor<LogSerialiser> constructor = LogSerialiser.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
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
