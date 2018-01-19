package es.upv.staq.testar.strategyparser;

import java.util.ArrayList;

import org.fruit.alayer.Action;

import es.upv.staq.testar.algorithms.StateManager;

public class SnRandomLeastExecutedAction extends StrategyNodeAction {

	public SnRandomLeastExecutedAction(ArrayList<StrategyNode> children) {
		super(children);
	}

	@Override
	public Action getAction(StateManager state) {
		return state.getRandomAction("LEAST");
	}

}
