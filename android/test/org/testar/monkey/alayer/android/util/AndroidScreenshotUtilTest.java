package org.testar.monkey.alayer.android.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testar.monkey.alayer.AWTCanvas;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.android.AndroidAppiumFramework;
import org.testar.util.ScreenshotUtil;

public class AndroidScreenshotUtilTest {

    @Test
    public void getStateshotSpyMode_NullStateThrows() {
        try {
            AndroidScreenshotUtil.getStateshotSpyMode(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException expected) {
            assertEquals("State cannot be null", expected.getMessage());
        }
    }

    @Test
    public void getStateshotBinary_NullStateThrows() {
        try {
            AndroidScreenshotUtil.getStateshotBinary(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException expected) {
            assertEquals("State cannot be null", expected.getMessage());
        }
    }

    @Test
    public void getActionshot_NullStateThrows() {
        Action action = mock(Action.class);
        try {
            AndroidScreenshotUtil.getActionshot(null, action);
            fail("Expected NullPointerException");
        } catch (NullPointerException expected) {
            assertEquals("State cannot be null", expected.getMessage());
        }
    }

    @Test
    public void getActionshot_NullActionThrows() {
        State state = mock(State.class);
        try {
            AndroidScreenshotUtil.getActionshot(state, null);
            fail("Expected NullPointerException");
        } catch (NullPointerException expected) {
            assertEquals("Action cannot be null", expected.getMessage());
        }
    }

    @Test
    public void getActionshot_FrameworkExceptionReturnsEmptyString() {
        State state = mock(State.class);
        Action action = mock(Action.class);

        try (MockedStatic<AndroidAppiumFramework> framework = Mockito.mockStatic(AndroidAppiumFramework.class)) {
            framework.when(() -> AndroidAppiumFramework.getScreenshotAction(state, action)).thenThrow(new RuntimeException("exception"));

            String result = AndroidScreenshotUtil.getActionshot(state, action);
            assertEquals("", result);
        }
    }

    @Test
    public void getStateshotBinary_FallbackWhenFrameworkThrows() {
        State state = mock(State.class);
        AWTCanvas expected = mock(AWTCanvas.class);

        try (MockedStatic<AndroidAppiumFramework> framework = Mockito.mockStatic(AndroidAppiumFramework.class);
             MockedStatic<ScreenshotUtil> coreUtil = Mockito.mockStatic(ScreenshotUtil.class)) {
            framework.when(() -> AndroidAppiumFramework.getScreenshotBinary(state)).thenThrow(new IOException("exception"));
            coreUtil.when(() -> ScreenshotUtil.getStateshotBinary(state)).thenReturn(expected);

            AWTCanvas result = AndroidScreenshotUtil.getStateshotBinary(state);
            assertSame(expected, result);
        }
    }
}
