/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

public final class WorkspaceSummaryDto {

    private final String name;
    private final String location;
    private final boolean availableInTestar;
    private final boolean availableInCli;

    public WorkspaceSummaryDto(String name, String location, boolean availableInTestar, boolean availableInCli) {
        this.name = name;
        this.location = location;
        this.availableInTestar = availableInTestar;
        this.availableInCli = availableInCli;
    }

    public String name() {
        return name;
    }

    public String location() {
        return location;
    }

    public boolean availableInTestar() {
        return availableInTestar;
    }

    public boolean availableInCli() {
        return availableInCli;
    }
}
