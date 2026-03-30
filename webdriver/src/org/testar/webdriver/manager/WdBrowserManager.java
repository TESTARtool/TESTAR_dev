/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.manager;

import org.openqa.selenium.remote.RemoteWebDriver;

public interface WdBrowserManager {

	public String resolveBinary(String browserPathCandidate);

	public RemoteWebDriver createWebDriver(String binaryPath, String extensionPath);

}
