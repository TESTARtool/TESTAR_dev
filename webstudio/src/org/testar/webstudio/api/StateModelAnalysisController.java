/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api;

import org.testar.webstudio.analysis.StateModelAnalysisService;
import org.testar.webstudio.api.dto.StateModelStatusDto;

public final class StateModelAnalysisController {

    private final StateModelAnalysisService service;

    public StateModelAnalysisController(StateModelAnalysisService service) {
        this.service = service;
    }

    public StateModelStatusDto open(String workspaceName) {
        return service.open(workspaceName);
    }

    public StateModelStatusDto status() {
        return service.status();
    }

    public StateModelStatusDto stop() {
        return service.stop();
    }
}
