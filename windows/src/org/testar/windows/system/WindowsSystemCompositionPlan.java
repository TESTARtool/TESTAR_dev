/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.system;

import org.testar.engine.system.SystemCompositionPlan;
import org.testar.windows.service.WindowsSystemService;

/**
 * Reusable default system composition plan for Windows-driven desktop testing.
 */
public final class WindowsSystemCompositionPlan {

    private WindowsSystemCompositionPlan() {
    }

    public static SystemCompositionPlan fromProcessId(long pid) {
        return SystemCompositionPlan.basic(WindowsSystemService.fromProcessId(pid));
    }

    public static SystemCompositionPlan fromProcessName(String processName) {
        return SystemCompositionPlan.basic(WindowsSystemService.fromProcessName(processName));
    }

    public static SystemCompositionPlan fromExecutable(String path,
                                                       boolean processListenerEnabled,
                                                       String sutProcesses) {
        return SystemCompositionPlan.basic(
                WindowsSystemService.fromExecutable(path, processListenerEnabled, sutProcesses)
        );
    }

    public static SystemCompositionPlan fromExecutable(String path,
                                                       boolean processListenerEnabled,
                                                       String sutProcesses,
                                                       double startupTimeSeconds,
                                                       double stateTimeoutSeconds,
                                                       boolean accessBridgeEnabled) {
        return SystemCompositionPlan.basic(
                WindowsSystemService.fromExecutable(
                        path,
                        processListenerEnabled,
                        sutProcesses,
                        startupTimeSeconds,
                        stateTimeoutSeconds,
                        accessBridgeEnabled
                )
        );
    }

    public static SystemCompositionPlan fromWindowTitle(String windowTitle,
                                                        double maxEngageTimeSeconds,
                                                        double stateTimeoutSeconds,
                                                        boolean accessBridgeEnabled,
                                                        String sutProcesses,
                                                        boolean forceToForeground) {
        return SystemCompositionPlan.basic(
                WindowsSystemService.fromWindowTitle(
                        windowTitle,
                        maxEngageTimeSeconds,
                        stateTimeoutSeconds,
                        accessBridgeEnabled,
                        sutProcesses,
                        forceToForeground
                )
        );
    }

    public static SystemCompositionPlan fromExecutableUwp(String appUserModelId) {
        return SystemCompositionPlan.basic(WindowsSystemService.fromExecutableUwp(appUserModelId));
    }
}
