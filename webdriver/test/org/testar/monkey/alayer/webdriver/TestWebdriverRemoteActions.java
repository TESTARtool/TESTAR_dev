package org.testar.monkey.alayer.webdriver;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.testar.monkey.Assert;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.actions.WdActionRoles;
import org.testar.monkey.alayer.actions.WdRemoteClickAction;
import org.testar.monkey.alayer.actions.WdRemoteScrollClickAction;
import org.testar.monkey.alayer.actions.WdRemoteScrollTypeAction;
import org.testar.monkey.alayer.actions.WdRemoteTypeAction;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;

public class TestWebdriverRemoteActions {

	private WdRootElement rootWdElement;
	private WdState rootWdState;

	private WdElement childWdElement;
	private WdWidget childWdWidget;

	// Before each JUnit test prepare the new objects
	@Before
	public void prepareWebdriverElements() {
		rootWdElement = new WdRootElement();
		rootWdState = new WdState(rootWdElement);

		childWdElement = new WdElement(rootWdElement, rootWdElement);
		childWdWidget = new WdWidget(rootWdState, rootWdState, childWdElement);
	}

	@Test
	public void test_create_WdRemoteClickAction() {
		// Custom a name, role, and rect values
		childWdElement.name = "custom_name_value";
		childWdElement.rect = Rect.from(0, 0, 100, 100);
		childWdWidget.set(Tags.Role, WdRoles.WdBUTTON);

		// Mock a remoteWebElement with getId
		RemoteWebElement remoteWebElement = Mockito.mock(RemoteWebElement.class);
		Mockito.when(remoteWebElement.getId()).thenReturn("remote_id_value");
		childWdElement.remoteWebElement = remoteWebElement;

		WdRemoteClickAction remoteClickAction = new WdRemoteClickAction(childWdWidget);

		// Verify the action and the Tags were created
		Assert.isTrue(remoteClickAction.get(Tags.OriginWidget).equals(childWdWidget));
		Assert.isTrue(remoteClickAction.get(Tags.Role).equals(WdActionRoles.RemoteClick));
		Assert.isTrue(remoteClickAction.get(Tags.Desc).equals("Remote click " + childWdElement.name + " : " + "remote_id_value"));
		Assert.isTrue(remoteClickAction.toShortString().equals("Remote click " + childWdElement.name));
		Assert.isTrue(remoteClickAction.toParametersString().equals("Remote click " + childWdElement.name));
		Assert.isTrue(remoteClickAction.toString(new Role[0]).equals("Remote click " + childWdElement.name));
		Assert.isTrue(remoteClickAction.toString().equals("Remote click " + childWdElement.name));

		// Verify that run the action invokes the remoteWebElement click event
		remoteClickAction.run(Mockito.mock(SUT.class), Mockito.mock(State.class), 1);
		Mockito.verify(remoteWebElement).click();
	}

	@Test
	public void test_create_WdRemoteScrollClickAction() {
		// Custom a name, role, and rect values
		childWdElement.name = "custom_name_value";
		childWdElement.rect = Rect.from(0, 0, 100, 100);
		childWdWidget.set(Tags.Role, WdRoles.WdBUTTON);

		// Mock a remoteWebElement with getId and getWrappedDriver
		RemoteWebElement remoteWebElement = Mockito.mock(RemoteWebElement.class);
		Mockito.when(remoteWebElement.getId()).thenReturn("remote_id_value");
		Mockito.when(remoteWebElement.getWrappedDriver()).thenReturn(Mockito.mock(RemoteWebDriver.class));
		childWdElement.remoteWebElement = remoteWebElement;

		WdRemoteScrollClickAction remoteScrollClickAction = new WdRemoteScrollClickAction(childWdWidget);

		// Verify the action and the Tags were created
		Assert.isTrue(remoteScrollClickAction.get(Tags.OriginWidget).equals(childWdWidget));
		Assert.isTrue(remoteScrollClickAction.get(Tags.Role).equals(WdActionRoles.RemoteScrollClick));
		Assert.isTrue(remoteScrollClickAction.get(Tags.Desc).equals("Remote scroll and click " + childWdElement.name + " : " + "remote_id_value"));
		Assert.isTrue(remoteScrollClickAction.toShortString().equals("Remote scroll and click " + childWdElement.name));
		Assert.isTrue(remoteScrollClickAction.toParametersString().equals("Remote scroll and click " + childWdElement.name));
		Assert.isTrue(remoteScrollClickAction.toString(new Role[0]).equals("Remote scroll and click " + childWdElement.name));
		Assert.isTrue(remoteScrollClickAction.toString().equals("Remote scroll and click " + childWdElement.name));

		// Verify that run the action invokes the remoteWebElement click event
		remoteScrollClickAction.run(Mockito.mock(SUT.class), Mockito.mock(State.class), 1);
		Mockito.verify(remoteWebElement).click();
	}

