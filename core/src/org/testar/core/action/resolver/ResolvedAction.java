/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.action.resolver;

import org.testar.core.Assert;
import org.testar.core.action.Action;

public final class ResolvedAction {

    private final Action action;

    public ResolvedAction(Action action) {
        this.action = Assert.notNull(action);
    }

    public Action action() {
        return action;
    }
}
