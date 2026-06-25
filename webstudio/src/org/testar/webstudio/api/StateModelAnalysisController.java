/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api;

import org.testar.webstudio.analysis.StateModelAnalysisService;
import org.testar.webstudio.api.dto.StateModelLaunchDto;

public final class StateModelAnalysisController {

    private final StateModelAnalysisService service;

    public StateModelAnalysisController(StateModelAnalysisService service) {
        this.service = service;
    }

    public StateModelLaunchDto open(String workspaceName, String runtime) {
        return service.open(workspaceName, runtime);
    }
}
