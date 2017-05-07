/*****************************************************************************************
 *                                                                                       *
 * COPYRIGHT (2015):                                                                     *
 * Universitat Politecnica de Valencia                                                   *
 * Camino de Vera, s/n                                                                   *
 * 46022 Valencia, Spain                                                                 *
 * www.upv.es                                                                            *
 *                                                                                       * 
 * D I S C L A I M E R:                                                                  *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)     *
 * in the context of the TESTAR Proof of Concept project:                                *
 *               "UPV, Programa de Prueba de Concepto 2014, SP20141402"                  *
 * This graph project is distributed FREE of charge under the TESTAR license, as an open *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *                                                                                        * 
 *                                                                                       *
 *****************************************************************************************/

package es.upv.staq.testar.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import es.upv.staq.testar.graph.GraphAction;
import es.upv.staq.testar.graph.GraphState;
import es.upv.staq.testar.graph.Grapher;
import es.upv.staq.testar.graph.IEnvironment;
import es.upv.staq.testar.graph.IGraphAction;
import es.upv.staq.testar.graph.IGraphState;
import es.upv.staq.testar.graph.Movement;
import es.upv.staq.testar.graph.WalkStopper;
import es.upv.staq.testar.graph.reporting.WalkReport;
import es.upv.staq.testar.prolog.JIPrologWrapper;

/**
 * An abstract graph walker.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public abstract class AbstractWalker implements IWalker {

	private JIPrologWrapper jipWrapper = null;
	
	public static final double BASE_REWARD = 1.0d; // default
	
	private static final int MOVEMENT_FEEDBACK_STEP = 10000; // number of mmovements
	
	@Override
	final public void setProlog(JIPrologWrapper jipWrapper){
		this.jipWrapper = jipWrapper;
	}
	
	@Override
	public double getBaseReward(){
		return BASE_REWARD;
	}
	
	@Override
	final public void walk(IEnvironment env, WalkStopper walkStopper){
		IGraphState lastS = null; IGraphAction lastA = null;
		int movementNumber = 0;		
		while(walkStopper.continueWalking()){
			if (jipWrapper != null)
				jipWrapper.setFacts(Grapher.getEnvironment());
			Movement m = env.getMovement();
			if (m != null){
				IGraphState s = m.getVertex();
				IGraphAction a = m.getEdge();
				if (a != null)
					s.actionExecuted(a.getTargetID());
				if (lastS == null)
					env.populateEnvironment(new GraphState(Grapher.GRAPH_NODE_ENTRY), new GraphAction(Grapher.GRAPH_ACTION_START), s);
				else if (lastA != null)
					env.populateEnvironment(lastS, lastA, s);
				lastS = s; lastA = a;
				movementNumber++;
				if (movementNumber % MOVEMENT_FEEDBACK_STEP == 0) System.out.println("Reached Movement: <" + movementNumber + ">"); // useful feedback for graph loadings
			}
		}
		env.finishGraph(walkStopper.walkStatus(), lastS, lastA, walkStopper.walkEndState());
	}
	
	@Override
	public Action selectAction(IEnvironment env, State state, Set<Action> actions, JIPrologWrapper jipWrapper) {
		Grapher.syncMovements(); // synchronize graph movements consumption for up to date rewards and states/actions exploration
		return null;
	}
	
	@Override
	public double getActionReward(IEnvironment env, IGraphAction action){
		if (action == null || !env.actionAtGraph(action))
			return getBaseReward();
		double actionReward = 0.0d;
		int[] actionWCount = env.getWalkedCount(action);
		if (actionWCount[0] == 0) // action count (concrete)
			actionReward = 1.0d;
		else
			actionReward = 1.0d / ( actionWCount[0] * // action count (concrete)
									Math.log(actionWCount[1] + Math.E - 1)); // action type count (abstract)
		Integer tc = env.getTargetState(action).getStateWidgetsExecCount().get(action.getTargetID());
		if (tc != null)
			actionReward /= Math.pow(2, tc);  // prevent too much repeated execution of the same action (e.g. typing with different texts)
		return actionReward;
	}
	
	@Override
	public double getStateReward(IEnvironment env, IGraphState state){
		if (state == null || !env.stateAtGraph(state))
			return getBaseReward();
		double stateReward = state.getUnexploredActionsSize();
		for (String aid : env.getOutgoingActions(state))
			stateReward +=  env.getTargetState(env.getAction(aid)).getUnexploredActionsSize();			
		return stateReward;
	}
	
	@Override
	final public Action selectProportional(IEnvironment env, State state, Set<Action> actions){
		// set each action reward
		Map<Action,Double> rewards = new HashMap<Action,Double>();
		IGraphAction ga;
		double sum = .0, rew;
		for (Action a : actions){
			ga = env.get(a);
			rew = this.getActionReward(env, ga) + this.getStateReward(env, ga == null ? null : env.getTargetState(ga));			
			rewards.put(a, rew);
			sum += rew;
		}
		// select proportional
		double r = sum * (new Random(System.currentTimeMillis()).nextDouble());
		double frac = 0.0, q;
		Action selection = null;
		for (Action a : actions){
			q = rewards.get(a).doubleValue();
			if((frac / sum <= r) && ((frac + q) / sum >= r)){
				selection = a;
				break;
			}
			frac += q;
		}
		if (selection != null)
			return selection;
		else
			return selectMax(rewards); // proportional selection failed
	}
	
	private Action selectMax(Map<Action,Double> rewards){
		double maxDesirability = 0.0;
		double q;
		Action selection = null;
		for (Action a : rewards.keySet()){
			q = rewards.get(a).doubleValue();
			if(q > maxDesirability){
				maxDesirability = q;
				selection = a;
			}
		}
		if (selection != null)
			return selection;
		else
			return new ArrayList<Action>(rewards.keySet()).get((new Random(System.currentTimeMillis())).nextInt(rewards.keySet().size())); // do it random
	}	
	
	@Override
	public WalkReport getReport() {
		// TODO Auto-generated method stub
		return null;
	}	
	
}
