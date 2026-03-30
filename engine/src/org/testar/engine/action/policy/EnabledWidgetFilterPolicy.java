/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.policy;

import org.testar.core.action.policy.WidgetFilterPolicy;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;

public final class EnabledWidgetFilterPolicy implements WidgetFilterPolicy {

    @Override
    public boolean allows(Widget widget) {
        return widget.get(Tags.Enabled, true);
    }
}
