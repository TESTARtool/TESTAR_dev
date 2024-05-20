/***************************************************************************************************
 *
 * Copyright (c) 2019 - 2023 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2023 Open Universiteit - www.ou.nl
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

import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testar.CodingManager;
import org.testar.RandomActionSelector;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.monkey.alayer.windows.UIARoles;
import org.testar.protocols.DesktopProtocol;

public class Protocol_desktop_generic_notepad_plus extends DesktopProtocol {

	@Override
	protected SUT startSystem() throws SystemStartException {
		SUT system =  super.startSystem();
		// Move the mouse to the corner because previous sequence mouse position may affect the model determinism (popup description)
		mouse.setCursor(5, 5);
		Util.pause(2);
		return system;
	}

	@Override
	protected State getState(SUT system) throws StateBuildException {
		State state = super.getState(system);

		// I was trying to deal with the UIAToolTip widgets by ignore them in the buildStateIdentifiers,
		// but also affects the widget path of all other widgets, something that I use in the generic abstraction. 
		// SO, if UIAToolTip exits, move the mouse and wait
		if(toolTipWidgetExists(state)) {
			// Move the mouse to the corner because previous sequence mouse position may affect the model determinism (popup description)
			mouse.setCursor(5, 5);
			Util.pause(2);
			// This approach does not seem to work when using the SPY mode, 
			// but for Generate mode it modifies the action transition with the second state
			return super.getState(system);
		}

		return state;
	}

	private boolean toolTipWidgetExists(State state) {
		for(Widget w : state) {
			if(w.get(Tags.Role, Roles.Widget).equals(UIARoles.UIAToolTip)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void buildStateIdentifiers(State state) {
		super.buildStateIdentifiers(state);

		// For menu and list widgets enrich the abstract custom id by using the widget title
		// This widget title is not being used by default because it is a dynamic property when text appear
		List<Role> list = Arrays.asList(new Role[]{UIARoles.UIAMenuItem, UIARoles.UIAListItem});
		for(Widget w : state) {
			if(list.contains(w.get(Tags.Role, Roles.Widget))) {
				String titleAbstractId = CodingManager.ID_PREFIX_WIDGET + CodingManager.ID_PREFIX_ABSTRACT + CodingManager.codify(w, Tags.Title, Tags.Role, Tags.Path);
				w.set(Tags.AbstractID, titleAbstractId);
			}
			// ScrollBar widgets can appear dynamically when lot of text appear
			// Ignore these widgets due we are ignoring if notepad has text or not
			if(isSonOfScrollBarWidgets(w)) {
				w.set(Tags.AbstractID, "");
			}

			// Thumb widgets also appear dynamically when lot of text appear (vertical + horizontal scrollbars)
			// It is better to also ignore them
			if(w.get(Tags.Role, Roles.Widget).equals(UIARoles.UIAThumb)) {
				w.set(Tags.AbstractID, "");
			}

			// Ignore tool tip messages that appear when the mouse position is over a widget
			// This is probably not needed anymore due to the getState checking that moves the mouse
			if(w.get(Tags.Role, Roles.Widget).equals(UIARoles.UIAToolTip)) {
				w.set(Tags.AbstractID, "");
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

	private boolean isSonOfScrollBarWidgets(Widget w) {
		if(w.get(Tags.Role, Roles.Widget).equals(UIARoles.UIAScrollBar)) return true;
		else if(w.parent() == null) return false;
		else if (w.parent().get(Tags.Role, Roles.Widget).equals(UIARoles.UIAScrollBar)) return true;
		else return isSonOfScrollBarWidgets(w.parent());
	}

	@Override
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException{
		Set<Action> actions = super.deriveActions(system,state);
		if(!actions.isEmpty()) return actions; // Here we return bringing SUT foreground or kill unwanted processes actions

		StdActionCompiler ac = new AnnotatingActionCompiler();

		// iterate through top widgets
		for(Widget w : getTopWidgets(state)){
			if(w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAMenu")){
				// filtering out actions on menu-containers (that would add an action in the middle of the menu)
				continue; // skip this widget
			}

			if(w.get(Enabled, true) && !w.get(Blocked, false)){ // only consider enabled and non-blocked widgets
				if (!blackListed(w)){  // do not build actions for tabu widgets  
					// only derive left clicks
					if(isClickable(w) && (isUnfiltered(w) || whiteListed(w))) {
						actions.add(ac.leftClickAt(w));
					}
				}
			}
		}

		// If the state does not contain a clickable element, but there is a close button, derive a click action
		// This is a more polite way of closing top level dialogs, panels or windows
		if(actions.isEmpty()) {
			for(Widget w : getTopWidgets(state)){
				if(w.get(Tags.Title, "").equalsIgnoreCase("Close") 
						|| w.get(Tags.Title, "").equalsIgnoreCase("Cerrar") 
						|| w.get(Tags.Title, "").equalsIgnoreCase("Cancel")
						|| w.get(Tags.Title, "").equalsIgnoreCase("No")
						|| w.get(Tags.Title, "").equalsIgnoreCase("+")) {
					actions.add(ac.leftClickAt(w));
				}
			}
		}

		return actions;

	}

	@Override
	protected boolean isClickable(Widget w) {
		boolean filter = super.isClickable(w);
		List<Role> list = Arrays.asList(new Role[]{UIARoles.UIAMenuItem, UIARoles.UIAListItem});
		return (filter && list.contains(w.get(Tags.Role, Roles.Widget)));
	}

	@Override
	protected Set<Action> retryDeriveAction(SUT system, int maxRetries, int waitingSeconds){
		// Custom maxRetries and waitingSeconds
		maxRetries = 1;
		waitingSeconds = 1;
		for(int i = 0; i < maxRetries; i++) {
			System.out.println("DEBUG: custom retryDeriveAction");
			Util.pause(waitingSeconds);
			State newState = getState(system);
			Set<Action> newActions = deriveActions(system, newState);
			if(!newActions.isEmpty()) {
				return newActions;
			}
		}
		return new HashSet<>();
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
	protected boolean executeAction(SUT system, State state, Action action){
		// If we are going to execute a ESC action 
		if(action.get(Tags.Desc,"").contains("VK_ESCAPE")) {
			// Move the mouse to the corner because the mouse position may affect the model determinism (popup description)
			mouse.setCursor(5, 5);
			Util.pause(0.5);
		}
		return super.executeAction(system, state, action);
	}

}