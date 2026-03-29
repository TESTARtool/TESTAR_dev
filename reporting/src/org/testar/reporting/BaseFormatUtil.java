/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2023-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2023-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.reporting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public abstract class BaseFormatUtil {
    private File file;
    protected ArrayList<String> content = new ArrayList<>();
    private final String fileSuffix; // lower case, includes period

    public File getFile() {
        return file;
    }

    protected BaseFormatUtil(String fileString, String suffix) {
        fileSuffix = (suffix.toLowerCase().startsWith(".")) ? suffix : "." + suffix; //add period if not included
        this.file = new File(enforceFileSuffix(fileString));
        createFile();
    }

    private String enforceFileSuffix(String fileName) {
        return fileName.toLowerCase().endsWith(fileSuffix) ? fileName : fileName + fileSuffix;
    }

    protected String[] splitStringAtNewline(String longString) {
        return longString.split("\\r?\\n|\\r");
    }

    private void createFile() {
        if (!file.exists()) {
            File parentDirectory = file.getParentFile();
            if (parentDirectory != null && !parentDirectory.exists() && (!parentDirectory.mkdirs())) {
                //try to create the needed directories
                System.err.println("Failed to create the directory structure.");
                System.err.println("File: <" + file.getAbsolutePath() + ">");
                System.err.println("Parent: <" + parentDirectory.getAbsolutePath() + ">"
                        + " exists=" + parentDirectory.exists()
                        + " isDirectory=" + parentDirectory.isDirectory()
                        + " canWrite=" + parentDirectory.canWrite());
                System.err.println("Working directory: <" + Paths.get(".").toAbsolutePath().normalize() + ">");
            }
            try {
                file.createNewFile();
            } catch (Exception e) {
                System.err.println("Error creating the file: " + e.getMessage());
                System.err.println("File: <" + file.getAbsolutePath() + ">");
            }
        }
    }

    public void appendToFileName(String appendToName) {
        try {
            Path oldFile = Paths.get(file.getAbsolutePath()); // get full name
            Path directory = Paths.get(file.getParent()); //get directory
            String newName = enforceFileSuffix(file.getName().replace(getFileExtension(file.getName()), appendToName));
            Files.move(oldFile, oldFile.resolveSibling(newName));
            file = new File(directory + File.separator + newName); //update the file path
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        return (lastDotIndex == -1) ? ".html" : fileName.substring(lastDotIndex);
    }

    public void renameFile(String newName) {
        try {
            Path path = Paths.get(file.getAbsolutePath()); // get full name
            Files.move(path, path.resolveSibling(enforceFileSuffix(newName)));
            file = new File(path.getParent() + File.separator + enforceFileSuffix(newName)); //update the file path
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void moveFile(String newDirectory) {
        try {
            Path newPath = Paths.get(newDirectory);
            Path oldPath = Paths.get(file.getAbsolutePath()); //full name
            String fileName = file.getName();
            Files.move(oldPath, newPath.resolve(fileName));
            file = new File(newPath + File.separator + file.getName()); //update the file path
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void duplicateFile(String destinationPath) {
        try {
            Files.copy(Paths.get(file.getAbsolutePath()), Paths.get(destinationPath),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (FileNotFoundException e) {
            System.out.println("Error copying the file: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToFile() {
        if (!content.isEmpty()) {
            try {
                PrintWriter writer = new PrintWriter(
                        new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8));

                for (String str : content) {
                    writer.println(str);
                }

                writer.close();
                content.clear(); //empty the queue
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
