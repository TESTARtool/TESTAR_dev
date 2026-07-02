/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

import java.util.List;

public final class TestGoalNodeDto {

    private final String name;
    private final String path;
    private final String type;
    private final boolean executable;
    private final List<TestGoalNodeDto> children;

    public TestGoalNodeDto(String name,
                           String path,
                           String type,
                           boolean executable,
                           List<TestGoalNodeDto> children) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.executable = executable;
        this.children = children;
    }

    public String name() {
        return name;
    }

    public String path() {
        return path;
    }

    public String type() {
        return type;
    }

    public boolean executable() {
        return executable;
    }

    public List<TestGoalNodeDto> children() {
        return children;
    }
}
