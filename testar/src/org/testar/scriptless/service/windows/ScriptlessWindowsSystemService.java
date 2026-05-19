/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.service.windows;

import org.testar.core.Assert;
import org.testar.core.exceptions.SystemStartException;
import org.testar.core.service.SystemService;
import org.testar.core.state.SUT;
import org.testar.plugin.process.SystemProcessHandling;
import org.testar.scriptless.RuntimeContext;
import org.testar.scriptless.service.ScriptlessSystemService;

public class ScriptlessWindowsSystemService extends ScriptlessSystemService {

    public ScriptlessWindowsSystemService(SystemService delegate, RuntimeContext runtimeContext) {
        super(delegate, runtimeContext);
    }

    @Override
    public SUT startSystem() throws SystemStartException {
        runtimeContext.setContextRunningProcesses(SystemProcessHandling.getRunningProcesses("START"));
        return super.startSystem();
    }

    @Override
    public void stopSystem(SUT system) {
        Assert.notNull(system);
        SystemProcessHandling.killTestLaunchedProcesses(runtimeContext.contextRunningProcesses());
        super.stopSystem(system);
    }
}
