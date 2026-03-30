/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver;

import java.nio.file.Files;
import java.nio.file.Path;

public final class WebdriverPathResolver {

    private WebdriverPathResolver() {
    }

    public static Path resolveTestarHome() {
        String configuredHome = System.getProperty("testar.home");
        if (configuredHome != null && !configuredHome.isBlank()) {
            return Path.of(configuredHome).toAbsolutePath().normalize();
        }

        return Path.of(".").toAbsolutePath().normalize();
    }

    public static Path resolveChromeDownloadDir() {
        return resolveTestarHome().resolve("webdriver").resolve("chrome");
    }

    public static Path resolveExtensionDir() {
        Path testarHome = resolveTestarHome();
        Path[] candidates = new Path[]{
                testarHome.resolve("web-extension"),
                testarHome.resolve("webdriver").resolve("web-extension")
        };

        for (Path candidate : candidates) {
            if (Files.isDirectory(candidate)) {
                return candidate.toAbsolutePath().normalize();
            }
        }

        throw new IllegalStateException("Unable to locate web-extension directory for WebDriver");
    }
}
