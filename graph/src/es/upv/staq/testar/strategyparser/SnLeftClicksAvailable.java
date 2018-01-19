package es.upv.staq.testar.strategyparser;

import java.util.ArrayList;



import org.fruit.alayer.actions.ActionRoles;

import es.upv.staq.testar.algorithms.StateManager;

public class SnLeftClicksAvailable extends StrategyNodeBoolean {

	public SnLeftClicksAvailable(ArrayList<StrategyNode> children) {
		super(children);
	}

	@Override
	public boolean getValue(StateManager state) {
		return state.isAvailable(ActionRoles.LeftClickAt);
	}

}
