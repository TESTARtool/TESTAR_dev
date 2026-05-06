/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.android.action.derivation;

import java.util.LinkedHashSet;
import java.util.Set;

import org.testar.android.action.AndroidSystemActionCall;
import org.testar.android.action.AndroidSystemActionOrientation;
import org.testar.android.action.AndroidSystemActionText;
import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.engine.action.derivation.ActionDeriver;
import org.testar.engine.policy.SessionPolicyContext;

/**
 * Adds optional Android device/system actions.
 */
public final class AndroidSystemActionDeriver implements ActionDeriver {

    private final boolean includeSystemActions;

    public AndroidSystemActionDeriver(boolean includeSystemActions) {
        this.includeSystemActions = includeSystemActions;
    }

    @Override
    public Set<Action> derive(SUT system, State state, SessionPolicyContext context) {
        Assert.notNull(state, context);
        if (!includeSystemActions) {
            return java.util.Collections.emptySet();
        }

        Set<Action> actions = new LinkedHashSet<>();
        actions.add(new AndroidSystemActionOrientation(state));
        actions.add(new AndroidSystemActionCall(state));
        actions.add(new AndroidSystemActionText(state));
        return actions;
    }
}
