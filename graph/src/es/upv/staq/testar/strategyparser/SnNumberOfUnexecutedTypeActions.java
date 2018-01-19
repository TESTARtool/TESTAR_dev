package es.upv.staq.testar.strategyparser;

import java.util.ArrayList;

import org.fruit.alayer.actions.ActionRoles;

import es.upv.staq.testar.algorithms.StateManager;

public class SnNumberOfUnexecutedTypeActions extends StrategyNodeNumber {

	public SnNumberOfUnexecutedTypeActions(ArrayList<StrategyNode> children) {
		super(children);
	}

	@Override
	public int getValue(StateManager state) {
		return state.getNumberOfActions(ActionRoles.Type, new String("UNEX"));
	}

}
