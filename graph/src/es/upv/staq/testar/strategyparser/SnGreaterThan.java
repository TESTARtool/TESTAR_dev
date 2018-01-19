package es.upv.staq.testar.strategyparser;

import java.util.ArrayList;

import es.upv.staq.testar.algorithms.StateManager;

public class SnGreaterThan extends StrategyNodeBoolean {
	StrategyNodeNumber child1;
	StrategyNodeNumber child2;
	
	public SnGreaterThan(ArrayList<StrategyNode> children){
		super(children);
		child1 = (StrategyNodeNumber)children.get(0);
		child2 = (StrategyNodeNumber)children.get(1);
	}
	@Override
	public boolean getValue(StateManager state) {
		return (child1.getValue(state) > child2.getValue(state));
	}

}
