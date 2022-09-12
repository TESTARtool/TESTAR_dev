/***************************************************************************************************
 *
 * Copyright (c) 2022 Open Universiteit - www.ou.nl
 * Copyright (c) 2022 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.securityanalysis;

import java.util.Arrays;
import java.util.List;

import org.testar.securityanalysis.oracles.ActiveSecurityOracle;

public class SecurityConfiguration {
    public static int maxBenchmarkExecutionTimes = 2;
    public static String resultWriterOutput = "JSON";

    // Enable always the passive HeaderAnalysisSecurityOracle oracle by default
    public List<String> oracles = Arrays.asList("HeaderAnalysisSecurityOracle");

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
}
