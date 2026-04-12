/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.derivation;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.testar.core.action.Action;
import org.testar.core.action.AnnotatingActionCompiler;
import org.testar.core.devices.KBKeys;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.engine.policy.SessionPolicyContext;

/**
 * Produces an ESC fallback action when no forced or default action was derived.
 */
public final class EscFallbackActionDeriver implements ActionDeriver {

    private final AnnotatingActionCompiler actionCompiler;

    public EscFallbackActionDeriver() {
        this(new AnnotatingActionCompiler());
    }

    EscFallbackActionDeriver(AnnotatingActionCompiler actionCompiler) {
        this.actionCompiler = actionCompiler;
    }

    @Override
    public Set<Action> derive(SUT system, State state, SessionPolicyContext context) {
        Set<Action> actions = new LinkedHashSet<>();
        Action action = actionCompiler.hitKey(KBKeys.VK_ESCAPE);
        action.mapOriginWidget(state);
        actions.add(action);
        return Collections.unmodifiableSet(actions);
    }
}
