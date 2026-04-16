/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.capability;

import org.testar.engine.manager.NativeHookManager;
import org.testar.scriptless.RuntimeContext;

public class TestSessionCapability {

    public void initializeTestSession(RuntimeContext runtimeContext) {
        NativeHookManager.registerNativeHook(runtimeContext.eventHandler());
    }

    public void closeTestSession(RuntimeContext runtimeContext) {
        NativeHookManager.unregisterNativeListener(runtimeContext.eventHandler());
    }

}
