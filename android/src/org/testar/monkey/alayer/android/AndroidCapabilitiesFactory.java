/***************************************************************************************************
 *
 * Copyright (c) 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2025 Open Universiteit - www.ou.nl
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.monkey.alayer.android;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

/**
 * Factory responsible for creating Appium DesiredCapabilities 
 * and determining the Appium server URL from a JSON configuration. 
 */
public class AndroidCapabilitiesFactory {

    private final String defaultAppiumUrl;

    /**
     * @param defaultAppiumUrl the URL that will be used when the JSON does not override it. 
     */
    public AndroidCapabilitiesFactory(String defaultAppiumUrl) {
        this.defaultAppiumUrl = Objects.requireNonNull(defaultAppiumUrl);
    }

    public Result fromJsonFile(String capabilitiesJsonFile) {
        try (FileReader reader = new FileReader(capabilitiesJsonFile)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            return fromJsonObject(json);
        } catch (IOException | IllegalStateException e) {
            System.err.println("ERROR: Exception reading Appium Desired Capabilities from JSON file: " + capabilitiesJsonFile);
            e.printStackTrace();

            // Preserve previous behaviour: return empty capabilities and keep the default URL.
            return new Result(new DesiredCapabilities(), defaultAppiumUrl);
        }
    }

    Result fromJsonObject(JsonObject json) {
        DesiredCapabilities cap = new DesiredCapabilities();

        // https://appium.io/docs/en/2.0/guides/caps/
        cap.setCapability("platformName", getString(json, "platformName", "Android"));

        cap.setCapability("appium:deviceName", getString(json, "deviceName", "Android Emulator"));
        cap.setCapability("appium:automationName", getString(json, "automationName", "UiAutomator2"));
        cap.setCapability("appium:newCommandTimeout", getInt(json, "newCommandTimeout", 600));
        cap.setCapability("appium:autoGrantPermissions", getBool(json, "autoGrantPermissions", false));

        String appiumUrl = defaultAppiumUrl;

        // If the APK is already installed we use appPackage identifier
        if (getBool(json, "isApkInstalled", false)) {
            String appPackage = getString(json, "appPackage", null);
            String appActivity = getString(json, "appActivity", null);

            if (appPackage == null || appPackage.isEmpty()) {
                throw new IllegalArgumentException("When isApkInstalled=true, 'appPackage' is required.");
            }
            if (appActivity == null || appActivity.isEmpty()) {
                throw new IllegalArgumentException(String.join("\n",
                        "When isApkInstalled=true, 'appActivity' is required (multiple launcher activities can exist).",
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
            String appPath = getString(json, "app", null);
            if (appPath == null || appPath.isEmpty()) {
                throw new IllegalArgumentException(
                        "When isApkInstalled=false, 'app' (APK path or URL) must be provided.");
            }

            boolean isEmulatorDocker = getBool(json, "isEmulatorDocker", false);
            String ipAddressAppium = getString(json, "ipAddressAppium", null);

            // If emulator is running inside a docker use the APK raw URL
            if (isEmulatorDocker && ipAddressAppium != null && !ipAddressAppium.isEmpty()) {
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

    private static String getString(JsonObject json, String key, String def) {
        return json.has(key) && !json.get(key).isJsonNull() ? json.get(key).getAsString() : def;
    }

    private static boolean getBool(JsonObject json, String key, boolean def) {
        return json.has(key) && !json.get(key).isJsonNull() ? json.get(key).getAsBoolean() : def;
    }

    private static int getInt(JsonObject json, String key, int def) {
        return json.has(key) && !json.get(key).isJsonNull() ? json.get(key).getAsInt() : def;
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
