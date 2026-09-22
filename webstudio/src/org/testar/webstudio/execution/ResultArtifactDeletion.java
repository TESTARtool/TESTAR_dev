/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.execution;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

final class ResultArtifactDeletion {

    private ResultArtifactDeletion() {
        // Utility class.
    }

    static void deleteResultFile(Path outputDirectory, String filePath) {
        Path resultFile = resolvePathInsideOutput(outputDirectory, filePath);
        if (!Files.isRegularFile(resultFile)) {
            throw new IllegalArgumentException("Result file not found: " + resultFile.getFileName());
        }

        try {
            Files.deleteIfExists(resultFile);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to delete result file: " + resultFile, exception);
        }
    }

    static void deleteResultGroup(Path outputDirectory, String groupPath) {
        Path resultGroup = resolvePathInsideOutput(outputDirectory, groupPath);
        if (!Files.isDirectory(resultGroup)) {
            throw new IllegalArgumentException("Result output folder not found: " + resultGroup.getFileName());
        }

        try {
            deleteDirectoryTree(resultGroup);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to delete result output folder: " + resultGroup, exception);
        }
    }

    private static Path resolvePathInsideOutput(Path outputDirectory, String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("Result path is required");
        }

        Path normalizedOutputDirectory = outputDirectory.toAbsolutePath().normalize();
        Path resolvedPath = Path.of(filePath).toAbsolutePath().normalize();
        if (!resolvedPath.startsWith(normalizedOutputDirectory)) {
            throw new IllegalArgumentException("Invalid result path: " + resolvedPath);
        }

        return resolvedPath;
    }

    private static void deleteDirectoryTree(Path directory) throws IOException {
        try (Stream<Path> paths = Files.walk(directory)) {
            List<Path> deletePaths = paths
                .sorted(Comparator.reverseOrder())
                .toList();

            for (Path path : deletePaths) {
                Files.deleteIfExists(path);
            }
        }
    }
}
