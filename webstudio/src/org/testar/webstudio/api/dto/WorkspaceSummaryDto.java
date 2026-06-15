/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

public final class WorkspaceSummaryDto {

    private final String name;
    private final String location;

    public WorkspaceSummaryDto(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public String name() {
        return name;
    }

    public String location() {
        return location;
    }
}
