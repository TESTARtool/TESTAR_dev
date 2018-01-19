package es.upv.staq.testar.strategyparser;

import java.util.ArrayList;

import es.upv.staq.testar.algorithms.StateManager;

public class SnOr extends StrategyNodeBoolean {
	StrategyNodeBoolean child1;
	StrategyNodeBoolean child2;

	public SnOr(ArrayList<StrategyNode> children) {
		super(children);
		child1 = (StrategyNodeBoolean)children.get(0);
		child2 = (StrategyNodeBoolean)children.get(1);
		
	}

	@Override
	public boolean getValue(StateManager state) {
		return (child1.getValue(state) || child2.getValue(state));
	}

}
