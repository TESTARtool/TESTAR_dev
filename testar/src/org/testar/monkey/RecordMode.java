/***************************************************************************************************
 *
 * Copyright (c) 2022 - 2023 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2022 - 2023 Open Universiteit - www.ou.nl
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

import org.testar.ActionStatus;
import org.testar.OutputStructure;
import org.testar.SutVisualization;
import org.testar.monkey.RuntimeControlsProtocol.Modes;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.devices.KBKeys;
import org.testar.monkey.alayer.devices.MouseButtons;
import org.testar.monkey.alayer.exceptions.WidgetNotFoundException;
import org.testar.serialisation.LogSerialiser;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RecordMode {

	private static final int MAX_ESC_ATTEMPTS = 99;

	/**
	 * Method to run TESTAR on Record User Actions Mode.
	 * @param system
	 */
	public void runRecordLoop(DefaultProtocol protocol) {
		// Prepare the output folders structure
		synchronized(this){
			OutputStructure.calculateInnerLoopDateString();
			OutputStructure.sequenceInnerLoopCount++;
		}

		//empty method in defaultProtocol - allowing implementation in application specific protocols
		//HTML report is created here in DefaultProtocol
		protocol.preSequencePreparations();

		//We need to invoke the SUT & the canvas representation
		SUT system = protocol.startSUTandLogger();
		protocol.cv = protocol.buildCanvas();
		protocol.actionCount = 1;

		//Generating the sequence file that can be replayed:
		protocol.generatedSequence = protocol.getAndStoreGeneratedSequence();
		protocol.currentSeq = protocol.getAndStoreSequenceFile();

		// notify the statemodelmanager
		protocol.stateModelManager.notifyTestSequencedStarted();

		/**
		 * Start Record User Action Loop
		 */
		while(protocol.mode() == Modes.Record && system.isRunning()) {
			State state = protocol.getState(system);
			protocol.cv.begin();
			Util.clear(protocol.cv);

			Set<Action> actions = protocol.deriveActions(system, state);
			protocol.buildStateActionsIdentifiers(state, actions);

			//notify the state model manager of the new state
			protocol.stateModelManager.notifyNewStateReached(state, actions);

			// If no actions are derived, create an ESC action
			if(actions.isEmpty()){
				if (protocol.escAttempts >= MAX_ESC_ATTEMPTS){
					LogSerialiser.log("No available actions to execute! Tried ESC <" + MAX_ESC_ATTEMPTS + "> times. Stopping sequence generation!\n", LogSerialiser.LogLevel.Critical);
				}
				//----------------------------------
				// THERE MUST ALMOST BE ONE ACTION!
				//----------------------------------
				// if we did not find any actions, then we just hit escape, maybe that works ;-)
				Action escAction = new AnnotatingActionCompiler().hitKey(KBKeys.VK_ESCAPE);
				protocol.buildEnvironmentActionIdentifiers(state, escAction);
				actions.add(escAction);
				protocol.escAttempts++;
			} else
				protocol.escAttempts = 0;

			ActionStatus actionStatus = new ActionStatus();

			//Start Wait User Action Loop to obtain the Action did by the User
			waitUserActionLoop(protocol, system, state, actionStatus);

			//Save the user action information into the logs
			if (actionStatus.isUserEventAction()) {

				protocol.buildStateActionsIdentifiers(state, Collections.singleton(actionStatus.getAction()));

				//notify the state model manager of the executed action
				protocol.stateModelManager.notifyActionExecution(actionStatus.getAction());

				protocol.saveActionInfoInLogs(state, actionStatus.getAction(), "RecordedAction");
				DefaultProtocol.lastExecutedAction = actionStatus.getAction();
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

		//If user closes the SUT while in Record-mode, TESTAR will close (or go back to SettingsDialog):
		if(!system.isRunning()){
			protocol.mode = Modes.Quit;
		}

		if(protocol.mode() == Modes.Quit){
			// notify the statemodelmanager
			protocol.stateModelManager.notifyTestSequenceStopped();

			// notify the state model manager of the sequence end
			protocol.stateModelManager.notifyTestingEnded();

			//Closing fragment for recording replayable test sequence:
			protocol.writeAndCloseFragmentForReplayableSequence();

			//Copy sequence file into proper directory:
			protocol.classifyAndCopySequenceIntoAppropriateDirectory(Verdict.OK, protocol.generatedSequence, protocol.currentSeq);

			protocol.postSequenceProcessing();

			//If we want to Quit the current execution we stop the system
			protocol.stopSystem(system);
		}
	}

	/**
	 * Waits for an user UI action.
	 * Requirement: Mode must be Record.
	 */
	private void waitUserActionLoop(DefaultProtocol protocol, SUT system, State state, ActionStatus actionStatus){
		while (protocol.mode() == Modes.Record && !actionStatus.isUserEventAction()){
			if (protocol.userEvent != null){
				actionStatus.setAction(mapUserEvent(protocol, state));
				actionStatus.setUserEventAction((actionStatus.getAction() != null));
				protocol.userEvent = null;
			}
			synchronized(this){
				try {
					this.wait(100);
				} catch (InterruptedException e) {}
			}
			state = protocol.getState(system);
			protocol.cv.begin();
			Util.clear(protocol.cv);

			//In Record-mode, we activate the visualization with Shift+ArrowUP:
			if(protocol.visualizationOn) {
				SutVisualization.visualizeState(false, 
						protocol.markParentWidget, 
						protocol.mouse, 
						protocol.lastPrintParentsOf, 
						protocol.cv, 
						state);
			}

			Set<Action> actions = protocol.deriveActions(system, state);
			protocol.buildStateActionsIdentifiers(state, actions);

			//In Record-mode, we activate the visualization with Shift+ArrowUP:
			if(protocol.visualizationOn) {
				protocol.visualizeActions(protocol.cv, state, actions);
			}

			protocol.cv.end();
		}
	}

	/**
	 * Records user action.
	 *
	 * @param state
	 * @return
	 */
	private Action mapUserEvent(DefaultProtocol protocol, State state){
		Assert.notNull(protocol.userEvent);
		if (protocol.userEvent[0] instanceof MouseButtons){ // mouse events
			double x = ((Double) protocol.userEvent[1]).doubleValue();
			double y = ((Double) protocol.userEvent[2]).doubleValue();
			Widget w = null;
			try {
				w = Util.widgetFromPoint(state, x, y);
				x = 0.5; y = 0.5;
				if (protocol.userEvent[0] == MouseButtons.BUTTON1) // left click
					return (new AnnotatingActionCompiler()).leftClickAt(w,x,y);
				else if (protocol.userEvent[0] == MouseButtons.BUTTON3) // right click
					return (new AnnotatingActionCompiler()).rightClickAt(w,x,y);
			} catch (WidgetNotFoundException we){
				System.out.println("Mapping user event ... widget not found @(" + x + "," + y + ")");
				return null;
			}
		} else if (protocol.userEvent[0] instanceof KBKeys) // key events
			return (new AnnotatingActionCompiler()).hitKey((KBKeys) protocol.userEvent[0]);
		else if (protocol.userEvent[0] instanceof String){ // type events
			if (DefaultProtocol.lastExecutedAction == null)
				return null;
			List<Finder> targets = DefaultProtocol.lastExecutedAction.get(Tags.Targets,null);
			if (targets == null || targets.size() != 1)
				return null;
			try {
				Widget w = targets.get(0).apply(state);
				return (new AnnotatingActionCompiler()).clickTypeInto(w, (String) protocol.userEvent[0], true);
			} catch (WidgetNotFoundException we){
				return null;
			}
		}
		return null;
	}

}
