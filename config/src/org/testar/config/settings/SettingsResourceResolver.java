/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.config.settings;

import java.io.File;

public final class SettingsResourceResolver {

    private SettingsResourceResolver() {
    }

    public static File resolve(String configuredPath) {
        File resourceFile = new File(configuredPath);
        if (resourceFile.isAbsolute()) {
            return resourceFile;
        }

        String settingsPath = Settings.getSettingsPath();
        if (settingsPath == null || settingsPath.isBlank()) {
            return resourceFile;
        }

        File settingsDirectory = new File(settingsPath);
        File workspaceRelativeFile = new File(settingsDirectory, configuredPath);
        if (workspaceRelativeFile.exists()) {
            return workspaceRelativeFile;
        }

        File settingsRootDirectory = settingsDirectory.getParentFile();
        if (settingsRootDirectory != null) {
            File settingsRootRelativeFile = new File(settingsRootDirectory, configuredPath);
            if (settingsRootRelativeFile.exists()) {
                return settingsRootRelativeFile;
            }

            File runtimeRootDirectory = settingsRootDirectory.getParentFile();
            if (runtimeRootDirectory != null) {
                File runtimeRootRelativeFile = new File(runtimeRootDirectory, configuredPath);
                if (runtimeRootRelativeFile.exists()) {
                    return runtimeRootRelativeFile;
                }
            }
        }

        return workspaceRelativeFile;
    }
}
