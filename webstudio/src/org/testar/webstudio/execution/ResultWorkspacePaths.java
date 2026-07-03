/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.execution;

import java.nio.file.Path;

final class ResultWorkspacePaths {

    private ResultWorkspacePaths() { }

    static Path workspaceOutputDirectory(Path runtimeHome, String workspaceName) {
        return runtimeHome
            .resolve("output")
            .resolve(validWorkspaceName(workspaceName))
            .toAbsolutePath()
            .normalize();
    }

    static String workspaceOutputSettingValue(String workspaceName) {
        return "./output/" + validWorkspaceName(workspaceName);
    }

    private static String validWorkspaceName(String workspaceName) {
        if (workspaceName == null || workspaceName.isBlank()) {
            throw new IllegalArgumentException("Workspace name is required for output results");
        }

        String normalizedWorkspaceName = workspaceName.trim();
        if (normalizedWorkspaceName.equals(".")
                || normalizedWorkspaceName.equals("..")
                || normalizedWorkspaceName.contains("/")
                || normalizedWorkspaceName.contains("\\")) {
            throw new IllegalArgumentException("Invalid workspace name for output results: " + workspaceName);
        }

        return normalizedWorkspaceName;
    }
}
