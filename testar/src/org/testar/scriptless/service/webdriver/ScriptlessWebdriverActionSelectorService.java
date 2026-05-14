/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.service.webdriver;

import java.util.Set;

import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.service.ActionSelectorService;
import org.testar.core.state.State;
import org.testar.scriptless.RuntimeContext;

public class ScriptlessWebdriverActionSelectorService implements ActionSelectorService {

    private final ActionSelectorService delegate;
    private final RuntimeContext runtimeContext;

    public ScriptlessWebdriverActionSelectorService(ActionSelectorService delegate, RuntimeContext runtimeContext) {
        this.delegate = Assert.notNull(delegate);
        this.runtimeContext = Assert.notNull(runtimeContext);
    }

    @Override
    public Action selectAction(State state, Set<Action> actions) {
        Assert.notNull(state, actions);
        return delegate.selectAction(state, actions);
    }
}
