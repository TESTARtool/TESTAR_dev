/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.service;

import org.testar.core.Assert;
import org.testar.core.exceptions.StateBuildException;
import org.testar.core.service.StateService;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.StateBuilder;
import org.testar.windows.state.UIAStateBuilder;

/**
 * Windows implementation of {@link StateService} backed by UI Automation.
 */
public final class WindowsStateService implements StateService, AutoCloseable {

    private final StateBuilder stateBuilder;
    private final Runnable releaser;

    public WindowsStateService() {
        this(new UIAStateBuilder());
    }

    public WindowsStateService(UIAStateBuilder stateBuilder) {
        this(Assert.notNull(stateBuilder), stateBuilder::release);
    }

    WindowsStateService(StateBuilder stateBuilder, Runnable releaser) {
        this.stateBuilder = Assert.notNull(stateBuilder);
        this.releaser = Assert.notNull(releaser);
    }

    public static WindowsStateService uiAutomation() {
        return new WindowsStateService();
    }

    public static WindowsStateService uiAutomation(double timeoutSeconds,
                                                   boolean accessBridgeEnabled,
                                                   String sutProcesses) {
        return new WindowsStateService(new UIAStateBuilder(
                timeoutSeconds,
                accessBridgeEnabled,
                sutProcesses
        ));
    }

    @Override
    public State getState(SUT system) throws StateBuildException {
        return stateBuilder.apply(Assert.notNull(system));
    }

    @Override
    public void close() {
        releaser.run();
    }
}
