/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.system;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.core.Assert;
import org.testar.core.alayer.Role;
import org.testar.core.exceptions.StateBuildException;
import org.testar.core.exceptions.SystemStartException;
import org.testar.core.exceptions.SystemStopException;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.core.util.Util;
import org.testar.windows.alayer.UIARoles;
import org.testar.windows.service.WindowsStateService;
import org.testar.windows.state.WinProcess;

import java.util.List;

public final class WindowsSystemStartupSupport {

    private static final Logger LOGGER = LogManager.getLogger();

    private WindowsSystemStartupSupport() {
    }

    public static SUT startExecutableAndWaitUntilAccessible(String path,
                                                            boolean processListenerEnabled,
                                                            String sutProcesses,
                                                            double startupTimeSeconds,
                                                            double stateTimeoutSeconds,
                                                            boolean accessBridgeEnabled) throws SystemStartException {
        Assert.notNull(path);

        boolean retryAfterStop = true;

        while (true) {
            SUT system = WinProcess.fromExecutable(path, processListenerEnabled, sutProcesses);
            long engageTimeMs = Math.round(startupTimeSeconds * 1000.0);

            try (WindowsStateService stateService = WindowsStateService.uiAutomation(
                    stateTimeoutSeconds,
                    accessBridgeEnabled,
                    sutProcesses
            )) {
                long startedAt = System.currentTimeMillis();
                while (System.currentTimeMillis() - startedAt < engageTimeMs) {
                    if (system.isRunning() && hasAccessibleWidgets(stateService, system)) {
                        return system;
                    }
                    Util.pauseMs(500);
                }
            }

            stopSystemQuietly(system);

            if (retryAfterStop) {
                retryAfterStop = false;
                continue;
            }

            if (path.contains("java -jar")) {
                String message = "Exception trying to launch: " + path + "\n"
                        + "1. Check whether current SUTs path is correctly defined \n";
                if (accessBridgeEnabled) {
                    throw new SystemStartException(
                            message + "2. Check if Java Access Bridge is enabled in the host systems"
                    );
                }
            }

            throw new SystemStartException(
                    "SUT not running with accessible UI after <" + Math.round(startupTimeSeconds * 2000.0) + "> ms!"
            );
        }
    }

    public static SUT connectByWindowTitle(String windowTitle,
                                           double maxEngageTimeSeconds,
                                           double stateTimeoutSeconds,
                                           boolean accessBridgeEnabled,
                                           String sutProcesses,
                                           boolean forceToForeground) throws SystemStartException {
        Assert.notNull(windowTitle);

        long startedAt = System.currentTimeMillis();
        long maxEngageTimeMs = Math.round(maxEngageTimeSeconds * 1000.0);

        try (WindowsStateService stateService = WindowsStateService.uiAutomation(
                stateTimeoutSeconds,
                accessBridgeEnabled,
                sutProcesses
        )) {
            while (System.currentTimeMillis() - startedAt < maxEngageTimeMs) {
                Util.pauseMs(100);
                List<SUT> systems = WinProcess.fromAll();
                if (systems == null) {
                    continue;
                }

                for (SUT system : systems) {
                    State state = getStateQuietly(stateService, system);
                    if (state == null) {
                        continue;
                    }
                    if (!state.get(Tags.Foreground, false) && !forceToForeground) {
                        continue;
                    }
                    if (containsWindowTitle(state, windowTitle)) {
                        System.out.println("SUT with Window Title -" + windowTitle + "- DETECTED!");
                        return system;
                    }
                }
            }
        }

        throw new SystemStartException("SUT Window Title not found!: -" + windowTitle + "-");
    }

    private static boolean hasAccessibleWidgets(WindowsStateService stateService, SUT system) {
        State state = getStateQuietly(stateService, system);
        if (state == null) {
            return false;
        }
        if (state.childCount() > 0) {
            return true;
        }
        LOGGER.debug("State accessible but empty while starting system");
        return false;
    }

    private static State getStateQuietly(WindowsStateService stateService, SUT system) {
        try {
            return stateService.getState(system);
        } catch (StateBuildException exception) {
            return null;
        }
    }

    private static boolean containsWindowTitle(State state, String windowTitle) {
        for (Widget widget : state) {
            Role role = widget.get(Tags.Role, null);
            if (role == null || !Role.isOneOf(role, UIARoles.UIAWindow)) {
                continue;
            }
            String title = widget.get(Tags.Title, null);
            if (title != null && title.contains(windowTitle)) {
                return true;
            }
        }
        return false;
    }

    private static void stopSystemQuietly(SUT system) {
        if (system == null) {
            return;
        }

        try {
            system.stop();
            Long pid = system.get(Tags.PID, null);
            if (pid != null && WinProcess.isRunning(pid)) {
                WinProcess.killProcess(pid);
            }
        } catch (SystemStopException exception) {
            LOGGER.debug("Unable to stop Windows system while retrying startup", exception);
        }
    }
}
