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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.nio.file.StandardCopyOption;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.testar.config.settings.Settings;
import org.testar.core.util.RuntimePathsUtil;

public final class CliSettingsLoader {

    private static final String DEFAULT_PROFILE_NAME = "cli_generic";
    private static final String PROFILE_SETTINGS_FILE_NAME = "test.settings";
    private static final List<String> DEFAULT_PROFILE_FILES = List.of(
            PROFILE_SETTINGS_FILE_NAME,
            "composition.properties",
            "policies.properties"
    );

    private CliSettingsLoader() {
    }

    public static Settings load() {
        return loadProfile(DEFAULT_PROFILE_NAME);
    }

    public static Settings loadProfile(String profileName) {
        String normalizedProfileName = normalizeProfileName(profileName);
        try {
            Path profileDirectory = ensureProfileDirectory(normalizedProfileName);
            Path settingsFile = profileDirectory.resolve(PROFILE_SETTINGS_FILE_NAME);
            Settings.setSettingsPath(profileDirectory.toString());
            return Settings.loadSettings(new String[0], settingsFile.toString());
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load CLI profile settings: " + normalizedProfileName, exception);
        }
    }

    public static List<String> listProfiles() {
        try {
            ensureProfileDirectory(DEFAULT_PROFILE_NAME);
            Path settingsDirectory = resolveSettingsDirectory();
            if (!Files.isDirectory(settingsDirectory)) {
                return List.of(DEFAULT_PROFILE_NAME);
            }

            try (Stream<Path> children = Files.list(settingsDirectory)) {
                List<String> profileNames = children
                        .filter(Files::isDirectory)
                        .filter(path -> Files.isRegularFile(path.resolve(PROFILE_SETTINGS_FILE_NAME)))
                        .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                        .map(path -> path.getFileName().toString())
                        .collect(Collectors.toCollection(ArrayList::new));

                if (!profileNames.contains(DEFAULT_PROFILE_NAME)) {
                    profileNames.add(0, DEFAULT_PROFILE_NAME);
                }

                return List.copyOf(profileNames);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to list CLI profiles", exception);
        }
    }

    public static String defaultProfileName() {
        return DEFAULT_PROFILE_NAME;
    }

    private static Path ensureProfileDirectory(String profileName) throws IOException {
        Path settingsDirectory = resolveSettingsDirectory();
        Path profileDirectory = settingsDirectory.resolve(profileName);
        Files.createDirectories(profileDirectory);

        if (DEFAULT_PROFILE_NAME.equals(profileName)) {
            for (String fileName : DEFAULT_PROFILE_FILES) {
                ensureProfileResourceFile(profileDirectory, profileName, fileName);
            }
        }

        return profileDirectory;
    }

    private static void ensureProfileResourceFile(Path profileDirectory,
                                                  String profileName,
                                                  String fileName) throws IOException {
        Path file = profileDirectory.resolve(fileName);
        if (Files.exists(file)) {
            return;
        }

        String resourcePath = "/settings/" + profileName + "/" + fileName;
        try (InputStream inputStream = CliSettingsLoader.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Unable to find bundled CLI profile resource: " + resourcePath);
            }
            Files.copy(inputStream, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static Path resolveSettingsDirectory() {
        return RuntimePathsUtil.resolveTestarHome().resolve("settings");
    }

    private static String normalizeProfileName(String profileName) {
        if (profileName == null || profileName.isBlank()) {
            return DEFAULT_PROFILE_NAME;
        }

        String normalizedProfileName = profileName.trim();
        if (normalizedProfileName.contains("/") || normalizedProfileName.contains("\\")) {
            throw new IllegalArgumentException("Invalid CLI profile name: " + profileName);
        }

        return normalizedProfileName;
    }
}
