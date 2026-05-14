/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.service.webdriver;

import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.service.ActionExecutionService;
import org.testar.core.state.SUT;
import org.testar.core.state.State;

public class ScriptlessWebdriverActionExecutionService implements ActionExecutionService {

    private final ActionExecutionService delegate;

    public ScriptlessWebdriverActionExecutionService(ActionExecutionService delegate) {
        this.delegate = Assert.notNull(delegate);
    }

    @Override
    public boolean executeAction(SUT system, State state, Action action) {
        Assert.notNull(system, state, action);
        return delegate.executeAction(system, state, action);
    }
}
