package org.testar.core.action;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.testar.core.devices.KBKeys;
import org.junit.Before;
import org.junit.Test;

public class StdActionCompilerTest {

    private StdActionCompiler compiler;

    @Before
    public void setUp() {
        compiler = new StdActionCompiler();
    }

    @Test
    public void testHitShortcutKey() {
        List<KBKeys> keys = new ArrayList<>();
        keys.add(KBKeys.VK_CONTROL);
        keys.add(KBKeys.VK_SHIFT);
        keys.add(KBKeys.VK_F);
        Action action = compiler.hitShortcutKey(keys);
        // TODO: instead of looking at a string, inspect the compound action's component actions
        String exp =
                "(VK_CONTROL)(VK_SHIFT)(VK_F)" // press
                + "(VK_F)(VK_SHIFT)(VK_CONTROL)"; // release
        assertEquals("All keys should be pressed first, then released in opposite order",
                exp, action.toParametersString());
    }

}
