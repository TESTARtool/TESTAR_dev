/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.service.android;

import org.testar.core.Assert;
import org.testar.core.exceptions.SystemStartException;
import org.testar.core.service.SystemService;
import org.testar.core.state.SUT;

public class ScriptlessAndroidSystemService implements SystemService {

    private final SystemService delegate;

    public ScriptlessAndroidSystemService(SystemService delegate) {
        this.delegate = Assert.notNull(delegate);
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
