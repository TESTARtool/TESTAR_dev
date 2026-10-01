package org.testar.webdriver.manager;

import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testar.webdriver.state.WdDriver;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WdChromeManagerOptionsTest {

    @After
    public void resetProfileSettings() {
        WdDriver.chromeUserDataDir = "";
        WdDriver.chromeProfileDirectory = "";
    }

    @Test
    public void includesConfiguredUserDataAndProfileDirectories() {
        WdDriver.chromeUserDataDir = "C:/profiles/testar";
        WdDriver.chromeProfileDirectory = "Profile 2";

        List<String> arguments = arguments(WdChromeManager.createOptions("chrome.exe", "extension"));

        assertTrue(arguments.contains("--user-data-dir=C:/profiles/testar"));
        assertTrue(arguments.contains("--profile-directory=Profile 2"));
    }

    @Test
    public void omitsProfileArgumentsWhenSettingsAreEmpty() {
        List<String> arguments = arguments(WdChromeManager.createOptions("chrome.exe", "extension"));

        assertFalse(arguments.stream().anyMatch(argument -> argument.startsWith("--user-data-dir=")));
        assertFalse(arguments.stream().anyMatch(argument -> argument.startsWith("--profile-directory=")));
    }

    @Test
    public void omitsProfileArgumentsWhenCustomCodeSetsNull() {
        WdDriver.chromeUserDataDir = null;
        WdDriver.chromeProfileDirectory = null;

        List<String> arguments = arguments(WdChromeManager.createOptions("chrome.exe", "extension"));

        assertFalse(arguments.stream().anyMatch(argument -> argument.startsWith("--user-data-dir=")));
        assertFalse(arguments.stream().anyMatch(argument -> argument.startsWith("--profile-directory=")));
    }

    @SuppressWarnings("unchecked")
    private static List<String> arguments(ChromeOptions options) {
        Map<String, Object> chromeOptions = (Map<String, Object>) options.asMap().get("goog:chromeOptions");
        return (List<String>) chromeOptions.get("args");
    }
}
