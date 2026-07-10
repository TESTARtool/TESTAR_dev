package org.testar.monkey.alayer.android;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import io.appium.java_client.appmanagement.ApplicationState;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.android.enums.AndroidTags;

import java.io.IOException;
import java.lang.reflect.Field;

public class TestAndroidDriverNullGuard {

    @After
    public void cleanup() throws Exception {
        setStaticDriver(null);
        AndroidAppiumFramework.resetDriverUnresponsive();
    }

    @Test
    public void getActivity_WhenDriverIsNull_ReturnsEmptyAndMarksUnresponsive() throws Exception {
        setStaticDriver(null);

        Assert.assertFalse(AndroidAppiumFramework.isDriverUnresponsive());
        Assert.assertEquals("", AndroidAppiumFramework.getActivity());
        Assert.assertTrue(AndroidAppiumFramework.isDriverUnresponsive());

        State state = buildStateWithUnresponsiveFlag();
        Assert.assertTrue(state.get(Tags.NotResponding, false));
    }

    @Test
    public void getAndroidPageSource_WhenDriverIsNull_ReturnsFeedbackAndMarksUnresponsive() throws Exception {
        setStaticDriver(null);

        Assert.assertFalse(AndroidAppiumFramework.isDriverUnresponsive());
        AndroidPageSourceResult result = AndroidAppiumFramework.getAndroidPageSource();

        Assert.assertFalse(result.hasDocument());
        Assert.assertNull(result.getDocument());
        Assert.assertTrue(result.getFeedback().contains("Android driver is null"));
        Assert.assertTrue(AndroidAppiumFramework.isDriverUnresponsive());

        State state = buildStateWithUnresponsiveFlag();
        Assert.assertTrue(state.get(Tags.NotResponding, false));
    }

    @Test
    public void getCurrentPackage_WhenDriverIsNull_ReturnsEmptyAndMarksUnresponsive() throws Exception {
        setStaticDriver(null);

        Assert.assertFalse(AndroidAppiumFramework.isDriverUnresponsive());
        Assert.assertEquals("", AndroidAppiumFramework.getCurrentPackage());
        Assert.assertTrue(AndroidAppiumFramework.isDriverUnresponsive());
    }

    @Test
    public void getScreenshotState_WhenDriverIsNull_ThrowsIoExceptionAndMarksUnresponsive() throws Exception {
        setStaticDriver(null);

        State state = Mockito.mock(State.class);

        Assert.assertFalse(AndroidAppiumFramework.isDriverUnresponsive());
        try {
            AndroidAppiumFramework.getScreenshotState(state);
            Assert.fail("Expected IOException");
        } catch (IOException expected) {
            Assert.assertTrue(expected.getMessage().contains("driver is null"));
        }
        Assert.assertTrue(AndroidAppiumFramework.isDriverUnresponsive());
    }

    @Test
    public void getStatus_WhenDriverIsNull_ReturnsNotRunningAndMarksUnresponsive() throws Exception {
        setStaticDriver(null);

        Assert.assertFalse(AndroidAppiumFramework.isDriverUnresponsive());
        Assert.assertEquals(ApplicationState.NOT_RUNNING, AndroidAppiumFramework.getStatus("com.testar.app"));
        Assert.assertTrue(AndroidAppiumFramework.isDriverUnresponsive());
    }

    @Test
    public void resolveElementByIdOrXPath_WhenDriverIsNull_ThrowsControlledExceptionAndMarksUnresponsive() throws Exception {
        setStaticDriver(null);

        Widget widget = Mockito.mock(Widget.class);
        Mockito.when(widget.get(AndroidTags.AndroidXpath)).thenReturn("//android.widget.TextView[1]");

        Assert.assertFalse(AndroidAppiumFramework.isDriverUnresponsive());
        try {
            AndroidAppiumFramework.resolveElementByIdOrXPath("some-id", widget);
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertTrue(expected.getMessage().contains("Android driver is null"));
        }
        Assert.assertTrue(AndroidAppiumFramework.isDriverUnresponsive());
    }

    private State buildStateWithUnresponsiveFlag() throws Exception {
        SUT system = Mockito.mock(SUT.class);
        Mockito.when(system.isRunning()).thenReturn(true);
        AndroidStateBuilder builder = new AndroidStateBuilder(1.0);
        return builder.apply(system);
    }

    private void setStaticDriver(Object testDriver) throws Exception {
        Field driver = AndroidAppiumFramework.class.getDeclaredField("driver");
        driver.setAccessible(true);
        driver.set(null, testDriver);
    }
}
