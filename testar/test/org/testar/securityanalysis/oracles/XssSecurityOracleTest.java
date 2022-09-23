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

package org.testar.securityanalysis.oracles;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.actions.WdSecurityUrlInjectionAction;
import org.testar.securityanalysis.JsonSecurityResultWriter;
import org.testar.securityanalysis.SecurityResultWriter;
import org.testar.stub.StateStub;

import java.util.Set;

public class XssSecurityOracleTest {

	private static RemoteWebDriver mockDriver;
	private static StateStub state;

	@BeforeClass
	public static void setup() {
		// Mock the driver URL to test XSS action injections
		mockDriver = Mockito.mock(RemoteWebDriver.class);
		Mockito.when(mockDriver.getCurrentUrl()).thenReturn("http://example.com/page?parameter=value&also=another");
	}

	@Test
	public void xss_url_injection_action() {
		SecurityResultWriter securityResultWriter = new JsonSecurityResultWriter();
		XssSecurityOracle xssSecurityOracle = 
				new XssSecurityOracle(securityResultWriter, mockDriver);

		XssSecurityOracle.setXssInjectionURL("<script>console.log(%27XSS%27);</script>");

		// Create an empty state to force to obtain only one URL XSS injection action
		state = new StateStub();
		Set<Action> xssActions = xssSecurityOracle.getActions(state);
		assertTrue(xssActions.size() == 1);

		// Check that the URL with the XSS injection was created correctly
		WdSecurityUrlInjectionAction xssInjectionURLaction = (WdSecurityUrlInjectionAction) xssActions.stream()
				.filter(action -> action instanceof WdSecurityUrlInjectionAction)
				.findFirst().get();

		assertTrue(xssInjectionURLaction.get(Tags.Desc, "").equals("Execute Webdriver script to redirect to different url"));

		String xssURL = xssInjectionURLaction.getText();
		assertTrue(xssURL.equals("http://example.com/page?parameter=<script>console.log(%27XSS%27);</script>&also=<script>console.log(%27XSS%27);</script>"));
	}

}
