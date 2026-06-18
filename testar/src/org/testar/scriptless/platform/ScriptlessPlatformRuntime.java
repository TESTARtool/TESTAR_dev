/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.platform;

import org.testar.config.composition.CompositionDescriptor;
import org.testar.plugin.PlatformServices;
import org.testar.plugin.configuration.PlatformSessionSpecification;
import org.testar.plugin.configuration.ServiceSessionConfiguration;
import org.testar.plugin.reporting.SessionReportingManager;
import org.testar.scriptless.RuntimeContext;
import org.testar.scriptless.ScriptlessCapabilities;
import org.testar.scriptless.TestingServices;
import org.testar.scriptless.capability.SettingsCapability;

public interface ScriptlessPlatformRuntime {

    ServiceSessionConfiguration createServiceConfiguration(PlatformSessionSpecification sessionSpec,
                                                           RuntimeContext runtimeContext,
                                                           CompositionDescriptor compositionDescriptor);

    SettingsCapability createSettingsCapability(RuntimeContext runtimeContext, CompositionDescriptor compositionDescriptor);

    TestingServices createTestingServices(PlatformServices platformServices,
                                          RuntimeContext runtimeContext,
                                          CompositionDescriptor compositionDescriptor,
                                          SessionReportingManager sessionReportingManager);

    ScriptlessCapabilities createCapabilities(RuntimeContext runtimeContext,
                                              CompositionDescriptor compositionDescriptor,
                                              ScriptlessCapabilities capabilities);
}
