/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.policy;

import java.util.Collections;
import java.util.List;

import org.testar.core.Assert;
import org.testar.core.policy.AtCanvasPolicy;
import org.testar.core.state.Widget;

public final class CompositeAtCanvasPolicy implements AtCanvasPolicy {

    private final List<AtCanvasPolicy> policies;

    public CompositeAtCanvasPolicy(List<AtCanvasPolicy> policies) {
        this.policies = Collections.unmodifiableList(Assert.notNull(policies));
    }

    public static CompositeAtCanvasPolicy allowAll() {
        return new CompositeAtCanvasPolicy(Collections.singletonList(widget -> true));
    }

    @Override
    public boolean isAtCanvas(Widget widget) {
        // ALL: every policy must consider the widget to be within the active canvas.
        for (AtCanvasPolicy policy : policies) {
            if (!policy.isAtCanvas(widget)) {
                return false;
            }
        }
        return true;
    }
}
