/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.statemodel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StateModelStorageBootstrap {

    private static final Logger logger = LogManager.getLogger();

    private StateModelStorageBootstrap() {
    }

    public static void setupOrientDB(String directoryPath, String database, String user, String pass) {
        String extractedOrientDB = downloadOrientDB(directoryPath);
        createOrientDB(extractedOrientDB, database, user, pass);
    }

    private static String downloadOrientDB(String directoryPath) {
        String downloadUrl = "https://repo1.maven.org/maven2/com/orientechnologies/orientdb-community/3.2.38/orientdb-community-3.2.38.zip";
        String zipFilePath = directoryPath + "/orientdb-community-3.2.38.zip";
        String extractedOrientDB = directoryPath + "/orientdb-community-3.2.38";

        // If OrientDB already exists, we dont need to download anything
        if(new File(extractedOrientDB).exists()) {
            logger.log(Level.INFO, "Using existing OrientDB: " + extractedOrientDB);
            return extractedOrientDB;
    }

        try {
            // Create the directory if it doesn't exist
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Download the zip file
            logger.log(Level.INFO, "Downloading OrientDB... " + downloadUrl);
            try (InputStream in = new URL(downloadUrl).openStream();
                    FileOutputStream out = new FileOutputStream(zipFilePath)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            // Extract the zip file
            logger.log(Level.INFO, "Extracting OrientDB... " + zipFilePath);
            try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
                ZipEntry entry;
                while ((entry = zipIn.getNextEntry()) != null) {
                    File filePath = new File(directoryPath, entry.getName());
                    if (entry.isDirectory()) {
                        filePath.mkdirs();
                    } else {
                        // Ensure parent directories exist
                        File parentDir = filePath.getParentFile();
                        if (!parentDir.exists()) {
                            parentDir.mkdirs();
                        }
                        try (FileOutputStream out = new FileOutputStream(filePath)) {
                            byte[] buffer = new byte[4096];
                            int len;
                            while ((len = zipIn.read(buffer)) > 0) {
                                out.write(buffer, 0, len);
                            }
                        }
                    }
                    zipIn.closeEntry();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.log(Level.INFO, "Extracted OrientDB: " + extractedOrientDB);
        return extractedOrientDB;
    }

    private static void createOrientDB(String extractedOrientDB, String database, String user, String pass) {
        try {
            // Change to the bin directory and execute the command
            logger.log(Level.INFO, "Creating OrientDB database... " + database);
            ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "console.bat", "CREATE", "DATABASE", "plocal:../databases/" + database, user, pass);
            processBuilder.directory(new File(extractedOrientDB + "/bin"));
            processBuilder.inheritIO();
            Process process = processBuilder.start();

            // Wait for the command to complete
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Command execution failed with exit code " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
