/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.derivation;

import java.util.Set;

import org.testar.core.action.Action;
import org.testar.core.action.ActivateSystem;
import org.testar.core.alayer.Role;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;

/**
 * Produces a foreground action when the system is not focused.
 */
public final class ForegroundActionDeriver implements ActionDeriver {

    @Override
    public void derive(SUT system, State state, ActionDerivationContext context, Set<Action> actions) {
        if (!state.get(Tags.Foreground, true) && system != null && system.get(Tags.SystemActivator, null) != null) {
            Action foregroundAction = new ActivateSystem();
            foregroundAction.set(Tags.Desc, "Bring the system to the foreground.");
            foregroundAction.set(Tags.Role, Role.from("System"));
            foregroundAction.mapOriginWidget(state);
            actions.add(foregroundAction);
        }
    }
}
