package es.upv.staq.testar.strategyparser;

import java.util.ArrayList;

import org.fruit.alayer.Role;
import org.fruit.alayer.actions.ActionRoles;

import es.upv.staq.testar.algorithms.StateManager;

public class SnTypeAction extends StrategyNodeActiontype {

	public SnTypeAction(ArrayList<StrategyNode> children) {
		super(children);
	}

	@Override
	public Role getActiontype(StateManager state) {
		return ActionRoles.Type;
	}

}
