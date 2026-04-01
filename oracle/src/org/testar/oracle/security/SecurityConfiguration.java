/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2022-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2022-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.security;

import java.util.Arrays;
import java.util.List;

public class SecurityConfiguration {
    public static int maxBenchmarkExecutionTimes = 2;
    public static String resultWriterOutput = "JSON";

    // Enable always the passive HeaderAnalysisSecurityOracle oracle by default
    private List<String> oracles = Arrays.asList("HeaderAnalysisSecurityOracle");

    public String loginUrl = "http://localhost:41948/Account/Login";
    public String usernameField = "username";
    public String passwordField = "password";
    public String submitButton = "1, 0, 2, 0, 0, 3, 0";
    public String logoutButton = "logout";

    public String username = "Admin";
    public String password = "123";

    public long tokenInvalidationWaitTime = 60000; /** One minute **/

    /**
     * Create the security configuration only with the passive HeaderAnalysisSecurityOracle. 
     */
    public SecurityConfiguration() {}

    /**
     * Create the security configuration with the passive HeaderAnalysisSecurityOracle 
     * and the chosen active security oracle. 
     * 
     * @param activeOracle
     */
    public SecurityConfiguration(ActiveSecurityOracle.ActiveOracle activeOracle) {
    	oracles = Arrays.asList("HeaderAnalysisSecurityOracle", activeOracle.getOracle());
    }

    public List<String> getOracles() {
    	return oracles;
    }
}
