package es.upv.staq.testar.strategyparser;

import java.util.ArrayList;

import org.fruit.alayer.Role;

import es.upv.staq.testar.algorithms.StateManager;

public abstract class StrategyNodeActiontype extends StrategyNode {
	public StrategyNodeActiontype(ArrayList<StrategyNode> children) {
		super(children);
		
	}

	public abstract Role getActiontype(StateManager state);

}
