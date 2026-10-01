package org.testar.android;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.appmanagement.ApplicationState;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testar.core.state.Widget;
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

    @Test
    public void pageSourceFailurePreservesFeedbackInNotRespondingState() throws Exception {
        AndroidDriver driver = mock(AndroidDriver.class);
        when(driver.getPageSource()).thenThrow(new WebDriverException("page source timeout"));
        when(driver.currentActivity()).thenReturn("TestActivity");
        setStaticDriver(driver);

        SUT system = mock(SUT.class);
        when(system.isRunning()).thenReturn(true);
        when(system.get(Tags.PID, (long) -1)).thenReturn((long) -1);

        State state = new AndroidStateBuilder(1.0).apply(system);

        Assert.assertTrue(state.get(Tags.NotResponding, false));
        Assert.assertTrue(state.get(Tags.StateFeedback, "").contains("page source timeout"));
        Assert.assertFalse(AndroidAppiumFramework.isDriverUnresponsive());
    }

    @Test
    public void alreadyUnresponsiveDriverDoesNotQuerySystemAgain() throws Exception {
        setStaticDriver(null);
        AndroidAppiumFramework.markDriverUnresponsive(new IllegalStateException("driver lost"));

        SUT system = mock(SUT.class);
        State state = new AndroidStateBuilder(1.0).apply(system);

        Assert.assertTrue(state.get(Tags.NotResponding, false));
        Assert.assertFalse(AndroidAppiumFramework.isDriverUnresponsive());
        Mockito.verify(system, Mockito.never()).isRunning();
    }

    @Test
    public void missingDriverReturnsPageSourceFeedback() throws Exception {
        setStaticDriver(null);

        AndroidPageSourceResult result = AndroidAppiumFramework.getAndroidPageSource();

        Assert.assertNull(result.getDocument());
        Assert.assertTrue(result.getFeedback().contains("Android driver is null"));
        Assert.assertTrue(AndroidAppiumFramework.isDriverUnresponsive());
    }

    @Test
    public void missingDriverReturnsEmptyActivityAndPackage() throws Exception {
        setStaticDriver(null);

        Assert.assertEquals("", AndroidAppiumFramework.getActivity());
        Assert.assertEquals("", AndroidAppiumFramework.getCurrentPackage());
        Assert.assertEquals(ApplicationState.NOT_RUNNING, AndroidAppiumFramework.getStatus("test.app"));
        Assert.assertTrue(AndroidAppiumFramework.isDriverUnresponsive());
    }

    @Test
    public void missingDriverFailsScreenshotWithIoException() throws Exception {
        setStaticDriver(null);

        try {
            AndroidAppiumFramework.getScreenshotState(mock(State.class));
            Assert.fail("Expected IOException");
        } catch (IOException expected) {
            Assert.assertTrue(expected.getMessage().contains("Android driver is null"));
        }
        Assert.assertTrue(AndroidAppiumFramework.isDriverUnresponsive());
    }

    @Test
    public void missingDriverFailsElementResolutionExplicitly() throws Exception {
        setStaticDriver(null);

        try {
            AndroidAppiumFramework.resolveElementByIdOrXPath(mock(Widget.class));
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertTrue(expected.getMessage().contains("Android driver is null"));
        }
        Assert.assertTrue(AndroidAppiumFramework.isDriverUnresponsive());
    }

    @Test
    public void missingDriverDoesNotSilentlyExecuteActions() throws Exception {
        setStaticDriver(null);

        try {
            AndroidAppiumFramework.clickBackButton();
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException expected) {
            Assert.assertTrue(expected.getMessage().contains("clickBackButton"));
        }
        Assert.assertTrue(AndroidAppiumFramework.isDriverUnresponsive());
    }

    @Test
    public void missingDriverReturnsEmptyReadOnlyResults() throws Exception {
        setStaticDriver(null);

        Assert.assertTrue(AndroidAppiumFramework.findElements(By.id("widget")).isEmpty());
        Assert.assertTrue(AndroidAppiumFramework.getWindowHandles().isEmpty());
        Assert.assertEquals("", AndroidAppiumFramework.getTitleOfCurrentPage());
        Assert.assertTrue(AndroidAppiumFramework.isDriverUnresponsive());
    }

    @Test
    public void invalidAppiumUrlFailsDuringInitialization() {
        String previousUrl = AndroidAppiumFramework.androidAppiumURL;
        AndroidAppiumFramework.androidAppiumURL = "not-a-url";
        try {
            new AndroidAppiumFramework(new DesiredCapabilities());
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertTrue(expected.getMessage().contains("Invalid Android Appium URL"));
            Assert.assertTrue(AndroidAppiumFramework.isDriverUnresponsive());
        } finally {
            AndroidAppiumFramework.androidAppiumURL = previousUrl;
        }
    }

    @Test
    public void initializationFailureClosesPartiallyCreatedDriver() {
        String previousUrl = AndroidAppiumFramework.androidAppiumURL;
        AndroidAppiumFramework.androidAppiumURL = "http://127.0.0.1:4723/wd/hub";
        try (MockedConstruction<AndroidDriver> construction = Mockito.mockConstruction(
                AndroidDriver.class,
                (driver, context) -> when(driver.executeScript(eq("mobile: shell"), any()))
                        .thenThrow(new WebDriverException("show touches failed")))) {
            try {
                new AndroidAppiumFramework(new DesiredCapabilities());
                Assert.fail("Expected WebDriverException");
            } catch (WebDriverException expected) {
                Assert.assertTrue(expected.getMessage().contains("show touches failed"));
            }

            AndroidDriver createdDriver = construction.constructed().get(0);
            Mockito.verify(createdDriver).quit();
            Assert.assertNull(AndroidAppiumFramework.getDriver());
            Assert.assertTrue(AndroidAppiumFramework.isDriverUnresponsive());
        } finally {
            AndroidAppiumFramework.androidAppiumURL = previousUrl;
        }
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
