/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

import java.util.List;

public final class ScriptlessResultsDto {

    private final String latestSequenceOutputPath;
    private final List<ResultOutputGroupDto> groups;
    private final List<ResultFileSummaryDto> files;

    public ScriptlessResultsDto(
        String latestSequenceOutputPath,
        List<ResultOutputGroupDto> groups,
        List<ResultFileSummaryDto> files
    ) {
        this.latestSequenceOutputPath = latestSequenceOutputPath;
        this.groups = groups;
        this.files = files;
    }

    public String latestSequenceOutputPath() {
        return latestSequenceOutputPath;
    }

    public List<ResultOutputGroupDto> groups() {
        return groups;
    }

    public List<ResultFileSummaryDto> files() {
        return files;
    }
}
