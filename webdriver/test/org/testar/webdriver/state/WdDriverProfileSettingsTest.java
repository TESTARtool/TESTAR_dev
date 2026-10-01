package org.testar.webdriver.state;

import org.junit.After;
import org.junit.Test;
import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.config.settings.SettingsDefaults;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class WdDriverProfileSettingsTest {

    @After
    public void resetProfileSettings() {
        WdDriver.chromeUserDataDir = "";
        WdDriver.chromeProfileDirectory = "";
    }

    @Test
    public void loadsAndResetsChromeProfileSettingsForEachWorkspace() {
        Properties configured = new Properties();
        configured.setProperty(ConfigTags.SUTConnectorValue.name(), "https://example.com");
        configured.setProperty(ConfigTags.WebChromeUserDataDir.name(), " C:/profiles/testar ");
        configured.setProperty(ConfigTags.WebChromeProfileDirectory.name(), " Profile 2 ");

        WdDriver.configureFromSettings(new Settings(SettingsDefaults.getSettingsDefaults(), configured));
        assertEquals("C:/profiles/testar", WdDriver.chromeUserDataDir);
        assertEquals("Profile 2", WdDriver.chromeProfileDirectory);

        Properties defaults = new Properties();
        defaults.setProperty(ConfigTags.SUTConnectorValue.name(), "https://example.com");
        WdDriver.configureFromSettings(new Settings(SettingsDefaults.getSettingsDefaults(), defaults));
        assertEquals("", WdDriver.chromeUserDataDir);
        assertEquals("", WdDriver.chromeProfileDirectory);
    }
}
