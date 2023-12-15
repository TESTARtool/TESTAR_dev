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

import org.antlr.v4.runtime.misc.MultiMap;
import org.testar.DerivedActions;
import org.testar.RandomActionSelector;
import org.testar.SutVisualization;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.DefaultProtocol;
import org.testar.settings.Settings;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.plugin.NativeLinker;
import org.testar.plugin.OperatingSystems;
import org.testar.protocols.DesktopProtocol;
import parsing.ParseUtil;
import strategynodes.enums.ActionType;

import java.util.*;

public class Protocol_desktop_generic_strategy extends DesktopProtocol
{
	private ParseUtil            parseUtil;
	private RandomActionSelector selector;
	private boolean              useRandom = false;
	private MultiMap<String, Object>    actionsExecuted      = new MultiMap<>();
	private ArrayList<String> operatingSystems = new ArrayList<>();
	
	@Override
	protected void initialize(Settings settings)
	{
		super.initialize(settings);
		
		useRandom = settings.get(ConfigTags.StrategyFile).isEmpty();
		if (useRandom)
			selector = new RandomActionSelector();
		else
			parseUtil = new ParseUtil(settings.get(ConfigTags.StrategyFile));
		
		for(OperatingSystems OS : NativeLinker.getPLATFORM_OS())
			operatingSystems.add(OS.toString());
	}
	
	@Override
	protected void beginSequence(SUT system, State state)
	{
		super.beginSequence(system, state);
		state.remove(Tags.PreviousAction);
		state.remove(Tags.PreviousActionID);
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
		if(DefaultProtocol.lastExecutedAction != null)
		{
			state.set(Tags.PreviousAction, DefaultProtocol.lastExecutedAction);
			state.set(Tags.PreviousActionID, DefaultProtocol.lastExecutedAction.get(Tags.AbstractIDCustom, null));
		}
		
		Action selectedAction = (useRandom) ?
				selector.selectAction(state, actions):
				parseUtil.selectAction(state, actions, actionsExecuted, operatingSystems);

		String actionID = selectedAction.get(Tags.AbstractIDCustom);

		//get the use count for the action
		List<Object> entry = actionsExecuted.getOrDefault(actionID, null);

		int timesUsed = (entry == null) ? 0 : (Integer) entry.get(0); //default to zero if null
		ActionType actionType = (entry == null) ? ActionType.getActionType(selectedAction) : (ActionType) entry.get(1);

		ArrayList<Object> updatedEntry = new ArrayList<>();
		updatedEntry.add(timesUsed + 1); //increase usage by one
		updatedEntry.add(actionType);
		actionsExecuted.replace(actionID, updatedEntry); //replace or create entry
		
		return selectedAction;
	}
}
