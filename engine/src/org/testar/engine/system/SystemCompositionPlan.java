/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.system;

import java.util.Collections;
import java.util.List;

import org.testar.core.Assert;
import org.testar.core.service.SystemService;
import org.testar.core.state.SUT;

/**
 * Minimal composition contract for system lifecycle orchestration.
 */
public final class SystemCompositionPlan {

    @FunctionalInterface
    public interface StartHook {

        void afterStart(SUT system);
    }

    @FunctionalInterface
    public interface StopHook {

        void beforeStop(SUT system);
    }

    private final SystemService systemService;
    private final List<StartHook> startHooks;
    private final List<StopHook> stopHooks;

    public SystemCompositionPlan(SystemService systemService,
                                 List<StartHook> startHooks,
                                 List<StopHook> stopHooks) {
        this.systemService = Assert.notNull(systemService);
        this.startHooks = Collections.unmodifiableList(Assert.notNull(startHooks));
        this.stopHooks = Collections.unmodifiableList(Assert.notNull(stopHooks));
    }

    public static SystemCompositionPlan basic(SystemService systemService) {
        return new SystemCompositionPlan(
                systemService,
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    public SystemService systemService() {
        return systemService;
    }

    public List<StartHook> startHooks() {
        return startHooks;
    }

    public List<StopHook> stopHooks() {
        return stopHooks;
    }
}
