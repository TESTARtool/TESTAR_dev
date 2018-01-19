package es.upv.staq.testar.algorithms;

import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import es.upv.staq.testar.graph.IEnvironment;
import es.upv.staq.testar.algorithms.StateManager;
import es.upv.staq.testar.prolog.JIPrologWrapper;
import es.upv.staq.testar.strategyparser.StrategyNode;
import es.upv.staq.testar.strategyparser.StrategyNodeAction;

public class StrategyWalker extends AbstractWalker {
	StrategyNodeAction strategyTree;
	StateManager statemgr = new StateManager();
	
	public StrategyWalker(StrategyNode main){
		strategyTree = (StrategyNodeAction)main;	
	}
	
	@Override
	public Action selectAction(IEnvironment env, State state, Set<Action> actions, JIPrologWrapper jipWrapper){
		statemgr.setState(env, state, actions);
		Action result = strategyTree.getAction(statemgr);
		if (result == null){
			System.out.println("Found no action with the strategy, returning a random action");
			env.incRandomAction();
			result = statemgr.getRandomAction();
		}
		statemgr.setPreviousAction(result);
		statemgr.setPreviousState(state);
		System.out.println("The selected action is of type "+result.get(Tags.Role));
		return result;
	}
	
	public void print() {
		strategyTree.print(0);
	}
	
	public StateManager getStateManager(){
		return statemgr;
	}
}
