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

import static org.testar.monkey.alayer.Tags.Desc;
import static org.testar.monkey.alayer.Tags.ExecutedAction;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.testar.OutputStructure;
import org.testar.SutVisualization;
import org.testar.monkey.RuntimeControlsProtocol.Modes;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Finder;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Taggable;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.exceptions.ActionFailedException;
import org.testar.monkey.alayer.exceptions.WidgetNotFoundException;
import org.testar.serialisation.LogSerialiser;

public class ReplayMode {

	/**
	 * Method to run TESTAR in replay mode.
	 * The sequence to replay is the one indicated in the settings parameter: PathToReplaySequence
	 * Read the replayable file, repeat saved actions and generate new sequences, oracles and logs
	 */
	public void runReplayLoop(DefaultProtocol protocol) {

		FileInputStream fis = null;
		BufferedInputStream bis = null;
		GZIPInputStream gis = null;
		ObjectInputStream ois = null;

		protocol.actionCount = 1;
		boolean success = true;

		//Reset LogSerialiser
		LogSerialiser.finish();
		LogSerialiser.exit();

		synchronized(this){
			OutputStructure.calculateInnerLoopDateString();
			OutputStructure.sequenceInnerLoopCount++;
		}

		protocol.preSequencePreparations();

		SUT system = protocol.startSystem();

		try{
			File seqFile = new File(protocol.settings().get(ConfigTags.PathToReplaySequence));

			fis = new FileInputStream(seqFile);
			bis = new BufferedInputStream(fis);
			gis = new GZIPInputStream(bis);
			ois = new ObjectInputStream(gis);

			/**
			 * Initialize the fragment to create a new sequence and logs
			 */

			//Generating the new sequence file that can be replayed:
			protocol.getAndStoreGeneratedSequence();
			protocol.getAndStoreSequenceFile();

			protocol.cv = protocol.buildCanvas();
			State state = protocol.getState(system);

			protocol.setReplayVerdict(protocol.getVerdict(state));

			// notify the statemodelmanager
			protocol.stateModelManager.notifyTestSequencedStarted();

			double rrt = protocol.settings().get(ConfigTags.ReplayRetryTime);

			while(success 
					&& protocol.getReplayVerdict().severity() == Verdict.OK.severity() 
					&& protocol.mode() == Modes.Replay) {

				//Initialize local fragment and read saved action of PathToReplaySequence File
				Taggable replayableFragment;
				Action actionToReplay;
				try {
					replayableFragment = (Taggable) ois.readObject();
					actionToReplay = replayableFragment.get(ExecutedAction);
				} catch(IOException ioe){
					// Check if exception thrown because we finished replaying data
					if(fis.available() <= 0) {
						success = true;
						break;
					} else {
						success = false;
						String msg = "Exception " + ioe.getMessage() + " reading TESTAR replayableFragment: " + seqFile;
						protocol.setReplayVerdict(new Verdict(Verdict.Severity.UNREPLAYABLE, msg));
						protocol.stateModelManager.notifyTestSequenceInterruptedBySystem(ioe.toString());
						break;
					}
				} catch(NullPointerException npe) {
					success = false;
					String msg = "Null exception replaying TESTAR action";
					protocol.setReplayVerdict(new Verdict(Verdict.Severity.UNREPLAYABLE, msg));
					protocol.stateModelManager.notifyTestSequenceInterruptedBySystem(npe.toString());
					break;
				}

				// Derive Actions of the current State
				Set<Action> actions = protocol.deriveActions(system,state);
				protocol.buildStateActionsIdentifiers(state, actions);

				// notify to state model the current state
				protocol.stateModelManager.notifyNewStateReached(state, actions);

				success = false;
				int tries = 0;
				double start = Util.time();

				while(!success && (Util.time() - start < rrt)){
					tries++;
					protocol.cv.begin();
					Util.clear(protocol.cv);
					protocol.cv.end();

					/**
					 * Check if we are replaying the sequence correctly,
					 * comparing saved widgets with existing widgets in the current state
					 */

					//Obtain the widget Title of the repayable fragment
					String widgetStringToFind = replayableFragment.get(Tags.Title, "");
					//Could exist actions not associated with widgets
					boolean actionHasWidgetAssociated = false;
					//Check if we found the widget
					boolean widgetTitleFound = false;

					if (state != null){
						List<Finder> targets = actionToReplay.get(Tags.Targets, null);
						if (targets != null){
							actionHasWidgetAssociated = true;
							Widget w;
							for (Finder f : targets){
								try {
									w = f.apply(state);
									if (w != null){
										//Can be this the widget the one that we want to find?
										if(widgetStringToFind.equals(w.get(Tags.Title, "")))
											widgetTitleFound = true;
									}
								} catch(WidgetNotFoundException e) {
									LogSerialiser.log("WidgetNotFoundException when trying to replay the widget: " + widgetStringToFind + "\n", 
											LogSerialiser.LogLevel.Critical);
								}
							}
						}
					}

					//If action has an associated widget and we don't find it, we are not in the correct state
					if(actionHasWidgetAssociated && !widgetTitleFound){
						success = false;
						String msg = "The Action " + actionToReplay.get(Tags.Desc, actionToReplay.toString())
						+ " of the replayed sequence can not been replayed into "
						+ " the State " + state.get(Tags.ConcreteID, state.toString());

						protocol.setReplayVerdict(new Verdict(Verdict.Severity.UNREPLAYABLE, msg));

						break;
					}

					// In Replay-mode, we only show the red dot if visualizationOn is true:
					if(protocol.visualizationOn) {
						SutVisualization.visualizeSelectedAction(protocol.settings(), 
								protocol.cv, 
								state, 
								actionToReplay);
					}

					double actionDuration = protocol.settings().get(ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay)
							? replayableFragment.get(Tags.ActionDuration, 0.0) : protocol.settings().get(ConfigTags.ActionDuration);
							double actionDelay = protocol.settings().get(ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay)
									? replayableFragment.get(Tags.ActionDelay, 0.0) : protocol.settings().get(ConfigTags.TimeToWaitAfterAction);

									try{
										if(tries < 2) {
											String replayMessage = String.format("Trying to replay (%d): %s... [time window = " + rrt + "]",
													protocol.actionCount, actionToReplay.get(Desc, actionToReplay.toString()));
											LogSerialiser.log(replayMessage, LogSerialiser.LogLevel.Info);
										} else {
											if(tries % 50 == 0)
												LogSerialiser.log(".\n", LogSerialiser.LogLevel.Info);
											else
												LogSerialiser.log(".", LogSerialiser.LogLevel.Info);
										}

										protocol.preSelectAction(system, state, actions);

										//before action execution, pass it to the state model manager
										protocol.stateModelManager.notifyActionExecution(actionToReplay);

										protocol.replayAction(system, state, actionToReplay, actionDelay, actionDuration);

										success = true;
										protocol.actionCount++;
										LogSerialiser.log("Success!\n", LogSerialiser.LogLevel.Info);
									} catch(ActionFailedException afe){}

									Util.pause(actionDelay);

									//Saving the actions and the executed action into replayable test sequence:
									protocol.saveActionIntoFragmentForReplayableSequence(actionToReplay, state, actions);

									state = protocol.getState(system);

									protocol.setReplayVerdict(protocol.getVerdict(state));
				}
			}

			// notify to state model the last state
			Set<Action> actions = protocol.deriveActions(system, state);
			protocol.buildStateActionsIdentifiers(state, actions);

			protocol.stateModelManager.notifyNewStateReached(state, actions);

			protocol.cv.release();

		} catch(IOException ioe){
			throw new RuntimeException("Cannot read file.", ioe);
		} catch (ClassNotFoundException cnfe) {
			throw new RuntimeException("Cannot read file.", cnfe);
		} finally {
			if (ois != null){
				try { ois.close(); } catch (IOException e) { e.printStackTrace(); }
			}
			if (gis != null){
				try { gis.close(); } catch (IOException e) { e.printStackTrace(); }
			}
			if (bis != null){
				try { bis.close(); } catch (IOException e) { e.printStackTrace(); }
			}
			if (fis != null){
				try { fis.close(); } catch (IOException e) { e.printStackTrace(); }
			}
			if (protocol.cv != null)
				protocol.cv.release();
			if (system != null)
				system.stop();
		}

		if(protocol.getReplayVerdict().severity() != Verdict.OK.severity()) {
			String msg = "Replayed Sequence contains Errors: "+ protocol.getReplayVerdict().info();
			System.out.println(msg);
			LogSerialiser.log(msg, LogSerialiser.LogLevel.Info);

		}else if(success){
			String msg = "Sequence successfully replayed!\n";
			System.out.println(msg);
			LogSerialiser.log(msg, LogSerialiser.LogLevel.Info);

		}else if(protocol.getReplayVerdict().severity() == Verdict.Severity.UNREPLAYABLE.getValue()){
			System.out.println(protocol.getReplayVerdict().info());
			LogSerialiser.log(protocol.getReplayVerdict().info(), LogSerialiser.LogLevel.Critical);

		}else{
			String msg = "Fail replaying sequence.\n";
			System.out.println(msg);
			LogSerialiser.log(msg, LogSerialiser.LogLevel.Critical);
		}

		//calling finishSequence() to allow scripting GUI interactions to close the SUT:
		protocol.finishSequence();

		// notify the state model manager of the sequence end
		protocol.stateModelManager.notifyTestSequenceStopped();

		//Close and save the replayable fragment of the current sequence
		protocol.writeAndCloseFragmentForReplayableSequence();

		//Copy sequence file into proper directory:
		protocol.classifyAndCopySequenceIntoAppropriateDirectory(protocol.getReplayVerdict());

		LogSerialiser.finish();

		protocol.postSequenceProcessing();

		//Stop system and close the SUT
		protocol.stopSystem(system);

		// notify the statemodelmanager that the testing has finished
		protocol.stateModelManager.notifyTestingEnded();

		// Going back to TESTAR settings dialog if it was used to start replay:
		protocol.mode = Modes.Quit;
	}
}
