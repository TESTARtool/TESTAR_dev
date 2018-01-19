package es.upv.staq.testar.strategyparser;

import java.util.ArrayList;


import es.upv.staq.testar.algorithms.StateManager;

public class SnStateHasNotChanged extends StrategyNodeBoolean {

	public SnStateHasNotChanged(ArrayList<StrategyNode> children) {
		super(children);
	}

	@Override
	public boolean getValue(StateManager state) {
		return state.hasStateNotChanged();
	}

}
