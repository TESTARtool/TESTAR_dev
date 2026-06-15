/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

public final class ResultFileSummaryDto {

    private final String name;
    private final String path;
    private final String contentType;
    private final String status;

    public ResultFileSummaryDto(String name, String path, String contentType, String status) {
        this.name = name;
        this.path = path;
        this.contentType = contentType;
        this.status = status;
    }

    public String name() {
        return name;
    }

    public String path() {
        return path;
    }

    public String contentType() {
        return contentType;
    }

    public String status() {
        return status;
    }
}
