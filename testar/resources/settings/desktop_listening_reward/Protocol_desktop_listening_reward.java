/***************************************************************************************************
 *
 * Copyright (c) 2019 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2019 Open Universiteit - www.ou.nl

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


import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import nl.ou.testar.RandomActionSelector;
import nl.ou.testar.StateModel.ModelArtifactManager;
import nl.ou.testar.StateModel.StateModelTags;

import org.fruit.Drag;
import org.fruit.alayer.AbsolutePosition;
import org.fruit.alayer.Point;
import org.fruit.alayer.Action;
import org.fruit.alayer.exceptions.*;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;
import es.upv.staq.testar.protocols.ClickFilterLayerProtocol;

import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.fruit.alayer.Tags;
import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;

import org.testar.json.object.JsonArtefactTestResults;
import org.testar.protocols.DesktopProtocol;

public class Protocol_desktop_listening_reward extends DesktopProtocol {

	private double userInterestReward = 2.5;
	
	SortedSet<String> sequencesOutputDir = new TreeSet<>();
	SortedSet<String> htmlOutputDir = new TreeSet<>();
	SortedSet<String> logsOutputDir = new TreeSet<>();
	SortedSet<String> sequencesVerdicts = new TreeSet<>();
	
	/**
	 * Initialize TESTAR with the given settings:
	 *
	 * @param settings
	 */
	@Override
	protected void initialize(Settings settings) {
		//Set before initialize StateModel
		settings.set(ConfigTags.ListeningMode, true);
		super.initialize(settings);
	}
	
	/**
	 * Select one of the available actions (e.g. at random)
	 * @param state the SUT's current state
	 * @param actions the set of derived actions
	 * @return  the selected action (non-null!)
	 */
	@Override
	protected Action selectAction(State state, Set<Action> actions){

		//Call the preSelectAction method from the AbstractProtocol so that, if necessary,
		//unwanted processes are killed and SUT is put into foreground.
		Action retAction = preSelectAction(state, actions);
		if (retAction== null) {
			//if no preSelected actions are needed, check if we have interesting actions
			retAction = interestingActions(actions);
		}
		if (retAction== null) {
			//if no preSelected actions are needed, then implement your own action selection strategy
			//using the action selector of the state model:
			retAction = stateModelManager.getAbstractActionToExecute(actions);
		}
		if(retAction==null) {
			System.out.println("State model based action selection did not find an action. Using default action selection.");
			// if state model fails, use default:
			retAction = super.selectAction(state, actions);
		}
		return retAction;
	}
	
	/**
	 * Select one Action randomly but applying Learning Mode Rewards to Increase the possibility to be selected.
	 * 
	 * @param actions
	 * @return Action
	 */
	public Action interestingActions(Set<Action> actions) {
		
		Set<Action> interestingModelActions = stateModelManager.getInterestingActions(actions);
		
		Action highAction = null;
		
		if(!interestingModelActions.isEmpty()) {
			System.out.println("\n ******* These interesting Actions come from State Model");
			for(Action a : interestingModelActions) {
				System.out.println("Description: " + a.get(Tags.Desc, "No description"));
				System.out.println("User Interest: " +a.get(StateModelTags.UserInterest, 0));
				highAction = a;
			}
			System.out.println("******* END of interesting Actions \n");
		}

		return highAction;
	}

	/**
	 * This methods is called after each test sequence, allowing for example using external profiling software on the SUT
	 *
	 * super.postSequenceProcessing() is adding test verdict into the HTML sequence report
	 */
	@Override
	protected void postSequenceProcessing() {
		super.postSequenceProcessing();
		sequencesOutputDir.add(getGeneratedSequenceName());
		logsOutputDir.add(getGeneratedLogName());
		htmlOutputDir.add(htmlReport.getGeneratedHTMLName());
		sequencesVerdicts.add(verdictInfo);
	}

	/**
	 *  This methods is called after finishing the last sequence
	 */
	@Override
	protected void closeTestSession() {
		JsonArtefactTestResults.createTestResultsArtefact(settings, sequencesOutputDir,
				logsOutputDir, htmlOutputDir, sequencesVerdicts);
		ModelArtifactManager.createAutomaticArtefact(settings);
	}


}
