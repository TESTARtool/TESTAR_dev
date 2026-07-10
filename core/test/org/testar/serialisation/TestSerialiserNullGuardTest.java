package org.testar.serialisation;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.testar.monkey.alayer.TaggableBase;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;

public class TestSerialiserNullGuardTest {

    @After
    public void cleanup() throws Exception {
        setStaticField("test", null);
        setStaticField("singletonTestSerialiser", null);
        setStaticField("alive", false);
        setStaticField("testSavingQueue", new LinkedList<>());
    }

    @Test
    public void exit_WhenTestStreamIsNull_DoesNotThrowAndClearsSingleton() throws Exception {
        setStaticField("test", null);
        setStaticField("singletonTestSerialiser", newTestSerialiserInstance());

        TestSerialiser.exit();

        Assert.assertNull(getStaticField("singletonTestSerialiser"));
    }

    @Test
    public void writethis_WhenTestStreamIsNull_DoesNotThrow() throws Exception {
        setStaticField("test", null);

        Method writethis = TestSerialiser.class.getDeclaredMethod("writethis", TaggableBase.class);
        writethis.setAccessible(true);
        writethis.invoke(null, new TaggableBase());
    }

    private TestSerialiser newTestSerialiserInstance() throws Exception {
        Constructor<TestSerialiser> constructor = TestSerialiser.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    private Object getStaticField(String fieldName) throws Exception {
        Field field = TestSerialiser.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(null);
    }

    private void setStaticField(String fieldName, Object value) throws Exception {
        Field field = TestSerialiser.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }
}
