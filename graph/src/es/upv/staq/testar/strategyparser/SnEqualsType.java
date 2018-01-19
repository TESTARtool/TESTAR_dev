package es.upv.staq.testar.strategyparser;

import java.util.ArrayList;

import es.upv.staq.testar.algorithms.StateManager;

public class SnEqualsType extends StrategyNodeBoolean {
	StrategyNodeActiontype child1;
	StrategyNodeActiontype child2;
	
	public SnEqualsType(ArrayList<StrategyNode> children){
		super(children);
		child1 = (StrategyNodeActiontype)children.get(0);
		child2 = (StrategyNodeActiontype)children.get(1);
	}
	
	@Override
	public boolean getValue(StateManager state) {
		return (child1.getActiontype(state) == child2.getActiontype(state));
	}

}
