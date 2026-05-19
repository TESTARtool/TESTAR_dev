/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.service;

import org.testar.core.Assert;
import org.testar.core.exceptions.SystemStartException;
import org.testar.core.service.SystemService;
import org.testar.core.state.SUT;
import org.testar.scriptless.RuntimeContext;

public class ScriptlessSystemService implements SystemService {

    private final SystemService delegate;
    protected final RuntimeContext runtimeContext;

    public ScriptlessSystemService(SystemService delegate, RuntimeContext runtimeContext) {
        this.delegate = Assert.notNull(delegate);
        this.runtimeContext = Assert.notNull(runtimeContext);
    }

    protected final SystemService delegate() {
        return delegate;
    }

    @Override
    public SUT startSystem() throws SystemStartException {
        return delegate.startSystem();
    }

    @Override
    public void stopSystem(SUT system) {
        Assert.notNull(system);
        delegate.stopSystem(system);
    }
}
