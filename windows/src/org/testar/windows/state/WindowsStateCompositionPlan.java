/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.state;

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
                                                    boolean javaAccessBridge,
                                                    String sutProcesses) {
        return StateCompositionPlan.fullState(
                WindowsStateService.uiAutomation(
                        timeoutSeconds,
                        javaAccessBridge,
                        sutProcesses
                )
        );
    }

    public static StateCompositionPlan uiAutomationLeafWidgets(double timeoutSeconds,
                                                               boolean javaAccessBridge,
                                                               String sutProcesses) {
        return StateCompositionPlan.leafWidgets(
                WindowsStateService.uiAutomation(
                        timeoutSeconds,
                        javaAccessBridge,
                        sutProcesses
                )
        );
    }

    public static StateCompositionPlan uiAutomationTextWidgets(double timeoutSeconds,
                                                               boolean javaAccessBridge,
                                                               String sutProcesses) {
        return StateCompositionPlan.widgetsWithText(
                WindowsStateService.uiAutomation(
                        timeoutSeconds,
                        javaAccessBridge,
                        sutProcesses
                ),
                Tags.Title
        );
    }

    public static StateCompositionPlan uiAutomationSemanticWidgets(double timeoutSeconds,
                                                                   boolean javaAccessBridge,
                                                                   String sutProcesses) {
        return StateCompositionPlan.semanticWidgets(
                WindowsStateService.uiAutomation(
                        timeoutSeconds,
                        javaAccessBridge,
                        sutProcesses
                ),
                new WindowsSemanticWidgetDescriptor()
        );
    }
}
