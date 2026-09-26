/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */
package org.testar.webstudio.workspace;

import org.testar.config.verdict.VerdictProcessing;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class WorkspaceIgnoredVerdictsService {

    private final WorkspaceService workspaceService;

    public WorkspaceIgnoredVerdictsService(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    public List<String> list(String workspaceName) {
        Path file = ignoreFile(workspaceName);
        if (!Files.exists(file)) {
            return List.of();
        }
        try {
            return Files.readAllLines(file, StandardCharsets.UTF_8).stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .distinct()
                .toList();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read ignored verdicts", exception);
        }
    }

    public List<String> remove(String workspaceName, List<String> selected) {
        if (selected == null || selected.isEmpty()) {
            throw new IllegalArgumentException("Select at least one ignored verdict to remove.");
        }
        Path file = ignoreFile(workspaceName);
        Set<String> selectedEntries = Set.copyOf(selected);
        List<String> current = list(workspaceName);
        List<String> remaining = new ArrayList<>(current);
        remaining.removeIf(selectedEntries::contains);
        if (!remaining.equals(current)) {
            write(file, remaining);
        }
        return remaining;
    }

    public List<String> clear(String workspaceName) {
        Path file = ignoreFile(workspaceName);
        if (Files.exists(file)) {
            write(file, List.of());
        }
        return List.of();
    }

    private Path ignoreFile(String workspaceName) {
        return workspaceService.workspaceDirectory(workspaceName)
            .resolve(VerdictProcessing.LIST_VERDICTS_FAILURES_FILENAME);
    }

    private void write(Path file, List<String> lines) {
        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to update ignored verdicts", exception);
        }
    }
}
