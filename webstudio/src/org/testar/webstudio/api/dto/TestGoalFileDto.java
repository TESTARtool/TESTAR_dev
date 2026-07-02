/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

public final class TestGoalFileDto {

    private final String name;
    private final String path;
    private final String content;
    private final boolean executable;

    public TestGoalFileDto(String name, String path, String content, boolean executable) {
        this.name = name;
        this.path = path;
        this.content = content;
        this.executable = executable;
    }

    public String name() {
        return name;
    }

    public String path() {
        return path;
    }

    public String content() {
        return content;
    }

    public boolean executable() {
        return executable;
    }
}
