/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webdriver.service;

import org.testar.core.state.SUT;
import org.testar.core.tag.Tags;
import org.testar.windows.Windows;
import org.testar.windows.state.WinProcess;

public final class WebdriverWindowHandleSupport {

    private WebdriverWindowHandleSupport() {
    }

    public static void bindWindowHandle(SUT system) {
        if (!isWindows() || system == null || system.get(Tags.HWND, null) != null) {
            return;
        }

        try {
            long hwnd = Windows.GetForegroundWindow();
            long pid = Windows.GetWindowProcessId(hwnd);

            if (WinProcess.procName(pid).contains("chrome")) {
                system.set(Tags.HWND, hwnd);
                system.set(Tags.PID, pid);
                System.out.printf("INFO System PID %d and window handle %d have been set\n", pid, hwnd);
            }
        } catch (ExceptionInInitializerError | NoClassDefFoundError exception) {
            System.out.println("INFO: We can not obtain the Windows 10 windows handle of WebDriver browser instance");
        }
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").contains("Windows");
    }
}
