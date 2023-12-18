/***************************************************************************************************
 *
 * Copyright (c) 2023 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2023 Open Universiteit - www.ou.nl
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

import org.testar.RandomActionSelector;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.exceptions.*;
import org.testar.monkey.alayer.android.actions.*;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import org.testar.protocols.AndroidProtocol;

import java.util.Set;

public class Protocol_android_myexpenses extends AndroidProtocol {

	@Override
	protected State getState(SUT system) throws StateBuildException {
		State state = super.getState(system);
		return state;
	}

	@Override
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException{
		//The super method returns a ONLY actions for killing unwanted processes if needed, or bringing the SUT to
		//the foreground. You should add all other actions here yourself.
		// These "special" actions are prioritized over the normal GUI actions in selectAction() / preSelectAction().
		Set<Action> actions = super.deriveActions(system,state);

		// create an action compiler, which helps us create actions
		// such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();

		// iterate through all widgets
		for (Widget widget : state) {
			// left clicks, but ignore links outside domain
			if (isClickable(widget)) {
				actions.add(
						new AndroidActionClick(state, widget,
								widget.get(AndroidTags.AndroidText,""),
								widget.get(AndroidTags.AndroidAccessibilityId,""),
								widget.get(AndroidTags.AndroidClassName, ""))
						);
			}

		}

		return actions;
	}

	@Override
	protected Action selectAction(State state, Set<Action> actions){
		Action retAction = stateModelManager.getAbstractActionToExecute(actions);
		if(retAction==null) {
			System.out.println("State model based action selection did not find an action. Using random action selection.");
			// if state model fails, use random (default would call preSelectAction() again, causing double actions HTML report):
			retAction = RandomActionSelector.selectRandomAction(actions);
		}
		return retAction;
	}

	@Override
	protected boolean executeAction(SUT system, State state, Action action) {
		return super.executeAction(system, state, action);
	}

}
