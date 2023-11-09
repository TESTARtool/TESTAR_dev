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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testar.CodingManager;
import org.testar.RandomActionSelector;
import org.testar.SystemProcessHandling;
import org.testar.monkey.Main;
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
import org.testar.monkey.alayer.windows.UIATags;
import org.testar.protocols.DesktopProtocol;

/**
 * 
 * Recommendation to automate killing the explorer process window if OBS opens a new file explorer:
 * https://superuser.com/questions/1008342/how-to-close-one-explorer-exes-window-instead-of-ending-the-whole-process
 * 
 * Also, when starting TESTAR close the explorer.exe process that manages the File Explorer windows
 * 
 * If not, TESTAR could open lot of file explorer windows and force OBS system to the foreground
 *
 */
public class Protocol_desktop_generic_obs extends DesktopProtocol {

	@Override
	protected SUT startSystem() throws SystemStartException {
		// Helper checking to do not run the OBS testing with more than one explorer process
		if(SystemProcessHandling.moreThanOneProcessRunning("explorer.exe")) {
			System.out.println("WARNING: More than one explorer process running");
			System.out.println("WARNING: This could open lot of File Explorer window");
			System.exit(0);
		}

		// Launch obs system using a batch file that changes to the correct directory
		launchObsSystem();
		Util.pause(10);
		// Then TESTAR will connect with process name
		SUT system =  super.startSystem();
		// Move the mouse to the corner because previous sequence mouse position may affect the model determinism (popup description)
		mouse.setCursor(5, 5);
		Util.pause(1);
		return system;
	}

	private void launchObsSystem() {
		try {
			// Run the batch script that changes directory and executes the obs application
			String batchScript = Main.settingsDir + Main.SSE_ACTIVATED + File.separator + "run_obs.bat";
			ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", batchScript);
			Process process = processBuilder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected State getState(SUT system) throws StateBuildException {
		State state = super.getState(system);

		// UIAToolTip widgets creates new widgets in the state and affects the path of all other widgets. 
		// To allow using the WidgetPath in the generic abstraction strategy. 
		// A possible solution is: if UIAToolTip exits, move the mouse and wait. 
		if(toolTipWidgetExists(state)) {
			// Move the mouse to the corner because previous sequence mouse position may affect the model determinism (popup description)
			mouse.setCursor(5, 5);
			Util.pause(1);
			// This approach does not seem to work when using the SPY mode, 
			// but for Generate mode it modifies the action transition with the second state
			return super.getState(system);
		}

		return state;
	}

	@Override
	protected void beginSequence(SUT system, State state){
		super.beginSequence(system, state);
		// If no clickable UIAMenuItem or UIAListItem exists in top widgets
		// This means the update window message has appeared
		Set<Action> initialActions = deriveActions(system, state);
		boolean existsMenuOrListWidget = checkForMenuOrListWidget(initialActions);
		// Force the closing window action
		if(!existsMenuOrListWidget) {
			initialActions.iterator().next().run(system, state, 1);
			Util.pause(1);
			// Move the mouse to the corner because previous sequence mouse position may affect the model determinism (popup description)
			mouse.setCursor(5, 5);
			Util.pause(1);
		}
	}

	private boolean checkForMenuOrListWidget(Set<Action> actions) {
		for (Action action : actions) {
			if (action.get(Tags.OriginWidget, null) != null) {
				if (isClickable(action.get(Tags.OriginWidget))) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean toolTipWidgetExists(State state) {
		for(Widget w : state) {
			if(w.get(UIATags.UIAAutomationId, "").contains("qtooltip_label")) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void buildStateIdentifiers(State state) {
		super.buildStateIdentifiers(state);

		for(Widget w : state) {
			// Ignore StatusBar widgets since these are different and dynamic
			if(isSonOfStatusBarWidget(w)) {
				w.set(Tags.AbstractIDCustom, "");
			}

			// Ignore tool tip messages that appear when the mouse position is over a widget
			// However, this in not enough because tool tip messages affects all widget paths
			// This is probably not needed anymore due to the getState checking that moves the mouse
			if(w.get(UIATags.UIAAutomationId, "").contains("qtooltip_label")) {
				w.set(Tags.AbstractIDCustom, "");
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
		state.set(Tags.AbstractIDCustom, CodingManager.ID_PREFIX_STATE + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM + CodingManager.lowCollisionID(finalStateAbstractIdCustom.toString()));
	}

	private boolean isSonOfStatusBarWidget(Widget w) {
		if(w.get(Tags.Role, Roles.Widget).equals(UIARoles.UIAStatusBar)) return true;
		else if(w.parent() == null) return false;
		else if (w.parent().get(Tags.Role, Roles.Widget).equals(UIARoles.UIAStatusBar)) return true;
		else return isSonOfStatusBarWidget(w.parent());
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
						|| w.get(Tags.Title, "").equalsIgnoreCase("No")) {
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

	@Override
	protected void stopSystem(SUT system) {
		super.stopSystem(system);
		// Kill the obs process
		try {
			String processName = "obs64.exe";
			Process process = Runtime.getRuntime().exec("taskkill /F /IM " + processName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Util.pause(2);
	}
}