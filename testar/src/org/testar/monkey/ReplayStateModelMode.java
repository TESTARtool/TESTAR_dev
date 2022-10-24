package org.testar.monkey;

import java.util.Arrays;
import java.util.Set;

import org.fruit.monkey.ReplayStateModelUtil;
import org.testar.CodingManager;
import org.testar.OutputStructure;
import org.testar.SutVisualization;
import org.testar.monkey.RuntimeControlsProtocol.Modes;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.actions.ActionRoles;
import org.testar.monkey.alayer.actions.CompoundAction;
import org.testar.monkey.alayer.actions.Type;
import org.testar.monkey.alayer.exceptions.ActionFailedException;
import org.testar.serialisation.LogSerialiser;

import nl.ou.testar.StateModel.Exception.StateModelException;

public class ReplayStateModelMode {

    private boolean faultySequence;

	/**
	 * Replay Mode using State Model Sequence Layer. 
	 * 
	 * @param settings
	 * @throws StateModelException
	 */
    protected void runReplayStateModelOuterLoop(DefaultProtocol protocol) throws StateModelException {
		// We need at least the name of the model we want to replay (maybe created without version)
		if (protocol.settings.get(ConfigTags.ReplayApplicationName,"").isEmpty()) {
		    System.err.println("------------------------------------------------------------------------");
		    System.err.println("ERROR: ReplayModel mode needs at least the setting ReplayApplicationName");
		    System.err.println("------------------------------------------------------------------------");

			// notify the stateModelManager that the testing has finished
			protocol.stateModelManager.notifyTestingEnded();

			// Finish TESTAR execution
			protocol.mode = Modes.Quit;
			return;
		}

		// Get State Model Identifier from the model we want to replay
		// We will need this to extract the abstract actions later
		String replayName = protocol.settings.get(ConfigTags.ReplayApplicationName,"");
		String replayVersion = protocol.settings.get(ConfigTags.ReplayApplicationVersion,"");
		String replayModelIdentifier = ReplayStateModelUtil.getReplayModelIdentifier(protocol.stateModelManager, replayName, replayVersion);

		// Check if the model to replay and current model are using different abstraction
		// If so, no sense to use replay model mode, because actions identifiers will be different
		String replayAbsAtt = ReplayStateModelUtil.getReplayModelAbstractAttributes(protocol.stateModelManager, replayModelIdentifier);
		String currentAbsAtt = Arrays.toString(CodingManager.getCustomTagsForAbstractId());
		if(!ReplayStateModelUtil.sameAbstractionAttributes(replayAbsAtt, currentAbsAtt)) {
		    System.err.println("--------------------------------------------------------------------------------");
		    System.err.println("ERROR: Replay and Current StateModel are using different AbstractStateAttributes");
		    System.err.println("ERROR: StateModel to replay AbstractStateAttributes: " + replayAbsAtt);
		    System.err.println("ERROR: Current StateModel AbstractStateAttributes: " + currentAbsAtt);
		    System.err.println("--------------------------------------------------------------------------------");

		    // notify the stateModelManager that the testing has finished
		    protocol.stateModelManager.notifyTestingEnded();

		    // Finish TESTAR execution
		    protocol.mode = Modes.Quit;
		    return;
		}

		// User has indicated the specific sequence id to replay,
		// Only replay this sequence and stop
		if(!protocol.settings.get(ConfigTags.ReplayModelSequenceId,"").isEmpty()) {
			String msg = String.format("ReplayStateModelOuterLoop... Specific TestSequence %s for AbstractStateModel (%s, %s)", protocol.settings.get(ConfigTags.ReplayModelSequenceId), replayName, replayVersion);
			System.out.println(msg);
			runReplayStateModelInnerLoop(protocol, protocol.settings.get(ConfigTags.ReplayModelSequenceId), replayModelIdentifier);
		}
		// User has indicated a specific sequence time to replay
		// Only replay this sequence and stop
		else if(!protocol.settings.get(ConfigTags.ReplayModelSequenceTime,"").isEmpty()) {
			String msg = String.format("ReplayStateModelOuterLoop... Specific TestSequence TIME %s for AbstractStateModel (%s, %s)", protocol.settings.get(ConfigTags.ReplayModelSequenceTime), replayName, replayVersion);
			System.out.println(msg);
			String sequenceIdentifier = ReplayStateModelUtil.getReplaySequenceIdentifierByTime(protocol.stateModelManager, protocol.settings.get(ConfigTags.ReplayModelSequenceTime));
			runReplayStateModelInnerLoop(protocol, sequenceIdentifier, replayModelIdentifier);
		}
		// User has indicated a complete state model (name and version) to replay
		// Replay all the existing sequences of this model
		else {
			// Get the number of TestSequences of the model we want to replay
			int numberTestSequences = ReplayStateModelUtil.getReplayTestSequenceNumber(protocol.stateModelManager, replayModelIdentifier, replayName, replayVersion);
			String msg = String.format("ReplayStateModelOuterLoop... %s TestSequences found for AbstractStateModel (%s, %s)", numberTestSequences, replayName, replayVersion);
			System.out.println(msg);

			// Get the counter of the initial TestSequence we want to replay.
			// Ex: maybe we need to replay 3 TestSequences from TS-7 to TS-9
			Set<String> sequenceIdsToReplay = ReplayStateModelUtil.getReplayAllSequenceIdFromModel(protocol.stateModelManager, replayModelIdentifier, replayName, replayVersion);

			// Iterate over all TestSequences to reproduce them
			for(String sequenceId : sequenceIdsToReplay) {
          runReplayStateModelInnerLoop(protocol, sequenceId, replayModelIdentifier);
			}
		}

		// notify the statemodelmanager that the testing has finished
		protocol.stateModelManager.notifyTestingEnded();

		// Going back to TESTAR settings dialog if it was used to start replay:
		protocol.mode = Modes.Quit;
	}

