/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.android;

import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Factory responsible for creating Appium DesiredCapabilities
 * and determining the Appium server URL from TESTAR settings.
 */
public class AndroidCapabilitiesFactory {

    private final String defaultAppiumUrl;

    /**
     * @param defaultAppiumUrl the URL that will be used when the settings do not override it.
     */
    public AndroidCapabilitiesFactory(String defaultAppiumUrl) {
        this.defaultAppiumUrl = Objects.requireNonNull(defaultAppiumUrl);
    }

    public Result fromSettings(Settings settings) {
        Objects.requireNonNull(settings, "settings");
        DesiredCapabilities cap = new DesiredCapabilities();

        // https://appium.io/docs/en/2.0/guides/caps/
        cap.setCapability("platformName", settings.get(ConfigTags.AppiumPlatformName));

        cap.setCapability("appium:deviceName", settings.get(ConfigTags.AppiumDeviceName));
        cap.setCapability("appium:automationName", settings.get(ConfigTags.AppiumAutomationName));
        cap.setCapability("appium:newCommandTimeout", settings.get(ConfigTags.AppiumNewCommandTimeout));
        cap.setCapability("appium:autoGrantPermissions", settings.get(ConfigTags.AppiumAutoGrantPermissions));

        cap.setCapability("appium:settings[allowInvisibleElements]", settings.get(ConfigTags.AppiumAllowInvisibleElements));

        cap.setCapability("appium:ignoreHiddenApiPolicyError", settings.get(ConfigTags.AppiumIgnoreHiddenApiPolicyError));

        // ADB / server timeouts
        cap.setCapability("appium:adbExecTimeout", settings.get(ConfigTags.AppiumAdbExecTimeout));
        cap.setCapability("appium:uiautomator2ServerInstallTimeout", settings.get(ConfigTags.AppiumUiautomator2ServerInstallTimeout));
        cap.setCapability("appium:uiautomator2ServerLaunchTimeout", settings.get(ConfigTags.AppiumUiautomator2ServerLaunchTimeout));

        String appiumUrl = defaultAppiumUrl;

        // If the APK is already installed we use appPackage identifier
        if (settings.get(ConfigTags.AppiumIsApkInstalled)) {
            String appPackage = settings.get(ConfigTags.AppiumAppPackage);
            String appActivity = settings.get(ConfigTags.AppiumAppActivity);

            if (appPackage == null || appPackage.isBlank()) {
                throw new IllegalArgumentException("When AppiumIsApkInstalled=true, 'AppiumAppPackage' is required.");
            }
            if (appActivity == null || appActivity.isBlank()) {
                throw new IllegalArgumentException(String.join("\n",
                        "When AppiumIsApkInstalled=true, 'AppiumAppActivity' is required (multiple launcher activities can exist).",
                        "",
                        "How to find it on Windows:",
                        "1) Manually open the app on the emulator.",
                        "2) Run:",
                        "   adb shell dumpsys activity activities | findstr /R /C:\"ResumedActivity\" /C:\"topResumedActivity\"",
                        "3) From the output, take the activity after the package name."
                        ));
            }

            cap.setCapability("appium:appPackage", appPackage);
            cap.setCapability("appium:appActivity", appActivity);
        } else {
            // Else we need to install the APK
            String appPath = settings.get(ConfigTags.AppiumApp);
            if (appPath == null || appPath.isBlank()) {
                throw new IllegalArgumentException(
                        "When AppiumIsApkInstalled=false, 'AppiumApp' must be provided.");
            }

            boolean isEmulatorDocker = settings.get(ConfigTags.AppiumIsEmulatorDocker);
            String ipAddressAppium = settings.get(ConfigTags.AppiumIpAddress);

            // If emulator is running inside a docker use the APK raw URL
            if (isEmulatorDocker && ipAddressAppium != null && !ipAddressAppium.isBlank()) {
                // Docker container (budtmo/docker-android) + Appium v2 do not use /wd/hub suffix anymore
                // It can be enabled using the APPIUM_ADDITIONAL_ARGS "--base-path /wd/hub" command
                cap.setCapability("appium:app", appPath);
                appiumUrl = "http://" + ipAddressAppium + ":4723/wd/hub";
            } else {
                // Else, obtain the local directory that contains the APK file
                try {
                    cap.setCapability("appium:app", new File(appPath).getCanonicalPath());
                } catch (IOException e) {
                    System.err.println("ERROR: Cannot resolve canonical path for APK: " + appPath);
                    e.printStackTrace();
                    cap.setCapability("appium:app", appPath);
                }
            }
        }

        return new Result(cap, appiumUrl);
    }

    /**
     * Value object returned by the factory so callers can configure both
     * the driver capabilities and the Appium server URL.
     */
    public static final class Result {
        private final DesiredCapabilities capabilities;
        private final String appiumServerUrl;

        Result(DesiredCapabilities capabilities, String appiumServerUrl) {
            this.capabilities = Objects.requireNonNull(capabilities, "capabilities");
            this.appiumServerUrl = Objects.requireNonNull(appiumServerUrl, "appiumServerUrl");
        }

        public DesiredCapabilities getCapabilities() {
            return capabilities;
        }

        public String getAppiumServerUrl() {
            return appiumServerUrl;
        }
    }

}
