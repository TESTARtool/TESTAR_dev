/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2019-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2019-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.testar.config.ConfigTags;
import org.testar.config.TestarDirectories;
import org.testar.config.settings.Settings;
import org.testar.core.util.Util;

public class OutputStructure {

    private OutputStructure() { }

    public static final String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";

    public static String startOuterLoopDateString;
    public static String startInnerLoopDateString;

    public static String executedSUTname;
    public static int sequenceInnerLoopCount;

    public static String outerLoopOutputDir;
    public static String screenshotsOutputDir;
    public static String htmlOutputDir;
    public static String logsOutputDir;
    public static String debugLogsOutputDir;
    public static String processListenerDir;

    public static void calculateOuterLoopDateString() {
        startOuterLoopDateString = "";
        String date = Util.dateString(OutputStructure.DATE_FORMAT);
        date = date + "s";
        date = date.substring(0, 16) + "m" + date.substring(17);
        date = date.substring(0, 13) + "h" + date.substring(14);
        if (System.getenv("HOSTNAME") != null) {
            startOuterLoopDateString = System.getenv("HOSTNAME") + "_";
        }
        startOuterLoopDateString += date;
    }

    public static void calculateInnerLoopDateString() {
        String date = Util.dateString(OutputStructure.DATE_FORMAT);
        date = date + "s";
        date = date.substring(0, 16) + "m" + date.substring(17);
        date = date.substring(0, 13) + "h" + date.substring(14);
        startInnerLoopDateString = date;
    }

    public static void createOutputSUTname(Settings settings) {
        executedSUTname = "";

        if (settings.get(ConfigTags.ApplicationName, "").equals("")) {
            String sutConnectorValue = settings.get(ConfigTags.SUTConnectorValue);

            sutConnectorValue = sutConnectorValue.replace("/", File.separator);

            try {
                if (sutConnectorValue.contains("http") && sutConnectorValue.contains("www.")) {
                    int indexWWW = sutConnectorValue.indexOf("www.") + 4;
                    int indexEnd = sutConnectorValue.indexOf(".", indexWWW);
                    String domain = sutConnectorValue.substring(indexWWW, indexEnd);
                    executedSUTname = domain;
                } else if (sutConnectorValue.contains(".exe")) {
                    int startSUT = sutConnectorValue.lastIndexOf(File.separator) + 1;
                    int endSUT = sutConnectorValue.indexOf(".exe");
                    String sutName = sutConnectorValue.substring(startSUT, endSUT);
                    executedSUTname = sutName;
                } else if (sutConnectorValue.contains(".jar")) {
                    int startSUT = sutConnectorValue.lastIndexOf(File.separator) + 1;
                    int endSUT = sutConnectorValue.indexOf(".jar");
                    String sutName = sutConnectorValue.substring(startSUT, endSUT);
                    executedSUTname = sutName;
                }
            } catch (Exception e) {
                // e.printStackTrace();
                System.out.println("Warning: This run generation will be stored with empty name");
            }
        } else {
            executedSUTname = settings.get(ConfigTags.ApplicationName, "");
        }

        String version = settings.get(ConfigTags.ApplicationVersion, "");
        if (!version.isEmpty()) {
            executedSUTname += "_" + version;
        }
    }

    public static void createOutputFolders() {
        outerLoopOutputDir = TestarDirectories.getOutputDir() + File.separator + startOuterLoopDateString
                + "_" + executedSUTname;
        File runDir = new File(outerLoopOutputDir);
        runDir.mkdirs();

        //Check if main output folder was created correctly, if not use unknown name with timestamp
        if (!runDir.exists()) {
            runDir = new File(TestarDirectories.getOutputDir() + File.separator
                    + startOuterLoopDateString + "_unknown");
            runDir.mkdirs();
        }

        if (!runDir.exists() || !runDir.isDirectory()) {
            reportDirectoryCreationFailure("run directory", runDir);
        }

        screenshotsOutputDir = outerLoopOutputDir + File.separator + "scrshots";
        File scrnDir = new File(screenshotsOutputDir);
        if (!scrnDir.exists() && !scrnDir.mkdirs()) {
            reportDirectoryCreationFailure("screenshots directory", scrnDir);
        }

        htmlOutputDir = outerLoopOutputDir + File.separator + "reports";
        File htmlDir = new File(htmlOutputDir);
        if (!htmlDir.exists() && !htmlDir.mkdirs()) {
            reportDirectoryCreationFailure("reports directory", htmlDir);
        }

        logsOutputDir = outerLoopOutputDir + File.separator + "logs";
        File logsDir = new File(logsOutputDir);
        if (!logsDir.exists() && !logsDir.mkdirs()) {
            reportDirectoryCreationFailure("logs directory", logsDir);
        }

        debugLogsOutputDir = logsOutputDir + File.separator + "debug";
        File logsDebugDir = new File(debugLogsOutputDir);
        if (!logsDebugDir.exists() && !logsDebugDir.mkdirs()) {
            reportDirectoryCreationFailure("debug directory", logsDebugDir);
        }

        processListenerDir = logsOutputDir + File.separator + "processListener";
        File procListDir = new File(processListenerDir);
        if (!procListDir.exists() && !procListDir.mkdirs()) {
            reportDirectoryCreationFailure("process listener directory", procListDir);
        }
    }

    private static void reportDirectoryCreationFailure(String purpose, File directory) {
        File parent = directory.getParentFile();
        Path cwd = Paths.get(".").toAbsolutePath().normalize();

        System.err.println("ERROR: Failed to create output directory (" + purpose + ").");
        System.err.println("ERROR: Directory: <" + directory.getAbsolutePath() + ">"
                + " exists=" + directory.exists()
                + " isDirectory=" + directory.isDirectory()
                + " canWrite=" + directory.canWrite());
        if (parent != null) {
            System.err.println("ERROR: Parent: <" + parent.getAbsolutePath() + ">"
                    + " exists=" + parent.exists()
                    + " isDirectory=" + parent.isDirectory()
                    + " canWrite=" + parent.canWrite());
        }
        System.err.println("ERROR: Working directory: <" + cwd + ">");
        System.err.println("ERROR: Config OutputDir: <" + TestarDirectories.getOutputDir() + ">");
    }
}
