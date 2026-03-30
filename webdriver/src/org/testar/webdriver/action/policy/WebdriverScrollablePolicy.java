/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webdriver.action.policy;

import org.testar.core.action.policy.ScrollablePolicy;
import org.testar.core.state.Widget;
import org.testar.webdriver.tag.WdTags;

public final class WebdriverScrollablePolicy implements ScrollablePolicy {

    @Override
    public boolean isScrollable(Widget widget) {
        return widget != null
                && (widget.get(WdTags.WebHorizontallyScrollable, Boolean.FALSE)
                || widget.get(WdTags.WebVerticallyScrollable, Boolean.FALSE));
    }
}
