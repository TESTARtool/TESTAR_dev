package org.testar.monkey;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.junit.Before;
import org.junit.Test;
import org.testar.EventHandler;
import org.testar.monkey.RuntimeControlsProtocol.Modes;

public class TestRuntimeControlListener {

	private DefaultProtocol defaultProtocol;

	@Before
	public void prepare_test_protocol() {
		// Initialize the DefaultProtocol which extends from RuntimeControlsProtocol
		defaultProtocol = new DefaultProtocol();
		defaultProtocol.eventHandler = defaultProtocol.initializeEventHandler();
		defaultProtocol.mode = Modes.Spy;
	}

	@Test
	public void testKeyBoardListenerEnabled() {
		// Initialize the KeyBoardListener settings to enable listening KeyBoard inputs
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(ConfigTags.KeyBoardListener, true));
		Settings settings = new Settings(tags, new Properties());
		defaultProtocol.settings = settings;

		// TESTAR is running in SPY mode
		Assert.isTrue(defaultProtocol.mode() == Modes.Spy);

		// Then, after a fake user event, KeyBoard SHIFT + DOWN
		simulateUserKeyEvent(defaultProtocol.eventHandler);

		// TESTAR changed to QUIT mode because KeyBoardListener is enabled
		Assert.isTrue(defaultProtocol.mode() == Modes.Quit);
	}

	@Test
	public void testKeyBoardListenerDisabled() {
		// Initialize the KeyBoardListener settings to disable listening KeyBoard inputs
		List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
		tags.add(Pair.from(ConfigTags.KeyBoardListener, false));
		Settings settings = new Settings(tags, new Properties());
		defaultProtocol.settings = settings;

		// TESTAR is running in SPY mode
		Assert.isTrue(defaultProtocol.mode() == Modes.Spy);

		// Then, after a fake user event, KeyBoard SHIFT + DOWN
		simulateUserKeyEvent(defaultProtocol.eventHandler);

		// TESTAR still running in SPY mode because KeyBoardListener is disabled
		Assert.isTrue(defaultProtocol.mode() == Modes.Spy);
	}

	/**
	 * Invoke org.testar.EventHandler to simulate NativeKeyEvents. 
	 * This allow us to avoid using real system events such as with java.awt.Robot. 
	 * 
	 * @param eventHandler
	 */
	private void simulateUserKeyEvent(EventHandler eventHandler) {
		NativeKeyEvent shiftPressEvent = new NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_PRESSED, 0, NativeKeyEvent.CTRL_L_MASK, NativeKeyEvent.VC_SHIFT, NativeKeyEvent.CHAR_UNDEFINED);
		NativeKeyEvent downPressEvent = new NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_PRESSED, 0, NativeKeyEvent.CTRL_L_MASK, NativeKeyEvent.VC_DOWN, NativeKeyEvent.CHAR_UNDEFINED);
		NativeKeyEvent shiftReleaseEvent = new NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_RELEASED, 0, NativeKeyEvent.CTRL_L_MASK, NativeKeyEvent.VC_SHIFT, NativeKeyEvent.CHAR_UNDEFINED);
		NativeKeyEvent downReleaseEvent = new NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_RELEASED, 0, NativeKeyEvent.CTRL_L_MASK, NativeKeyEvent.VC_DOWN, NativeKeyEvent.CHAR_UNDEFINED);

		eventHandler.nativeKeyPressed(shiftPressEvent);
		eventHandler.nativeKeyPressed(downPressEvent);
		eventHandler.nativeKeyReleased(shiftReleaseEvent);
		eventHandler.nativeKeyReleased(downReleaseEvent);
	}

}
