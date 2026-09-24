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
    private static String workspacesDir = BASE_DIR.resolve("workspaces").toString() + File.separator;
    private static String oraclesDir = BASE_DIR.resolve("oracles").toString() + File.separator;
    private static String outputDir = BASE_DIR.resolve("output").toString() + File.separator;
    private static String tempDir = BASE_DIR.resolve("output").resolve("temp").toString() + File.separator;
    private static String selectedWorkspaceName;

    private TestarDirectories() { }

    public static String getTestarDir() {
        return testarDir;
    }

    public static void setTestarDir(String directory) {
        testarDir = directory;
    }

    public static String getWorkspacesDir() {
        return workspacesDir;
    }

    public static void setWorkspacesDir(String directory) {
        workspacesDir = directory;
    }

    public static String getOraclesDir() {
        return oraclesDir;
    }

    public static void setOraclesDir(String directory) {
        oraclesDir = directory;
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

    public static String getSelectedWorkspaceName() {
        return selectedWorkspaceName;
    }

    public static void setSelectedWorkspaceName(String workspaceName) {
        selectedWorkspaceName = workspaceName;
    }

    public static String[] getSseFiles() {
        return new File(workspacesDir).list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(SUT_SETTINGS_EXT);
            }
        });
    }

    public static String getTestSettingsFile() {
        return workspacesDir + selectedWorkspaceName + File.separator + SETTINGS_FILE;
    }

    public static String getSelectedWorkspaceDir() {
        if (selectedWorkspaceName == null || selectedWorkspaceName.isBlank()) {
            return workspacesDir;
        }
        return workspacesDir + selectedWorkspaceName + File.separator;
    }

    public static String getWorkspaceOraclesDir() {
        return getSelectedWorkspaceDir() + "oracles" + File.separator;
    }

    public static String getWorkspaceOracleJavaDir() {
        return getWorkspaceOraclesDir() + "java" + File.separator;
    }

    public static String getWorkspaceOracleDslDir() {
        return getWorkspaceOraclesDir() + "dsl" + File.separator;
    }

    public static String getWorkspaceOracleCompiledDir() {
        return getWorkspaceOraclesDir() + "compiled" + File.separator;
    }
}
