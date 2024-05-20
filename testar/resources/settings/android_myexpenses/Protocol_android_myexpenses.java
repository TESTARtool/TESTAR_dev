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
import org.testar.monkey.alayer.actions.NOP;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.exceptions.*;
import org.testar.monkey.alayer.android.AndroidAppiumFramework;
import org.testar.monkey.alayer.android.actions.*;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import org.testar.protocols.AndroidProtocol;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Protocol_android_myexpenses extends AndroidProtocol {

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
		AndroidAppiumFramework.getDriver().findElement(By.xpath("//*[@text='" + "Manage accounts" + "']")).click();
		Util.pause(2);

		return system;
	}

	@Override
	protected State getState(SUT system) throws StateBuildException {
		State state = super.getState(system);

		// If state contains snackbar popup text, wait to avoid a high number of combinatorial states
		if(stateContainsSnackbarText(state)) {
			Util.pause(10);
			state = super.getState(system);
		}

		return state;
	}

	private boolean stateContainsSnackbarText(State state) {
		for(Widget w : state) {
			if(w.get(AndroidTags.AndroidResourceId, "").contains("snackbar_text")) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void buildStateIdentifiers(State state) {
		super.buildStateIdentifiers(state);

		boolean dynamicSearchDateState = stateContainsSearchForDateMenu(state);
		boolean statesWithCheckOptions = stateContainsMenuWithTitle(state, List.of("Sort by", "Grouping"));

		for(Widget w : state) {
			// Ignore dynamic search for date menu
			if(dynamicSearchDateState) {
				String widgetAbstractId = CodingManager.ID_PREFIX_WIDGET + CodingManager.ID_PREFIX_ABSTRACT + CodingManager.codify(w, Tags.Role, Tags.Path);
				w.set(Tags.AbstractID, widgetAbstractId);
			}
			// Ignore dynamic calculator content
			if(isSonOfCalculator(w)) {
				String widgetAbstractId = CodingManager.ID_PREFIX_WIDGET + CodingManager.ID_PREFIX_ABSTRACT + CodingManager.codify(w, Tags.Role, Tags.Path);
				w.set(Tags.AbstractID, widgetAbstractId);
			}
			// Ignore dynamic spinner text content
			if(w.get(AndroidTags.AndroidResourceId, "").contains("id/text1") && isSonOfSpinner(w)) {
				String widgetAbstractId = CodingManager.ID_PREFIX_WIDGET + CodingManager.ID_PREFIX_ABSTRACT + CodingManager.codify(w, Tags.Role, Tags.Path);
				w.set(Tags.AbstractID, widgetAbstractId);
			}
			// Use checked property if check widget is son of sort by menu to avoid non-determinism
			if(w.get(AndroidTags.AndroidClassName, "").contains("CheckedTextView") && statesWithCheckOptions) {
				String widgetAbstractId = CodingManager.ID_PREFIX_WIDGET + CodingManager.ID_PREFIX_ABSTRACT + CodingManager.codify(w, Tags.Role, Tags.Path, Tags.Title, AndroidTags.AndroidChecked);
				w.set(Tags.AbstractID, widgetAbstractId);
			}
			// Ignore dates and hours dynamic titles
			if(checkDatePattern(w.get(Tags.Title, "")) || checkHourPattern(w.get(Tags.Title, ""))) {
				String widgetAbstractId = CodingManager.ID_PREFIX_WIDGET + CodingManager.ID_PREFIX_ABSTRACT + CodingManager.codify(w, Tags.Role, Tags.Path);
				w.set(Tags.AbstractID, widgetAbstractId);
			}
		}

		// Rebuild state identifier
		StringBuilder finalStateAbstractIdCustom;
		finalStateAbstractIdCustom = new StringBuilder();
		for (Widget w : state){
			if (!(w instanceof State)) {
				finalStateAbstractIdCustom.append(w.get(Tags.AbstractID));
			}
		}

		state.set(Tags.AbstractID, CodingManager.ID_PREFIX_STATE + CodingManager.ID_PREFIX_ABSTRACT + CodingManager.lowCollisionID(finalStateAbstractIdCustom.toString()));
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

	private boolean stateContainsMenuWithTitle(State state, List<String> menuTitles) {
		for (Widget w : state) {
			if(menuTitles.contains(w.get(AndroidTags.AndroidText, "nothing"))
					&& w.get(AndroidTags.AndroidResourceId, "").contains("alertTitle")) {
				return true;
			}
		}

		return false;
	}

	private boolean isSonOfCalculator(Widget widget) {
		if(widget.parent() == null) return false;
		else if (widget.parent().get(AndroidTags.AndroidResourceId, "").contains("id/Calculator")) return true;
		else return isSonOfCalculator(widget.parent());
	}

	private boolean isSonOfSpinner(Widget widget) {
		if(widget.parent() == null) return false;
		else if (widget.parent().get(AndroidTags.AndroidClassName, "").contains("widget.Spinner")) return true;
		else return isSonOfSpinner(widget.parent());
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
				actions.add(new AndroidActionClick(state, widget));
			}

		}

		return actions;
	}

	@Override
	protected boolean isClickable(Widget w) {
		// Ignore clicking the "Tell a friend" widget
		// This checking can maybe be improved using other android properties
		if(w.childCount() > 0 && 
				w.child(0).childCount() > 0 && 
				w.child(0).child(0).get(Tags.Title, "").toLowerCase().contains("friend")) {
			w.set(AndroidTags.AndroidClickable, false);
			return false;
		}
		// We ignore dates and hours dynamic titles in the abstraction
		// But also filter interaction with them because they lead to other dynamic states
		if(checkDatePattern(w.get(Tags.Title, "")) || checkHourPattern(w.get(Tags.Title, ""))) {
			w.set(AndroidTags.AndroidClickable, false);
			return false;
		}
		// Switch actions increase the combination and state space, we need to avoid click or custom an abstraction
		if(w.get(AndroidTags.AndroidClassName, "").contains("widget.Switch")) {
			w.set(AndroidTags.AndroidClickable, false);
			return false;
		}
		// Filter actions in the calculator
		if(isSonOfCalculator(w)) {
			return false;
		}
		// Filter initial state action that increase the state model search space due to dealing with non-determinism
		if(w.get(AndroidTags.AndroidClassName, "").contains("TextView") 
				&& w.get(AndroidTags.AndroidText, "").toLowerCase().contains("cash account")) {
			w.set(AndroidTags.AndroidClickable, false);
			return false;
		}
		if(w.get(AndroidTags.AndroidClassName, "").contains("view.View") 
				&& w.childCount() > 0 && w.child(0).get(AndroidTags.AndroidAccessibilityId, "").equals("Collapse")) {
			w.set(AndroidTags.AndroidClickable, false);
			return false;
		}
		if(w.get(AndroidTags.AndroidClassName, "").contains("view.View") 
				&& w.childCount() > 0 && w.child(0).get(AndroidTags.AndroidText, "").equals("Budget Book")) {
			w.set(AndroidTags.AndroidClickable, false);
			return false;
		}
		if(w.get(AndroidTags.AndroidClassName, "").contains("FrameLayout") 
				&& w.childCount() > 0 && w.child(0).get(AndroidTags.AndroidText, "").equals("Manage accounts")) {
			w.set(AndroidTags.AndroidClickable, false);
			return false;
		}
		return super.isClickable(w);
	}

	protected Set<Action> preSelectAction(SUT system, State state, Set<Action> actions){
		// If actions is empty because we decided not to derive actions
		// just execute NOP actions because the deep of sequences is 3
		if (actions.isEmpty()){
			System.out.println("DEBUG: Forcing NOP action in preActionSelection");
			Action nopAction = new NOP();
			nopAction.set(Tags.OriginWidget, state);
			nopAction.set(Tags.Desc, "No Operation");
			nopAction.set(Tags.Role, Roles.System);
			nopAction.set(Tags.AbstractID, state.get(Tags.AbstractID) + "_NOP");
			nopAction.set(Tags.ConcreteID, state.get(Tags.ConcreteID) + "_NOP");
			return new HashSet<>(Collections.singletonList(nopAction));
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
