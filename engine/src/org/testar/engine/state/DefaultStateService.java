/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.state;

import org.testar.core.Assert;
import org.testar.core.CodingManager;
import org.testar.core.exceptions.StateBuildException;
import org.testar.core.service.StateService;
import org.testar.core.state.SUT;
import org.testar.core.state.State;

/**
 * Engine-side state service that prepares state-widget identifiers after the raw
 * platform state is fetched.
 */
public final class DefaultStateService implements StateService, AutoCloseable {

    private final StateService delegate;

    public DefaultStateService(StateService delegate) {
        this.delegate = Assert.notNull(delegate);
    }

    @Override
    public State getState(SUT system) throws StateBuildException {
        State state = delegate.getState(system);
        CodingManager.buildIDs(state);
        return state;
    }

    @Override
    public void close() throws Exception {
        if (delegate instanceof AutoCloseable) {
            ((AutoCloseable) delegate).close();
        }
    }
}
