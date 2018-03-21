/*****************************************************************************************
 *                                                                                       *
 * COPYRIGHT (2017):                                                                     *
 * Universitat Politecnica de Valencia                                                   *
 * Camino de Vera, s/n                                                                   *
 * 46022 Valencia, Spain                                                                 *
 * www.upv.es                                                                            *
 *                                                                                       * 
 * D I S C L A I M E R:                                                                  *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)     *
 * in the context of the TESTAR development team (www.testar.org):                       *
 * This graph project is distributed FREE of charge under the TESTAR license, as an open *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *
 *                                                                                       * 
 *****************************************************************************************/

package es.upv.staq.testar.algorithms;

import java.util.Random;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import es.upv.staq.testar.graph.IEnvironment;
import es.upv.staq.testar.graph.IGraphAction;
import es.upv.staq.testar.graph.IGraphState;
import es.upv.staq.testar.prolog.JIPrologWrapper;

/**
 * A random walker with periodic state restarts.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class RandomRestartsWalker extends RandomWalker {
	
	public RandomRestartsWalker(Random rnd, int testSequenceLength){
		super(rnd);
		RestartsWalkerUtil.setTestSequenceLength(testSequenceLength);
	}

	@Override
	public Action selectAction(IEnvironment env, State state, Set<Action> actions, JIPrologWrapper jipWrapper) {
		if (RestartsWalkerUtil.notifyActionSelection(this,env, state))
			return super.selectProportional(env, state, actions);
		else
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
