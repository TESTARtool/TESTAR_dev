/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.dialog.editor;

public final class CustomPoliciesEditor extends AbstractTextFileEditor {

    private static final long serialVersionUID = -2269784771514583608L;

    public CustomPoliciesEditor(String policiesResourcePath) {
        super(
                "Custom Policies Editor",
                policiesResourcePath,
                String.join(
                        System.lineSeparator(),
                        "# Scriptless custom policies resource",
                        "# Additive policy composition. Multiple classes can be declared with ';'",
                        "# Set replace...=true to ignore the built-in policies for that seam.",
                        "",
                        "replaceClickablePolicies=false",
                        "clickablePolicies=",
                        "replaceTypeablePolicies=false",
                        "typeablePolicies=",
                        "replaceScrollablePolicies=false",
                        "scrollablePolicies=",
                        "replaceSelectablePolicies=false",
                        "selectablePolicies=",
                        "replaceEnabledPolicies=false",
                        "enabledPolicies=",
                        "replaceBlockedPolicies=false",
                        "blockedPolicies=",
                        "replaceWidgetFilterPolicies=false",
                        "widgetFilterPolicies=",
                        "replaceVisiblePolicies=false",
                        "visiblePolicies=",
                        "replaceTopLevelPolicies=false",
                        "topLevelPolicies=",
                        ""
                )
        );
    }
}
