/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.execution;

public final class ScriptlessRunRequest {

    private final String workspaceName;
    private final String mode;

    public ScriptlessRunRequest(String workspaceName, String mode) {
        this.workspaceName = workspaceName;
        this.mode = mode;
    }

    public String workspaceName() {
        return workspaceName;
    }

    public String mode() {
        return mode;
    }
}
