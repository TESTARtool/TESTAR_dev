package org.testar.monkey.alayer.android;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.google.gson.JsonObject;

public class AndroidCapabilitiesFactoryTest {

    private static final String DEFAULT_URL = "http://127.0.0.1:4723/wd/hub";

    @Test
    public void buildsCapabilitiesForInstalledApp() {
        JsonObject json = new JsonObject();
        json.addProperty("isApkInstalled", true);
        json.addProperty("platformName", "Android");
        json.addProperty("appPackage", "com.example.app");
        json.addProperty("appActivity", "com.example.app.MainActivity");

        AndroidCapabilitiesFactory factory = new AndroidCapabilitiesFactory(DEFAULT_URL);

        AndroidCapabilitiesFactory.Result result = factory.fromJsonObject(json);
        DesiredCapabilities caps = result.getCapabilities();

        assertEquals(Platform.ANDROID, caps.getCapability("platformName"));
        assertEquals("com.example.app", caps.getCapability("appium:appPackage"));
        assertEquals("com.example.app.MainActivity", caps.getCapability("appium:appActivity"));
        assertEquals(DEFAULT_URL, result.getAppiumServerUrl());
    }

    @Test
    public void buildsCapabilitiesForDockerEmulator() {
        JsonObject json = new JsonObject();
        json.addProperty("isApkInstalled", false);
        json.addProperty("app", "http://somewhere/sample.apk");
        json.addProperty("isEmulatorDocker", true);
        json.addProperty("ipAddressAppium", "10.1.2.34");

        AndroidCapabilitiesFactory factory = new AndroidCapabilitiesFactory(DEFAULT_URL);

        AndroidCapabilitiesFactory.Result result = factory.fromJsonObject(json);
        DesiredCapabilities caps = result.getCapabilities();

        assertEquals(Platform.ANDROID, caps.getCapability("platformName"));
        assertEquals("http://somewhere/sample.apk", caps.getCapability("appium:app"));
        assertEquals("http://10.1.2.34:4723/wd/hub", result.getAppiumServerUrl());
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void apkInstalledWithoutAppPackage_throwsIllegalArgumentException() {
        JsonObject json = new JsonObject();
        json.addProperty("isApkInstalled", true);
        // appPackage missing
        json.addProperty("appActivity", "com.example.app.MainActivity");

        AndroidCapabilitiesFactory factory = new AndroidCapabilitiesFactory(DEFAULT_URL);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("When isApkInstalled=true, 'appPackage' is required.");

        factory.fromJsonObject(json);
    }

    @Test
    public void apkInstalledWithEmptyAppPackage_throwsIllegalArgumentException() {
        JsonObject json = new JsonObject();
        json.addProperty("isApkInstalled", true);
        json.addProperty("appPackage", ""); // empty
        json.addProperty("appActivity", "com.example.app.MainActivity");

        AndroidCapabilitiesFactory factory = new AndroidCapabilitiesFactory(DEFAULT_URL);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("When isApkInstalled=true, 'appPackage' is required.");

        factory.fromJsonObject(json);
    }

    @Test
    public void apkInstalledWithoutAppActivity_throwsIllegalArgumentException() {
        JsonObject json = new JsonObject();
        json.addProperty("isApkInstalled", true);
        json.addProperty("appPackage", "com.example.app");
        // appActivity missing

        AndroidCapabilitiesFactory factory = new AndroidCapabilitiesFactory(DEFAULT_URL);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("When isApkInstalled=true, 'appActivity' is required");

        factory.fromJsonObject(json);
    }

    @Test
    public void apkInstalledWithEmptyAppActivity_throwsIllegalArgumentException() {
        JsonObject json = new JsonObject();
        json.addProperty("isApkInstalled", true);
        json.addProperty("appPackage", "com.example.app");
        json.addProperty("appActivity", ""); // empty

        AndroidCapabilitiesFactory factory = new AndroidCapabilitiesFactory(DEFAULT_URL);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("When isApkInstalled=true, 'appActivity' is required");

        factory.fromJsonObject(json);
    }

    @Test
    public void apkNotInstalledAndAppMissing_throwsIllegalArgumentException() {
        JsonObject json = new JsonObject();
        json.addProperty("isApkInstalled", false);
        // "app" missing

        AndroidCapabilitiesFactory factory = new AndroidCapabilitiesFactory(DEFAULT_URL);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("When isApkInstalled=false, 'app' (APK path or URL) must be provided.");

        factory.fromJsonObject(json);
    }

    @Test
    public void apkNotInstalledAndAppEmpty_throwsIllegalArgumentException() {
        JsonObject json = new JsonObject();
        json.addProperty("isApkInstalled", false);
        json.addProperty("app", ""); // empty

        AndroidCapabilitiesFactory factory = new AndroidCapabilitiesFactory(DEFAULT_URL);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("When isApkInstalled=false, 'app' (APK path or URL) must be provided.");

        factory.fromJsonObject(json);
    }

}
