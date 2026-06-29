/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin;

import java.util.List;
import java.util.Set;

import org.testar.core.action.Action;
import org.testar.core.action.resolver.ResolvedAction;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.verdict.Verdict;

/**
 * Live platform session holding the started system plus the composed services
 * needed to interact with it.
 */
public interface PlatformSession extends AutoCloseable {

    SUT system();

    State getState();

    Set<Action> getDerivedActions();

    ResolvedAction resolveAction(List<String> arguments);

    boolean executeAction(Action action);

    default void stopSystem() {
        stopSystem(List.of(Verdict.OK));
    }

    void stopSystem(List<Verdict> finalVerdicts);

    @Override
    void close();
}
