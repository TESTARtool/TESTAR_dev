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

package org.testar.securityanalysis.helpers;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.securityanalysis.JsonSecurityResultWriter;
import org.testar.securityanalysis.SecurityConfiguration;
import org.testar.securityanalysis.SecurityResultWriter;
import org.testar.securityanalysis.oracles.ActiveSecurityOracle;

public class SecurityOracleOrchestratorTest {

	private static DevTools mockDevTools;

	@BeforeClass
	public static void setup() {
		// Mock DevTools listener to do nothing
		mockDevTools = Mockito.mock(DevTools.class);
		Mockito.doNothing().when(mockDevTools).addListener(null, null);
	}

	@Test
	public void orchestrator_passive_oracle() {
		SecurityConfiguration securityConfiguration = new SecurityConfiguration();
		SecurityResultWriter securityResultWriter = new JsonSecurityResultWriter();
		RemoteWebDriver webDriver = WdDriver.getRemoteWebDriver();

		SecurityOracleOrchestrator oracleOrchestrator = 
				new SecurityOracleOrchestrator(securityResultWriter, 
						securityConfiguration.getOracles(), 
						webDriver, 
						mockDevTools);

		assertFalse(oracleOrchestrator.hasActiveOracle());
	}

	@Test
	public void orchestrator_active_oracle() {
		SecurityConfiguration securityConfiguration = 
				new SecurityConfiguration(ActiveSecurityOracle.ActiveOracle.SQL_INJECTION);
		SecurityResultWriter securityResultWriter = new JsonSecurityResultWriter();
		RemoteWebDriver webDriver = WdDriver.getRemoteWebDriver();

		SecurityOracleOrchestrator oracleOrchestrator = 
				new SecurityOracleOrchestrator(securityResultWriter, 
						securityConfiguration.getOracles(), 
						webDriver, 
						mockDevTools);

		assertTrue(oracleOrchestrator.hasActiveOracle());
	}

}
