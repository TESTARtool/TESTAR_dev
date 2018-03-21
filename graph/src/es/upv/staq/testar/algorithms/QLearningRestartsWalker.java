/**********************************************************************************************
 *                                                                                            *
 * COPYRIGHT (2017):                                                                          *
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

import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import es.upv.staq.testar.graph.IEnvironment;
import es.upv.staq.testar.graph.IGraphAction;
import es.upv.staq.testar.graph.IGraphState;
import es.upv.staq.testar.prolog.JIPrologWrapper;

/**
 * A Q-learning walker extension with periodic state restarts.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class QLearningRestartsWalker extends QLearningWalker {
			
	public QLearningRestartsWalker(double discount, double maxReward, int testSequenceLength){
		super(discount,maxReward);
		RestartsWalkerUtil.setTestSequenceLength(testSequenceLength);
	}

	@Override
	public Action selectAction(IEnvironment env, State state, Set<Action> actions, JIPrologWrapper jipWrapper) {
		RestartsWalkerUtil.notifyActionSelection(this,env, state);
		return super.selectAction(env, state, actions, jipWrapper);
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