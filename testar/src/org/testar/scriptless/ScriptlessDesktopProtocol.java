/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless;

import org.testar.core.action.Action;
import org.testar.core.exceptions.ActionBuildException;
import org.testar.core.exceptions.StateBuildException;
import org.testar.core.exceptions.SystemStartException;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.scriptless.capability.ScriptlessCapabilities;

import java.util.Set;

/**
 * Desktop protocol implementation.
 */
public class ScriptlessDesktopProtocol extends ComposedProtocol {

    public ScriptlessDesktopProtocol() {
        super();
    }

    protected ScriptlessDesktopProtocol(ScriptlessCapabilities scriptlessCapabilities) {
        super(scriptlessCapabilities);
    }

    @Override
    protected SUT startSystem() throws SystemStartException {
        return super.startSystem();
    }

    @Override
    protected State getState(SUT system) throws StateBuildException {
        return super.getState(system);
    }

    @Override
    protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
        return super.deriveActions(system, state);
    }

    @Override
    protected boolean executeAction(SUT system, State state, Action action) {
        return super.executeAction(system, state, action);
    }

    @Override
    protected void stopSystem(SUT system) {
        super.stopSystem(system);
    }
}
