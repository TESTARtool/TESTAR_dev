/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api;

import org.testar.webstudio.api.dto.ValidationResultDto;
import org.testar.webstudio.validation.ValidationService;

public final class ValidationController {

    private final ValidationService validationService;

    public ValidationController(ValidationService validationService) {
        this.validationService = validationService;
    }

    public ValidationResultDto validateWorkspace(String workspaceName) {
        return validationService.validateWorkspace(workspaceName);
    }
}
