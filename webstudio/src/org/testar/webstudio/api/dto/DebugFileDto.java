/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

public final class DebugFileDto {

    private final String name;
    private final String path;
    private final String content;

    public DebugFileDto(String name, String path, String content) {
        this.name = name;
        this.path = path;
        this.content = content;
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
}
