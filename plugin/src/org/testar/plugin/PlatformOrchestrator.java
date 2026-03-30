/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin;

import org.testar.core.execution.SystemService;
import org.testar.engine.action.DefaultActionExecutionService;
import org.testar.engine.state.DefaultStateService;
import org.testar.windows.service.WindowsStateService;
import org.testar.windows.service.WindowsSystemService;

/**
 * TESTAR platform/plugin orchestration
 */
public final class PlatformOrchestrator {

    private PlatformOrchestrator() {
    }

    public static String moduleName() {
        return "plugin";
    }

    public static PlatformServices resolve(PlatformSessionSpec sessionSpec) {
        switch (sessionSpec.getOperatingSystem()) {
            case WINDOWS:
            case WINDOWS_7:
            case WINDOWS_10:
                return windows(sessionSpec);
            default:
                throw new UnsupportedPlatformException(
                        "Unsupported operating system: " + sessionSpec.getOperatingSystem()
                );
        }
    }

    private static PlatformServices windows(PlatformSessionSpec sessionSpec) {
        SystemService systemService;
        switch (sessionSpec.getTargetType()) {
            case EXECUTABLE:
                systemService = WindowsSystemService.fromExecutable(
                        sessionSpec.getTarget(),
                        sessionSpec.isProcessListenerEnabled(),
                        sessionSpec.getSutProcesses()
                );
                break;
            case PROCESS_NAME:
                systemService = WindowsSystemService.fromProcessName(sessionSpec.getTarget());
                break;
            case PROCESS_ID:
                systemService = WindowsSystemService.fromProcessId(Long.parseLong(sessionSpec.getTarget()));
                break;
            case UWP:
                systemService = WindowsSystemService.fromExecutableUwp(sessionSpec.getTarget());
                break;
            default:
                throw new UnsupportedPlatformException(
                        "Unsupported Windows target type: " + sessionSpec.getTargetType()
                );
        }

        return new PlatformServices(
                systemService,
                new DefaultStateService(
                        WindowsStateService.uiAutomation(
                                sessionSpec.getStateTimeoutSeconds(),
                                sessionSpec.isAccessBridgeEnabled(),
                                sessionSpec.getSutProcesses()
                        )
                ),
                WindowsDesktopActionDerivationFactory.create(sessionSpec.getProcessesToKillDuringTest()),
                new DefaultActionExecutionService()
        );
    }
}
