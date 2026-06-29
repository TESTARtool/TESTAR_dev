package org.testar.cli;

import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.config.settings.SettingsDefaults;
import org.testar.core.verdict.Verdict;
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
    public void buildSessionSpecAllowsExplicitTargetWithWorkspaceArgument() {
        CliDaemonServer server = new CliDaemonServer();
        Settings settings = defaultSettings();

        PlatformSessionSpecification sessionSpec = server.buildSessionSpec(
                CliRequest.of(CliCommand.START_SESSION, List.of("webdriver", "https://example.org", "webdriver_generic")),
                settings
        );

        Assert.assertEquals(OperatingSystems.WEBDRIVER, sessionSpec.getOperatingSystem());
        Assert.assertEquals("https://example.org", sessionSpec.getTarget());
        Assert.assertEquals("https://example.org", settings.get(ConfigTags.SUTConnectorValue));
    }

    @Test
    public void buildSessionSpecRejectsTooManyExplicitArguments() {
        CliDaemonServer server = new CliDaemonServer();

        try {
            server.buildSessionSpec(
                    CliRequest.of(CliCommand.START_SESSION, List.of("webdriver", "https://example.org", "webdriver_generic", "extra")),
                    defaultSettings()
            );
            Assert.fail("Expected explicit startSession syntax failure");
        } catch (IllegalArgumentException exception) {
            Assert.assertTrue(exception.getMessage().contains("startSession <workspace>"));
            Assert.assertTrue(exception.getMessage().contains("startSession <platform> <target> [workspace]"));
        }
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

    @Test
    public void resolveCliTargetFromWebdriverConnector() {
        Settings settings = defaultSettings();
        settings.set(ConfigTags.SUTConnector, Settings.SUT_CONNECTOR_WEBDRIVER);
        settings.set(ConfigTags.SUTConnectorValue, "\"https://example.org\"");

        CliSessionTarget target = CliSessionTargetResolver.resolve(settings);

        Assert.assertEquals(OperatingSystems.WEBDRIVER, target.operatingSystem());
        Assert.assertEquals("https://example.org", target.target());
    }

    @Test
    public void resolveCliTargetFromCommandLineConnector() {
        Settings settings = defaultSettings();
        settings.set(ConfigTags.SUTConnector, Settings.SUT_CONNECTOR_CMDLINE);
        settings.set(ConfigTags.SUTConnectorValue, "notepad.exe");

        CliSessionTarget target = CliSessionTargetResolver.resolve(settings);

        Assert.assertEquals(OperatingSystems.WINDOWS, target.operatingSystem());
        Assert.assertEquals("notepad.exe", target.target());
    }

    @Test
    public void resolveCliTargetFromAndroidConnectorUsesAppPackageFallback() {
        Settings settings = defaultSettings();
        settings.set(ConfigTags.SUTConnector, Settings.SUT_CONNECTOR_ANDROID_APPIUM);
        settings.set(ConfigTags.AppiumApp, "");
        settings.set(ConfigTags.AppiumAppPackage, "org.example.app");

        CliSessionTarget target = CliSessionTargetResolver.resolve(settings);

        Assert.assertEquals(OperatingSystems.ANDROID, target.operatingSystem());
        Assert.assertEquals("org.example.app", target.target());
    }

    @Test
    public void resolveCliTargetRejectsDesktopAttachConnector() {
        Settings settings = defaultSettings();
        settings.set(ConfigTags.SUTConnector, Settings.SUT_CONNECTOR_WINDOW_TITLE);
        settings.set(ConfigTags.SUTConnectorValue, "Calculator");

        try {
            CliSessionTargetResolver.resolve(settings);
            Assert.fail("Expected unsupported connector failure");
        } catch (IllegalArgumentException exception) {
            Assert.assertTrue(exception.getMessage().contains("Unsupported SUTConnector"));
        }
    }

    @Test
    public void stopSessionWithoutVerdictDefaultsToOk() {
        CliDaemonServer server = new CliDaemonServer();

        List<Verdict> verdicts = server.finalVerdictsFromStopRequest(
                CliRequest.of(CliCommand.STOP_SESSION, List.of())
        );

        Assert.assertEquals(1, verdicts.size());
        Assert.assertEquals(Verdict.OK, verdicts.get(0));
    }

    @Test
    public void stopSessionAcceptsLlmCompleteWithReason() {
        CliDaemonServer server = new CliDaemonServer();

        List<Verdict> verdicts = server.finalVerdictsFromStopRequest(
                CliRequest.of(CliCommand.STOP_SESSION, List.of("LLM_COMPLETE", "Login", "verified"))
        );

        Assert.assertEquals(1, verdicts.size());
        Assert.assertEquals(Verdict.Severity.LLM_COMPLETE.getValue(), verdicts.get(0).severity(), 0.0);
        Assert.assertEquals("Login verified", verdicts.get(0).info());
    }

    @Test
    public void stopSessionAcceptsLlmInvalidWithReason() {
        CliDaemonServer server = new CliDaemonServer();

        List<Verdict> verdicts = server.finalVerdictsFromStopRequest(
                CliRequest.of(CliCommand.STOP_SESSION, List.of("LLM_INVALID", "Welcome message was not shown"))
        );

        Assert.assertEquals(1, verdicts.size());
        Assert.assertEquals(Verdict.Severity.LLM_INVALID.getValue(), verdicts.get(0).severity(), 0.0);
        Assert.assertEquals("Welcome message was not shown", verdicts.get(0).info());
    }

    @Test
    public void stopSessionRejectsUnsupportedFinalVerdict() {
        CliDaemonServer server = new CliDaemonServer();

        try {
            server.finalVerdictsFromStopRequest(
                    CliRequest.of(CliCommand.STOP_SESSION, List.of("WARNING", "Not valid for agent finalization"))
            );
            Assert.fail("Expected unsupported stopSession verdict failure");
        } catch (IllegalArgumentException exception) {
            Assert.assertTrue(exception.getMessage().contains("Unsupported stopSession verdict"));
            Assert.assertTrue(exception.getMessage().contains("LLM_COMPLETE"));
            Assert.assertTrue(exception.getMessage().contains("LLM_INVALID"));
        }
    }

    private static Settings defaultSettings() {
        Properties properties = new Properties();
        properties.setProperty(ConfigTags.SUTConnectorValue.name(), "notepad.exe");
        return new Settings(SettingsDefaults.getSettingsDefaults(), properties);
    }
}
