/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin;

import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.core.Assert;
import org.testar.plugin.configuration.PlatformSessionSpecification;
import org.testar.plugin.exceptions.UnsupportedPlatformException;

/**
 * Central factory for converting TESTAR settings into one platform session
 * specification.
 */
public final class PlatformSessionSpecFactory {

    private PlatformSessionSpecFactory() {
    }

    public static PlatformSessionSpecification fromSettings(Settings settings) {
        Assert.notNull(settings);

        OperatingSystems operatingSystem = detectOperatingSystem(settings);
        PlatformSessionSpecification.TargetType targetType = detectTargetType(
                operatingSystem,
                settings.get(ConfigTags.SUTConnector, "")
        );
        String target = deriveTarget(operatingSystem, settings);

        if (target == null || target.isBlank()) {
            throw new UnsupportedPlatformException("Platform session target cannot be empty");
        }

        return PlatformSessionSpecification.builder(
                operatingSystem,
                targetType,
                target,
                settings
        ).build();
    }

    public static PlatformSessionSpecification create(OperatingSystems operatingSystem,
                                             PlatformSessionSpecification.TargetType targetType,
                                             String target,
                                             Settings settings) {
        Assert.notNull(operatingSystem);
        Assert.notNull(targetType);
        Assert.notNull(settings);

        if (target == null || target.isBlank()) {
            throw new UnsupportedPlatformException("Platform session target cannot be empty");
        }

        return PlatformSessionSpecification.builder(
                operatingSystem,
                targetType,
                target,
                settings
        ).build();
    }

    private static OperatingSystems detectOperatingSystem(Settings settings) {
        String sutConnector = settings.get(ConfigTags.SUTConnector, "");
        if (Settings.SUT_CONNECTOR_WEBDRIVER.equals(sutConnector)) {
            return OperatingSystems.WEBDRIVER;
        }
        if (Settings.SUT_CONNECTOR_ANDROID_APPIUM.equals(sutConnector)) {
            return OperatingSystems.ANDROID;
        }
        if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER)) {
            return OperatingSystems.WEBDRIVER;
        }
        if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.ANDROID)) {
            return OperatingSystems.ANDROID;
        }
        if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WINDOWS_10)) {
            return OperatingSystems.WINDOWS_10;
        }
        if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WINDOWS)) {
            return OperatingSystems.WINDOWS;
        }
        throw new UnsupportedPlatformException(
                "Platform session composition currently supports only Windows, WebDriver, and Android"
        );
    }

    private static PlatformSessionSpecification.TargetType detectTargetType(OperatingSystems operatingSystem,
                                                                   String sutConnector) {
        if (operatingSystem == OperatingSystems.ANDROID) {
            return PlatformSessionSpecification.TargetType.EXECUTABLE;
        }
        if (operatingSystem == OperatingSystems.WEBDRIVER) {
            return PlatformSessionSpecification.TargetType.EXECUTABLE;
        }
        if (Settings.SUT_CONNECTOR_CMDLINE.equals(sutConnector)) {
            return PlatformSessionSpecification.TargetType.EXECUTABLE;
        }
        if (Settings.SUT_CONNECTOR_WINDOW_TITLE.equals(sutConnector)) {
            return PlatformSessionSpecification.TargetType.WINDOW_TITLE;
        }
        if (Settings.SUT_CONNECTOR_PROCESS_NAME.equals(sutConnector)) {
            return PlatformSessionSpecification.TargetType.PROCESS_NAME;
        }
        throw new UnsupportedPlatformException(
                "Unsupported platform session connector: " + sutConnector
        );
    }

    private static String deriveTarget(OperatingSystems operatingSystem, Settings settings) {
        // Windows and Webdriver sessions use the sut connector value
        if (operatingSystem != OperatingSystems.ANDROID) {
            return settings.get(ConfigTags.SUTConnectorValue, "");
        }

        // Android sessions can install or use already installed apks
        if (settings.get(ConfigTags.AppiumIsApkInstalled, false)) {
            String appPackage = settings.get(ConfigTags.AppiumAppPackage, "");
            if (!appPackage.isBlank()) {
                return appPackage;
            }
        } else {
            String app = settings.get(ConfigTags.AppiumApp, "");
            if (!app.isBlank()) {
                return app;
            }
        }

        String applicationName = settings.get(ConfigTags.ApplicationName, "");
        if (!applicationName.isBlank()) {
            return applicationName;
        }

        return "android";
    }
}
