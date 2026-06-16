/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

public final class DebugFileSummaryDto {

    private final String name;
    private final String path;

    public DebugFileSummaryDto(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String name() {
        return name;
    }

    public String path() {
        return path;
    }
}
