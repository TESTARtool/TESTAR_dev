/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.policy;

import org.testar.core.policy.TopLevelPolicy;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;

public final class ZIndexTopLevelPolicy implements TopLevelPolicy {

    @Override
    public boolean isTopLevel(Widget widget) {
        State state = widget.root();
        if (state == null) {
            return false;
        }

        Double widgetZIndex = widget.get(Tags.ZIndex, null);
        Double maxZIndex = state.get(Tags.MaxZIndex, null);
        if (widgetZIndex == null || maxZIndex == null) {
            return false;
        }

        return Double.compare(widgetZIndex, maxZIndex) == 0;
    }
}
