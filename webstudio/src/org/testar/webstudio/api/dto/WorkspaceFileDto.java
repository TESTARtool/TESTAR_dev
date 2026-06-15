/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

public final class WorkspaceFileDto {

    private final String name;
    private final String location;
    private final String content;
    private final String category;

    public WorkspaceFileDto(String name, String location, String content, String category) {
        this.name = name;
        this.location = location;
        this.content = content;
        this.category = category;
    }

    public String name() {
        return name;
    }

    public String location() {
        return location;
    }

    public String content() {
        return content;
    }

    public String category() {
        return category;
    }
}
