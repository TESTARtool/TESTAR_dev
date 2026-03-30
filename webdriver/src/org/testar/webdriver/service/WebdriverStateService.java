/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webdriver.service;

import org.testar.core.Assert;
import org.testar.core.execution.StateService;
import org.testar.core.exceptions.StateBuildException;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.StateBuilder;
import org.testar.webdriver.state.WdStateBuilder;

/**
 * WebDriver implementation of {@link StateService}.
 */
public final class WebdriverStateService implements StateService {

    private final StateBuilder stateBuilder;

    public WebdriverStateService() {
        this(new WdStateBuilder(10));
    }

    public WebdriverStateService(double timeoutSeconds) {
        this(new WdStateBuilder(timeoutSeconds));
    }

    public WebdriverStateService(WdStateBuilder stateBuilder) {
        this.stateBuilder = Assert.notNull(stateBuilder);
    }

    WebdriverStateService(StateBuilder stateBuilder) {
        this.stateBuilder = Assert.notNull(stateBuilder);
    }

    public static WebdriverStateService browser() {
        return new WebdriverStateService();
    }

    public static WebdriverStateService browser(double timeoutSeconds) {
        return new WebdriverStateService(timeoutSeconds);
    }

    @Override
    public State getState(SUT system) throws StateBuildException {
        return stateBuilder.apply(Assert.notNull(system));
    }
}
