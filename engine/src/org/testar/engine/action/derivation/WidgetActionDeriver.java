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
import org.testar.core.state.Widget;
import org.testar.engine.policy.SessionPolicyContext;

/**
 * Returns the actions produced for one widget during a derivation run.
 */
public interface WidgetActionDeriver {

    Set<Action> derive(SUT system,
                       State state,
                       Widget widget,
                       SessionPolicyContext context);
}
