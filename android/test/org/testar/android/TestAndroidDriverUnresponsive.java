package org.testar.android;

import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;
import org.testar.core.state.State;
import org.testar.android.state.AndroidStateBuilder;
import org.testar.core.state.SUT;
import org.testar.core.tag.Tags;

import java.lang.reflect.Field;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestAndroidDriverUnresponsive {

    @After
    public void cleanup() throws Exception {
        setStaticDriver(null);
        AndroidAppiumFramework.resetDriverUnresponsive();
    }

    @Test
    public void getCurrentPackage_DriverNotResponding() throws Exception {
        AndroidDriver driver = mock(AndroidDriver.class);
        when(driver.getCurrentPackage()).thenThrow(new WebDriverException("timeout"));
        setStaticDriver(driver);

        Assert.assertFalse(AndroidAppiumFramework.isDriverUnresponsive());
        String currentPackage = AndroidAppiumFramework.getCurrentPackage();
        Assert.assertEquals("", currentPackage);
        Assert.assertTrue(AndroidAppiumFramework.isDriverUnresponsive());

        State state = buildStateWithUnresponsiveFlag();
        Assert.assertTrue(state.get(Tags.NotResponding, false));
    }

    @Test
    public void getActivity_DriverNotResponding() throws Exception {
        AndroidDriver driver = mock(AndroidDriver.class);
        when(driver.currentActivity()).thenThrow(new WebDriverException("timeout"));
        setStaticDriver(driver);

        Assert.assertFalse(AndroidAppiumFramework.isDriverUnresponsive());
        String activity = AndroidAppiumFramework.getActivity();
        Assert.assertEquals("", activity);
        Assert.assertTrue(AndroidAppiumFramework.isDriverUnresponsive());

        State state = buildStateWithUnresponsiveFlag();
        Assert.assertTrue(state.get(Tags.NotResponding, false));
    }

    @Test
    public void dumpLogcat_DriverNotResponding()  throws Exception {
        AndroidDriver driver = mock(AndroidDriver.class);
        when(driver.executeScript(eq("mobile: shell"), any())).thenThrow(new WebDriverException("appium timeout"));
        setStaticDriver(driver);

        Assert.assertFalse(AndroidAppiumFramework.isDriverUnresponsive());
        String dumpLogcat = AndroidAppiumFramework.dumpLogcatThreadtimeForPackage("org.testar.app");
        Assert.assertEquals("", dumpLogcat);
        Assert.assertTrue(AndroidAppiumFramework.isDriverUnresponsive());

        State state = buildStateWithUnresponsiveFlag();
        Assert.assertTrue(state.get(Tags.NotResponding, false));
    }

    @Test
    public void getScreenshotState_DriverNotResponding() throws Exception {
        AndroidDriver driver = mock(AndroidDriver.class);
        when(driver.getScreenshotAs(eq(OutputType.BYTES))).thenThrow(new WebDriverException("screenshot timeout"));
        setStaticDriver(driver);

        State state = Mockito.mock(State.class);
        when(state.get(eq(Tags.ConcreteID), Mockito.anyString())).thenReturn("state-id");

        Assert.assertFalse(AndroidAppiumFramework.isDriverUnresponsive());
        try {
            AndroidAppiumFramework.getScreenshotState(state);
            Assert.fail("Expected IOException");
        } catch (IOException expected) {
            // we expect this exception
        }
        Assert.assertTrue(AndroidAppiumFramework.isDriverUnresponsive());

        State notResponding = buildStateWithUnresponsiveFlag();
        Assert.assertTrue(notResponding.get(Tags.NotResponding, false));
    }

    private State buildStateWithUnresponsiveFlag() throws Exception {
        SUT system = Mockito.mock(SUT.class);
        when(system.isRunning()).thenReturn(true);
        AndroidStateBuilder builder = new AndroidStateBuilder(1.0);
        return builder.apply(system);
    }

    private void setStaticDriver(AndroidDriver testDriver) throws Exception {
        Field driver = AndroidAppiumFramework.class.getDeclaredField("driver");
        driver.setAccessible(true);
        driver.set(null, testDriver);
    }

}
