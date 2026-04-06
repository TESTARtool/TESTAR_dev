/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.util;

import java.nio.file.Path;
import java.util.Objects;

public final class RuntimePathsUtil {

    private static final String TESTAR_HOME_PROPERTY = "testar.home";

    private RuntimePathsUtil() {
    }

    public static Path resolveTestarHome() {
        String configuredHome = System.getProperty(TESTAR_HOME_PROPERTY);
        if (configuredHome != null && !configuredHome.isBlank()) {
            return Path.of(configuredHome).toAbsolutePath().normalize();
        }

        String classPath = System.getProperty("java.class.path", "");
        String[] entries = classPath.split(java.util.regex.Pattern.quote(System.getProperty("path.separator")));
        for (String entry : entries) {
            if (entry == null || entry.isBlank()) {
                continue;
            }

            Path candidate;
            try {
                candidate = Path.of(entry).toAbsolutePath().normalize();
            } catch (RuntimeException exception) {
                continue;
            }
            Path parent = candidate.getParent();
            if (parent != null && ".runtime".equals(parent.getFileName() != null ? parent.getFileName().toString() : "")) {
                Path testarHome = parent.getParent();
                if (testarHome != null) {
                    return testarHome;
                }
            }

            if (parent != null && "lib".equals(parent.getFileName() != null ? parent.getFileName().toString() : "")) {
                Path runtimeDirectory = parent.getParent();
                if (runtimeDirectory != null
                        && ".runtime".equals(runtimeDirectory.getFileName() != null ? runtimeDirectory.getFileName().toString() : "")) {
                    Path testarHome = runtimeDirectory.getParent();
                    if (testarHome != null) {
                        return testarHome;
                    }
                }
            }
        }

        return Path.of(".").toAbsolutePath().normalize();
    }

    public static Path resolveRuntimeDirectory() {
        return resolveTestarHome().resolve(".runtime").toAbsolutePath().normalize();
    }

    public static Path resolveAgainstTestarHome(String path) {
        return resolveAgainst(resolveTestarHome(), path);
    }

    public static Path resolveAgainstRuntimeDirectory(String path) {
        return resolveAgainst(resolveRuntimeDirectory(), path);
    }

    private static Path resolveAgainst(Path basePath, String path) {
        Objects.requireNonNull(basePath, "Base path cannot be null");

        if (path == null || path.isBlank()) {
            return basePath;
        }

        Path candidate = Path.of(path);
        if (candidate.isAbsolute()) {
            return candidate.normalize();
        }

        return basePath.resolve(candidate).normalize();
    }
}
