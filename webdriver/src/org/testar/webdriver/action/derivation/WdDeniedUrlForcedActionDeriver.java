/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webdriver.action.derivation;

import org.testar.config.settings.Settings;
import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.engine.action.derivation.ActionDeriver;
import org.testar.engine.policy.SessionPolicyContext;
import org.testar.webdriver.action.WdCloseTabAction;
import org.testar.webdriver.action.WdHistoryBackAction;
import org.testar.webdriver.state.WdDriver;
import org.testar.webdriver.util.WebNavigationUtil;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class WdDeniedUrlForcedActionDeriver implements ActionDeriver {

    private final Settings settings;

    public WdDeniedUrlForcedActionDeriver(Settings settings) {
        this.settings = Assert.notNull(settings);
    }

    @Override
    public Set<Action> derive(SUT system, State state, SessionPolicyContext context) {
        Assert.notNull(state);

        String currentUrl = WdDriver.getCurrentUrl();
        if (!WebNavigationUtil.isUrlDenied(settings, currentUrl)
                && !WebNavigationUtil.isExtensionDenied(settings, currentUrl)) {
            return Collections.emptySet();
        }

        Set<Action> actions = new LinkedHashSet<Action>();
        if (WdDriver.getWindowHandles().size() > 1) {
            actions.add(new WdCloseTabAction(state));
        } else {
            actions.add(new WdHistoryBackAction(state));
        }

        return Collections.unmodifiableSet(actions);
    }
}
