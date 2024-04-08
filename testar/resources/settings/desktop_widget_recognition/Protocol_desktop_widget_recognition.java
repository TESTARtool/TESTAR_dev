/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2023 Universitat Politecnica de Valencia - www.upv.es
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


import eye.Eye;
import eye.Match;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Screen;
import org.testar.DerivedActions;
import org.testar.ProtocolUtil;
import org.testar.SutVisualization;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.ActionFailedException;
import org.testar.monkey.alayer.exceptions.NoSuchTagException;
import org.testar.monkey.alayer.exceptions.WidgetNotFoundException;
import org.testar.protocols.DesktopProtocol;
import org.testar.settings.Settings;
import org.testar.simplestategraph.GuiStateGraphWithVisitedActions;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Set;

/**
 * This protocol shows how to integrate visual recognition software, such as Eye or Sikuli, 
 * to recognize the widgets of the SUT. 
 */
public class Protocol_desktop_widget_recognition extends DesktopProtocol {

	enum Detector{
		EYE,
		SCREEN
	}

	private GuiStateGraphWithVisitedActions stateGraphWithVisitedActions;
	private Detector detector;

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings  the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings){
		// initializing simple GUI state graph:
		stateGraphWithVisitedActions = new GuiStateGraphWithVisitedActions();
		//detector = Detector.EYE;
		detector = Detector.SCREEN;
		super.initialize(settings);
	}

	/**
	 * This method is used by TESTAR to determine the set of currently available actions.
	 * You can use the SUT's current state, analyze the widgets and their properties to create
	 * a set of sensible actions, such as: "Click every Button which is enabled" etc.
	 * The return value is supposed to be non-null. If the returned set is empty, TESTAR
	 * will stop generation of the current action and continue with the next one.
	 * @param system the SUT
	 * @param state the SUT's current state
	 * @return  a set of actions
	 */
	@Override
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {

		//The super method returns a ONLY actions for killing unwanted processes if needed, or bringing the SUT to
		//the foreground. You should add all other actions here yourself.
		// These "special" actions are prioritized over the normal GUI actions in selectAction() / preSelectAction().
		Set<Action> actions = super.deriveActions(system,state);

		// Derive left-click actions, click and type actions, and scroll actions from
		// top level widgets of the GUI:
		DerivedActions derived = deriveClickTypeScrollActionsFromTopLevelWidgets(actions, state);

		if(derived.getAvailableActions().isEmpty()){
			// If the top level widgets did not have any executable widgets, try all widgets:
			// Derive left-click actions, click and type actions, and scroll actions from
			// all widgets of the GUI:
			derived = deriveClickTypeScrollActionsFromAllWidgets(actions, state);
		}

		Set<Action> filteredActions = derived.getFilteredActions();
		actions = derived.getAvailableActions();

		//Showing the grey dots for filtered actions if visualization is on:
		if(visualizationOn || mode() == Modes.Spy) SutVisualization.visualizeFilteredActions(cv, state, filteredActions);

		//return the set of derived actions
		return actions;
	}

	/**
	 * Select one of the available actions using an action selection algorithm. 
	 * 
	 * It uses the state model action selector if the state model inference settings are configured and enabled. 
	 * If the state model is not enabled, it returns a random action. 
	 * 
	 * super.selectAction(state, actions) also updates the HTML sequence report information. 
	 *
	 * @param state the SUT's current state
	 * @param actions the set of derived actions
	 * @return  the selected action (non-null!)
	 */
	@Override
	protected Action selectAction(State state, Set<Action> actions){
		// HTML is not having the unvisited actions by default, so
		// adding actions and unvisited actions to the HTML sequence report:
		try {
			reportManager.addActionsAndUnvisitedActions(actions, stateGraphWithVisitedActions.getAbstractIdsOfUnvisitedActions(state));
		}catch(Exception e){
			// catching null for the first state or any new state, when unvisited actions is still null,
			// not adding the unvisited actions on those cases:
			reportManager.addActions(actions);
		}
		//Call the preSelectAction method from the DefaultProtocol so that, if necessary,
		//unwanted processes are killed and SUT is put into foreground.
		//if no preSelected actions are needed, then implement your own action selection strategy
		// Maintaining memory of visited states and selected actions, and selecting randomly from unvisited actions:
		Action retAction = stateGraphWithVisitedActions.selectAction(state,actions);
		if (retAction== null) {
			retAction = super.selectAction(state, actions);
		}
		return retAction;
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
		// adding the action that is going to be executed into HTML report:
		reportManager.addSelectedAction(state, action);
		double waitTime = settings().get(ConfigTags.TimeToWaitAfterAction);
		double halfWait = waitTime == 0 ? 0.01 : waitTime / 2.0;
		Util.pause(halfWait); // help for a better match of the state' actions visualization

		try {
			// Resetting the visualization before taking the action screenshot
			// This avoids taking action screenshots with painted dots that provoke image recognition issues
			Util.clear(cv); cv.end();

			String widgetScreenshotPath = ProtocolUtil.getActionshot(state, action);
			System.out.println("widgetScreenshotPath " + widgetScreenshotPath);

			if (detector == Detector.EYE) {
				return detectEyeWidget(action, widgetScreenshotPath, halfWait);
			} else if (detector == Detector.SCREEN) {
				return detectSikuliScreenWidget(action, widgetScreenshotPath, halfWait);
			}
		}catch(ActionFailedException afe){
			return false;
		}catch(WidgetNotFoundException wnfe){
			return false;
		}catch (NoSuchTagException e) {
			e.printStackTrace();
			return false;
		}

		return super.executeAction(system, state, action);
	}

	protected boolean detectEyeWidget(Action action, String widgetScreenshotPath, double waitTime){
		Eye eye = new Eye();
		try {
			waitForScreenshotFile(widgetScreenshotPath, waitTime);
			Util.pause(1);
			BufferedImage image = eye.loadImage(widgetScreenshotPath);
			Match match = eye.findImage(image);
			eye.click(match.getCenterLocation());
			if(action.toShortString().contains("ClickTypeInto(")){
				eye.type(getTextToType(action));
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	protected boolean detectSikuliScreenWidget(Action action, String widgetScreenshotPath, double waitTime){
		Screen sikuliScreen = new Screen();
		try {
			waitForScreenshotFile(widgetScreenshotPath, waitTime);
			widgetScreenshotPath = new File(widgetScreenshotPath).getAbsolutePath();
			Util.pause(1);
			if(action.toShortString().equalsIgnoreCase("LeftClickAt")){
				sikuliScreen.click(widgetScreenshotPath);
			}
			else if(action.toShortString().contains("ClickTypeInto(")){
				sikuliScreen.type(widgetScreenshotPath, getTextToType(action));
			}
			return true;
		} catch (FindFailed findFailed) {
			findFailed.printStackTrace();
			return false;
		}
	}

	protected String getTextToType(Action action){
		return action.get(Tags.Desc).substring(nthIndexOf(action.get(Tags.Desc), "'", 1) + 1, nthIndexOf(action.get(Tags.Desc), "'", 2));
	}

	private int nthIndexOf(String input, String substring, int nth) {
		if (nth == 1) {
			return input.indexOf(substring);
		} else {
			return input.indexOf(substring, nthIndexOf(input, substring, nth - 1) + substring.length());
		}
	}

	protected void waitForScreenshotFile(String widgetScreenshotPath, double waitTime) throws FindFailed {
		//System.out.println("DEBUG: sikuli clicking ");
		if(widgetScreenshotPath == null) {
			// This situation can occur with sliding actions because TESTAR does not take action screenshots for them
			throw new FindFailed("TESTAR action screenthot not found! This approach only works with click and type actions");
		}
		while(!new File(widgetScreenshotPath).exists()){
			//System.out.println("Waiting for image file to exist");
			Util.pause(waitTime);
		}
	}
}
