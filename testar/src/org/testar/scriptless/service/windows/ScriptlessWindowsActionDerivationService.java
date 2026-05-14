/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.service.windows;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.testar.core.Assert;
import org.testar.core.action.Action;
import org.testar.core.action.NOP;
import org.testar.core.service.ActionDerivationService;
import org.testar.core.state.SUT;
import org.testar.core.state.State;

public class ScriptlessWindowsActionDerivationService implements ActionDerivationService {

    private final ActionDerivationService delegate;

    public ScriptlessWindowsActionDerivationService(ActionDerivationService delegate) {
        this.delegate = Assert.notNull(delegate);
    }

    @Override
    public Set<Action> deriveActions(SUT system, State state) {
        Assert.notNull(system, state);

        Set<Action> actions = delegate.deriveActions(system, state);
        if (actions.isEmpty()) {
            return new HashSet<>(Collections.singletonList(new NOP()));
        }

        return actions;
    }
}
