package es.upv.staq.testar.strategyparser;

import java.util.ArrayList;

import es.upv.staq.testar.algorithms.StateManager;

public class SnNumberOfPreviousActions extends StrategyNodeNumber {

	public SnNumberOfPreviousActions(ArrayList<StrategyNode> children) {
		super(children);
	}

	@Override
	public int getValue(StateManager state) {
		return state.getNumberOfPreviousActions();
	}

}
