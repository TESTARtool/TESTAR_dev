/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.service;

import org.testar.core.Assert;
import org.testar.core.CodingManager;
import org.testar.core.service.StateIdentifierService;
import org.testar.core.state.State;

public final class DefaultStateIdentifierService implements StateIdentifierService {

    @Override
    public State identifyState(State state) {
        CodingManager.buildIDs(Assert.notNull(state));
        return state;
    }
}
