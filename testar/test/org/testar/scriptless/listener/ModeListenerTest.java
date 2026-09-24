package org.testar.scriptless.listener;

import org.junit.Test;
import org.testar.config.TestarMode;
import org.testar.core.devices.KBKeys;
import org.testar.scriptless.RuntimeContext;

import static org.junit.Assert.assertEquals;

public final class ModeListenerTest {

    @Test
    public void shiftDownRequestsQuitWhenKeyboardListenerIsEnabled() {
        RuntimeContext context = new RuntimeContext();
        context.setMode(TestarMode.Spy);
        ModeListener listener = new ModeListener(context, true);

        listener.keyDown(KBKeys.VK_SHIFT);
        listener.keyDown(KBKeys.VK_DOWN);

        assertEquals(TestarMode.Quit, context.mode());
    }

    @Test
    public void shiftDownDoesNotChangeModeWhenKeyboardListenerIsDisabled() {
        RuntimeContext context = new RuntimeContext();
        context.setMode(TestarMode.Spy);
        ModeListener listener = new ModeListener(context, false);

        listener.keyDown(KBKeys.VK_SHIFT);
        listener.keyDown(KBKeys.VK_DOWN);

        assertEquals(TestarMode.Spy, context.mode());
    }
}
