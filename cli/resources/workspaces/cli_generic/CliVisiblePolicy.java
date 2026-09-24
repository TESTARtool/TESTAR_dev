/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

import org.testar.core.Assert;
import org.testar.core.alayer.Shape;
import org.testar.core.policy.VisiblePolicy;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;
import org.testar.webdriver.tag.WdTags;

public final class CliVisiblePolicy implements VisiblePolicy {

    public CliVisiblePolicy() {
    }

    @Override
    public boolean isVisible(Widget widget) {
        Assert.notNull(widget);
        return isAtBrowserCanvas(widget);
    }

    private boolean isAtBrowserCanvas(Widget widget) {
        Shape shape = widget.get(Tags.Shape, null);
        if (shape == null) {
            return false;
        }

        return widget.get(WdTags.WebIsFullOnScreen, false);
    }
}
