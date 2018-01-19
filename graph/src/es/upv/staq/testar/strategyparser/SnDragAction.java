package es.upv.staq.testar.strategyparser;

import java.util.ArrayList;

import org.fruit.alayer.Role;
import org.fruit.alayer.actions.ActionRoles;

import es.upv.staq.testar.algorithms.StateManager;

public class SnDragAction extends StrategyNodeActiontype {

	public SnDragAction(ArrayList<StrategyNode> children) {
		super(children);
	}

	@Override
	public Role getActiontype(StateManager state) {
		return ActionRoles.Drag;
	}

}
