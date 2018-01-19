package es.upv.staq.testar.strategyparser;

import java.util.ArrayList;

import es.upv.staq.testar.algorithms.StateManager;

public abstract class StrategyNodeNumber extends StrategyNode{
	public StrategyNodeNumber(ArrayList<StrategyNode> children) {
		super(children);
		
	}

	public abstract int getValue(StateManager state);
}
