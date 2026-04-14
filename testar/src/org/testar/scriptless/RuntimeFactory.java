/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless;

import org.testar.config.settings.Settings;
import org.testar.core.Assert;
import org.testar.plugin.PlatformOrchestrator;
import org.testar.plugin.PlatformServices;
import org.testar.plugin.PlatformSessionSpec;
import org.testar.plugin.PlatformSessionSpecFactory;
import org.testar.plugin.configuration.SessionPolicyConfiguration;
import org.testar.plugin.configuration.SessionServiceConfiguration;
import org.testar.plugin.reporting.SessionReportingManager;

/**
 * Creates TESTAR scriptless runtime services from the composed platform
 * architecture.
 */
public final class RuntimeFactory {

    private RuntimeFactory() {
    }

    public static RuntimeServices resolve(Settings settings) {
        Assert.notNull(settings);

        PlatformSessionSpec sessionSpec = PlatformSessionSpecFactory.fromSettings(settings);
        PlatformServices platformServices = PlatformOrchestrator.resolve(
                sessionSpec,
                SessionPolicyConfiguration.defaults(),
                SessionServiceConfiguration.defaults()
        );
        SessionReportingManager sessionReportingManager = SessionReportingManager.create();

        return RuntimeServices.fromPlatformServices(platformServices, sessionReportingManager);
    }
}
