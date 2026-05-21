/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin.configuration;

import org.testar.core.Assert;
import org.testar.engine.policy.SessionPolicyContext;

/**
 * Bundles the platform defaults needed to assemble one resolved session.
 */
public final class PlatformSessionAssembly {

    private final SessionPolicyContext sessionPolicyContext;
    private final PlatformDefaultServicePlans defaultServicePlans;

    public PlatformSessionAssembly(SessionPolicyContext sessionPolicyContext,
                                   PlatformDefaultServicePlans defaultServicePlans) {
        this.sessionPolicyContext = Assert.notNull(sessionPolicyContext);
        this.defaultServicePlans = Assert.notNull(defaultServicePlans);
    }

    public SessionPolicyContext sessionPolicyContext() {
        return sessionPolicyContext;
    }

    public PlatformDefaultServicePlans defaultServicePlans() {
        return defaultServicePlans;
    }
}
