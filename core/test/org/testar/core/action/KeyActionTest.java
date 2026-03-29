package org.testar.core.action;

import static org.junit.Assert.*;

import org.testar.core.devices.KBKeys;
import org.junit.Test;

public class KeyActionTest {

    @Test
    public void testEquals() {
        Action a = new KeyDown(KBKeys.VK_CONTROL);
        assertFalse(a.equals("Not a KeyAction"));
        assertFalse(a.equals(new KeyDown(KBKeys.VK_ALT)));
        assertFalse(a.equals(new KeyUp(KBKeys.VK_CONTROL)));
        assertTrue(a.equals(new KeyDown(KBKeys.VK_CONTROL)));
        a = new KeyUp(KBKeys.VK_CONTROL);
        assertFalse(a.equals("Not a KeyAction"));
        assertFalse(a.equals(new KeyUp(KBKeys.VK_ALT)));
        assertFalse(a.equals(new KeyDown(KBKeys.VK_CONTROL)));
        assertTrue(a.equals(new KeyUp(KBKeys.VK_CONTROL)));
    }

}
