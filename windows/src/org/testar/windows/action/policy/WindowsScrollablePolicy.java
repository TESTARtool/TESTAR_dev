/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.action.policy;

import org.testar.core.action.policy.ScrollablePolicy;
import org.testar.core.state.Widget;

/**
 * Default Windows scrollability policy based on available scroll drags.
 */
public final class WindowsScrollablePolicy implements ScrollablePolicy {

    @Override
    public boolean isScrollable(Widget widget) {
        return widget.scrollDrags(36.0, 16.0) != null;
    }
}
