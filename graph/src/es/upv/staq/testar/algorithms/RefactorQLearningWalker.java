/***************************************************************************************************
*
* Copyright (c) 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import es.upv.staq.testar.graph.GraphAction;
import es.upv.staq.testar.graph.GraphState;
import es.upv.staq.testar.graph.Grapher;
import es.upv.staq.testar.graph.IEnvironment;
import es.upv.staq.testar.graph.IGraphAction;
import es.upv.staq.testar.graph.IGraphState;
import es.upv.staq.testar.graph.Movement;
import es.upv.staq.testar.graph.WalkStopper;
import es.upv.staq.testar.prolog.JIPrologWrapper;

public class RefactorQLearningWalker implements IWalker{

	protected double maxReward;
	protected double discount;
	protected double learningRead;
	protected boolean egreedy;

	public double BASE_REWARD = 1.0d;

	private JIPrologWrapper jipWrapper = null;
	private boolean walkingMemento = false;
	private IGraphState startState = null;
	
	private Map<String,Double> qValues = new HashMap<String,Double>();
	
	public void putQvalue(Action a, Double d) {
		String id = a.get(Tags.ConcreteID);
		
		qValues.put(id, d);	
	}
	public Map<String,Double> getQvalues(){
		return this.qValues;
	}

	public RefactorQLearningWalker(double discount, double maxReward, double learningRead, boolean egreedy){
		this.maxReward = maxReward;
		this.discount = discount;
		this.learningRead = learningRead;
		this.egreedy = egreedy;
	}

	public double getMaxReward() {
		return this.maxReward;
	}

	public double getBaseReward(){
		return this.BASE_REWARD;
	}
	
	public double getLearningRead() {
		return this.learningRead;
	}
	
	public boolean getEgreedy() {
		return this.egreedy;
	}

	@Override
	final public void setProlog(JIPrologWrapper jipWrapper) {
		this.jipWrapper = jipWrapper;

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
	public void walk(IEnvironment env, WalkStopper walkStopper) {
		IGraphState lastS = null; IGraphAction lastA = null;
		int movementNumber = 0;	
		while(walkStopper.continueWalking()){
			if (jipWrapper != null)
				jipWrapper.setFacts(Grapher.getEnvironment());
			Movement m = env.getMovement();
			if (m != null){
				IGraphState s = m.getVertex();
				IGraphAction a = m.getEdge();	
				if (a != null){
					s.actionExecuted(a.getTargetWidgetID());
					if (this.walkingMemento)
						a.knowledge(true);
					else
						a.revisited(true);
				}
				if (s!= null){
					if (this.walkingMemento)
						s.knowledge(true);
					else
						s.revisited(true);
					if (lastS != null && lastA != null)
						env.populateEnvironment(lastS, lastA, s);
					if (startState == null && !this.walkingMemento){
						startState = new GraphState(Grapher.GRAPH_NODE_ENTRY);
						env.setStartingNode(startState);
						env.populateEnvironment(startState, new GraphAction(Grapher.GRAPH_ACTION_START), s);
					}
				}
				lastS = s; lastA = a;
				if (s != null && a != null)
					movementNumber++;
			}
		}
		env.finishGraph(walkStopper.walkStatus(), lastS, lastA, walkStopper.walkEndState());

	}

	@Override
	public Action selectAction(IEnvironment env, State state, Set<Action> actions, JIPrologWrapper jipWrapper) {
		Grapher.syncMovements(); // synchronize graph movements consumption for up to date rewards and states/actions exploration
		Action aprueba = selectProportional(env, state, actions);
		
		return aprueba;// selectProportional(env, state, actions);
	}


	// NOT used ?¿
	@Override
	public double getStateReward(IEnvironment env, IGraphState state) {
		return 0.0;
	}
	/**
	 * Calculates a rewarding score (0.0 .. 1.0; or MAX_REWARD), which determines how interesting is a state' action.
	 * @param Graph environment. 
	 * @param action A graph action..
	 * @return A rewarding score between 0.0 (no interest at all) and 1.0 (maximum interest); or MAX_REWARD.
	 */
	protected double calculateRewardForAction(IEnvironment env, IGraphAction action){
		if (action == null || !env.actionAtGraph(action)) {
			return getMaxReward();} // MaxReward if we never executed this action

		double actionReward = 0.0d;
		int[] actionWCount = env.getWalkedCount(action);

		if (actionWCount[0] == 0) // action count (concrete), nº times we walk to state' with this action
			actionReward = getMaxReward(); // ¿same as not executed? ¿1.0 or max Reward?
		else
			actionReward = 1.0d / (actionWCount[0] + 1); // 1.0 / nº of times we have  executed this action

		return actionReward;
	}

	/**
	 * Calculates a rewarding score (0.0 .. 1.0; or MAX_REWARD), which determines how interesting is a state. 
	 * @param env Graph environment.
	 * @param state A graph state.
	 * @return A rewarding score between 0.0 (no interest at all) and 1.0 (maximum interest);  or MAX_REWARD.
	 */
	protected double calculateRewardForState(IEnvironment env, IGraphState state, Set<Action> availableActions){
		if (state == null || !env.stateAtGraph(state)) {
			return getMaxReward();}   //We never walk to state'

		double actionQ = 0.0;
		double q = 0.0;
		
		//Unexplored size actions' from state'
		int unx = state.getUnexploredActionsSize();
		
		//If exist minimum one action at state' not executed, q value it's minimum maxReward. 
		if(unx>0) q = maxReward;
		
		//String with all unexplored AbstractID actions' from state'
		//String unxActions = state.getUnexploredActionsString();
		
		Set<Action> stateActions = new HashSet<Action>();
		
		//We check the available Actions of graph enviroment of state'
		for(Action a: availableActions) {
			//String id = a.get(Tags.ConcreteID);
			//check if its an explored action' of state'
			Set<String> idstates = env.get(a).getTargetStateIDs();
			if(idstates.contains(state.getConcreteID())) {
				stateActions.add(a);
			}				
		}
		
		//Now we check the Actions q-values of state'
		Map <String, Double> qVrew = getQvalues();
		
		if(stateActions != null) {
			for (Action a : stateActions) {
				if(qVrew.containsKey(a.get(Tags.ConcreteID))){
					actionQ = qVrew.get(a.get(Tags.ConcreteID));
				}
				if(actionQ > q)
					q = actionQ;
			}
		}
	
		return q;
	}


	@Override
	public Action selectProportional(IEnvironment env, State state, Set<Action> actions) {

		IGraphAction ga;
		IGraphState[] targetStates;
		double sum = .0, rew, trew;
		for (Action a : actions){
			ga = env.get(a);
			trew = .0;
			targetStates = env.getTargetStates(ga);
			if (targetStates != null){
				for (IGraphState gs : targetStates) { // ¿Can we have more than 1 target state from 1 action?
					trew += this.calculateRewardForState(env, gs, actions);};
					trew = trew * discount; // gamma * max a V(s',a)
			}
			rew = getLearningRead()*(this.calculateRewardForAction(env, ga) + trew);

			putQvalue(a, rew); // We add or update qValue for action
			
			sum += rew;
		}
		
		//Update actions rewards in this iteration with qValue stored
		Map <Action, Double> rewards = new HashMap<Action,Double>();
		Map <String, Double> qVrew = getQvalues();
		
		for(Action a : actions) {
			if(qVrew.containsKey(a.get(Tags.ConcreteID))){
				rewards.put(a, qVrew.get(a.get(Tags.ConcreteID)));
			}
			else {
				rewards.put(a, maxReward);
			}
		}
		
		// select proportional
		// e-greedy
		if(getEgreedy()) {
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
		else { //greedy
			return selectMax(rewards);
		}

	}
	//greedy
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

}
