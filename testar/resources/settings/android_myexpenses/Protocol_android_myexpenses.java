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

import org.openqa.selenium.By;
import org.testar.CodingManager;
import org.testar.RandomActionSelector;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.exceptions.*;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.windows.UIARoles;
import org.testar.monkey.alayer.windows.UIATags;
import org.testar.monkey.alayer.android.AndroidAppiumFramework;
import org.testar.monkey.alayer.android.actions.*;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import org.testar.protocols.AndroidProtocol;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Protocol_android_myexpenses extends AndroidProtocol {

	private String previousStateIdentifier = "";

	@Override
	protected SUT startSystem() throws SystemStartException {
		SUT system =  super.startSystem();

		// Move forward the initial states to start the expenses main state
		Util.pause(2);
		AndroidAppiumFramework.getDriver().findElement(By.xpath("//*[@text='" + "Next" + "']")).click();
		Util.pause(2);
		AndroidAppiumFramework.getDriver().findElement(By.xpath("//*[@text='" + "Next" + "']")).click();
		Util.pause(2);
		AndroidAppiumFramework.getDriver().findElement(By.xpath("//*[@text='" + "Get started" + "']")).click();
		Util.pause(2);

		return system;
	}

	@Override
	protected State getState(SUT system) throws StateBuildException {
		State state = super.getState(system);
		return state;
	}

	@Override
	protected void buildStateIdentifiers(State state) {
		super.buildStateIdentifiers(state);

		boolean dynamicSearchDateState = stateContainsSearchForDateMenu(state);

		for(Widget w : state) {
			// Ignore dynamic search for date menu
			if(dynamicSearchDateState) {
				String widgetAbstractId = CodingManager.ID_PREFIX_WIDGET + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM + CodingManager.codify(w, Tags.Role, Tags.Path);
				w.set(Tags.AbstractIDCustom, widgetAbstractId);
			}
			// Ignore dates and hours dynamic titles
			if(checkDatePattern(w.get(Tags.Title, "")) || checkHourPattern(w.get(Tags.Title, ""))) {
				String widgetAbstractId = CodingManager.ID_PREFIX_WIDGET + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM + CodingManager.codify(w, Tags.Role, Tags.Path);
				w.set(Tags.AbstractIDCustom, widgetAbstractId);
			}
		}

		// Rebuild state identifier
		StringBuilder finalStateAbstractIdCustom;
		finalStateAbstractIdCustom = new StringBuilder();
		for (Widget w : state){
			if (!(w instanceof State)) {
				finalStateAbstractIdCustom.append(w.get(Tags.AbstractIDCustom));
			}
		}

		// If the state is a popup menu, the state identifier depends of previous state
		if(state.childCount()>0 && state.child(0).get(AndroidTags.AndroidBounds).y() > 1.0) {
			System.out.println("DEBUG: Detected popup menu previousStateIdentifier: " + previousStateIdentifier);
			finalStateAbstractIdCustom.append(previousStateIdentifier);
		}

		state.set(Tags.AbstractIDCustom, CodingManager.ID_PREFIX_STATE + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM + CodingManager.lowCollisionID(finalStateAbstractIdCustom.toString()));
	}

	private boolean checkDatePattern(String input) {
		// Date pattern: DD/MM/YY
		String datePattern = "\\d{1,2}/\\d{1,2}/\\d{2}";
		return input.matches(datePattern);
	}

	private boolean checkHourPattern(String input) {
		// Hour pattern: HH:MM AM/PM
		String hourPattern = "([0-1]?[0-9]|2[0-3]):[0-5][0-9]\\s(?:AM|PM)";
		return input.matches(hourPattern);
	}

	private boolean stateContainsSearchForDateMenu(State state) {
		for(Widget w : state) {
			if(w.get(AndroidTags.AndroidText, "").equals("Search for date")
					&& w.get(AndroidTags.AndroidResourceId, "").contains("alertTitle")) {
				return true;
			}
		}
		return false;
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
	protected boolean isClickable(Widget w) {
		// Ignore clicking the "Tell a friend" widget
		// It looks ugly but it works and ugly people have the rights to exist
		if(w.childCount() > 0 && 
				w.child(0).childCount() > 0 && 
				w.child(0).child(0).get(Tags.Title, "").toLowerCase().contains("friend")) {
			return false;
		}
		// We ignore dates and hours dynamic titles in the abstraction
		// But also filter interaction with them because they lead to other dynamic states
		if(checkDatePattern(w.get(Tags.Title, "")) || checkHourPattern(w.get(Tags.Title, ""))) {
			return false;
		}
		return super.isClickable(w);
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
		previousStateIdentifier = state.get(Tags.AbstractIDCustom);
		return super.executeAction(system, state, action);
	}

}
