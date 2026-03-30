/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin;

import org.testar.core.execution.SystemService;
import org.testar.engine.action.DesktopActionDerivationFactory;
import org.testar.engine.action.DescriptionActionResolver;
import org.testar.engine.action.DefaultActionExecutionService;
import org.testar.engine.state.DefaultStateService;
import org.testar.plugin.exceptions.UnsupportedPlatformException;
import org.testar.webdriver.action.WebdriverActionDerivationFactory;
import org.testar.webdriver.action.policy.WebdriverClickablePolicy;
import org.testar.webdriver.action.policy.WebdriverScrollablePolicy;
import org.testar.webdriver.action.policy.WebdriverTypeablePolicy;
import org.testar.webdriver.service.WebdriverStateService;
import org.testar.webdriver.service.WebdriverSystemService;
import org.testar.windows.service.WindowsStateService;
import org.testar.windows.service.WindowsSystemService;
import org.testar.windows.action.policy.WindowsClickablePolicy;
import org.testar.windows.action.policy.WindowsScrollablePolicy;
import org.testar.windows.action.policy.WindowsTypeablePolicy;

/**
 * TESTAR platform/plugin orchestration
 */
public final class PlatformOrchestrator {

    private PlatformOrchestrator() {
    }

    public static String moduleName() {
        return "plugin";
    }

    public static PlatformServices resolve(PlatformSessionSpec sessionSpec) {
        switch (sessionSpec.getOperatingSystem()) {
            case WINDOWS:
            case WINDOWS_7:
            case WINDOWS_10:
                return windows(sessionSpec);
            case WEBDRIVER:
                return webdriver(sessionSpec);
            default:
                throw new UnsupportedPlatformException(
                        "Unsupported operating system: " + sessionSpec.getOperatingSystem()
                );
        }
    }

    public static PlatformSession openSession(PlatformSessionSpec sessionSpec) {
        PlatformServices services = resolve(sessionSpec);
        return new DefaultPlatformSession(services, services.systemService().startSystem());
    }

    private static PlatformServices windows(PlatformSessionSpec sessionSpec) {
        SystemService systemService;
        switch (sessionSpec.getTargetType()) {
            case EXECUTABLE:
                systemService = WindowsSystemService.fromExecutable(
                        sessionSpec.getTarget(),
                        sessionSpec.isProcessListenerEnabled(),
                        sessionSpec.getSutProcesses()
                );
                break;
            case PROCESS_NAME:
                systemService = WindowsSystemService.fromProcessName(sessionSpec.getTarget());
                break;
            case PROCESS_ID:
                systemService = WindowsSystemService.fromProcessId(Long.parseLong(sessionSpec.getTarget()));
                break;
            case UWP:
                systemService = WindowsSystemService.fromExecutableUwp(sessionSpec.getTarget());
                break;
            default:
                throw new UnsupportedPlatformException(
                        "Unsupported Windows target type: " + sessionSpec.getTargetType()
                );
        }

        return new PlatformServices(
                systemService,
                new DefaultStateService(
                        WindowsStateService.uiAutomation(
                                sessionSpec.getStateTimeoutSeconds(),
                                sessionSpec.isAccessBridgeEnabled(),
                                sessionSpec.getSutProcesses()
                        )
                ),
                DesktopActionDerivationFactory.create(
                        new WindowsClickablePolicy(),
                        new WindowsTypeablePolicy(),
                        new WindowsScrollablePolicy(),
                        sessionSpec.getProcessesToKillDuringTest(),
                        widget -> "TESTAR"
                ),
                new DefaultActionExecutionService(),
                new DescriptionActionResolver()
        );
    }

    private static PlatformServices webdriver(PlatformSessionSpec sessionSpec) {
        if (sessionSpec.getTargetType() != PlatformSessionSpec.TargetType.EXECUTABLE) {
            throw new UnsupportedPlatformException(
                    "Unsupported WebDriver target type: " + sessionSpec.getTargetType()
            );
        }

        return new PlatformServices(
                WebdriverSystemService.fromSutConnector(sessionSpec.getTarget()),
                new DefaultStateService(
                        WebdriverStateService.browser(sessionSpec.getStateTimeoutSeconds())
                ),
                WebdriverActionDerivationFactory.create(
                        new WebdriverClickablePolicy(),
                        new WebdriverTypeablePolicy(),
                        new WebdriverScrollablePolicy(),
                        widget -> "TESTAR"
                ),
                new DefaultActionExecutionService(),
                new DescriptionActionResolver()
        );
    }
}
