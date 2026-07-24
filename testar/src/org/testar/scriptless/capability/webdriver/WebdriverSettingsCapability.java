/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.capability.webdriver;

import org.testar.config.settings.Settings;
import org.testar.core.Assert;
import org.testar.scriptless.capability.SettingsCapability;
import org.testar.webdriver.state.WdDriver;

public class WebdriverSettingsCapability extends SettingsCapability {

    private final SettingsCapability delegate;

    public WebdriverSettingsCapability(SettingsCapability delegate) {
        this.delegate = Assert.notNull(delegate);
    }

    @Override
    public Settings initializeSettings(Settings settings) {
        Assert.notNull(settings);
        settings = delegate.initializeSettings(settings);

        WdDriver.configureFromSettings(settings);

        return settings;
    }
}
