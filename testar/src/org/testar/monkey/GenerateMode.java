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

import org.testar.OutputStructure;
import org.testar.SutVisualization;
import org.testar.monkey.RuntimeControlsProtocol.Modes;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.serialisation.LogSerialiser;

import java.util.Arrays;
import java.util.Set;
import java.util.StringJoiner;

public class GenerateMode {

	private boolean exceptionThrown = false;

	/**
	 * Method to run TESTAR on Generate mode
	 *
	 * @param protocol
	 */
	public void runGenerateOuterLoop(DefaultProtocol protocol) {

		//method for defining other init actions, like setup of external environment
		protocol.initTestSession();

		//initializing TESTAR for generate mode:
		protocol.initGenerateMode();

		/*
		 ***** OUTER LOOP - STARTING A NEW SEQUENCE
		 */
		while (protocol.mode() != Modes.Quit && protocol.moreSequences()) {
			exceptionThrown = false;

			// Prepare the output folders structure
			synchronized(this){
				OutputStructure.calculateInnerLoopDateString();
				OutputStructure.sequenceInnerLoopCount++;
			}

			//empty method in defaultProtocol - allowing implementation in application specific protocols
			//HTML report is created here in DefaultProtocol
			protocol.preSequencePreparations();

			//starting system or connect to a running one
			SUT system = protocol.startSUTandLogger();

			//Generating the sequence file that can be replayed:
			protocol.generatedSequence = protocol.getAndStoreGeneratedSequence();
			protocol.currentSeq = protocol.getAndStoreSequenceFile();

			//initializing TESTAR and the protocol canvas for a new sequence:
			protocol.startTestSequence(system);

			try {
				// getState() called before beginSequence:
				LogSerialiser.log("Obtaining system state before beginSequence...\n", LogSerialiser.LogLevel.Debug);
				State state = protocol.getState(system);

				// beginSequence() - a script to interact with GUI, for example login screen
				LogSerialiser.log("Starting sequence " + protocol.sequenceCount 
						+ " (output as: " + protocol.generatedSequence + ")\n\n", LogSerialiser.LogLevel.Info);
				protocol.beginSequence(system, state);

				//update state after begin sequence SUT modification
				state = protocol.getState(system);

				// notify the statemodelmanager
				protocol.stateModelManager.notifyTestSequencedStarted();

				/*
				 ***** starting the INNER LOOP:
				 */
				protocol.finalVerdict = runGenerateInnerLoop(protocol, system, state);

				//calling finishSequence() to allow scripting GUI interactions to close the SUT:
				protocol.finishSequence();

				// notify the state model manager of the sequence end
				protocol.stateModelManager.notifyTestSequenceStopped();

				protocol.writeAndCloseFragmentForReplayableSequence();

				if (protocol.finalVerdict.severity() != Verdict.OK.severity())
					LogSerialiser.log("Sequence contained faults!\n", LogSerialiser.LogLevel.Critical);

				//Copy sequence file into proper directory:
				protocol.classifyAndCopySequenceIntoAppropriateDirectory(protocol.getFinalVerdict(), 
						protocol.generatedSequence, 
						protocol.currentSeq);

				//calling postSequenceProcessing() to allow resetting test environment after test sequence, etc
				protocol.postSequenceProcessing();

				//Ending test sequence of TESTAR:
				protocol.endTestSequence();

				LogSerialiser.log("End of test sequence - shutting down the SUT...\n", LogSerialiser.LogLevel.Info);
				protocol.stopSystem(system);
				LogSerialiser.log("... SUT has been shut down!\n", LogSerialiser.LogLevel.Debug);

				protocol.sequenceCount++;

			} catch (Exception e) { //TODO figure out what kind of exceptions can happen here
				e.printStackTrace();
				String message = "Thread: name=" + Thread.currentThread().getName() + ",id=" + Thread.currentThread().getId() + ", TESTAR throws exception";
				System.out.println(message);
				StringJoiner stackTrace = new StringJoiner(System.lineSeparator());
				stackTrace.add(message);
				Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).forEach(stackTrace::add);
				protocol.stateModelManager.notifyTestSequenceInterruptedBySystem(stackTrace.toString());
				exceptionThrown = true;
				protocol.emergencyTerminateTestSequence(system, e);
			}
		}

		if (protocol.mode() == Modes.Quit && !exceptionThrown) {
			// the user initiated the shutdown
			protocol.stateModelManager.notifyTestSequenceInterruptedByUser();
		}

		// notify the statemodelmanager that the testing has finished
		protocol.stateModelManager.notifyTestingEnded();

		protocol.mode = Modes.Quit;
	}

	/**
	 * This is the inner loop of TESTAR Generate-mode:
	 * * 			GetState
	 * * 			GetVerdict
	 * * 			StopCriteria (moreActions/moreSequences/time?)
	 * * 			DeriveActions
	 * * 			SelectAction
	 * * 			ExecuteAction
	 *
	 * @param system
	 */
	private Verdict runGenerateInnerLoop(DefaultProtocol protocol, SUT system, State state) {

		//Deriving actions from the state:
		Set<Action> actions = protocol.deriveActions(system, state);
		protocol.buildStateActionsIdentifiers(state, actions);

		// First check if we have some pre select action to execute (retryDeriveAction or ESC)
		actions = protocol.preSelectAction(system, state, actions);

		// notify to state model the current state
		protocol.stateModelManager.notifyNewStateReached(state, actions);

		/*
		 ***** INNER LOOP:
		 */
		while (protocol.mode() != Modes.Quit && protocol.moreActions(state)) {

			// getState() including getVerdict() that is saved into the state:
			LogSerialiser.log("Obtained system state in inner loop of TESTAR...\n", LogSerialiser.LogLevel.Debug);
			protocol.cv.begin();
			Util.clear(protocol.cv);

			//Selecting one of the available actions:
			Action action = protocol.selectAction(state, actions);

			//Showing the actions if visualization is on:
			if(protocol.visualizationOn) {
				protocol.visualizeActions(protocol.cv, state, actions);
				SutVisualization.visualizeSelectedAction(protocol.settings, protocol.cv, state, action);
				protocol.cv.paintBatch();
			}

			//before action execution, pass it to the state model manager
			protocol.stateModelManager.notifyActionExecution(action);

			//Executing the selected action:
			protocol.executeAction(system, state, action);
			DefaultProtocol.lastExecutedAction = action;
			protocol.actionCount++;

			// Resetting the visualization:
			Util.clear(protocol.cv);
			protocol.cv.end();

			//Saving the actions and the executed action into replayable test sequence:
			protocol.saveActionIntoFragmentForReplayableSequence(action, state, actions);

			// fetch the new state
			state = protocol.getState(system);

			//Deriving actions from the state:
			actions = protocol.deriveActions(system, state);
			protocol.buildStateActionsIdentifiers(state, actions);

			// First check if we have some pre select action to execute (retryDeriveAction or ESC)
			actions = protocol.preSelectAction(system, state, actions);

			// notify to state model the current state
			protocol.stateModelManager.notifyNewStateReached(state, actions);
		}

		return state.get(Tags.OracleVerdict, Verdict.OK);
	}
}
