/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.derivation;

import java.util.Set;

import org.testar.core.action.Action;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.engine.policy.SessionPolicyContext;

/**
 * Returns the actions produced for one derivation step in the current
 * derivation phase.
 */
public interface ActionDeriver {

    Set<Action> derive(SUT system, State state, SessionPolicyContext context);
}
