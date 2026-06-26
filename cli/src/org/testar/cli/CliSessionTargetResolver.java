/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.cli;

import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.plugin.OperatingSystems;

final class CliSessionTargetResolver {

    private CliSessionTargetResolver() {
    }

    static CliSessionTarget resolve(Settings settings) {
        String sutConnector = settings.get(ConfigTags.SUTConnector, "").trim();

        if (Settings.SUT_CONNECTOR_WEBDRIVER.equals(sutConnector)) {
            return new CliSessionTarget(
                    OperatingSystems.WEBDRIVER,
                    requireTarget(cleanQuotedValue(settings.get(ConfigTags.SUTConnectorValue, "")), sutConnector)
            );
        }

        if (Settings.SUT_CONNECTOR_ANDROID_APPIUM.equals(sutConnector)) {
            String appTarget = cleanQuotedValue(settings.get(ConfigTags.AppiumApp, ""));
            if (appTarget.isBlank()) {
                appTarget = cleanQuotedValue(settings.get(ConfigTags.AppiumAppPackage, ""));
            }

            return new CliSessionTarget(
                    OperatingSystems.ANDROID,
                    requireTarget(appTarget, sutConnector)
            );
        }

        if (Settings.SUT_CONNECTOR_CMDLINE.equals(sutConnector)) {
            return new CliSessionTarget(
                    OperatingSystems.WINDOWS,
                    requireTarget(cleanQuotedValue(settings.get(ConfigTags.SUTConnectorValue, "")), sutConnector)
            );
        }

        throw new IllegalArgumentException(
                "Unsupported SUTConnector for CLI mode: " + sutConnector
                        + ". CLI mode supports WEB_DRIVER, ANDROID_APPIUM, and COMMAND_LINE."
        );
    }

    private static String requireTarget(String target, String sutConnector) {
        if (target == null || target.isBlank()) {
            throw new IllegalArgumentException("CLI target is required for SUTConnector: " + sutConnector);
        }

        return target.trim();
    }

    private static String cleanQuotedValue(String value) {
        String text = value == null ? "" : value.trim();
        if (text.length() < 2) {
            return text;
        }

        boolean doubleQuoted = text.startsWith("\"") && text.endsWith("\"");
        boolean singleQuoted = text.startsWith("'") && text.endsWith("'");
        if (doubleQuoted || singleQuoted) {
            return text.substring(1, text.length() - 1).trim();
        }

        return text;
    }
}
