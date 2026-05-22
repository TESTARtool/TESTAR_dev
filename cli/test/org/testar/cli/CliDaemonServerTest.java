package org.testar.cli;

import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.config.settings.SettingsDefaults;
import org.testar.plugin.OperatingSystems;
import org.testar.plugin.configuration.PlatformSessionSpecification;

public class CliDaemonServerTest {

    @Test
    public void buildSessionSpecForAndroidCopiesApkArgumentIntoSettings() {
        CliDaemonServer server = new CliDaemonServer();
        Settings settings = defaultSettings();

        PlatformSessionSpecification sessionSpec = server.buildSessionSpec(
                CliRequest.of(CliCommand.START_SESSION, List.of("android", "/tmp/example.apk")),
                settings
        );

        Assert.assertEquals(OperatingSystems.ANDROID, sessionSpec.getOperatingSystem());
        Assert.assertEquals(PlatformSessionSpecification.TargetType.EXECUTABLE, sessionSpec.getTargetType());
        Assert.assertEquals("/tmp/example.apk", sessionSpec.getTarget());
        Assert.assertFalse(settings.get(ConfigTags.AppiumIsApkInstalled));
        Assert.assertEquals("/tmp/example.apk", settings.get(ConfigTags.AppiumApp));
    }

    @Test
    public void buildSessionSpecForWebdriverCopiesTargetIntoSettings() {
        CliDaemonServer server = new CliDaemonServer();
        Settings settings = defaultSettings();

        PlatformSessionSpecification sessionSpec = server.buildSessionSpec(
                CliRequest.of(CliCommand.START_SESSION, List.of("webdriver", "https://example.org")),
                settings
        );

        Assert.assertEquals(OperatingSystems.WEBDRIVER, sessionSpec.getOperatingSystem());
        Assert.assertEquals("https://example.org", sessionSpec.getTarget());
        Assert.assertEquals("https://example.org", settings.get(ConfigTags.SUTConnectorValue));
    }

    @Test
    public void buildSessionSpecForWindowsKeepsTargetInSessionSpec() {
        CliDaemonServer server = new CliDaemonServer();
        Settings settings = defaultSettings();

        PlatformSessionSpecification sessionSpec = server.buildSessionSpec(
                CliRequest.of(CliCommand.START_SESSION, List.of("windows", "notepad.exe")),
                settings
        );

        Assert.assertEquals(OperatingSystems.WINDOWS, sessionSpec.getOperatingSystem());
        Assert.assertEquals("notepad.exe", sessionSpec.getTarget());
    }

    private static Settings defaultSettings() {
        return new Settings(SettingsDefaults.getSettingsDefaults(), new Properties());
    }
}
