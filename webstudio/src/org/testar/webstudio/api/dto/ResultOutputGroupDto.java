/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

import java.util.List;

public final class ResultOutputGroupDto {

    private final String name;
    private final String path;
    private final String status;
    private final int totalSequenceCount;
    private final List<ResultFileSummaryDto> files;

    public ResultOutputGroupDto(String name, String path, String status, int totalSequenceCount, List<ResultFileSummaryDto> files) {
        this.name = name;
        this.path = path;
        this.status = status;
        this.totalSequenceCount = totalSequenceCount;
        this.files = files;
    }

    public String name() {
        return name;
    }

    public String path() {
        return path;
    }

    public String status() {
        return status;
    }

    public int totalSequenceCount() {
        return totalSequenceCount;
    }

    public List<ResultFileSummaryDto> files() {
        return files;
    }
}
