/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.android.system;

import org.testar.android.service.AndroidSystemService;
import org.testar.config.settings.Settings;
import org.testar.engine.system.SystemCompositionPlan;

/**
 * Reusable default system composition plan for Android/Appium-driven testing.
 */
public final class AndroidSystemCompositionPlan {

    private AndroidSystemCompositionPlan() {
    }

    public static SystemCompositionPlan fromSettings(Settings settings) {
        return SystemCompositionPlan.basic(AndroidSystemService.fromSettings(settings));
    }
}
