package org.testar.oracle.security.helpers;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.oracle.security.JsonSecurityResultWriter;
import org.testar.oracle.security.SecurityConfiguration;
import org.testar.oracle.security.SecurityResultWriter;
import org.testar.webdriver.state.WdDriver;
import org.testar.oracle.security.ActiveSecurityOracle;

public class SecurityOracleOrchestratorTest {

	private static DevTools mockDevTools;

	@BeforeClass
	public static void setup() {
		// Mock DevTools
		mockDevTools = Mockito.mock(DevTools.class);
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
