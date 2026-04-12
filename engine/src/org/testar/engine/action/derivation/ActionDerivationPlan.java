/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.derivation;

import java.util.Collections;
import java.util.List;

import org.testar.core.Assert;

/**
 * Action-derivation phases grouped as one composition plan.
 */
public final class ActionDerivationPlan {

    private final List<ActionDeriver> forcedDerivers;
    private final List<ActionDeriver> defaultDerivers;
    private final List<ActionDeriver> fallbackDerivers;

    public ActionDerivationPlan(List<ActionDeriver> forcedDerivers,
                                List<ActionDeriver> defaultDerivers,
                                List<ActionDeriver> fallbackDerivers) {
        this.forcedDerivers = Collections.unmodifiableList(Assert.notNull(forcedDerivers));
        this.defaultDerivers = Collections.unmodifiableList(Assert.notNull(defaultDerivers));
        this.fallbackDerivers = Collections.unmodifiableList(Assert.notNull(fallbackDerivers));
    }

    public List<ActionDeriver> forcedDerivers() {
        return forcedDerivers;
    }

    public List<ActionDeriver> defaultDerivers() {
        return defaultDerivers;
    }

    public List<ActionDeriver> fallbackDerivers() {
        return fallbackDerivers;
    }
}
