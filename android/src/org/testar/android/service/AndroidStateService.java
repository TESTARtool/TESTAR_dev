/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.android.service;

import org.testar.android.state.AndroidStateBuilder;
import org.testar.core.Assert;
import org.testar.core.exceptions.StateBuildException;
import org.testar.core.service.StateService;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.StateBuilder;

/**
 * Android implementation of {@link StateService}.
 */
public final class AndroidStateService implements StateService {

    private final StateBuilder stateBuilder;

    public AndroidStateService() {
        this(new AndroidStateBuilder(10.0));
    }

    public AndroidStateService(double timeoutSeconds) {
        this(new AndroidStateBuilder(timeoutSeconds));
    }

    public AndroidStateService(AndroidStateBuilder stateBuilder) {
        this((StateBuilder) Assert.notNull(stateBuilder));
    }

    AndroidStateService(StateBuilder stateBuilder) {
        this.stateBuilder = Assert.notNull(stateBuilder);
    }

    public static AndroidStateService appium() {
        return new AndroidStateService();
    }

    public static AndroidStateService appium(double timeoutSeconds) {
        return new AndroidStateService(timeoutSeconds);
    }

    @Override
    public State getState(SUT system) throws StateBuildException {
        return stateBuilder.apply(Assert.notNull(system));
    }
}
