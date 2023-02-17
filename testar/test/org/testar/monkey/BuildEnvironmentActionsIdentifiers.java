package org.testar.monkey;

import java.util.Set;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.junit.Before;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.actions.ActivateSystem;
import org.testar.monkey.alayer.windows.WinProcessActivator;
import org.testar.stub.StateStub;

public class BuildEnvironmentActionsIdentifiers {

	private static DefaultProtocol defaultProtocol;
	private static StateStub state;

	@BeforeClass
	public static void setup() {
		// To avoid issues with java awt robot, we only execute this unit tests in windows environments.
		Assume.assumeTrue(System.getProperty("os.name").toLowerCase().contains("windows"));
		defaultProtocol = new DefaultProtocol();
	}

	@Before
	public void prepare_widget_and_state() {
		state = new StateStub();

		// Build widget and state identifiers
		defaultProtocol.buildStateIdentifiers(state);
	}

	@Test
	public void checkActivateSystemAction() {
		// Create the settings and prepare an empty ProcessesToKillDuringTest value
		defaultProtocol.settings = new Settings();
		defaultProtocol.settings.set(ConfigTags.ProcessesToKillDuringTest, "");
		// Mock the system and prepare a system activator
		SUT system = Mockito.mock(SUT.class);
		Mockito.when(system.get(Tags.SystemActivator, null)).thenReturn(new WinProcessActivator(1));
		// Prepare a custom state that is not in the foreground tag
		state.set(Tags.Foreground, false);
		
		// With these conditions, call the derive action to force getting an ActivateSystem action
		Set<Action> actions = defaultProtocol.deriveActions(system, state);
		Assert.isTrue(actions.size() == 1);
		Assert.isTrue(actions.iterator().next().getClass().getName().equals(ActivateSystem.class.getName()));
		Assert.notNull(actions.iterator().next().get(Tags.OriginWidget));
		
		// Then build the action identifier
		defaultProtocol.buildStateActionsIdentifiers(state, actions);
		// To check that Action identifiers were built
		Assert.notNull(actions.iterator().next().get(Tags.AbstractIDCustom));
		Assert.notNull(actions.iterator().next().get(Tags.ConcreteIDCustom));
	}
}
