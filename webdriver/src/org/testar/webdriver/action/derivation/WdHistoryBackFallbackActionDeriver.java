/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webdriver.action.derivation;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.testar.core.action.Action;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.engine.action.derivation.ActionDeriver;
import org.testar.engine.policy.SessionPolicyContext;
import org.testar.webdriver.action.WdHistoryBackAction;
import org.testar.webdriver.state.WdDriver;

/**
 * Produces a history-back fallback action when no default WebDriver action was
 * derived.
 */
public final class WdHistoryBackFallbackActionDeriver implements ActionDeriver {

    @Override
    public Set<Action> derive(SUT system, State state, SessionPolicyContext context) {
        System.out.println("** WEBDRIVER WARNING: The state seems to have no interactive widgets");
        System.out.println(String.format("** URL: %s", WdDriver.getCurrentUrl()));
        System.out.println("** Please try to navigate with SPY mode and configure clickableClasses inside Java protocol");

        Set<Action> actions = new LinkedHashSet<Action>();
        actions.add(new WdHistoryBackAction(state));
        return Collections.unmodifiableSet(actions);
    }
}
