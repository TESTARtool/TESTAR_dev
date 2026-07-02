/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.testgoal;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.testar.webstudio.api.dto.TestGoalFileDto;
import org.testar.webstudio.api.dto.TestGoalNodeDto;

public final class TestGoalService {

    private final Path settingsRoot;

    public TestGoalService(Path testarHomeDirectory) {
        this.settingsRoot = testarHomeDirectory
                .resolve("settings")
                .toAbsolutePath()
                .normalize();
    }

    public Path testGoalsRoot(String workspaceName) {
        Path testGoalsRoot = resolveWorkspaceTestGoalsRoot(workspaceName);
        ensureRootDirectory(testGoalsRoot);
        return testGoalsRoot;
    }

    public TestGoalNodeDto tree(String workspaceName) {
        Path testGoalsRoot = testGoalsRoot(workspaceName);
        return toNode(testGoalsRoot, testGoalsRoot);
    }

    public TestGoalFileDto readFile(String workspaceName, String relativePath) {
        Path testGoalsRoot = testGoalsRoot(workspaceName);
        Path file = resolveInsideRoot(testGoalsRoot, relativePath);
        if (!Files.isRegularFile(file)) {
            throw new IllegalArgumentException("Test goal file does not exist: " + relativePath);
        }

        try {
            return new TestGoalFileDto(
                    file.getFileName().toString(),
                    toRelativePath(testGoalsRoot, file),
                    Files.readString(file, StandardCharsets.UTF_8),
                    isExecutableGoalFile(file)
            );
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read test goal file: " + relativePath, exception);
        }
    }

