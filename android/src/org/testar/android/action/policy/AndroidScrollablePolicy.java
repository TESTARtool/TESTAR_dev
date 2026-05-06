/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.android.action.policy;

import org.testar.android.tag.AndroidTags;
import org.testar.core.policy.ScrollablePolicy;
import org.testar.core.state.Widget;

/**
 * Default Android scrollability policy backed by the Appium scrollable flag.
 */
public final class AndroidScrollablePolicy implements ScrollablePolicy {

    @Override
    public boolean isScrollable(Widget widget) {
        return widget != null && widget.get(AndroidTags.AndroidScrollable, false);
    }
}
