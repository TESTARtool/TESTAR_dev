/*****************************************************************************************
 *                                                                                       *
 * COPYRIGHT (2016):                                                                     *
 * Universitat Politecnica de Valencia                                                   *
 * Camino de Vera, s/n                                                                   *
 * 46022 Valencia, Spain                                                                 *
 * www.upv.es                                                                            *
 *                                                                                       * 
 * D I S C L A I M E R:                                                                  *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)     *
 * in the context of the TESTAR Proof of Concept project:                                *
 *               "UPV, Programa de Prueba de Concepto 2014, SP20141402"                  *
 * and the STaQ (Software Testing and Quality) research group: staq.dsic.upv.es          *
 * This graph project is distributed FREE of charge under the TESTAR license, as an open *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *
 *                                                                                       *
 *****************************************************************************************/

package es.upv.staq.testar.algorithms;

import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import es.upv.staq.testar.graph.IEnvironment;
import es.upv.staq.testar.graph.IGraphAction;
import es.upv.staq.testar.graph.IGraphState;
import es.upv.staq.testar.prolog.JIPrologWrapper;

/**
 * Q-learning walker.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class QLearningWalker extends AbstractWalker { // Q = Reward
	
	protected double maxReward;
	protected double discount;
		
	public QLearningWalker(double discount, double maxReward){
		this.maxReward = maxReward;
		this.discount = discount;
	}
	
	@Override
	public double getBaseReward(){
		return this.maxReward;
	}	

	@Override
	public Action selectAction(IEnvironment env, State state, Set<Action> actions, JIPrologWrapper jipWrapper) {
		super.selectAction(env, state, actions, jipWrapper);
		return super.selectProportional(env, state, actions);
	}
	
	@Override
	public double calculateRewardForState(IEnvironment env, IGraphState state){
		double r = super.calculateRewardForState(env, state);
		if (r == getBaseReward())
			return r;
		else
			return r / Math.log(state.getCount() + Math.E - 1);
	}

	@Override
	protected double calculateRewardForAction(IEnvironment env, IGraphAction action){
		double r = super.calculateRewardForAction(env, action);
		return discount * r;
	}
		
}
