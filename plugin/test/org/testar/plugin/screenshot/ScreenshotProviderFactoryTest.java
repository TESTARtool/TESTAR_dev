package org.testar.monkey.screenshot;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testar.android.util.AndroidScreenshotUtil;
import org.testar.monkey.alayer.AWTCanvas;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.webdriver.util.WdScreenshotUtil;
import org.testar.plugin.NativeLinker;
import org.testar.plugin.OperatingSystems;
import org.testar.plugin.screenshot.ScreenshotProvider;
import org.testar.plugin.screenshot.ScreenshotProviderFactory;
import org.testar.util.ScreenshotUtil;

public class ScreenshotProviderFactoryTest {

    @Test
    public void webdriver_provider() {
        State state = mock(State.class);
        Action action = mock(Action.class);
        AWTCanvas expected_stateshot = mock(AWTCanvas.class);
        String expected_actionshot = "webdriver-action-path";

        try (MockedStatic<NativeLinker> nativeLinker = Mockito.mockStatic(NativeLinker.class);
             MockedStatic<WdScreenshotUtil> wd = Mockito.mockStatic(WdScreenshotUtil.class)) {

            nativeLinker.when(NativeLinker::getPLATFORM_OS).thenReturn(EnumSet.of(OperatingSystems.WEBDRIVER));
            wd.when(() -> WdScreenshotUtil.getStateshotBinary(state)).thenReturn(expected_stateshot);
            wd.when(() -> WdScreenshotUtil.getActionshot(state, action)).thenReturn(expected_actionshot);

            ScreenshotProvider provider = ScreenshotProviderFactory.current();
            assertSame(expected_stateshot, provider.getStateshotBinary(state));
            assertSame(expected_actionshot, provider.getActionshot(state, action));
        }
    }

    @Test
    public void android_provider() {
        State state = mock(State.class);
        Action action = mock(Action.class);
        AWTCanvas expected_stateshot = mock(AWTCanvas.class);
        String expected_actionshot = "android-action-path";

        try (MockedStatic<NativeLinker> nativeLinker = Mockito.mockStatic(NativeLinker.class);
             MockedStatic<AndroidScreenshotUtil> android = Mockito.mockStatic(AndroidScreenshotUtil.class)) {

            nativeLinker.when(NativeLinker::getPLATFORM_OS).thenReturn(EnumSet.of(OperatingSystems.ANDROID));
            android.when(() -> AndroidScreenshotUtil.getStateshotBinary(state)).thenReturn(expected_stateshot);
            android.when(() -> AndroidScreenshotUtil.getActionshot(state, action)).thenReturn(expected_actionshot);

            ScreenshotProvider provider = ScreenshotProviderFactory.current();
            assertSame(expected_stateshot, provider.getStateshotBinary(state));
            assertSame(expected_actionshot, provider.getActionshot(state, action));
        }
    }

    @Test
    public void default_provider() {
        State state = mock(State.class);
        Action action = mock(Action.class);
        AWTCanvas expected_stateshot = mock(AWTCanvas.class);
        String expected_actionshot = "default-action-path";

        try (MockedStatic<NativeLinker> nativeLinker = Mockito.mockStatic(NativeLinker.class);
             MockedStatic<ScreenshotUtil> defaultUtil = Mockito.mockStatic(ScreenshotUtil.class)) {

            nativeLinker.when(NativeLinker::getPLATFORM_OS).thenReturn(EnumSet.of(OperatingSystems.WINDOWS));
            defaultUtil.when(() -> ScreenshotUtil.getStateshotBinary(state)).thenReturn(expected_stateshot);
            defaultUtil.when(() -> ScreenshotUtil.getActionshot(state, action)).thenReturn(expected_actionshot);

            ScreenshotProvider provider = ScreenshotProviderFactory.current();
            assertSame(expected_stateshot, provider.getStateshotBinary(state));
            assertSame(expected_actionshot, provider.getActionshot(state, action));
        }
    }

