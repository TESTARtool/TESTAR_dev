/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

import org.testar.core.policy.ClickablePolicy;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.tag.WdTags;

public final class WebdriverSparkClickablePolicy implements ClickablePolicy {

    @Override
    public boolean isClickable(Widget widget) {
        if (widget == null) {
            return false;
        }

        String cssClasses = widget.get(WdTags.WebCssClasses, "");
        boolean buttonListItem = WdRoles.WdLI.equals(widget.get(Tags.Role, null))
                && cssClasses.contains("Button");

        return buttonListItem
                || cssClasses.contains("projectMain")
                || cssClasses.contains("makeStyles-activityMain");
    }
}
