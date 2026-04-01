package org.testar.oracle.security;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.core.action.Action;
import org.testar.core.alayer.Rect;
import org.testar.core.tag.Tags;
import org.testar.plugin.NativeLinker;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;
import org.testar.webdriver.action.WdSecurityInjectionAction;
import org.testar.webdriver.action.WdSecurityUrlInjectionAction;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.tag.WdTags;

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
