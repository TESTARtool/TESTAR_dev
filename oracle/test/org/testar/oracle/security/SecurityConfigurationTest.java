package org.testar.oracle.security;

import org.junit.Test;
import static org.junit.Assert.*;

public class SecurityConfigurationTest {

	@Test
	public void select_security_configuration() {
		SecurityConfiguration securityConfiguration = new SecurityConfiguration();
		assertTrue(securityConfiguration.getOracles().size() == 1);
		assertTrue(securityConfiguration.getOracles().contains("HeaderAnalysisSecurityOracle"));

		securityConfiguration = new SecurityConfiguration(ActiveSecurityOracle.ActiveOracle.SQL_INJECTION);
		assertTrue(securityConfiguration.getOracles().size() == 2);
		assertTrue(securityConfiguration.getOracles().contains("HeaderAnalysisSecurityOracle"));
		assertTrue(securityConfiguration.getOracles().contains("SqlInjectionSecurityOracle"));

		securityConfiguration = new SecurityConfiguration(ActiveSecurityOracle.ActiveOracle.XSS_INJECTION);
		assertTrue(securityConfiguration.getOracles().size() == 2);
		assertTrue(securityConfiguration.getOracles().contains("HeaderAnalysisSecurityOracle"));
		assertTrue(securityConfiguration.getOracles().contains("XssSecurityOracle"));

		securityConfiguration = new SecurityConfiguration(ActiveSecurityOracle.ActiveOracle.TOKEN_INVALIDATION);
		assertTrue(securityConfiguration.getOracles().size() == 2);
		assertTrue(securityConfiguration.getOracles().contains("HeaderAnalysisSecurityOracle"));
		assertTrue(securityConfiguration.getOracles().contains("TokenInvalidationSecurityOracle"));
	}

}
