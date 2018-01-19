package es.upv.staq.testar.strategyparser;

import java.util.ArrayList;

import org.fruit.alayer.Action;

import es.upv.staq.testar.algorithms.StateManager;

public class SnRandomUnexecutedAction extends StrategyNodeAction {

	public SnRandomUnexecutedAction(ArrayList<StrategyNode> children) {
		super(children);
	}

	@Override
	public Action getAction(StateManager state) {
		return state.getRandomAction("UNEX");
	}

}
