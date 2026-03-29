/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.analysis.jsonformat;

import com.fasterxml.jackson.annotation.JsonGetter;

public class Edge extends Document {

    private String sourceId;

    private String targetId;

    public Edge(String id, String sourceId, String targetId) {
        super(id);
        this.sourceId = sourceId;
        this.targetId = targetId;
    }

    @JsonGetter("source")
    public String getSourceId() {
        return sourceId;
    }

    @JsonGetter("target")
    public String getTargetId() {
        return targetId;
    }
}