    @Test
    public void prioritization_provider() {
        State state = mock(State.class);
        AWTCanvas expected = mock(AWTCanvas.class);

        try (MockedStatic<NativeLinker> nativeLinker = Mockito.mockStatic(NativeLinker.class);
             MockedStatic<WdScreenshotUtil> wd = Mockito.mockStatic(WdScreenshotUtil.class)) {

            nativeLinker.when(NativeLinker::getPLATFORM_OS).thenReturn(EnumSet.of(OperatingSystems.WEBDRIVER, OperatingSystems.ANDROID));
            wd.when(() -> WdScreenshotUtil.getStateshotBinary(state)).thenReturn(expected);

            ScreenshotProvider provider = ScreenshotProviderFactory.current();
            assertSame(expected, provider.getStateshotBinary(state));
        }
    }

    @Test
    public void default_provider_when_platform_is_null() {
        State state = mock(State.class);
        Action action = mock(Action.class);
        AWTCanvas expected_stateshot = mock(AWTCanvas.class);
        String expected_actionshot = "default-action-path";

        try (MockedStatic<NativeLinker> nativeLinker = Mockito.mockStatic(NativeLinker.class);
             MockedStatic<ScreenshotUtil> defaultUtil = Mockito.mockStatic(ScreenshotUtil.class)) {

            nativeLinker.when(NativeLinker::getPLATFORM_OS).thenReturn(null);
            defaultUtil.when(() -> ScreenshotUtil.getStateshotBinary(state)).thenReturn(expected_stateshot);
            defaultUtil.when(() -> ScreenshotUtil.getActionshot(state, action)).thenReturn(expected_actionshot);

            ScreenshotProvider provider = ScreenshotProviderFactory.current();
            assertSame(expected_stateshot, provider.getStateshotBinary(state));
            assertSame(expected_actionshot, provider.getActionshot(state, action));
        }
    }

    @Test
    public void provider_stateshot_null_state_throws() {
        List<Set<OperatingSystems>> platforms = Arrays.asList(
                EnumSet.of(OperatingSystems.WINDOWS),
                EnumSet.of(OperatingSystems.WEBDRIVER),
                EnumSet.of(OperatingSystems.ANDROID)
        );

        for (Set<OperatingSystems> platform : platforms) {
            try (MockedStatic<NativeLinker> nativeLinker = Mockito.mockStatic(NativeLinker.class)) {
                nativeLinker.when(NativeLinker::getPLATFORM_OS).thenReturn(platform);
                ScreenshotProvider provider = ScreenshotProviderFactory.current();
                try {
                    provider.getStateshotBinary(null);
                    fail("Expected NullPointerException for platform " + platform);
                } catch (NullPointerException expected) {
                    assertEquals("State cannot be null", expected.getMessage());
                }
            }
        }
    }

    @Test
    public void provider_actionshot_null_state_throws() {
        Action action = mock(Action.class);

        List<Set<OperatingSystems>> platforms = Arrays.asList(
                EnumSet.of(OperatingSystems.WINDOWS),
                EnumSet.of(OperatingSystems.WEBDRIVER),
                EnumSet.of(OperatingSystems.ANDROID)
        );

        for (Set<OperatingSystems> platform : platforms) {
            try (MockedStatic<NativeLinker> nativeLinker = Mockito.mockStatic(NativeLinker.class)) {
                nativeLinker.when(NativeLinker::getPLATFORM_OS).thenReturn(platform);
                ScreenshotProvider provider = ScreenshotProviderFactory.current();
                try {
                    provider.getActionshot(null, action);
                    fail("Expected NullPointerException for platform " + platform);
                } catch (NullPointerException expected) {
                    assertEquals("State cannot be null", expected.getMessage());
                }
            }
        }
    }

    @Test
    public void provider_actionshot_null_action_throws() {
        State state = mock(State.class);

        List<Set<OperatingSystems>> platforms = Arrays.asList(
                EnumSet.of(OperatingSystems.WINDOWS),
                EnumSet.of(OperatingSystems.WEBDRIVER),
                EnumSet.of(OperatingSystems.ANDROID)
        );

        for (Set<OperatingSystems> platform : platforms) {
            try (MockedStatic<NativeLinker> nativeLinker = Mockito.mockStatic(NativeLinker.class)) {
                nativeLinker.when(NativeLinker::getPLATFORM_OS).thenReturn(platform);
                ScreenshotProvider provider = ScreenshotProviderFactory.current();
                try {
                    provider.getActionshot(state, null);
                    fail("Expected NullPointerException for platform " + platform);
                } catch (NullPointerException expected) {
                    assertEquals("Action cannot be null", expected.getMessage());
                }
            }
        }
    }
}