	@Test
	public void test_create_WdRemoteTypeAction() {
		// Custom a name, role, and rect values
		childWdElement.name = "custom_name_value";
		childWdElement.rect = Rect.from(0, 0, 100, 100);
		childWdWidget.set(Tags.Role, WdRoles.WdBUTTON);

		// Mock a remoteWebElement with getId
		RemoteWebElement remoteWebElement = Mockito.mock(RemoteWebElement.class);
		Mockito.when(remoteWebElement.getId()).thenReturn("remote_id_value");
		childWdElement.remoteWebElement = remoteWebElement;

		String typedText = "input_text";
		WdRemoteTypeAction remoteTypeAction = new WdRemoteTypeAction(childWdWidget, typedText);

		// Verify the action and the Tags were created
		Assert.isTrue(remoteTypeAction.get(Tags.OriginWidget).equals(childWdWidget));
		Assert.isTrue(remoteTypeAction.get(Tags.Role).equals(WdActionRoles.RemoteType));
		Assert.isTrue(remoteTypeAction.get(Tags.Desc).equals("Remote type " + typedText + " to widget " + childWdElement.name + " : " + "remote_id_value"));
		Assert.isTrue(remoteTypeAction.toShortString().equals("Remote type " + typedText + " " + childWdElement.name));
		Assert.isTrue(remoteTypeAction.toParametersString().equals("Remote type " + typedText + " " + childWdElement.name));
		Assert.isTrue(remoteTypeAction.toString(new Role[0]).equals("Remote type " + typedText + " " + childWdElement.name));
		Assert.isTrue(remoteTypeAction.toString().equals("Remote type " + typedText + " " + childWdElement.name));

		// Verify that run the action invokes the remoteWebElement sendKeys event
		remoteTypeAction.run(Mockito.mock(SUT.class), Mockito.mock(State.class), 1);
		Mockito.verify(remoteWebElement).sendKeys(typedText);
	}

	@Test
	public void test_create_WdRemoteScrollTypeAction() {
		// Custom a name, role, and rect values
		childWdElement.name = "custom_name_value";
		childWdElement.rect = Rect.from(0, 0, 100, 100);
		childWdWidget.set(Tags.Role, WdRoles.WdBUTTON);

		// Mock a remoteWebElement with getId and getWrappedDriver
		RemoteWebElement remoteWebElement = Mockito.mock(RemoteWebElement.class);
		Mockito.when(remoteWebElement.getId()).thenReturn("remote_id_value");
		Mockito.when(remoteWebElement.getWrappedDriver()).thenReturn(Mockito.mock(RemoteWebDriver.class));
		childWdElement.remoteWebElement = remoteWebElement;

		String typedText = "input_scroll_text";
		WdRemoteScrollTypeAction remoteScrollTypeAction = new WdRemoteScrollTypeAction(childWdWidget, typedText);

		// Verify the action and the Tags were created
		Assert.isTrue(remoteScrollTypeAction.get(Tags.OriginWidget).equals(childWdWidget));
		Assert.isTrue(remoteScrollTypeAction.get(Tags.Role).equals(WdActionRoles.RemoteScrollType));
		Assert.isTrue(remoteScrollTypeAction.get(Tags.Desc).equals("Remote scroll and type " + typedText + " to widget " + childWdElement.name + " : " + "remote_id_value"));
		Assert.isTrue(remoteScrollTypeAction.toShortString().equals("Remote scroll and type " + typedText + " " + childWdElement.name));
		Assert.isTrue(remoteScrollTypeAction.toParametersString().equals("Remote scroll and type " + typedText + " " + childWdElement.name));
		Assert.isTrue(remoteScrollTypeAction.toString(new Role[0]).equals("Remote scroll and type " + typedText + " " + childWdElement.name));
		Assert.isTrue(remoteScrollTypeAction.toString().equals("Remote scroll and type " + typedText + " " + childWdElement.name));

		// Verify that run the action invokes the remoteWebElement sendKeys event
		remoteScrollTypeAction.run(Mockito.mock(SUT.class), Mockito.mock(State.class), 1);
		Mockito.verify(remoteWebElement).sendKeys(typedText);
	}

}
