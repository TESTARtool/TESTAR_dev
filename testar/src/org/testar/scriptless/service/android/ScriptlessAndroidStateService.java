/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.service.android;

import org.testar.core.Assert;
import org.testar.core.exceptions.StateBuildException;
import org.testar.core.service.StateService;
import org.testar.core.state.SUT;
import org.testar.core.state.State;

public class ScriptlessAndroidStateService implements StateService {

    private final StateService delegate;

    public ScriptlessAndroidStateService(StateService delegate) {
        this.delegate = Assert.notNull(delegate);
    }

    @Override
    public State getState(SUT system) throws StateBuildException {
        Assert.notNull(system);
        return delegate.getState(system);
    }
}
