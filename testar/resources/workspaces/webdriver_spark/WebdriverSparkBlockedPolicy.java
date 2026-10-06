/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

import org.testar.core.policy.BlockedPolicy;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.webdriver.tag.WdTags;

public final class WebdriverSparkBlockedPolicy implements BlockedPolicy {

    @Override
    public boolean isBlocked(Widget widget) {
        if (widget == null) {
            return true;
        }

        String cssClasses = widget.get(WdTags.WebCssClasses, "");

        // Either Material UI exception bypasses the blocked tag; other widgets retain normal blocking.
        return widget.get(Tags.Blocked, false)
                && !cssClasses.contains("MuiFab-label")
                && !cssClasses.contains("MuiSelect-selectMenu");
    }
}
