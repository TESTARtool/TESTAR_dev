/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.policy;

import org.testar.core.policy.BlockedPolicy;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;

public final class TagBlockedPolicy implements BlockedPolicy {

    @Override
    public boolean isBlocked(Widget widget) {
        return widget.get(Tags.Blocked, false);
    }
}
