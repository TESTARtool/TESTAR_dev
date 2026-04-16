/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.capability;

import org.testar.config.settings.Settings;
import org.testar.core.Assert;

public class SettingsCapability {

    public Settings initializeSettings(Settings settings) {
        return Assert.notNull(settings);
    }

}
