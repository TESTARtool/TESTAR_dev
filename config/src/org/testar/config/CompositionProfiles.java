/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.config;

import org.testar.config.settings.Settings;

public final class CompositionProfiles {

    public static final String WINDOWS_COMPOSITION = "windows_composition";
    public static final String WEBDRIVER_COMPOSITION = "webdriver_composition";
    public static final String ANDROID_COMPOSITION = "android_composition";

    private static final String[] VALUES = new String[]{
            WINDOWS_COMPOSITION,
            WEBDRIVER_COMPOSITION,
            ANDROID_COMPOSITION
    };

    private CompositionProfiles() {
    }

    public static String[] values() {
        return VALUES.clone();
    }

    public static boolean isSupported(String compositionProfile) {
        if (compositionProfile == null) {
            return false;
        }

        for (String value : VALUES) {
            if (value.equals(compositionProfile)) {
                return true;
            }
        }

        return false;
    }

    public static String resolve(String configuredProfile, String sutConnector) {
        if (configuredProfile != null && !configuredProfile.isBlank()) {
            return configuredProfile;
        }

        if (Settings.SUT_CONNECTOR_ANDROID_APPIUM.equals(sutConnector)) {
            return ANDROID_COMPOSITION;
        }

        if (Settings.SUT_CONNECTOR_WEBDRIVER.equals(sutConnector)) {
            return WEBDRIVER_COMPOSITION;
        }

        return WINDOWS_COMPOSITION;
    }
}
