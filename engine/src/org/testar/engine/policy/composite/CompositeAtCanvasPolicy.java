/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.policy.composite;

import java.util.Collections;
import java.util.List;

import org.testar.core.Assert;
import org.testar.core.policy.AtCanvasPolicy;
import org.testar.core.state.Widget;

public final class CompositeAtCanvasPolicy implements AtCanvasPolicy {

    private final CompositePolicySupport<AtCanvasPolicy> support;

    public CompositeAtCanvasPolicy(List<AtCanvasPolicy> policies) {
        this.support = new CompositePolicySupport<>(
                Collections.unmodifiableList(Assert.notNull(policies)),
                CompositePolicyRule.ALL
        );
    }

    @Override
    public boolean isAtCanvas(Widget widget) {
        // ALL: every policy must consider the widget to be within the active canvas.
        return support.matches(policy -> policy.isAtCanvas(widget));
    }
}
