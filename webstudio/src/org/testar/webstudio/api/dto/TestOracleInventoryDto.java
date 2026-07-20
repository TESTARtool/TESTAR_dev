/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

import java.util.List;

public final class TestOracleInventoryDto {

    private final String workspaceName;
    private final List<String> activeOracles;
    private final List<TestOracleItemDto> items;

    public TestOracleInventoryDto(String workspaceName, List<String> activeOracles, List<TestOracleItemDto> items) {
        this.workspaceName = workspaceName;
        this.activeOracles = activeOracles;
        this.items = items;
    }

    public String workspaceName() {
        return workspaceName;
    }

    public List<String> activeOracles() {
        return activeOracles;
    }

    public List<TestOracleItemDto> items() {
        return items;
    }
}
