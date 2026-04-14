/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin;

import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.core.Assert;
import org.testar.plugin.exceptions.UnsupportedPlatformException;

/**
 * Central factory for converting TESTAR settings into a platform session
 * specification.
 */
public final class PlatformSessionSpecFactory {

    private PlatformSessionSpecFactory() {
    }

    public static PlatformSessionSpec fromSettings(Settings settings) {
        Assert.notNull(settings);

        OperatingSystems operatingSystem = detectOperatingSystem();
        PlatformSessionSpec.TargetType targetType = detectTargetType(
                operatingSystem,
                settings.get(ConfigTags.SUTConnector, "")
        );
        String target = settings.get(ConfigTags.SUTConnectorValue, "");

        if (target == null || target.isBlank()) {
            throw new UnsupportedPlatformException("SUTConnectorValue cannot be empty for platform session composition");
        }

        return PlatformSessionSpec.builder(
                operatingSystem,
                targetType,
                target,
                settings
        ).build();
    }

    public static PlatformSessionSpec create(OperatingSystems operatingSystem,
                                             PlatformSessionSpec.TargetType targetType,
                                             String target,
                                             Settings settings) {
        Assert.notNull(operatingSystem);
        Assert.notNull(targetType);
        Assert.notNull(settings);

        if (target == null || target.isBlank()) {
            throw new UnsupportedPlatformException("SUTConnectorValue cannot be empty for platform session composition");
        }

        return PlatformSessionSpec.builder(
                operatingSystem,
                targetType,
                target,
                settings
        ).build();
    }

    private static OperatingSystems detectOperatingSystem() {
        if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER)) {
            return OperatingSystems.WEBDRIVER;
        }
        if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WINDOWS_10)) {
            return OperatingSystems.WINDOWS_10;
        }
        if (NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WINDOWS)) {
            return OperatingSystems.WINDOWS;
        }
        throw new UnsupportedPlatformException(
                "Platform session composition currently supports only Windows and WebDriver"
        );
    }

    private static PlatformSessionSpec.TargetType detectTargetType(OperatingSystems operatingSystem,
                                                                   String sutConnector) {
        if (operatingSystem == OperatingSystems.WEBDRIVER) {
            return PlatformSessionSpec.TargetType.EXECUTABLE;
        }
        if (Settings.SUT_CONNECTOR_CMDLINE.equals(sutConnector)) {
            return PlatformSessionSpec.TargetType.EXECUTABLE;
        }
        if (Settings.SUT_CONNECTOR_WINDOW_TITLE.equals(sutConnector)) {
            return PlatformSessionSpec.TargetType.WINDOW_TITLE;
        }
        if (Settings.SUT_CONNECTOR_PROCESS_NAME.equals(sutConnector)) {
            return PlatformSessionSpec.TargetType.PROCESS_NAME;
        }
        throw new UnsupportedPlatformException(
                "Unsupported platform session connector: " + sutConnector
        );
    }
}
