/***************************************************************************************************
 *
 * Copyright (c) 2022 Open Universiteit - www.ou.nl
 * Copyright (c) 2022 Universitat Politecnica de Valencia - www.upv.es
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

import java.util.Collections;
import java.util.Set;

import org.testar.ActionStatus;
import org.testar.OutputStructure;
import org.testar.monkey.RuntimeControlsProtocol.Modes;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.devices.KBKeys;
import org.testar.serialisation.LogSerialiser;

public class RecordMode {

	/**
	 * Method to run TESTAR on Record User Actions Mode.
	 * @param system
	 */
	public void runRecordLoop(DefaultProtocol protocol, SUT system) {
		boolean startedRecordMode = false;

		//If system it's null means that we have started TESTAR from the Record User Actions Mode
		//We need to invoke the SUT & the canvas representation
		if(system == null) {

			synchronized(this){
				OutputStructure.calculateInnerLoopDateString();
				OutputStructure.sequenceInnerLoopCount++;
			}

			protocol.preSequencePreparations();

			//reset the faulty variable because we started a new execution
			DefaultProtocol.faultySequence = false;

			system = protocol.startSystem();
			startedRecordMode = true;
			protocol.cv = protocol.buildCanvas();
			protocol.actionCount = 1;

			//Reset LogSerialiser
			LogSerialiser.finish();
			LogSerialiser.exit();

			//Generating the sequence file that can be replayed:
			protocol.generatedSequence = protocol.getAndStoreGeneratedSequence();
			protocol.currentSeq = protocol.getAndStoreSequenceFile();

			//Activate process Listeners if enabled in the test.settings
			if(protocol.enabledProcessListener)
				protocol.processListener.startListeners(system, protocol.settings());

			// notify the statemodelmanager
			protocol.stateModelManager.notifyTestSequencedStarted();
		}
		//else, SUT & canvas exists (startSystem() & buildCanvas() created from Generate mode)

		/**
		 * Start Record User Action Loop
		 */
		while(protocol.mode() == Modes.Record && system.isRunning()) {
			State state = protocol.getState(system);
			protocol.cv.begin();
			Util.clear(protocol.cv);

			Set<Action> actions = protocol.deriveActions(system,state);
			protocol.buildStateActionsIdentifiers(state, actions);

			//notify the state model manager of the new state
			protocol.stateModelManager.notifyNewStateReached(state, actions);

			if(actions.isEmpty()){
				if (protocol.escAttempts >= DefaultProtocol.MAX_ESC_ATTEMPTS){
					LogSerialiser.log("No available actions to execute! Tried ESC <" + DefaultProtocol.MAX_ESC_ATTEMPTS + "> times. Stopping sequence generation!\n", LogSerialiser.LogLevel.Critical);
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
			protocol.waitUserActionLoop(protocol.cv, system, state, actionStatus);

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
				//System.out.println("DEBUG: User action is not null");
				protocol.saveActionIntoFragmentForReplayableSequence(actionStatus.getAction(), state, actions);
			}else {
				//System.out.println("DEBUG: User action ----- null");
			}

			Util.clear(protocol.cv);
			protocol.cv.end();
		}

		//If user change to Generate mode & we start TESTAR on Record mode, invoke Generate mode with the created SUT
		if(protocol.mode() == Modes.Generate && startedRecordMode){
			Util.clear(protocol.cv);
			protocol.cv.end();

			new GenerateMode().runGenerateOuterLoop(protocol, system);
		}

		//If user closes the SUT while in Record-mode, TESTAR will close (or go back to SettingsDialog):
		if(!system.isRunning()){
			protocol.mode = Modes.Quit;
		}

		if(startedRecordMode && protocol.mode() == Modes.Quit){
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
}
