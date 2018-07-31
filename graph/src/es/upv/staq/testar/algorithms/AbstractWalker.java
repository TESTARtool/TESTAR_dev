/***************************************************************************************************
*
* Copyright (c) 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* 1. Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
* 2. Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* 3. Neither the name of the copyright holder nor the names of its
* contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
* ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************************************/


package es.upv.staq.testar.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import es.upv.staq.testar.graph.GraphAction;
import es.upv.staq.testar.graph.GraphEdge;
import es.upv.staq.testar.graph.GraphState;
import es.upv.staq.testar.graph.Grapher;
import es.upv.staq.testar.graph.IEnvironment;
import es.upv.staq.testar.graph.IGraphAction;
import es.upv.staq.testar.graph.IGraphState;
import es.upv.staq.testar.graph.Movement;
import es.upv.staq.testar.graph.WalkStopper;
import es.upv.staq.testar.prolog.JIPrologWrapper;

/**
 * An abstract graph walker.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public abstract class AbstractWalker implements IWalker {

	private boolean walkingMemento = false;
	private IGraphState startState = null;
	
	private JIPrologWrapper jipWrapper = null;
	
	public static final double BASE_REWARD = 1.0d; // default
	
	private static final int MOVEMENT_FEEDBACK_STEP = 10000; // number of movements
	
	@Override
	public final void setProlog(JIPrologWrapper jipWrapper) {
		this.jipWrapper = jipWrapper;
	}
	
	@Override
	public double getBaseReward() {
		return BASE_REWARD;
	}
	
	@Override
	public void enablePreviousWalk() {
		this.walkingMemento = true;
	}

	@Override
	public void disablePreviousWalk() {
		this.walkingMemento = false;		
	}

	@Override
	public final void walk(IEnvironment env, WalkStopper walkStopper) {
		IGraphState lastS = null; 
		IGraphAction lastA = null;
		int movementNumber = 0;	
		while (walkStopper.continueWalking()) {
			if (jipWrapper != null) {
				jipWrapper.setFacts(Grapher.getEnvironment());
			}
			Movement m = env.getMovement();
			if (m != null) {
				IGraphState s = m.getVertex();
				IGraphAction a = m.getEdge();	
				if (a != null) {
					s.actionExecuted(a.getTargetWidgetID());
					if (this.walkingMemento) {
						a.knowledge(true);
					} else {
						a.revisited(true);
					}
				}
				if (s != null) {
					if (this.walkingMemento) {
						s.knowledge(true);
					} else {
						s.revisited(true);
					}
					if (lastS != null && lastA != null) {
						env.populateEnvironment(lastS, lastA, s);
					}
					if (startState == null && !this.walkingMemento) {
						startState = new GraphState(Grapher.GRAPH_NODE_ENTRY);
						env.setStartingNode(startState);
						env.populateEnvironment(startState, new GraphAction(Grapher.GRAPH_ACTION_START), s);
					}
				}
				lastS = s; 
				lastA = a;
				if (s != null && a != null) {
					movementNumber++;
				}
			}
		}
		env.finishGraph(walkStopper.walkStatus(), lastS, lastA, walkStopper.walkEndState());
	}
	
	@Override
	public Action selectAction(IEnvironment env, State state, Set<Action> actions, JIPrologWrapper jipWrapper) {
		Grapher.syncMovements(); // synchronize graph movements consumption for up to date rewards and states/actions exploration
		return null;
	}
	
	/**
	 * Calculates a rewarding score (0.0 .. 1.0; or MAX_REWARD), which determines how interesting is a state' action.
	 * @param env environment. 
	 * @param action A graph action..
	 * @return A rewarding score between 0.0 (no interest at all) and 1.0 (maximum interest); or MAX_REWARD.
	 */
	protected double calculateRewardForAction(IEnvironment env, IGraphAction action) {
		if (action == null || !env.actionAtGraph(action)) {
			return getBaseReward();
		}
		double actionReward = 0.0d;
		int[] actionWCount = env.getWalkedCount(action);
		if (actionWCount[0] == 0) { // action count (concrete) 
			actionReward = 1.0d;
		} else {
			actionReward = 1.0d / (actionWCount[0] * // action count (concrete)
									Math.log(actionWCount[1] + Math.E - 1)); // action type count (abstract)
		}
		IGraphState gs = env.getSourceState(action);
		if (gs != null) {
			Integer tc = gs.getStateWidgetsExecCount().get(action.getTargetWidgetID());
			if (tc != null) {
				actionReward /= Math.pow(2, tc.intValue());  // prevent too much repeated execution of the same action (e.g. typing with different texts)			
			}
		}
		return actionReward;
	}
	
	/**
	 * Calculates a rewarding score (0.0 .. 1.0; or MAX_REWARD), which determines how interesting is a state. 
	 * @param env Graph environment.
	 * @param state A graph state.
	 * @return A rewarding score between 0.0 (no interest at all) and 1.0 (maximum interest);  or MAX_REWARD.
	 */
	protected double calculateRewardForState(IEnvironment env, IGraphState state) {
		if (state == null || !env.stateAtGraph(state)) {
			return getBaseReward();
		}
		int unx = state.getUnexploredActionsSize();
		if (unx == 0) {
			return 0.0;
		} else {
			return (1.0 - 1.0 / (unx + 0.1));
		}
	}
	
	@Override
	public double getStateReward(IEnvironment env, IGraphState state) {
		if (state == null || !env.stateAtGraph(state)) {
			return getBaseReward();
		}
		double stateReward = state.getUnexploredActionsSize();
		double actionReward;
		if (stateReward == 0) {
			return 0.0;
		}
		IGraphState[] targetStates;
		for (GraphEdge edge : env.getOutgoingActions(state)) {
			actionReward = .0;
			targetStates = env.getTargetStates(env.getAction(edge.getActionID()));
			for (IGraphState gs : targetStates) {
				if (gs.getUnexploredActionsSize() == 0) { // state fully explored
					actionReward = targetStates.length; // make the action reward lower (but not 0) as it can lead to the fully explored state
					break;
				}
				actionReward += gs.getUnexploredActionsSize();
			}
			stateReward += actionReward;
		}
		return stateReward;
	}
	
	@Override
	public final Action selectProportional(IEnvironment env, State state, Set<Action> actions) {
		// set each action reward
		Map<Action,Double> rewards = new HashMap<Action,Double>();
		IGraphAction ga;
		IGraphState[] targetStates;
		double sum = .0; 
		double rew;
		double trew;
		for (Action a : actions) {
			ga = env.get(a);			
			trew = .0;
			targetStates = env.getTargetStates(ga);
			if (targetStates != null) {
				for (IGraphState gs : targetStates) {
					trew += this.calculateRewardForState(env, gs);
				}
			}
			rew = this.calculateRewardForAction(env, ga) + trew;			
			rewards.put(a, rew);
			sum += rew;
		}
		// select proportional
		double r = sum * (new Random(System.currentTimeMillis()).nextDouble());
		double frac = 0.0;
		double q;
		Action selection = null;
		for (Action a : actions) {
			q = rewards.get(a).doubleValue();
			if ((frac / sum <= r) && ((frac + q) / sum >= r)) {
				selection = a;
				break;
			}
			frac += q;
		}
		if (selection != null) {
			return selection;
		} else {
			return selectMax(rewards); // proportional selection failed
		}
	}
	
	private Action selectMax(Map<Action,Double> rewards) {
		double maxDesirability = 0.0;
		double q;
		Action selection = null;
		for (Action a : rewards.keySet()) {
			q = rewards.get(a).doubleValue();
			if (q > maxDesirability) {
				maxDesirability = q;
				selection = a;
			}
		}
		if (selection != null) {
			return selection;
		} else {
			// do it random
			return new ArrayList<Action>(rewards.keySet()).get((new Random(System.currentTimeMillis())).nextInt(rewards.keySet().size())); 
		}
	}		
}
