/***************************************************************************************************
 *
 * Copyright (c) 2013, 2014, 2015, 2016, 2017, 2018, 2019 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018, 2019 Open Universiteit - www.ou.nl
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


import java.util.HashSet;
import java.util.Set;

import nl.ou.testar.ActionSelectionUtils;
import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.*;
import org.fruit.monkey.Settings;
import org.testar.protocols.DesktopProtocol;

/**
 * This protocol provides default TESTAR behaviour to test Windows desktop applications.
 *
 * It uses random action selection algorithm.
 */
public class Protocol_desktop_generic_action_selection extends DesktopProtocol {


	private Set<Action> executedActions = new HashSet<Action> ();
	private Set<Action> previousActions;

	/**
	 * Select one of the available actions using an action selection algorithm (for example random action selection)
	 *
	 * super.selectAction(state, actions) updates information to the HTML sequence report
	 *
	 * @param state the SUT's current state
	 * @param actions the set of derived actions
	 * @return  the selected action (non-null!)
	 */
	@Override
	protected Action selectAction(State state, Set<Action> actions){
		System.out.println("*** Sequence "+sequenceCount+", Action "+actionCount()+" ***");
		Set<Action> prioritizedActions = new HashSet<Action> ();
		//checking if it is the first round of actions:
		if(previousActions==null) {
			//all actions are new actions:
			//System.out.println("DEBUG: the first round of actions");
			prioritizedActions = actions;
		}else{
			//if not the first round, get the new actions compared to previous state:
			prioritizedActions = ActionSelectionUtils.getSetOfNewActions(actions, previousActions);
		}
		if(prioritizedActions.size()>0){
			//there are new actions to choose from, checking if they have been already executed:
			prioritizedActions = ActionSelectionUtils.getSetOfNewActions(prioritizedActions, executedActions);
		}
		if(prioritizedActions.size()>0){
			// found new actions that have not been executed before - choose randomly
			//System.out.println("DEBUG: found NEW actions that have not been executed before");
		}else{
			// no new unexecuted actions, checking if any unexecuted actions:
			prioritizedActions = ActionSelectionUtils.getSetOfNewActions(actions, executedActions);
		}
		if(prioritizedActions.size()>0){
			// found actions that have not been executed before - choose randomly
			//System.out.println("DEBUG: found actions that have not been executed before");
		}else{
			// no unexecuted actions, choose randomly on any of the available actions:
			//System.out.println("DEBUG: NO actions that have not been executed before");
			prioritizedActions = actions;
		}
		//saving the current actions for the next round:
		previousActions = actions;
		return(super.selectAction(state, prioritizedActions));
	}

	/**
	 * Execute the selected action.
	 *
	 * super.executeAction(system, state, action) is updating the HTML sequence report with selected action
	 *
	 * @param system the SUT
	 * @param state the SUT's current state
	 * @param action the action to execute
	 * @return whether or not the execution succeeded
	 */
	@Override
	protected boolean executeAction(SUT system, State state, Action action){
		executedActions.add(action);
		System.out.println("executed action: "+action.get(Tags.Desc, "NoCurrentDescAvailable"));
		return super.executeAction(system, state, action);
	}
}
