package org.testar.environment;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class EnvironmentTest {

    @Test
    public void testGetDefaultInstanceReturnsUnknown() {
        IEnvironment env = Environment.getInstance();
        assertNotNull(env);
        assertTrue(env instanceof UnknownEnvironment);
        assertNotNull(env.getDisplayScale(0L));
        assertEquals(1.0, env.getDisplayScale(0L), 0.0001);
    }

    @Test
    public void testSetInstance() {
        IEnvironment testEnv = new TestEnvironment();
        Environment.setInstance(testEnv);

        IEnvironment result = Environment.getInstance();
        assertNotNull(result);
        assertSame(testEnv, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNullInstanceThrowsException() {
        Environment.setInstance(null);
    }

}

class TestEnvironment implements IEnvironment {
    @Override
    public double getDisplayScale(long windowHandle) {
        return 0;
    }
}
