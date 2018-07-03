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
	
	public static final int MAX_REPEATED_WIDGET_ACTIONS = 100;
	public static final int MIN_WALKER_STEPS = 500; // force this number of actions (e.g. random) even if everything is fully explored
	
	public MaxCoverageWalker(Random rnd, int testSequenceLength) {
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
		for (Action a : actions) {
			ga = env.get(a);
			flag = false;
			if (!env.actionAtGraph(ga)) {
				targetWidgetID = ga.getTargetWidgetID();
				if (targetWidgetID == null) {
					flag = true;
				} else {
					cInt = env.get(state).getStateWidgetsExecCount().get(targetWidgetID);
					if (cInt == null) {
						flag = true;
					} else if (cInt.intValue() < MAX_REPEATED_WIDGET_ACTIONS) {
						flag = true;
					}
				}
			}
			if (flag) {
				unexploredActions.add(a);	
			}
		}
		if (!unexploredActions.isEmpty()) {
			return new ArrayList<Action>(unexploredActions).get(rnd.nextInt(unexploredActions.size()));
		}
		// check target states
		boolean allStatesUnexplored = true;
		for(Action a : actions) {
			ga = env.get(a);
			if (env.actionAtGraph(ga)) {
				for (IGraphState gs : env.getTargetStates(ga)) {
					if (gs.getUnexploredActionsSize() == 0) {
						allStatesUnexplored = false;
						break;
					}
				}
				if (allStatesUnexplored) {
					System.out.println("[" + getClass().getSimpleName() + "] [MaxCoverageWalker] Moving to unexplored state from >" + state.get(Tags.ConcreteID) + "> through <" + ga.getConcreteID() + ">");
					return a;
				}
			}
		}

		System.out.println("[" + getClass().getSimpleName() + "] [MaxCoverageWalker] Completely explored state: " + state.get(Tags.ConcreteID));
		// jump to unexplored state
		if (RestartsWalkerUtil.forceStateRestart(this,env, state)) {
			System.out.println("[" + getClass().getSimpleName() + "] [MaxCoverageWalker] Trying to discover new UI states by state-restart from: " + state.get(Tags.ConcreteID));
			return super.selectProportional(env, state, actions);
		}
		
		System.out.println("[" + getClass().getSimpleName() + "] [MaxCoverageWalker] No unexplored UI reachable from: " + state.get(Tags.ConcreteID));
		if (RestartsWalkerUtil.getTestSquenceIdx() < MIN_WALKER_STEPS) {
			System.out.println("[" + getClass().getSimpleName() + "] [MaxCoverageWalker] Test steps <" + RestartsWalkerUtil.getTestSquenceIdx() + "> lower than MIN_WALKER_STEPS < " + MIN_WALKER_STEPS + ">: doing RANDOM");
			return new ArrayList<Action>(actions).get(rnd.nextInt(actions.size())); // force random
		} else {
			System.out.println("[" + getClass().getSimpleName() + "] [MaxCoverageWalker] Forcing test sequence end");
			return null; // force test sequence end
		}
	}

	@Override
	public double calculateRewardForAction(IEnvironment env, IGraphAction action) {
		RestartsWalkerUtil.notifyRewardCalculation(env, action);
		return super.calculateRewardForAction(env, action);
	}
	
	@Override
	public double calculateRewardForState(IEnvironment env, IGraphState targetState) {
		double r = RestartsWalkerUtil.getTargetReward(env, targetState);
		if (r != Double.MIN_VALUE) {
			return r;
		} else {
			return super.calculateRewardForState(env, targetState);
		}
	}		
}
