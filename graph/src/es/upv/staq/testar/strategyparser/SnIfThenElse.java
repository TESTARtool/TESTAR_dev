package es.upv.staq.testar.strategyparser;

import java.util.ArrayList;

import org.fruit.alayer.Action;

import es.upv.staq.testar.algorithms.StateManager;

public class SnIfThenElse extends StrategyNodeAction {
	StrategyNodeBoolean child1;
	StrategyNodeAction child2;
	StrategyNodeAction child3;
	
	public SnIfThenElse(ArrayList<StrategyNode> children){
		super(children);
		child1 = (StrategyNodeBoolean)children.get(0);
		child2 = (StrategyNodeAction)children.get(1);
		child3 = (StrategyNodeAction)children.get(2);
	}
	@Override
	public Action getAction(StateManager state) {
		if (child1.getValue(state)){
			return child2.getAction(state);
		} else {
			return child3.getAction(state);
		}
	}

}
