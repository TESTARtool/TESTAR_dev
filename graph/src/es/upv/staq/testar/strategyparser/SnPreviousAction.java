package es.upv.staq.testar.strategyparser;

import java.util.ArrayList;

import org.fruit.alayer.Action;

import es.upv.staq.testar.algorithms.StateManager;

public class SnPreviousAction extends StrategyNodeAction {

	public SnPreviousAction(ArrayList<StrategyNode> children) {
		super(children);
	}

	@Override
	public Action getAction(StateManager state) {
		return state.previousAction();
	}

}
