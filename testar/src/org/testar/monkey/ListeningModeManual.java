/***************************************************************************************************
 *
 * Copyright (c) 2023 - 2024 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2023 - 2024 Open Universiteit - www.ou.nl
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

package org.testar.monkey;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.testar.ActionStatus;
import org.testar.CodingManager;
import org.testar.OutputStructure;
import org.testar.SutVisualization;
import org.testar.monkey.RuntimeControlsProtocol.Modes;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.UnknownEventAction;
import org.testar.monkey.alayer.devices.KBKeys;
import org.testar.monkey.alayer.devices.MouseButtons;
import org.testar.monkey.alayer.exceptions.WidgetNotFoundException;
import org.testar.monkey.alayer.webdriver.CanvasDimensions;

import com.google.common.collect.Sets;

public class ListeningModeManual {

	private State lastListenState;

	/**
	 * Method to run TESTAR on Listening Event Actions Mode.
	 * @param system
	 */
	public void runListeningLoop(DefaultProtocol protocol) {
		System.out.println("Running TESTAR in Listening mode... runListeningLoop");

		// Prepare the output folders structure
		synchronized(this){
			OutputStructure.calculateInnerLoopDateString();
			OutputStructure.sequenceInnerLoopCount++;
		}

		//empty method in defaultProtocol - allowing implementation in application specific protocols
		//HTML report is created here in Desktop and Webdriver protocols
		protocol.preSequencePreparations();

		//We need to invoke the SUT & the canvas representation
		SUT system = protocol.startSUTandLogger();
		protocol.cv = protocol.buildCanvas();
		protocol.actionCount = 1;

		//Generating the sequence file that can be replayed:
		protocol.getAndStoreGeneratedSequence();
		protocol.getAndStoreSequenceFile();

		// notify the statemodelmanager
		protocol.stateModelManager.notifyTestSequencedStarted();

		/**
		 * Start Listening User Action Loop
		 */
		while(protocol.mode() == Modes.ListeningManual && system.isRunning()) {
			State state = protocol.getState(system);
			protocol.cv.begin();
			Util.clear(protocol.cv);

			Set<Action> actions = protocol.deriveActions(system, state);
			protocol.buildStateActionsIdentifiers(state, actions);
			// Add TESTAR derived actions into the report
			protocol.reportManager.addActions(actions);

			//notify the state model manager of the new state
			protocol.stateModelManager.notifyNewStateReached(state, actions);

			// If no actions are derived, create an ESC action
			if(actions.isEmpty()){
				//----------------------------------
				// THERE MUST ALMOST BE ONE ACTION!
				//----------------------------------
				// if we did not find any actions, then we just hit escape, maybe that works ;-)
				Action escAction = new AnnotatingActionCompiler().hitKey(KBKeys.VK_ESCAPE);
				protocol.buildEnvironmentActionIdentifiers(state, escAction);
				actions.add(escAction);
			}

			ActionStatus actionStatus = new ActionStatus();

			//Start Wait User Action Loop to obtain the Action did by the User
			waitUserActionLoop(protocol, system, state, actionStatus);

			//Save the user action information into the logs
			if (actionStatus.isUserEventAction()) {

				Action listenedAction = actionStatus.getAction();

				protocol.buildStateActionsIdentifiers(state, Collections.singleton(listenedAction));

				// Map Listened Actions, that are not Type, with existing actions
				if(!listenedAction.get(Tags.Desc, "Nothing").contains("Type")) {
					// Search MapEventUser action on previous builded actions (To match AbstractID)
					for(Action a : actions) {
						if(a.get(Tags.Desc, "Nothing").equals(listenedAction.get(Tags.Desc, "None"))) {
							listenedAction = a;
							break;
						}
					}
				}
				// Listened Type Actions will have specific input text, 
				// so mapping with previously random generated Type Actions is not an appropriate solution
				// The listenedAction will be a new Concrete Action and maybe an existing Abstract Action

				// If something went wrong trying to find the action, we need to create the AbstractID
				if(listenedAction.get(Tags.AbstractID, null) == null) {
					System.out.println("Listened Action has not AbstractID, creating...");
					CodingManager.buildIDs(state, Sets.newHashSet(listenedAction));
					System.out.println(listenedAction.get(Tags.AbstractID));
				}

				// Add listened action into the report
				protocol.reportManager.addSelectedAction(state, listenedAction);
				System.out.println("DEBUG: Listened Action: " + listenedAction.get(Tags.Desc, "NoDesc"));

				//notify the state model manager of the listened action
				protocol.stateModelManager.notifyListenedAction(listenedAction);

				protocol.saveActionInfoInLogs(state, listenedAction, "ListenedAction");
				DefaultProtocol.lastExecutedAction = listenedAction;
				lastListenState = state;
				protocol.actionCount++;
			}

			/**
			 * When we close TESTAR with Shift+down arrow, last actions is detected like null
			 */
			if(actionStatus.getAction()!=null) {
				protocol.saveActionIntoFragmentForReplayableSequence(actionStatus.getAction(), state, actions);
			} else {
				//If null just ignore
			}

			Util.clear(protocol.cv);
			protocol.cv.end();
		}

		//If user closes the SUT while in Listening-mode, TESTAR will close (or go back to SettingsDialog):
		if(!system.isRunning()){
			protocol.mode = Modes.Quit;
		}

		if(protocol.mode() == Modes.Quit){
			// notify to state model the last state
			State state = protocol.getState(system);
			Set<Action> actions = protocol.deriveActions(system, state);
			protocol.buildStateActionsIdentifiers(state, actions);
			protocol.stateModelManager.notifyNewStateReached(state, actions);

			// notify the statemodelmanager
			protocol.stateModelManager.notifyTestSequenceStopped();

			// notify the state model manager of the sequence end
			protocol.stateModelManager.notifyTestingEnded();

			//Closing fragment for listened replayable test sequence:
			protocol.writeAndCloseFragmentForReplayableSequence();

			//Copy sequence file into proper directory:
			protocol.classifyAndCopySequenceIntoAppropriateDirectory(protocol.getFinalVerdict());

			protocol.postSequenceProcessing();

			//If we want to Quit the current execution we stop the system
			protocol.stopSystem(system);
		}
	}

	/**
	 * Waits for an user UI action.
	 */
	protected void waitUserActionLoop(DefaultProtocol protocol, SUT system, State state, ActionStatus actionStatus){
		while (protocol.mode() == Modes.ListeningManual && !actionStatus.isUserEventAction()){

			if (protocol.userEvent != null){
				Action mapAction = mapUserEvent(protocol, system, state);
				//Only set the Action if was found on widget tree map
				if(mapAction != null) {
					actionStatus.setAction(mapAction);
					actionStatus.setUserEventAction((actionStatus.getAction() != null));
				}
				protocol.userEvent = null;
			}
			synchronized(this){
				try {
					this.wait(100);
				} catch (InterruptedException e) {}
			}

			if(!protocol.getState(system).equals(state)) {
				lastListenState = state;
			}

			state = protocol.getState(system);

			protocol.cv.begin();
			Util.clear(protocol.cv);

			//In Listening-mode, we activate the visualization with Shift+ArrowUP:
			if(protocol.visualizationOn) SutVisualization.visualizeState(false, protocol.markParentWidget, protocol.mouse, protocol.lastPrintParentsOf, protocol.cv, state);

			Set<Action> actions = protocol.deriveActions(system,state);
			CodingManager.buildIDs(state, actions);

			// Update the current abstract state.
			// Not detected User actions could modify the state and change the abstract state
			if (protocol.userEvent == null && !actionStatus.isUserEventAction()) {

				// Create unknown Action event to create a temporal transition
				// Should be replaced or removed if the correct Actions was found later
				Action unknown = new UnknownEventAction();
				unknown.set(Tags.Role, Roles.System);
				unknown.set(Tags.OriginWidget, lastListenState);
				unknown.set(Tags.Desc, "Unknown Event Action");

				CodingManager.buildIDs(state, new HashSet<Action>(Arrays.asList(unknown)));

				protocol.stateModelManager.notifyConcurrenceStateReached(state, actions, unknown);
			}

			//In Listening-mode, we activate the visualization with Shift+ArrowUP:
			if(protocol.visualizationOn) protocol.visualizeActions(protocol.cv, state, actions);

			protocol.cv.end();
		}
	}

	/**
	 * Listens user action.
	 *
	 * @param state
	 * @return
	 */
	private Action mapUserEvent(DefaultProtocol protocol, SUT system, State state){
		Assert.notNull(protocol.userEvent);
		if (protocol.userEvent[0] instanceof MouseButtons){ // mouse events
			// Extract the x,y coordinates form the user input mouse event
			double x = ((Double) protocol.userEvent[1]).doubleValue();
			double y = ((Double) protocol.userEvent[2]).doubleValue();
			// Because the widget web coordinates are inside the system host browser, 
			// there is a mismatching browser-web deviation that must be calculated.
			int browserHeightDeviation = CanvasDimensions.getCanvasY();
			int browserWidthDeviation = CanvasDimensions.getCanvasX();
			try {
				// Extract the widget from the point by subtracting the browser deviation
				Widget w = Util.widgetFromPoint(state, x - browserWidthDeviation, y - browserHeightDeviation);
				x = 0.5; y = 0.5;
				if (protocol.userEvent[0] == MouseButtons.BUTTON1) // left click
					return (new AnnotatingActionCompiler()).leftClickAt(w,x,y);
				else if (protocol.userEvent[0] == MouseButtons.BUTTON3) // right click
					return (new AnnotatingActionCompiler()).rightClickAt(w,x,y);
			} catch (WidgetNotFoundException we){
				System.out.println("Mapping user event ... widget not found @(" + x + "," + y + ")");

				// Update the current abstract state.
				// because map fail and the State Model is not synchronized with the SUT
				Set<Action> actions = protocol.deriveActions(system,state);
				CodingManager.buildIDs(state, actions);

				// Create unknown Action event to create a temporal transition
				// Should be replaced or removed if the correct Actions was found later
				Action unknown = new UnknownEventAction();
				unknown.set(Tags.Role, Roles.System);
				unknown.set(Tags.OriginWidget, lastListenState);
				unknown.set(Tags.Desc, "Unknown Event Action");

				CodingManager.buildIDs(state, new HashSet<Action>(Arrays.asList(unknown)));

				protocol.stateModelManager.notifyConcurrenceStateReached(state, actions, unknown);

				return null;
			}
		} else if (protocol.userEvent[0] instanceof KBKeys) { // key events
			return (new AnnotatingActionCompiler()).hitKey((KBKeys)protocol.userEvent[0]);
		} else if (protocol.userEvent[0] instanceof String){ // type events
			// Check the origin widget of last executed action to map the type text with previous clicked widget
			try {
				Widget w = DefaultProtocol.lastExecutedAction.get(Tags.OriginWidget);
				return (new AnnotatingActionCompiler()).clickTypeInto(w, (String) protocol.userEvent[0], true);
			} catch (NullPointerException npe) {
				// lastExecutedAction or OriginWidget was null
				System.out.println("Can not map typed text (" + protocol.userEvent[0] + ") with any clicked widget");
				return null;
			}
		}
		return null;
	}
}
