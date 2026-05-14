/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.capability.windows;

import org.testar.core.Assert;
import org.testar.scriptless.RuntimeContext;
import org.testar.scriptless.capability.TestSessionCapability;

public class WindowsTestSessionCapability extends TestSessionCapability {

    private final TestSessionCapability delegate;

    public WindowsTestSessionCapability(TestSessionCapability delegate) {
        this.delegate = Assert.notNull(delegate);
    }

    @Override
    public void initializeTestSession(RuntimeContext runtimeContext) {
        Assert.notNull(runtimeContext);
        delegate.initializeTestSession(runtimeContext);
    }

    @Override
    public void closeTestSession(RuntimeContext runtimeContext) {
        Assert.notNull(runtimeContext);
        delegate.closeTestSession(runtimeContext);
    }
}
