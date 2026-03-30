/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin;

import java.util.Set;

import org.testar.core.action.Action;
import org.testar.core.state.SUT;
import org.testar.core.state.State;

/**
 * Live platform session holding the started system plus the composed services
 * needed to interact with it.
 */
public interface PlatformSession extends AutoCloseable {

    SUT system();

    State getState();

    Set<Action> getDerivedActions();

    boolean executeAction(Action action);

    void stopSystem();

    @Override
    void close();
}
