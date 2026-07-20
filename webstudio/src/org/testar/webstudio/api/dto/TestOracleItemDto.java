/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

public final class TestOracleItemDto {

    private final String name;
    private final String origin;
    private final String path;
    private final boolean active;
    private final boolean editable;
    private final boolean overridesBuiltIn;

    public TestOracleItemDto(String name,
                             String origin,
                             String path,
                             boolean active,
                             boolean editable,
                             boolean overridesBuiltIn) {
        this.name = name;
        this.origin = origin;
        this.path = path;
        this.active = active;
        this.editable = editable;
        this.overridesBuiltIn = overridesBuiltIn;
    }

    public String name() {
        return name;
    }

    public String origin() {
        return origin;
    }

    public String path() {
        return path;
    }

    public boolean active() {
        return active;
    }

    public boolean editable() {
        return editable;
    }

    public boolean overridesBuiltIn() {
        return overridesBuiltIn;
    }
}
