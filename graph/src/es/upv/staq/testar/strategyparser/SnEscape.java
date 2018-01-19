package es.upv.staq.testar.strategyparser;

import java.util.ArrayList;

import org.fruit.alayer.Action;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.devices.KBKeys;

import es.upv.staq.testar.algorithms.StateManager;

public class SnEscape extends StrategyNodeAction {

	public SnEscape(ArrayList<StrategyNode> children) {
		super(children);
	}

	@Override
	public Action getAction(StateManager state) {
		return new AnnotatingActionCompiler().hitKey(KBKeys.VK_ESCAPE);
	}

}
