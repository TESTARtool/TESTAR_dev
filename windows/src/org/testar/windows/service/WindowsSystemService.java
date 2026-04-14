/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.service;

import org.testar.core.Assert;
import org.testar.core.exceptions.SystemStartException;
import org.testar.core.exceptions.SystemStopException;
import org.testar.core.service.SystemService;
import org.testar.core.state.SUT;
import org.testar.core.tag.Tags;
import org.testar.windows.system.WindowsSystemStartupSupport;
import org.testar.windows.state.WinProcess;

/**
 * Windows implementation of {@link SystemService} with explicit startup
 * strategies. Composition code such as CLI can choose the appropriate startup
 * mode without changing the core contract.
 */
public final class WindowsSystemService implements SystemService {

    @FunctionalInterface
    public interface Starter {
        SUT start() throws SystemStartException;
    }

    private final Starter starter;

    public WindowsSystemService(Starter starter) {
        this.starter = Assert.notNull(starter);
    }

    public static WindowsSystemService fromProcessId(long pid) {
        return new WindowsSystemService(() -> WinProcess.fromPID(pid));
    }

    public static WindowsSystemService fromProcessName(String processName) {
        return new WindowsSystemService(() -> WinProcess.fromProcName(processName));
    }

    public static WindowsSystemService fromExecutable(String path) {
        return fromExecutable(path, false, "");
    }

    public static WindowsSystemService fromExecutable(String path,
                                                      boolean processListenerEnabled,
                                                      String sutProcesses) {
        return new WindowsSystemService(
                () -> WinProcess.fromExecutable(path, processListenerEnabled, sutProcesses)
        );
    }

    public static WindowsSystemService fromExecutable(String path,
                                                      boolean processListenerEnabled,
                                                      String sutProcesses,
                                                      double startupTimeSeconds,
                                                      double stateTimeoutSeconds,
                                                      boolean accessBridgeEnabled) {
        return new WindowsSystemService(
                () -> WindowsSystemStartupSupport.startExecutableAndWaitUntilAccessible(
                        path,
                        processListenerEnabled,
                        sutProcesses,
                        startupTimeSeconds,
                        stateTimeoutSeconds,
                        accessBridgeEnabled
                )
        );
    }

    public static WindowsSystemService fromWindowTitle(String windowTitle,
                                                       double maxEngageTimeSeconds,
                                                       double stateTimeoutSeconds,
                                                       boolean accessBridgeEnabled,
                                                       String sutProcesses,
                                                       boolean forceToForeground) {
        return new WindowsSystemService(
                () -> WindowsSystemStartupSupport.connectByWindowTitle(
                        windowTitle,
                        maxEngageTimeSeconds,
                        stateTimeoutSeconds,
                        accessBridgeEnabled,
                        sutProcesses,
                        forceToForeground
                )
        );
    }

    public static WindowsSystemService fromExecutableUwp(String appUserModelId) {
        return new WindowsSystemService(() -> WinProcess.fromExecutableUwp(appUserModelId));
    }

    @Override
    public SUT startSystem() throws SystemStartException {
        return starter.start();
    }

    @Override
    public void stopSystem(SUT system) {
        if (system == null) {
            return;
        }
        Long pid = system.get(Tags.PID, null);
        try {
            system.stop();
            if (pid != null && WinProcess.isRunning(pid)) {
                WinProcess.killProcess(pid);
            }
        } catch (SystemStopException exception) {
            throw new IllegalStateException("Unable to stop Windows system", exception);
        }
    }
}
