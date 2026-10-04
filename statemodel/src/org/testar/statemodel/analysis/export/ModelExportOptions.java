/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.analysis.export;

import java.util.List;

/** Retrieval choices shared by portable snapshots and on-demand JSON downloads. */
public record ModelExportOptions(String format, boolean includeWidgetTrees, boolean includeScreenshots) {

    public ModelExportOptions {
        if (!List.of("snapshot", "inventory", "abstract", "hybrid", "concrete", "traces").contains(format)) {
            throw new IllegalArgumentException("Unsupported model export format.");
        }
        if ("inventory".equals(format) && (includeWidgetTrees || includeScreenshots)) {
            throw new IllegalArgumentException("Export inventory cannot include artifact data.");
        }
    }
}
