package es.upv.staq.testar.strategyparser;

import java.util.ArrayList;

import org.fruit.alayer.Action;

import es.upv.staq.testar.algorithms.StateManager;


public abstract class StrategyNodeAction extends StrategyNode {
	public StrategyNodeAction(ArrayList<StrategyNode> children) {
		super(children);
		
	}

	public abstract Action getAction(StateManager state);
}
