/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.execution;

public enum ExecutionBackend {

    CLI("cli"),
    SCRIPTLESS("scriptless"),
    REMOTE("remote");

    private final String id;

    ExecutionBackend(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public static ExecutionBackend fromId(String id) {
        for (ExecutionBackend backend : values()) {
            if (backend.id.equalsIgnoreCase(id)) {
                return backend;
            }
        }

        throw new IllegalArgumentException("Unknown execution backend id: " + id);
    }
}
