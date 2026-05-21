/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.service;

import org.testar.core.Assert;
import org.testar.core.exceptions.SystemStartException;
import org.testar.core.service.SystemService;
import org.testar.core.state.SUT;
import org.testar.engine.system.SystemCompositionPlan;

/**
 * Engine-side system service that delegates to the configured system service.
 */
public final class ComposedSystemService implements SystemService {

    private final SystemCompositionPlan plan;

    public ComposedSystemService(SystemCompositionPlan plan) {
        this.plan = Assert.notNull(plan);
    }

    public static ComposedSystemService compose(SystemCompositionPlan plan) {
        return new ComposedSystemService(plan);
    }

    @Override
    public SUT startSystem() throws SystemStartException {
        return plan.systemService().startSystem();
    }

    @Override
    public void stopSystem(SUT system) {
        plan.systemService().stopSystem(system);
    }

    public SystemCompositionPlan plan() {
        return plan;
    }
}
