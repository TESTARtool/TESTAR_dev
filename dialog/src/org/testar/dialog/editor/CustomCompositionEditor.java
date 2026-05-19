/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.dialog.editor;

public final class CustomCompositionEditor extends AbstractTextFileEditor {

    private static final long serialVersionUID = 6873968608284840042L;

    public CustomCompositionEditor(String compositionResourcePath) {
        super(
                "Custom Composition Editor",
                compositionResourcePath,
                String.join(
                        System.lineSeparator(),
                        "# Scriptless custom composition resource",
                        "# Select one of: windows_composition, webdriver_composition, android_composition",
                        "baseProfile=windows_composition",
                        "",
                        "# Optional capability wrappers.",
                        "settingsCapabilityClass=",
                        "testSessionCapabilityClass=",
                        "testSequenceCapabilityClass=",
                        "stopCriteriaCapabilityClass=",
                        "",
                        "# Optional service wrappers.",
                        "systemServiceClass=",
                        "stateServiceClass=",
                        "actionDerivationServiceClass=",
                        "actionSelectorServiceClass=",
                        "actionExecutionServiceClass=",
                        "oracleComposerClass=",
                        ""
                )
        );
    }
}
