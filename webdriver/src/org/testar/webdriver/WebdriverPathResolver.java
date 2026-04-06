/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver;

import java.nio.file.Files;
import java.nio.file.Path;

import org.testar.core.util.RuntimePathsUtil;

public final class WebdriverPathResolver {

    private WebdriverPathResolver() {
    }

    public static Path resolveTestarHome() {
        return RuntimePathsUtil.resolveTestarHome();
    }

    public static Path resolveChromeDownloadDir() {
        return RuntimePathsUtil.resolveRuntimeDirectory().resolve("webdriver").resolve("chrome");
    }

    public static Path resolveExtensionDir() {
        Path[] candidates = new Path[]{
                RuntimePathsUtil.resolveRuntimeDirectory().resolve("web-extension")
        };

        for (Path candidate : candidates) {
            if (Files.isDirectory(candidate)) {
                return candidate.toAbsolutePath().normalize();
            }
        }

        throw new IllegalStateException("Unable to locate web-extension directory for WebDriver");
    }
}
