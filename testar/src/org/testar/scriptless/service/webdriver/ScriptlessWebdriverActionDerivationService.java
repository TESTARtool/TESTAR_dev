/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.service.webdriver;

import java.util.Set;
import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.service.ActionDerivationService;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.scriptless.RuntimeContext;

public class ScriptlessWebdriverActionDerivationService implements ActionDerivationService {

    private final ActionDerivationService delegate;

    public ScriptlessWebdriverActionDerivationService(ActionDerivationService delegate, RuntimeContext runtimeContext) {
        this.delegate = Assert.notNull(delegate);
        Assert.notNull(runtimeContext);
    }

    @Override
    public Set<Action> deriveActions(SUT system, State state) {
        Assert.notNull(system, state);
        return delegate.deriveActions(system, state);
    }
}
