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
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.actions.WdSecurityInjectionAction;
import org.testar.monkey.alayer.actions.WdSecurityUrlInjectionAction;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.plugin.NativeLinker;
import org.testar.securityanalysis.JsonSecurityResultWriter;
import org.testar.securityanalysis.SecurityResultWriter;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

import java.util.Set;

public class SqlInjectionSecurityOracleTest {

	private static RemoteWebDriver mockDriver;

	@BeforeClass
	public static void setup() {
		mockDriver = Mockito.mock(RemoteWebDriver.class);
		NativeLinker.addWdDriverOS();
	}

	@Test
	public void sql_url_injection_action() {
		// Mock the driver URL to test SQL action injections
		Mockito.when(mockDriver.getCurrentUrl()).thenReturn("http://example.com/page?parameter=value&also=another");
		// And create an empty state to force to obtain only one URL SQL injection action
		StateStub state = new StateStub();

		SecurityResultWriter securityResultWriter = new JsonSecurityResultWriter();
		SqlInjectionSecurityOracle sqlInjectionSecurityOracle = 
				new SqlInjectionSecurityOracle(securityResultWriter, mockDriver);

		SqlInjectionSecurityOracle.setSqlInjectionURL("%27");

		Set<Action> sqlActions = sqlInjectionSecurityOracle.getActions(state);
		assertTrue(sqlActions.size() == 1);

		// Check that the URL with the SQL injection was created correctly
		WdSecurityUrlInjectionAction sqlInjectionURLaction = (WdSecurityUrlInjectionAction) sqlActions.stream()
				.filter(action -> action instanceof WdSecurityUrlInjectionAction)
				.findFirst().get();

		assertTrue(sqlInjectionURLaction.get(Tags.Desc, "").equals("Execute Webdriver script to redirect to different url"));

		String sqlURL = sqlInjectionURLaction.getText();
		assertTrue(sqlURL.equals("http://example.com/page?parameter=%27&also=%27"));
	}

	@Test
	public void sql_widget_injection_action() {
		// Mock the driver URL without parameters
		Mockito.when(mockDriver.getCurrentUrl()).thenReturn("http://example.com/page");
		// And create a state with a text widget to obtain only one text area SQL injection action
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		// Widget must be visible at browser canvas and typeable
		widget.set(WdTags.WebIsFullOnScreen, true);
		widget.set(Tags.Shape, Rect.from(1, 1, 1, 1));
		widget.set(Tags.Role, WdRoles.WdTEXTAREA);

		SecurityResultWriter securityResultWriter = new JsonSecurityResultWriter();
		SqlInjectionSecurityOracle sqlInjectionSecurityOracle = 
				new SqlInjectionSecurityOracle(securityResultWriter, mockDriver);

		SqlInjectionSecurityOracle.setSqlInjectionText("' and sleep(10)");

		Set<Action> sqlActions = sqlInjectionSecurityOracle.getActions(state);
		assertTrue(sqlActions.size() == 1);

		// Check that text area SQL injection action was created correctly
		WdSecurityInjectionAction sqlInjectionWidgetAction = (WdSecurityInjectionAction) sqlActions.stream()
				.filter(action -> action instanceof WdSecurityInjectionAction)
				.findFirst().get();

		assertTrue(sqlInjectionWidgetAction.get(Tags.Desc, "").equals("Inject text that contains special characters"));

		assertTrue(sqlInjectionWidgetAction.getText().equals("' and sleep(10)"));
	}

}
