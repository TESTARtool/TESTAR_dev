/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.tag;

import org.testar.core.tag.Tags;
import org.testar.engine.state.StateCompositionPlan;
import org.testar.windows.service.WindowsStateService;

/**
 * Reusable default state composition plan for Windows-driven desktop testing.
 */
public final class WindowsStateCompositionPlan {

    private WindowsStateCompositionPlan() {
    }

    public static StateCompositionPlan uiAutomation() {
        return StateCompositionPlan.fullState(WindowsStateService.uiAutomation());
    }

    public static StateCompositionPlan uiAutomation(double timeoutSeconds,
                                                    boolean accessBridgeEnabled,
                                                    String sutProcesses) {
        return StateCompositionPlan.fullState(
                WindowsStateService.uiAutomation(
                        timeoutSeconds,
                        accessBridgeEnabled,
                        sutProcesses
                )
        );
    }

    public static StateCompositionPlan uiAutomationLeafWidgets(double timeoutSeconds,
                                                               boolean accessBridgeEnabled,
                                                               String sutProcesses) {
        return StateCompositionPlan.leafWidgets(
                WindowsStateService.uiAutomation(
                        timeoutSeconds,
                        accessBridgeEnabled,
                        sutProcesses
                )
        );
    }

    public static StateCompositionPlan uiAutomationTextWidgets(double timeoutSeconds,
                                                               boolean accessBridgeEnabled,
                                                               String sutProcesses) {
        return StateCompositionPlan.widgetsWithText(
                WindowsStateService.uiAutomation(
                        timeoutSeconds,
                        accessBridgeEnabled,
                        sutProcesses
                ),
                Tags.Title
        );
    }
}
