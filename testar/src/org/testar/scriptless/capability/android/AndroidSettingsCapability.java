/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.capability.android;

import org.testar.config.settings.Settings;
import org.testar.core.Assert;
import org.testar.scriptless.capability.SettingsCapability;

public class AndroidSettingsCapability extends SettingsCapability {

    private final SettingsCapability delegate;

    public AndroidSettingsCapability(SettingsCapability delegate) {
        this.delegate = Assert.notNull(delegate);
    }

    @Override
    public Settings initializeSettings(Settings settings) {
        Assert.notNull(settings);
        return delegate.initializeSettings(settings);
    }
}
