/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.cli.settings;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.testar.config.settings.Settings;

public final class CliSettingsLoader {

    private static final String SETTINGS_RESOURCE = "/settings/cli.settings";
    private static final String SETTINGS_FILE_NAME = "cli.settings";

    private CliSettingsLoader() {
    }

    public static Settings load() {
        try {
            Path settingsFile = ensureSettingsFile();
            return Settings.loadSettings(new String[0], settingsFile.toString());
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load CLI settings from " + SETTINGS_RESOURCE, exception);
        }
    }

    private static Path ensureSettingsFile() throws IOException {
        Path settingsDirectory = resolveSettingsDirectory();
        Path settingsFile = settingsDirectory.resolve(SETTINGS_FILE_NAME);
        if (Files.exists(settingsFile)) {
            return settingsFile;
        }
        Files.createDirectories(settingsDirectory);
        try (InputStream inputStream = CliSettingsLoader.class.getResourceAsStream(SETTINGS_RESOURCE)) {
            if (inputStream == null) {
                throw new IOException("Unable to find bundled CLI settings resource: " + SETTINGS_RESOURCE);
            }
            Files.copy(inputStream, settingsFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return settingsFile;
    }

    private static Path resolveSettingsDirectory() {
        return Paths.get(".").resolve("settings");
    }
}
