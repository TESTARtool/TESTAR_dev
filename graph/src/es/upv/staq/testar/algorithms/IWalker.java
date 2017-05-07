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

import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import es.upv.staq.testar.graph.IEnvironment;
import es.upv.staq.testar.graph.IGraphAction;
import es.upv.staq.testar.graph.IGraphState;
import es.upv.staq.testar.graph.WalkStopper;
import es.upv.staq.testar.graph.reporting.WalkReport;
import es.upv.staq.testar.prolog.JIPrologWrapper;

/**
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public interface IWalker {
	
	/**
	 * Sets prolog service.
	 * @param jipWrapper A wrapper to prolog.
	 */
	public void setProlog(JIPrologWrapper jipWrapper);
	
	/**
	 * The base reward to use.
	 * @return The base reward.
	 */
	public double getBaseReward();	
	
	/**
	 * Walking algorithm.
	 * @param env Graph environment.
	 * @param walkStopper A walk stopping criteria.
	 */
	public void walk(IEnvironment env, WalkStopper walkStopper);
	
	/**
	 * Selects an action to be executed from a set of available actions for a SUT state.
	 * @param state SUT state.
	 * @param actions Available actions for SUT state.
	 * @return The selected algorithm action.
	 */
	public Action selectAction(IEnvironment env, State state, Set<Action> actions, JIPrologWrapper jipWrapper);	
	
	/**
	 * Calculates a rewarding score (0.0 .. 1.0; or MAX_REWARD), which determines how interesting is the state' action.
	 * @param env Graph environment.
	 * @param action A graph action..
	 * @return A rewarding score between 0.0 (no interest at all) and 1.0 (maximum interest); or MAX_REWARD.
	 */
	public double getActionReward(IEnvironment env, IGraphAction action);
	
	/**
	 * Calculates a rewarding score (0.0 .. 1.0; or MAX_REWARD), which determines how interesting is the state. 
	 * @param env Graph environment.
	 * @param state A graph state.
	 * @return A rewarding score between 0.0 (no interest at all) and 1.0 (maximum interest);  or MAX_REWARD.
	 */
	public double getStateReward(IEnvironment env, IGraphState state);
	
	/**
	 * Proportional action selection.
	 * @param env Graph environment.
	 * @param state A state.
	 * @param actions State' actions.
	 * @return A proportional selected action.
	 */
	public Action selectProportional(IEnvironment env, State state, Set<Action> actions);
	
	public WalkReport getReport();
	
}
