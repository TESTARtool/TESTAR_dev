/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.config;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class TestarDirectories {

    public static final String SETTINGS_FILE = "test.settings";
    public static final String SUT_SETTINGS_EXT = ".sse";

    private static final Path BASE_DIR = Paths.get(".").toAbsolutePath().normalize();

    private static String testarDir = BASE_DIR.toString() + File.separator;
    private static String settingsDir = BASE_DIR.resolve("settings").toString() + File.separator;
    private static String outputDir = BASE_DIR.resolve("output").toString() + File.separator;
    private static String tempDir = BASE_DIR.resolve("output").resolve("temp").toString() + File.separator;
    private static String selectedSse;

    private TestarDirectories() { }

    public static String getTestarDir() {
        return testarDir;
    }

    public static void setTestarDir(String directory) {
        testarDir = directory;
    }

    public static String getSettingsDir() {
        return settingsDir;
    }

    public static void setSettingsDir(String directory) {
        settingsDir = directory;
    }

    public static String getOutputDir() {
        return outputDir;
    }

    public static void setOutputDir(String directory) {
        outputDir = directory;
    }

    public static String getTempDir() {
        return tempDir;
    }

    public static void setTempDir(String directory) {
        tempDir = directory;
    }

    public static String getSelectedSse() {
        return selectedSse;
    }

    public static void setSelectedSse(String sse) {
        selectedSse = sse;
    }

    public static String[] getSseFiles() {
        return new File(settingsDir).list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(SUT_SETTINGS_EXT);
            }
        });
    }

    public static String getTestSettingsFile() {
        return settingsDir + selectedSse + File.separator + SETTINGS_FILE;
    }
}