    public TestGoalFileDto saveFile(String workspaceName, String relativePath, String content) {
        Path testGoalsRoot = testGoalsRoot(workspaceName);
        Path file = resolveInsideRoot(testGoalsRoot, relativePath);
        if (Files.isDirectory(file)) {
            throw new IllegalArgumentException("Test goal path is a directory: " + relativePath);
        }

        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, content == null ? "" : content, StandardCharsets.UTF_8);
            return readFile(workspaceName, toRelativePath(testGoalsRoot, file));
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to save test goal file: " + relativePath, exception);
        }
    }

    public TestGoalFileDto createFile(String workspaceName, String relativePath) {
        Path testGoalsRoot = testGoalsRoot(workspaceName);
        Path file = resolveInsideRoot(testGoalsRoot, relativePath);
        if (Files.exists(file)) {
            throw new IllegalArgumentException("Test goal file already exists: " + relativePath);
        }

        String initialContent = isExecutableGoalFile(file)
                ? "version: 1\r\ngoals:\r\n  - id: new-test-goal\r\n    title: New test goal\r\n    objective: Describe the objective.\r\n    expected_outcomes:\r\n      - Describe the expected outcome.\r\n"
                : "";
        return saveFile(workspaceName, relativePath, initialContent);
    }

    public TestGoalNodeDto createFolder(String workspaceName, String relativePath) {
        Path testGoalsRoot = testGoalsRoot(workspaceName);
        Path folder = resolveInsideRoot(testGoalsRoot, relativePath);
        try {
            Files.createDirectories(folder);
            return tree(workspaceName);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to create test goal folder: " + relativePath, exception);
        }
    }

    public TestGoalNodeDto delete(String workspaceName, String relativePath) {
        Path testGoalsRoot = testGoalsRoot(workspaceName);
        Path target = resolveInsideRoot(testGoalsRoot, relativePath);
        if (target.equals(testGoalsRoot)) {
            throw new IllegalArgumentException("Cannot delete the test goals root folder.");
        }

        if (!Files.exists(target)) {
            return tree(workspaceName);
        }

        try (Stream<Path> paths = Files.walk(target)) {
            List<Path> sortedPaths = paths
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());
            for (Path path : sortedPaths) {
                Files.deleteIfExists(path);
            }
            return tree(workspaceName);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to delete test goal path: " + relativePath, exception);
        }
    }

    private TestGoalNodeDto toNode(Path testGoalsRoot, Path path) {
        String type = Files.isDirectory(path) ? "folder" : "file";
        List<TestGoalNodeDto> children = List.of();

        if (Files.isDirectory(path)) {
            try (Stream<Path> stream = Files.list(path)) {
                children = stream
                        .sorted(this::comparePaths)
                        .map(child -> toNode(testGoalsRoot, child))
                        .collect(Collectors.toList());
            } catch (IOException exception) {
                throw new IllegalStateException("Unable to list test goal folder: " + path, exception);
            }
        }

        return new TestGoalNodeDto(
                path.equals(testGoalsRoot) ? "test_goals" : path.getFileName().toString(),
                path.equals(testGoalsRoot) ? "" : toRelativePath(testGoalsRoot, path),
                type,
                isExecutableGoalFile(path),
                children
        );
    }

    private int comparePaths(Path left, Path right) {
        boolean leftDirectory = Files.isDirectory(left);
        boolean rightDirectory = Files.isDirectory(right);
        if (leftDirectory != rightDirectory) {
            return leftDirectory ? -1 : 1;
        }

        return left.getFileName().toString().compareToIgnoreCase(right.getFileName().toString());
    }

    private Path resolveInsideRoot(Path testGoalsRoot, String relativePath) {
        ensureRootDirectory(testGoalsRoot);
        String normalizedRelativePath = relativePath == null ? "" : relativePath.replace('\\', '/').trim();
        if (normalizedRelativePath.isBlank()) {
            return testGoalsRoot;
        }

        Path resolvedPath = testGoalsRoot.resolve(normalizedRelativePath).toAbsolutePath().normalize();
        if (!resolvedPath.startsWith(testGoalsRoot)) {
            throw new IllegalArgumentException("Invalid test goal path: " + relativePath);
        }

        rejectExistingSymlinkEscape(testGoalsRoot, resolvedPath, relativePath);
        return resolvedPath;
    }

    private void rejectExistingSymlinkEscape(Path testGoalsRoot, Path resolvedPath, String relativePath) {
        if (!Files.exists(resolvedPath)) {
            return;
        }

        try {
            Path realRoot = testGoalsRoot.toRealPath();
            Path realPath = resolvedPath.toRealPath();
            if (!realPath.startsWith(realRoot)) {
                throw new IllegalArgumentException("Invalid test goal path: " + relativePath);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to resolve test goal path: " + relativePath, exception);
        }
    }

    private String toRelativePath(Path testGoalsRoot, Path path) {
        return testGoalsRoot.relativize(path.toAbsolutePath().normalize()).toString().replace('\\', '/');
    }

    private boolean isExecutableGoalFile(Path path) {
        String fileName = path.getFileName() == null ? "" : path.getFileName().toString().toLowerCase();
        return fileName.endsWith(".yaml") || fileName.endsWith(".yml");
    }

    private Path resolveWorkspaceTestGoalsRoot(String workspaceName) {
        String normalizedWorkspaceName = workspaceName == null ? "" : workspaceName.trim();
        if (normalizedWorkspaceName.isBlank()
                || normalizedWorkspaceName.contains("/")
                || normalizedWorkspaceName.contains("\\")) {
            throw new IllegalArgumentException("Invalid workspace name: " + workspaceName);
        }

        Path workspaceRoot = settingsRoot.resolve(normalizedWorkspaceName).toAbsolutePath().normalize();
        if (!workspaceRoot.startsWith(settingsRoot) || workspaceRoot.equals(settingsRoot)) {
            throw new IllegalArgumentException("Invalid workspace name: " + workspaceName);
        }

        return workspaceRoot.resolve("test_goals").toAbsolutePath().normalize();
    }

    private void ensureRootDirectory(Path testGoalsRoot) {
        try {
            Files.createDirectories(testGoalsRoot);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to create test goals root: " + testGoalsRoot, exception);
        }
    }
}
