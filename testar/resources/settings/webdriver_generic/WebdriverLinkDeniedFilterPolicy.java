/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.core.Assert;
import org.testar.core.policy.WidgetFilterPolicy;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.webdriver.state.WdDriver;
import org.testar.webdriver.tag.WdTags;
import org.testar.webdriver.util.WebNavigationUtil;

public final class WebdriverLinkDeniedFilterPolicy implements WidgetFilterPolicy {

    private final Settings settings;

    public WebdriverLinkDeniedFilterPolicy(Settings settings) {
        this.settings = Assert.notNull(settings);
    }

    @Override
    public boolean allows(Widget widget) {
        Assert.notNull(widget);
        return !isLinkDenied(widget, getCurrentUrl(widget));
    }

    private boolean isLinkDenied(Widget widget, String currentUrl) {
        String linkUrl = widget.get(WdTags.WebHref, "");
        return WebNavigationUtil.isLinkDenied(settings, linkUrl, currentUrl);
    }

    private String getCurrentUrl(Widget widget) {
        State root = widget.root();
        if (root != null) {
            String rootUrl = root.get(WdTags.WebHref, "");
            if (rootUrl != null && !rootUrl.isBlank()) {
                return rootUrl;
            }
        }

        return WdDriver.getCurrentUrl();
    }
}
