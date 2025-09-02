package org.testar.monkey;

import java.util.Collections;
import java.util.Set;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.junit.Before;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.actions.ActivateSystem;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.windows.WinProcessActivator;
import org.testar.reporting.ReportManager;
import org.testar.settings.Settings;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class TestEnvironmentActions extends DefaultProtocol {

	private static StateStub state;
	private static WidgetStub widget;
	private static StdActionCompiler ac = new AnnotatingActionCompiler();

	private static String pathTest = "[0,0,1]";

	@BeforeClass
	public static void setup() {
		// To avoid issues with java awt robot, we only execute this unit tests in windows environments.
		Assume.assumeTrue(System.getProperty("os.name").toLowerCase().contains("windows"));
	}

	@Before
	public void prepare_widget_and_state() {
		settings = new Settings();
		state = new StateStub();
		widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Shape, Rect.fromCoordinates(0, 0, 100, 100));
		widget.set(Tags.Role, Roles.Button);
		widget.set(Tags.Path, pathTest);

		// Build widget and state identifiers
		buildStateIdentifiers(state);
	}

	@Test
	public void testActivateSystemActionIdentifier() {
		// Prepare an empty ProcessesToKillDuringTest value
		settings.set(ConfigTags.ProcessesToKillDuringTest, "");
		// Mock the system and prepare a system activator
		SUT system = Mockito.mock(SUT.class);
		Mockito.when(system.get(Tags.SystemActivator, null)).thenReturn(new WinProcessActivator(1));
		// Prepare a custom state that is not in the foreground tag
		state.set(Tags.Foreground, false);

		// With these conditions, call the derive action to force getting an ActivateSystem action
		Set<Action> actions = deriveActions(system, state);
		Assert.isTrue(actions.size() == 1);
		Assert.isTrue(actions.iterator().next().getClass().getName().equals(ActivateSystem.class.getName()));
		Assert.notNull(actions.iterator().next().get(Tags.OriginWidget));

		// Then build the action identifier
		buildStateActionsIdentifiers(state, actions);
		// To check that Action identifiers were built
		Assert.notNull(actions.iterator().next().get(Tags.AbstractID));
		Assert.notNull(actions.iterator().next().get(Tags.ConcreteID));
		// Check that the OriginWidget, Description, and Role are not null
		Assert.notNull(actions.iterator().next().get(Tags.OriginWidget));
		Assert.notNull(actions.iterator().next().get(Tags.Desc));
		Assert.notNull(actions.iterator().next().get(Tags.Role));
	}

	@Test
	public void testActivateSystemActionPreSelection() {
		// Prepare an empty ProcessesToKillDuringTest value
		settings.set(ConfigTags.ProcessesToKillDuringTest, "");
		// Mock the system and prepare a system activator
		SUT system = Mockito.mock(SUT.class);
		Mockito.when(system.get(Tags.SystemActivator, null)).thenReturn(new WinProcessActivator(1));
		// Prepare a custom state that is not in the foreground tag
		state.set(Tags.Foreground, false);

		// With these conditions, call the derive action to force getting an ActivateSystem action
		Set<Action> defaultActions = deriveActions(system, state);
		// Add a fake GUI left click action
		Action leftClickAction = ac.leftClickAt(widget);
		defaultActions.add(leftClickAction);
		// We must have the leftClick and the ActivateSystem actions
		Assert.isTrue(defaultActions.size() == 2);

		// Because there exists an ActivateSystem action
		// preSelectAction must force to return it
		reportManager = Mockito.mock(ReportManager.class);
		Set<Action> preSelectedActions = preSelectAction(system, state, defaultActions);
		Assert.isTrue(preSelectedActions.size() == 1);
		Action forcedAction = preSelectedActions.iterator().next();

		Assert.isTrue(forcedAction instanceof ActivateSystem);
	}

	@Test
	public void testEscActionIdentifier() {
		// Mock the system
		SUT system = Mockito.mock(SUT.class);
		// Create an empty set of actions to force creating a ESC key action
		Set<Action> initialActions = Collections.emptySet();

		// Because there are no actions, this method returns a Set with one ESC key action
		// This method already builds the environment action identifier
		reportManager = Mockito.mock(ReportManager.class);
		Set<Action> preActions = preSelectAction(system, state, initialActions);
		Assert.isTrue(preActions.size() == 1);

		// To check that Action identifiers were built
		Assert.notNull(preActions.iterator().next().get(Tags.AbstractID));
		Assert.notNull(preActions.iterator().next().get(Tags.ConcreteID));
		// Check that the OriginWidget, Description, and Role are not null
		Assert.notNull(preActions.iterator().next().get(Tags.OriginWidget));
		Assert.notNull(preActions.iterator().next().get(Tags.Desc));
		Assert.notNull(preActions.iterator().next().get(Tags.Role));
	}
}
