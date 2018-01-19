package es.upv.staq.testar.strategyparser;

import java.util.ArrayList;

import org.fruit.alayer.Action;
import org.fruit.alayer.Role;
import org.fruit.alayer.Tags;

import es.upv.staq.testar.algorithms.StateManager;

public class SnTypeOfActionOf extends StrategyNodeActiontype {
	StrategyNodeAction child1;

	public SnTypeOfActionOf(ArrayList<StrategyNode> children) {
		super(children);
		child1 = (StrategyNodeAction)children.get(0);
	}

	@Override
	public Role getActiontype(StateManager state) {
		Action action = child1.getAction(state);
		if (action != null){
			return action.get(Tags.Role);
		} else {
			return null;
		}
	}

}
