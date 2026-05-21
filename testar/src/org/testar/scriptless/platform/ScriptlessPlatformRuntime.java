/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.platform;

import org.testar.plugin.PlatformServices;
import org.testar.plugin.PlatformSessionSpec;
import org.testar.plugin.configuration.SessionServiceConfiguration;
import org.testar.plugin.reporting.SessionReportingManager;
import org.testar.scriptless.RuntimeContext;
import org.testar.scriptless.ScriptlessCapabilities;
import org.testar.scriptless.TestingServices;
import org.testar.scriptless.composition.ScriptlessCompositionDescriptor;

public interface ScriptlessPlatformRuntime {

    SessionServiceConfiguration createServiceConfiguration(PlatformSessionSpec sessionSpec, RuntimeContext runtimeContext);

    TestingServices createTestingServices(PlatformServices platformServices,
                                          RuntimeContext runtimeContext,
                                          ScriptlessCompositionDescriptor compositionDescriptor,
                                          SessionReportingManager sessionReportingManager);

    ScriptlessCapabilities createCapabilities(RuntimeContext runtimeContext,
                                              ScriptlessCompositionDescriptor compositionDescriptor,
                                              ScriptlessCapabilities capabilities);
}
