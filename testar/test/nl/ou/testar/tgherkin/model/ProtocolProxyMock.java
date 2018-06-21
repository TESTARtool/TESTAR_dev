package nl.ou.testar.tgherkin.model;

import org.fruit.alayer.State;
import org.fruit.alayer.Widget;
import org.fruit.monkey.Settings;

import nl.ou.testar.tgherkin.protocol.DocumentProtocol;

/**
 * ProtocolProxyMock is a mock for ProtocolProxy.
 *
 */
public class ProtocolProxyMock extends DocumentProtocol implements ProtocolProxy {

	private Settings settings;
	private State state;

	/**
	 * Constructor.
	 * @param settings given configuration settings
	 * @param state given state
	 */
	public ProtocolProxyMock(Settings settings, State state) {
		super();
		this.settings = settings;
		this.state = state;
	}

	@Override
	public Settings getSettings() {
		return settings;
	}

	@Override
	public State getState() {
		return state;
	}

	@Override
	public boolean isUnfiltered(Widget widget) {
		return true;
	}

	@Override
	public void storeWidget(String stateID, Widget widget) {
		// no action
	}
	
}
