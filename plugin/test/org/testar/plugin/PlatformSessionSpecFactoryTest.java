package org.testar.plugin;

import static org.junit.Assert.assertEquals;
import java.util.EnumSet;
import java.util.Properties;

import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.config.settings.SettingsDefaults;
import org.testar.plugin.configuration.PlatformSessionSpecification;

public class PlatformSessionSpecFactoryTest {

    @Test
    public void androidInstalledApp_usesAppPackageAsTarget() {
        Settings settings = defaultSettings();
        settings.set(ConfigTags.SUTConnector, Settings.SUT_CONNECTOR_ANDROID_APPIUM);
        settings.set(ConfigTags.AppiumIsApkInstalled, true);
        settings.set(ConfigTags.AppiumAppPackage, "com.example.app");

        try (MockedStatic<NativeLinker> nativeLinker = Mockito.mockStatic(NativeLinker.class)) {
            nativeLinker.when(NativeLinker::getPLATFORM_OS).thenReturn(EnumSet.of(OperatingSystems.ANDROID));

            PlatformSessionSpecification sessionSpec = PlatformSessionSpecFactory.fromSettings(settings);

            assertEquals(OperatingSystems.ANDROID, sessionSpec.getOperatingSystem());
            assertEquals(PlatformSessionSpecification.TargetType.EXECUTABLE, sessionSpec.getTargetType());
            assertEquals("com.example.app", sessionSpec.getTarget());
        }
    }

    @Test
    public void androidApkFile_usesAppAsTarget() {
        Settings settings = defaultSettings();
        settings.set(ConfigTags.SUTConnector, Settings.SUT_CONNECTOR_ANDROID_APPIUM);
        settings.set(ConfigTags.AppiumIsApkInstalled, false);
        settings.set(ConfigTags.AppiumApp, "/tmp/sample.apk");

        try (MockedStatic<NativeLinker> nativeLinker = Mockito.mockStatic(NativeLinker.class)) {
            nativeLinker.when(NativeLinker::getPLATFORM_OS).thenReturn(EnumSet.of(OperatingSystems.ANDROID));

            PlatformSessionSpecification sessionSpec = PlatformSessionSpecFactory.fromSettings(settings);

            assertEquals("/tmp/sample.apk", sessionSpec.getTarget());
        }
    }

    @Test
    public void androidWithoutAppConfiguration_fallsBackToApplicationName() {
        Settings settings = defaultSettings();
        settings.set(ConfigTags.SUTConnector, Settings.SUT_CONNECTOR_ANDROID_APPIUM);
        settings.set(ConfigTags.ApplicationName, "android-generic");

        try (MockedStatic<NativeLinker> nativeLinker = Mockito.mockStatic(NativeLinker.class)) {
            nativeLinker.when(NativeLinker::getPLATFORM_OS).thenReturn(EnumSet.of(OperatingSystems.ANDROID));

            PlatformSessionSpecification sessionSpec = PlatformSessionSpecFactory.fromSettings(settings);

            assertEquals("android-generic", sessionSpec.getTarget());
        }
    }

    @Test
    public void androidWithoutAppConfigurationOrApplicationName_usesDefaultTarget() {
        Settings settings = defaultSettings();
        settings.set(ConfigTags.SUTConnector, Settings.SUT_CONNECTOR_ANDROID_APPIUM);

        try (MockedStatic<NativeLinker> nativeLinker = Mockito.mockStatic(NativeLinker.class)) {
            nativeLinker.when(NativeLinker::getPLATFORM_OS).thenReturn(EnumSet.of(OperatingSystems.ANDROID));

            PlatformSessionSpecification sessionSpec = PlatformSessionSpecFactory.fromSettings(settings);

            assertEquals("android", sessionSpec.getTarget());
        }
    }

    private static Settings defaultSettings() {
        return new Settings(SettingsDefaults.getSettingsDefaults(), new Properties());
    }
}
