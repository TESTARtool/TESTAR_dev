/**********************************************************************************************
 *                                                                                            *
 * COPYRIGHT (2016):                                                                          *
 * Universitat Politecnica de Valencia                                                        *
 * Camino de Vera, s/n                                                                        *
 * 46022 Valencia, Spain                                                                      *
 * www.upv.es                                                                                 *
 *                                                                                            * 
 * D I S C L A I M E R:                                                                       *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)          *
 * in the context of the STaQ (Software Testing and Quality) research group: staq.dsic.upv.es *
 * This software is distributed FREE of charge under the TESTAR license, as an open           *
 * source project under the BSD3 license (http://opensource.org/licenses/BSD-3-Clause)        *                                                                                        * 
 *                                                                                            *
 **********************************************************************************************/

package es.upv.staq.testar.algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import es.upv.staq.testar.graph.IEnvironment;
import es.upv.staq.testar.graph.IGraphAction;
import es.upv.staq.testar.graph.IGraphState;
import es.upv.staq.testar.prolog.JIPrologWrapper;

/**
 * A walker to maximise UI coverage.
 * Approach: taboo actions + state restarts.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class MaxCoverageWalker extends AbstractWalker {	
	
	private Random rnd;
	
	public static final int MAX_REPEATED_WIDGET_ACTIONS = 100,
							MIN_WALKER_STEPS = 500; // force this number of actions (e.g. random) even if everything is fully explored
	
	public MaxCoverageWalker(Random rnd, int testSequenceLength){
		this.rnd = rnd;
		RestartsWalkerUtil.setTestSequenceLength(testSequenceLength);
	}

	@Override
	public Action selectAction(IEnvironment env, State state, Set<Action> actions, JIPrologWrapper jipWrapper) {
		super.selectAction(env, state, actions, jipWrapper);
		RestartsWalkerUtil.notifyActionSelection(this,env, state);
		
		// seek for unexplored state actions
		Set<Action> unexploredActions = new HashSet<Action>();
		boolean flag;
		String targetWidgetID;
		Integer cInt;
		IGraphAction ga;
		for (Action a : actions){
			ga = env.get(a);
			flag = false;
			if (!env.actionAtGraph(ga)){
				targetWidgetID = ga.getTargetWidgetID();
				if (targetWidgetID == null)
					flag = true;
				else {
					cInt = env.get(state).getStateWidgetsExecCount().get(targetWidgetID);
					if (cInt == null)
						flag = true;
					else if (cInt.intValue() < MAX_REPEATED_WIDGET_ACTIONS)
						flag = true;
				}
			}
			if (flag)
				unexploredActions.add(a);				
		}
		if (!unexploredActions.isEmpty())
			return new ArrayList<Action>(unexploredActions).get(rnd.nextInt(unexploredActions.size()));

		// check target states
		boolean allStatesUnexplored = true;
		for(Action a : actions){
			ga = env.get(a);
			if (env.actionAtGraph(ga)){
				for (IGraphState gs : env.getTargetStates(ga)){
					if (gs.getUnexploredActionsSize() == 0){
						allStatesUnexplored = false;
						break;
					}
				}
				if (allStatesUnexplored){
					System.out.println("[MaxCoverageWalker] Moving to unexplored state from >" + state.get(Tags.ConcreteID) + "> through <" + ga.getConcreteID() + ">");
					return a;
				}
			}
		}

		System.out.println("[MaxCoverageWalker] Completely explored state: " + state.get(Tags.ConcreteID));
		// jump to unexplored state
		if (RestartsWalkerUtil.forceStateRestart(this,env, state)){
			System.out.println("[MaxCoverageWalker] Trying to discover new UI states by state-restart from: " + state.get(Tags.ConcreteID));
			return super.selectProportional(env, state, actions);
		}
		
		System.out.println("[MaxCoverageWalker] No unexplored UI reachable from: " + state.get(Tags.ConcreteID));
		if (RestartsWalkerUtil.getTestSquenceIdx() < MIN_WALKER_STEPS){
			System.out.println("[MaxCoverageWalker] Test steps <" + RestartsWalkerUtil.getTestSquenceIdx() + "> lower than MIN_WALKER_STEPS < " + MIN_WALKER_STEPS + ">: doing RANDOM");
			return new ArrayList<Action>(actions).get(rnd.nextInt(actions.size())); // force random
		} else {
			System.out.println("[MaxCoverageWalker] Forcing test sequence end");
			return null; // force test sequence end
		}
	}

	@Override
	public double calculateRewardForAction(IEnvironment env, IGraphAction action) {
		RestartsWalkerUtil.notifyRewardCalculation(env, action);
		return super.calculateRewardForAction(env, action);
	}
	
	@Override
	public double calculateRewardForState(IEnvironment env, IGraphState targetState){
		double r = RestartsWalkerUtil.getTargetReward(env, targetState);
		if (r != Double.MIN_VALUE)
			return r;
		else
			return super.calculateRewardForState(env, targetState);
	}		
		
}
