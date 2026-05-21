/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.oracle;

import org.testar.core.Assert;
import org.testar.core.service.OracleEvaluationService;

/**
 * Orchestrator wrapper for the configured oracle evaluation service.
 */
public final class OracleEvaluationPlan {

    private final OracleEvaluationService oracleEvaluationService;

    public OracleEvaluationPlan(OracleEvaluationService oracleEvaluationService) {
        this.oracleEvaluationService = Assert.notNull(oracleEvaluationService);
    }

    public static OracleEvaluationPlan basic(OracleEvaluationService oracleEvaluationService) {
        return new OracleEvaluationPlan(oracleEvaluationService);
    }

    public OracleEvaluationService oracleEvaluationService() {
        return oracleEvaluationService;
    }
}
