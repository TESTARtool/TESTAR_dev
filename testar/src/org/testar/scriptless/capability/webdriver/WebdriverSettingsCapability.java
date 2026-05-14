/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.capability.webdriver;

import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.core.Assert;
import org.testar.plugin.NativeLinker;
import org.testar.scriptless.capability.SettingsCapability;
import org.testar.webdriver.state.WdDriver;
import org.testar.webdriver.util.WdConstants;

public final class WebdriverSettingsCapability extends SettingsCapability {

    private final SettingsCapability delegate;

    public WebdriverSettingsCapability(SettingsCapability delegate) {
        this.delegate = Assert.notNull(delegate);
    }

    @Override
    public Settings initializeSettings(Settings settings) {
        NativeLinker.addWdDriverOS();

        settings = delegate.initializeSettings(settings);

        // If true, follow links opened in new tabs
        // If false, stay with the original (ignore links opened in new tabs)
        WdDriver.followLinks = settings.get(ConfigTags.FollowLinks);

        //Force the browser to run in full screen mode
        WdDriver.fullScreen = settings.get(ConfigTags.BrowserFullScreen);

        //Force webdriver to switch to a new tab if opened
        //This feature can block the correct display of select dropdown elements 
        WdDriver.forceActivateTab = settings.get(ConfigTags.SwitchNewTabs);

        // List of HTML tags that TESTAR should ignore when obtaining the web state
        WdConstants.setIgnoredTags(settings.get(ConfigTags.WebIgnoredTags));

        // List of web attributes that TESTAR should ignore when obtaining the web state
        WdConstants.setIgnoredAttributes(settings.get(ConfigTags.WebIgnoredAttributes));

        return settings;
    }
}