  protected void runReplayStateModelInnerLoop(DefaultProtocol protocol, String sequenceIdentifier, String replayModelIdentifier) throws StateModelException {
		// Number of actions that contains the current sequence to replay
		int replayActionCount = ReplayStateModelUtil.getReplayActionStepsCount(protocol.stateModelManager, sequenceIdentifier);

		//Reset LogSerialiser
		LogSerialiser.finish();
		LogSerialiser.exit();

		synchronized(this){
			OutputStructure.calculateInnerLoopDateString();
			OutputStructure.sequenceInnerLoopCount++;
		}

		preSequencePreparations();

		// action extracted or not successfully
		boolean success = true;
		// reset the faulty variable because we started a new execution
		boolean faultySequence = false;

		SUT system = protocol.startSystem();

		//Generating the new sequence file that can be replayed:
		protocol.generatedSequence = protocol.getAndStoreGeneratedSequence();
		protocol.currentSeq = protocol.getAndStoreSequenceFile();

		protocol.cv = protocol.buildCanvas();
		State state = protocol.getState(system);

		protocol.setReplayVerdict(protocol.getVerdict(state));

		// notify the statemodelmanager
		protocol.stateModelManager.notifyTestSequencedStarted();

		double rrt = protocol.settings().get(ConfigTags.ReplayRetryTime);

		// count the action execution number
		protocol.actionCount = 1;

		while(success && !faultySequence && protocol.mode() == Modes.ReplayModel && protocol.actionCount <= replayActionCount){
			/**
			 * Extract the action we want to replay
			 */
			// Get the counter of the action step
			// We need to do this because one model contains multiple sequences
			String actionSequence = sequenceIdentifier + "-" + protocol.actionCount + "-" + sequenceIdentifier + "-" + (protocol.actionCount+1);
			String concreteActionId = ReplayStateModelUtil.getReplayConcreteActionStep(protocol.stateModelManager, actionSequence);
			String actionDescriptionReplay = ReplayStateModelUtil.getReplayActionDescription(protocol.stateModelManager, actionSequence);

			// Now we get the AbstractActionId of the model that contains this counter action step
			// This is the action we want to replay and we need to search in the state
			String abstractActionReplayId = ReplayStateModelUtil.getReplayAbstractActionIdFromConcreteAction(protocol.stateModelManager, replayModelIdentifier, concreteActionId);
			// Derive Actions of the current State
			Set<Action> actions = protocol.deriveActions(system,state);
			protocol.buildStateActionsIdentifiers(state, actions);

			// Now lets see if current state contains the action we want to replay
			Action actionToReplay = null;
			// First, use the AbstractIDCustom of current state actions to find the action we want to replay
			for(Action a : actions) {
			    if(a.get(Tags.AbstractIDCustom, "").equals(abstractActionReplayId)) {
			        actionToReplay = a;
			        // For Type actions we need to type the same text
			        if(actionToReplay.get(Tags.Role, ActionRoles.Action).toString().contains("Type")) {
			            actionToReplay = actionTypeToReplay(actionToReplay, actionDescriptionReplay);
			        }
			        break;
			    }
			}
			// State actions does not contain the action we want to replay
			if(actionToReplay == null) {
			    // But lets check system preSelectedActions (ESC, foreground, kill process)
			    Action systemAction = protocol.preSelectAction(system, state, actions).stream().findAny().get();
			    if(systemAction!=null && systemAction.get(Tags.AbstractIDCustom, "").equals(abstractActionReplayId)) {
			        actionToReplay = systemAction;
			    }
			}

			// notify to state model the current state
			protocol.stateModelManager.notifyNewStateReached(state, actions);

			// We did not find the action we want to replay in the current state or in the system actions
			// The SUT has changed or we are using a different abstraction
			// But the sequence is not replayable
			if(actionToReplay == null) {
				String msg = String.format("Action 'AbstractIDCustom=%s' to replay '%s' not found. ", 
				        abstractActionReplayId, actionDescriptionReplay);
				msg = msg.concat("\n");
				msg = msg.concat("The State is different or action is not derived");
				System.out.println(msg);
				protocol.setReplayVerdict(new Verdict(Verdict.SEVERITY_UNREPLAYABLE, msg));

				// We do not success trying to found the action to replay
				success = false;
			} else {
				// Action to Replay was found, lets execute it

				double actionDelay = protocol.settings.get(ConfigTags.TimeToWaitAfterAction, 1.0);
				double actionDuration = protocol.settings.get(ConfigTags.ActionDuration, 1.0);

				protocol.cv.begin(); Util.clear(protocol.cv);
				protocol.cv.end();

				// In Replay-mode, we only show the red dot if visualizationOn is true:
				if(protocol.visualizationOn) {
          SutVisualization.visualizeSelectedAction(protocol.settings,protocol. cv, state, actionToReplay);
        }

				try{
					String replayMessage = String.format("Trying to replay (%d): %s... [time window = " + rrt + "]", protocol.actionCount, actionToReplay.get(Tags.Desc, ""));
					LogSerialiser.log(replayMessage, LogSerialiser.LogLevel.Info);

					protocol.preSelectAction(system, state, actions);

					//before action execution, pass it to the state model manager
					protocol.stateModelManager.notifyActionExecution(actionToReplay);

					actionToReplay.run(system, state, actionDuration);

					protocol.actionCount++;
					LogSerialiser.log("Success!\n", LogSerialiser.LogLevel.Info);
				} catch(ActionFailedException afe){}

				Util.pause(actionDelay);

				state = protocol.getState(system);

				//Saving the actions and the executed action into replayable test sequence:
				protocol.saveActionIntoFragmentForReplayableSequence(actionToReplay, state, actions);

				protocol.setReplayVerdict(protocol.getVerdict(state));
			}
		}

		// notify to state model the last state
		Set<Action> actions = protocol.deriveActions(system, state);
		protocol.buildStateActionsIdentifiers(state, actions);
		for(Action a : actions)
			if(a.get(Tags.AbstractIDCustom, null) == null)
				protocol.buildEnvironmentActionIdentifiers(state, a);

		protocol.stateModelManager.notifyNewStateReached(state, actions);

		protocol.cv.release();

		if (protocol.cv != null) { protocol.cv.release(); }
		if (system != null) { system.stop(); }

		if(faultySequence) {
			String msg = "Replayed Sequence contains Errors: "+ protocol.getReplayVerdict().info();
			System.out.println(msg);
			LogSerialiser.log(msg, LogSerialiser.LogLevel.Info);

		} else if(success) {
			String msg = "Sequence successfully replayed!\n";
			System.out.println(msg);
			LogSerialiser.log(msg, LogSerialiser.LogLevel.Info);

		} else if(protocol.getReplayVerdict().severity() == Verdict.SEVERITY_UNREPLAYABLE) {			
			System.out.println(protocol.getReplayVerdict().info());
			LogSerialiser.log(protocol.getReplayVerdict().info(), LogSerialiser.LogLevel.Critical);

		} else {
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
		protocol.classifyAndCopySequenceIntoAppropriateDirectory(protocol.getReplayVerdict(), protocol.generatedSequence, protocol.currentSeq);

		LogSerialiser.finish();

		protocol.postSequenceProcessing();

		//Stop system and close the SUT
		protocol.stopSystem(system);
	}

	/**
	 * Replay a Type action requires to reuse the same text. 
	 * 
	 * @param actionToReplay
	 * @param actionDescriptionReplay
	 * @return
	 */
	private Action actionTypeToReplay(Action actionToReplay, String actionDescriptionReplay) {
	    for(Action compAct : ((CompoundAction)actionToReplay).getActions()) {
	        if(compAct instanceof Type) {
	            //Type 'kotrnrls' into 'Editor de texto
	            String replayText = actionDescriptionReplay.substring(6);
	            replayText = replayText.substring(0, replayText.indexOf("'"));
	            ((Type)compAct).setText(replayText);
	            actionToReplay.set(Tags.Desc, actionDescriptionReplay);
	        }
	    }
	    return actionToReplay;
	}
	protected void preSequencePreparations() {
	}
}
