/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.android.action.derivation;

import java.util.Collections;
import java.util.Set;

import org.testar.android.action.AndroidBackAction;
import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.engine.action.derivation.ActionDeriver;
import org.testar.engine.policy.SessionPolicyContext;

/**
 * Android fallback action deriver that returns the Back action.
 */
public final class AndroidBackFallbackActionDeriver implements ActionDeriver {

    @Override
    public Set<Action> derive(SUT system, State state, SessionPolicyContext context) {
        Assert.notNull(state, context);
        return Collections.singleton(new AndroidBackAction(state));
    }
}
