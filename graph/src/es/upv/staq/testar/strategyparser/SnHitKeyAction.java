package es.upv.staq.testar.strategyparser;

import java.util.ArrayList;

import org.fruit.alayer.Role;
import org.fruit.alayer.actions.ActionRoles;

import es.upv.staq.testar.algorithms.StateManager;

public class SnHitKeyAction extends StrategyNodeActiontype {
	public SnHitKeyAction(ArrayList<StrategyNode> children) {
		super(children);
	}

	public Role getActiontype(StateManager state){
		return ActionRoles.HitKey;
	}
}
