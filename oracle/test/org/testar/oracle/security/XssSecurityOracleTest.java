package org.testar.oracle.security;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.plugin.NativeLinker;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;
import org.testar.webdriver.action.WdSecurityInjectionAction;
import org.testar.webdriver.action.WdSecurityUrlInjectionAction;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.tag.WdTags;
import org.testar.core.action.Action;
import org.testar.core.alayer.Rect;
import org.testar.core.tag.Tags;

import java.util.Set;

public class XssSecurityOracleTest {

	private static RemoteWebDriver mockDriver;

	@BeforeClass
	public static void setup() {
		mockDriver = Mockito.mock(RemoteWebDriver.class);
		NativeLinker.addWdDriverOS();
	}

	@Test
	public void xss_url_injection_action() {
		// Mock the driver URL to test XSS action injections
		Mockito.when(mockDriver.getCurrentUrl()).thenReturn("http://example.com/page?parameter=value&also=another");
		// And create an empty state to force to obtain only one URL XSS injection action
		StateStub state = new StateStub();

		SecurityResultWriter securityResultWriter = new JsonSecurityResultWriter();
		XssSecurityOracle xssSecurityOracle = 
				new XssSecurityOracle(securityResultWriter, mockDriver);

		XssSecurityOracle.setXssInjectionURL("<script>console.log(%27XSS%27);</script>");

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

	@Test
	public void xss_widget_injection_action() {
		// Mock the driver URL without parameters
		Mockito.when(mockDriver.getCurrentUrl()).thenReturn("http://example.com/page");
		// And create a state with a text widget to obtain only one text area XSS injection action
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		// Widget must be visible at browser canvas and typeable
		widget.set(WdTags.WebIsFullOnScreen, true);
		widget.set(Tags.Shape, Rect.from(1, 1, 1, 1));
		widget.set(Tags.Role, WdRoles.WdTEXTAREA);

		SecurityResultWriter securityResultWriter = new JsonSecurityResultWriter();
		XssSecurityOracle xssSecurityOracle = 
				new XssSecurityOracle(securityResultWriter, mockDriver);

		XssSecurityOracle.setXssInjectionText("<script>console.log('XSS_detected');</script>");

		Set<Action> xssActions = xssSecurityOracle.getActions(state);
		assertTrue(xssActions.size() == 1);

		// Check that text area XSS injection action was created correctly
		WdSecurityInjectionAction xssInjectionWidgetAction = (WdSecurityInjectionAction) xssActions.stream()
				.filter(action -> action instanceof WdSecurityInjectionAction)
				.findFirst().get();

		assertTrue(xssInjectionWidgetAction.get(Tags.Desc, "").equals("Inject text that contains special characters"));

		assertTrue(xssInjectionWidgetAction.getText().equals("<script>console.log('XSS_detected');</script>"));
	}

}
