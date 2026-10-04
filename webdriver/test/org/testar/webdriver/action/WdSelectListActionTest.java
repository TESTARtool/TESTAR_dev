package org.testar.webdriver.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.remote.RemoteWebElement;
import org.testar.core.exceptions.ActionFailedException;
import org.testar.stub.WidgetStub;
import org.testar.webdriver.state.WdDriver;
import org.testar.webdriver.tag.WdTags;

public class WdSelectListActionTest {

    @Test
    public void passesCssAndValueAsArgumentsToThePackagedScript() {
        String selector = "select[name=\"O'Reilly\"]";
        String value = "O'Reilly\\TESTAR";
        WidgetStub widget = new WidgetStub();
        RemoteWebElement element = Mockito.mock(RemoteWebElement.class);
        widget.set(WdTags.WebElementSelenium, element);
        WdSelectListAction action = new WdSelectListAction(selector, value, widget, WdSelectListAction.JsTargetMethod.CSS);

        try (MockedStatic<WdDriver> driver = Mockito.mockStatic(WdDriver.class)) {
            driver.when(() -> WdDriver.executeScript(anyString(), eq(selector), eq(element), eq(value), eq("CSS"))).thenReturn(true);

            action.run(null, null, 0);

            driver.verify(() -> WdDriver.executeScript(anyString(), eq(selector), eq(element), eq(value), eq("CSS")));
        }
    }

    @Test
    public void reportsReturnedScriptFailuresAsActionFailures() {
        WdSelectListAction action = new WdSelectListAction("#cars", "Saab", new WidgetStub(), WdSelectListAction.JsTargetMethod.CSS);
        try (MockedStatic<WdDriver> driver = Mockito.mockStatic(WdDriver.class)) {
            driver.when(() -> WdDriver.executeScript(anyString(), eq("#cars"), isNull(), eq("Saab"), eq("CSS")))
                    .thenReturn("Select option value is missing or ambiguous");

            try {
                action.run(null, null, 0);
                fail("Selection should fail rather than silently changing the value");
            } catch (ActionFailedException exception) {
                assertTrue(exception.getMessage().contains("missing or ambiguous"));
            }
        }
    }

    @Test(expected = ActionFailedException.class)
    public void reportsSkippedScriptExecutionAsAnActionFailure() {
        WdSelectListAction action = new WdSelectListAction("#cars", "Saab", new WidgetStub(), WdSelectListAction.JsTargetMethod.CSS);
        try (MockedStatic<WdDriver> driver = Mockito.mockStatic(WdDriver.class)) {
            action.run(null, null, 0);
        }
    }

    @Test
    public void preservesTheCauseOfWebDriverFailures() {
        WdSelectListAction action = new WdSelectListAction("#cars", "Saab", new WidgetStub(), WdSelectListAction.JsTargetMethod.CSS);
        WebDriverException failure = new WebDriverException("stale element");
        try (MockedStatic<WdDriver> driver = Mockito.mockStatic(WdDriver.class)) {
            driver.when(() -> WdDriver.executeScript(anyString(), eq("#cars"), isNull(), eq("Saab"), eq("CSS"))).thenThrow(failure);

            try {
                action.run(null, null, 0);
                fail("Selection should report the stale element");
            } catch (ActionFailedException exception) {
                assertEquals(failure, exception.getCause());
            }
        }
    }

    @Test
    public void resolvesCssLocatorWhenSeleniumRejectsTheCapturedElementAsStale() {
        WidgetStub widget = new WidgetStub();
        RemoteWebElement element = Mockito.mock(RemoteWebElement.class);
        widget.set(WdTags.WebElementSelenium, element);
        WdSelectListAction action = new WdSelectListAction("#cars", "Saab", widget, WdSelectListAction.JsTargetMethod.CSS);
        try (MockedStatic<WdDriver> driver = Mockito.mockStatic(WdDriver.class)) {
            driver.when(() -> WdDriver.executeScript(anyString(), eq("#cars"), eq(element), eq("Saab"), eq("CSS")))
                    .thenThrow(new StaleElementReferenceException("select replaced"));
            driver.when(() -> WdDriver.executeScript(anyString(), eq("#cars"), isNull(), eq("Saab"), eq("CSS")))
                    .thenReturn(true);

            action.run(null, null, 0);

            driver.verify(() -> WdDriver.executeScript(anyString(), eq("#cars"), eq(element), eq("Saab"), eq("CSS")));
            driver.verify(() -> WdDriver.executeScript(anyString(), eq("#cars"), isNull(), eq("Saab"), eq("CSS")));
        }
    }

    @Test(expected = ActionFailedException.class)
    public void staleElementOnlyTargetFailsWithoutRetryingAnotherField() {
        WidgetStub widget = new WidgetStub();
        RemoteWebElement element = Mockito.mock(RemoteWebElement.class);
        widget.set(WdTags.WebElementSelenium, element);
        WdSelectListAction action = new WdSelectListAction("", "Saab", widget, WdSelectListAction.JsTargetMethod.ELEMENT);
        try (MockedStatic<WdDriver> driver = Mockito.mockStatic(WdDriver.class)) {
            driver.when(() -> WdDriver.executeScript(anyString(), eq(""), eq(element), eq("Saab"), eq("ELEMENT")))
                    .thenThrow(new StaleElementReferenceException("select replaced"));

            try {
                action.run(null, null, 0);
            } finally {
                driver.verify(() -> WdDriver.executeScript(anyString(), eq(""), eq(element), eq("Saab"), eq("ELEMENT")));
                driver.verifyNoMoreInteractions();
            }
        }
    }
}
