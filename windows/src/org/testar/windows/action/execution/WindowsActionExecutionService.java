/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.action.execution;

import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.service.ActionExecutionService;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.util.Util;
import org.testar.windows.state.WinProcess;

/**
 * Windows execution service that waits for CPU activity to settle after an action.
 */
public final class WindowsActionExecutionService implements ActionExecutionService {

    private static final double MAX_ACTION_WAIT_FRAME = 1.0d;
    private static final double MINIMUM_HALF_WAIT = 0.01d;

    private final ActionExecutionService delegate;
    private final double timeToWaitAfterAction;

    public WindowsActionExecutionService(ActionExecutionService delegate, double timeToWaitAfterAction) {
        this.delegate = Assert.notNull(delegate);
        this.timeToWaitAfterAction = timeToWaitAfterAction;
    }

    @Override
    public boolean executeAction(SUT system, State state, Action action) {
        boolean executed = delegate.executeAction(system, state, action);
        if (!executed || !(system instanceof WinProcess)) {
            return executed;
        }

        double halfWait = calculateHalfWait();
        int waitCycles = (int) (MAX_ACTION_WAIT_FRAME / halfWait);
        long actionCpu;
        WinProcess winProcess = (WinProcess) system;

        do {
            long[] cpuBefore = WinProcess.getCPUsage(winProcess);
            Util.pause(halfWait);
            long[] cpuAfter = WinProcess.getCPUsage(winProcess);
            actionCpu = (cpuAfter[0] + cpuAfter[1] - cpuBefore[0] - cpuBefore[1]);
            waitCycles--;
        } while (actionCpu > 0 && waitCycles > 0);

        return true;
    }

    private double calculateHalfWait() {
        if (timeToWaitAfterAction <= 0.0d) {
            return MINIMUM_HALF_WAIT;
        }

        return timeToWaitAfterAction / 2.0d;
    }
}
