package es.upv.staq.testar.strategyparser;

import java.util.ArrayList;

import org.fruit.alayer.Action;
import org.fruit.alayer.Role;

import es.upv.staq.testar.algorithms.StateManager;

public class SnRandomActionOfTypeOtherThan extends StrategyNodeAction {
	StrategyNodeActiontype child1;

	public SnRandomActionOfTypeOtherThan(ArrayList<StrategyNode> children) {
		super(children);
		child1 = (StrategyNodeActiontype)children.get(0);
		
	}

	@Override
	public Action getAction(StateManager state) {
		Role role = child1.getActiontype(state);
		if (role != null){
			return state.getRandomActionOfTypeOtherThan(role);
		} else {
			return null;
		}
	}

}
