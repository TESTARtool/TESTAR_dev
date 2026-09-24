/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webdriver.action.derivation;

import org.testar.core.action.Action;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.engine.action.derivation.ActionDeriver;
import org.testar.engine.action.derivation.ForegroundActionDeriver;
import org.testar.engine.policy.SessionPolicyContext;

import java.util.Set;

public final class WebdriverForcedActionDeriver implements ActionDeriver {

    private final ForegroundActionDeriver foregroundActionDeriver;
    private final WdDeniedUrlForcedActionDeriver deniedUrlForcedActionDeriver;
    private final WebdriverPopupActionDeriver popupActionDeriver;

    public WebdriverForcedActionDeriver(WdDeniedUrlForcedActionDeriver deniedUrlForcedActionDeriver) {
        this(deniedUrlForcedActionDeriver, new WebdriverPopupActionDeriver(java.util.Collections.emptyList()));
    }

    public WebdriverForcedActionDeriver(WdDeniedUrlForcedActionDeriver deniedUrlForcedActionDeriver,
                                        WebdriverPopupActionDeriver popupActionDeriver) {
        this.foregroundActionDeriver = new ForegroundActionDeriver();
        this.deniedUrlForcedActionDeriver = deniedUrlForcedActionDeriver;
        this.popupActionDeriver = popupActionDeriver;
    }

    @Override
    public Set<Action> derive(SUT system, State state, SessionPolicyContext context) {
        Set<Action> foregroundActions = foregroundActionDeriver.derive(system, state, context);
        if (!foregroundActions.isEmpty()) {
            return foregroundActions;
        }

        Set<Action> deniedUrlActions = deniedUrlForcedActionDeriver.derive(system, state, context);
        if (!deniedUrlActions.isEmpty()) {
            return deniedUrlActions;
        }

        return popupActionDeriver.derive(system, state, context);
    }
}
