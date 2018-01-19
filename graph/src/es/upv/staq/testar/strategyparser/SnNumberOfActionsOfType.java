package es.upv.staq.testar.strategyparser;

import java.util.ArrayList;

import es.upv.staq.testar.algorithms.StateManager;

public class SnNumberOfActionsOfType extends StrategyNodeNumber {
	StrategyNodeActiontype child1;

	public SnNumberOfActionsOfType(ArrayList<StrategyNode> children) {
		super(children);
		child1 = (StrategyNodeActiontype)children.get(0);
	}

	@Override
	public int getValue(StateManager state) {
		
		return state.getNumberOfActions(child1.getActiontype(state));
	}

}
