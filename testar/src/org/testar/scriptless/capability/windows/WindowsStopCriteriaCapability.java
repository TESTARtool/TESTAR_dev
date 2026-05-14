/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.capability.windows;

import org.testar.core.Assert;
import org.testar.core.state.State;
import org.testar.scriptless.RuntimeContext;
import org.testar.scriptless.capability.StopCriteriaCapability;

public class WindowsStopCriteriaCapability extends StopCriteriaCapability {

    private final StopCriteriaCapability delegate;

    public WindowsStopCriteriaCapability(StopCriteriaCapability delegate) {
        this.delegate = Assert.notNull(delegate);
    }

    @Override
    public boolean stopTestSequence(RuntimeContext runtimeContext, State state) {
        Assert.notNull(runtimeContext, state);
        return delegate.stopTestSequence(runtimeContext, state);
    }

    @Override
    public boolean stopTestSession(RuntimeContext runtimeContext) {
        Assert.notNull(runtimeContext);
        return delegate.stopTestSession(runtimeContext);
    }
}
