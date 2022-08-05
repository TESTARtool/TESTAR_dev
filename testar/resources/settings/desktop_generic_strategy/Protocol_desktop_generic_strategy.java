import org.testar.DerivedActions;
import org.testar.SutVisualization;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.protocols.DesktopProtocol;
import parsing.Parse;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/***************************************************************************************************
 *
 * Copyright (c) 2020 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 Open Universiteit - www.ou.nl
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

public class Protocol_desktop_generic_strategy extends DesktopProtocol
{
	private Parse parseStrategy;
	private Map<String, Integer> actionsExecuted = new HashMap<String, Integer>();
	
	@Override
	protected void initialize(Settings settings)
	{
		super.initialize(settings);
		parseStrategy = new Parse(settings.get(ConfigTags.StrategyFile));
	}
	
	@Override
	protected State getState(SUT system) throws StateBuildException
	{
		State state = super.getState(system);
		if(latestState == null)
			state.set(Tags.StateChanged, true);
		else
		{
			String previousStateID = latestState.get(Tags.AbstractIDCustom);
			boolean stateChanged = ! previousStateID.equals(state.get(Tags.AbstractIDCustom));
			state.set(Tags.StateChanged, stateChanged);
		}
		
		return state;
	}
	@Override
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException
	{
		Set<Action> actions = super.deriveActions(system,state);
		
		DerivedActions derived = deriveClickTypeScrollActionsFromTopLevelWidgets(actions, state);
		
		if(derived.getAvailableActions().isEmpty())
			derived = deriveClickTypeScrollActionsFromAllWidgets(actions, state);
		
		Set<Action> filteredActions = derived.getFilteredActions();
		actions = derived.getAvailableActions();
		
		if(visualizationOn || mode() == Modes.Spy) SutVisualization.visualizeFilteredActions(cv, state, filteredActions);
		
		return actions;
	}
	
	@Override
	protected Action selectAction(State state, Set<Action> actions)
	{
		Action selectedAction = parseStrategy.selectAction(state, actions, actionsExecuted);
		
//		System.out.println(selectedAction.toString());
//		System.out.println("Action role" + selectedAction.get(Tags.Role, null).toString());
		
		state.set(Tags.PreviousAction, selectedAction);
		
		String actionID = selectedAction.get(Tags.AbstractIDCustom);
		Integer timesUsed = actionsExecuted.containsKey(actionID) ? actionsExecuted.get(actionID) : 0; //get the use count for the action
		actionsExecuted.put(actionID, timesUsed + 1); //increase by one
		
		return selectedAction;
	}
}
