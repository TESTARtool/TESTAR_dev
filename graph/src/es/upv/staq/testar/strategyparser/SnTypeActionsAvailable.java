package es.upv.staq.testar.strategyparser;

import java.util.ArrayList;

import org.fruit.alayer.actions.ActionRoles;

import es.upv.staq.testar.algorithms.StateManager;

public class SnTypeActionsAvailable extends StrategyNodeBoolean {

	public SnTypeActionsAvailable(ArrayList<StrategyNode> children) {
		super(children);
	}

	@Override
	public boolean getValue(StateManager state) {
		return state.isAvailable(ActionRoles.Type);
	}

}
