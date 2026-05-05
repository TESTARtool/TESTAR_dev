package org.testar.android;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.config.settings.SettingsDefaults;

public class AndroidCapabilitiesFactoryTest {

    private static final String DEFAULT_URL = "http://127.0.0.1:4723/wd/hub";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void buildsCapabilitiesForInstalledApp() {
        Settings settings = defaultSettings();
        settings.set(ConfigTags.AppiumIsApkInstalled, true);
        settings.set(ConfigTags.AppiumAppPackage, "com.example.app");
        settings.set(ConfigTags.AppiumAppActivity, "com.example.app.MainActivity");

        AndroidCapabilitiesFactory factory = new AndroidCapabilitiesFactory(DEFAULT_URL);

        AndroidCapabilitiesFactory.Result result = factory.fromSettings(settings);
        DesiredCapabilities caps = result.getCapabilities();

        assertEquals(Platform.ANDROID, caps.getCapability("platformName"));
        assertEquals("com.example.app", caps.getCapability("appium:appPackage"));
        assertEquals("com.example.app.MainActivity", caps.getCapability("appium:appActivity"));
        assertEquals(DEFAULT_URL, result.getAppiumServerUrl());
    }

    @Test
    public void buildsCapabilitiesForDockerEmulator() {
        Settings settings = defaultSettings();
        settings.set(ConfigTags.AppiumIsApkInstalled, false);
        settings.set(ConfigTags.AppiumApp, "http://somewhere/sample.apk");
        settings.set(ConfigTags.AppiumIsEmulatorDocker, true);
        settings.set(ConfigTags.AppiumIpAddress, "10.1.2.34");

        AndroidCapabilitiesFactory factory = new AndroidCapabilitiesFactory(DEFAULT_URL);

        AndroidCapabilitiesFactory.Result result = factory.fromSettings(settings);
        DesiredCapabilities caps = result.getCapabilities();

        assertEquals(Platform.ANDROID, caps.getCapability("platformName"));
        assertEquals("http://somewhere/sample.apk", caps.getCapability("appium:app"));
        assertEquals("http://10.1.2.34:4723/wd/hub", result.getAppiumServerUrl());
    }

    @Test
    public void apkInstalledWithoutAppPackage_throwsIllegalArgumentException() {
        Settings settings = defaultSettings();
        settings.set(ConfigTags.AppiumIsApkInstalled, true);
        settings.set(ConfigTags.AppiumAppActivity, "com.example.app.MainActivity");

        AndroidCapabilitiesFactory factory = new AndroidCapabilitiesFactory(DEFAULT_URL);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("When AppiumIsApkInstalled=true, 'AppiumAppPackage' is required.");

        factory.fromSettings(settings);
    }

    @Test
    public void apkInstalledWithEmptyAppPackage_throwsIllegalArgumentException() {
        Settings settings = defaultSettings();
        settings.set(ConfigTags.AppiumIsApkInstalled, true);
        settings.set(ConfigTags.AppiumAppPackage, "");
        settings.set(ConfigTags.AppiumAppActivity, "com.example.app.MainActivity");

        AndroidCapabilitiesFactory factory = new AndroidCapabilitiesFactory(DEFAULT_URL);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("When AppiumIsApkInstalled=true, 'AppiumAppPackage' is required.");

        factory.fromSettings(settings);
    }

    @Test
    public void apkInstalledWithoutAppActivity_throwsIllegalArgumentException() {
        Settings settings = defaultSettings();
        settings.set(ConfigTags.AppiumIsApkInstalled, true);
        settings.set(ConfigTags.AppiumAppPackage, "com.example.app");

        AndroidCapabilitiesFactory factory = new AndroidCapabilitiesFactory(DEFAULT_URL);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("When AppiumIsApkInstalled=true, 'AppiumAppActivity' is required");

        factory.fromSettings(settings);
    }

    @Test
    public void apkInstalledWithEmptyAppActivity_throwsIllegalArgumentException() {
        Settings settings = defaultSettings();
        settings.set(ConfigTags.AppiumIsApkInstalled, true);
        settings.set(ConfigTags.AppiumAppPackage, "com.example.app");
        settings.set(ConfigTags.AppiumAppActivity, "");

        AndroidCapabilitiesFactory factory = new AndroidCapabilitiesFactory(DEFAULT_URL);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("When AppiumIsApkInstalled=true, 'AppiumAppActivity' is required");

        factory.fromSettings(settings);
    }

    @Test
    public void apkNotInstalledAndAppMissing_throwsIllegalArgumentException() {
        Settings settings = defaultSettings();
        settings.set(ConfigTags.AppiumIsApkInstalled, false);

        AndroidCapabilitiesFactory factory = new AndroidCapabilitiesFactory(DEFAULT_URL);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("When AppiumIsApkInstalled=false, 'AppiumApp' must be provided.");

        factory.fromSettings(settings);
    }

    @Test
    public void apkNotInstalledAndAppEmpty_throwsIllegalArgumentException() {
        Settings settings = defaultSettings();
        settings.set(ConfigTags.AppiumIsApkInstalled, false);
        settings.set(ConfigTags.AppiumApp, "");

        AndroidCapabilitiesFactory factory = new AndroidCapabilitiesFactory(DEFAULT_URL);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("When AppiumIsApkInstalled=false, 'AppiumApp' must be provided.");

        factory.fromSettings(settings);
    }

    private static Settings defaultSettings() {
        return new Settings(SettingsDefaults.getSettingsDefaults(), new Properties());
    }

}
