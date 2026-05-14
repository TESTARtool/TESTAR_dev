/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.capability.android;

import org.testar.core.Assert;
import org.testar.plugin.NativeLinker;
import org.testar.scriptless.RuntimeContext;
import org.testar.scriptless.capability.TestSessionCapability;

public class AndroidTestSessionCapability extends TestSessionCapability {

    private final TestSessionCapability delegate;

    public AndroidTestSessionCapability(TestSessionCapability delegate) {
        this.delegate = Assert.notNull(delegate);
    }

    @Override
    public void closeTestSession(RuntimeContext runtimeContext) {
        delegate.closeTestSession(runtimeContext);
        NativeLinker.cleanAndroidOS();
    }
}
