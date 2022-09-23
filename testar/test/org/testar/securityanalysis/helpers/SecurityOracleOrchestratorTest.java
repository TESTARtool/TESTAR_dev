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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.securityanalysis.JsonSecurityResultWriter;
import org.testar.securityanalysis.SecurityConfiguration;
import org.testar.securityanalysis.SecurityResultWriter;
import org.testar.securityanalysis.oracles.ActiveSecurityOracle;

/**
 * Ignore by default because it has a dependency on a directory with chromedriver. 
 */

@Ignore
public class SecurityOracleOrchestratorTest {

	private static ChromeDriver driver;

	@BeforeClass
	public static void setup() {
		// Set the path to the desired chromedriver in the system
		System.setProperty("webdriver.chrome.driver", "/Windows/chromedriver.exe");
		driver = new ChromeDriver();
	}

	@Test
	public void orchestrator_passive_oracle() {
		SecurityConfiguration securityConfiguration = new SecurityConfiguration();
		SecurityResultWriter securityResultWriter = new JsonSecurityResultWriter();
		RemoteWebDriver webDriver = WdDriver.getRemoteWebDriver();
		DevTools devTools = driver.getDevTools();

		SecurityOracleOrchestrator oracleOrchestrator = 
				new SecurityOracleOrchestrator(securityResultWriter, 
						securityConfiguration.getOracles(), 
						webDriver, 
						devTools);

		assertFalse(oracleOrchestrator.hasActiveOracle());
	}

	@Test
	public void orchestrator_active_oracle() {
		SecurityConfiguration securityConfiguration = 
				new SecurityConfiguration(ActiveSecurityOracle.ActiveOracle.SQL_INJECTION);
		SecurityResultWriter securityResultWriter = new JsonSecurityResultWriter();
		RemoteWebDriver webDriver = WdDriver.getRemoteWebDriver();
		DevTools devTools = driver.getDevTools();

		SecurityOracleOrchestrator oracleOrchestrator = 
				new SecurityOracleOrchestrator(securityResultWriter, 
						securityConfiguration.getOracles(), 
						webDriver, 
						devTools);

		assertTrue(oracleOrchestrator.hasActiveOracle());
	}

	@AfterClass
	public static void cleanUp(){
		if (driver != null) {
			driver.close();
			driver.quit();
		}
	}

}
