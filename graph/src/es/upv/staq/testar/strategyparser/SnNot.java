package es.upv.staq.testar.strategyparser;

import java.util.ArrayList;

import es.upv.staq.testar.algorithms.StateManager;

public class SnNot extends StrategyNodeBoolean {
	StrategyNodeBoolean child1;

	public SnNot(ArrayList<StrategyNode> children) {
		super(children);
		child1 = (StrategyNodeBoolean)children.get(0);
	}

	@Override
	public boolean getValue(StateManager state) {
		return !(child1.getValue(state));
	}

}
