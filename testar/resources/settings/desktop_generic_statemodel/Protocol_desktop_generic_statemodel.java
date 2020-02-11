/***************************************************************************************************
 *
 * Copyright (c) 2019 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018, 2019 Open Universiteit - www.ou.nl
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

import java.util.HashSet;
import java.util.Set;
import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.fruit.monkey.RuntimeControlsProtocol.Modes;
import org.testar.json.object.JsonArtefactStateModel;
import org.testar.json.object.JsonArtefactTestResults;
import org.testar.protocols.DesktopProtocol;

import nl.ou.testar.ScreenshotJsonFile.JsonUtils;
import nl.ou.testar.StateModel.ModelArtifactManager;

/**
 * This is a small change to Desktop Generic Protocol to use the learned state model for
 * improved action selection algorithm.
 *
 * Please note, that this requires state model learning to be enabled in the test settings
 * (or in Setting Dialog user interface of TESTAR).
 */
public class Protocol_desktop_generic_statemodel extends DesktopProtocol {


	Set<String> sequencesOutputDir = new HashSet<>();
	Set<String> htmlOutputDir = new HashSet<>();
	Set<String> logsOutputDir = new HashSet<>();
	Set<String> sequencesVerdicts = new HashSet<>();

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
	 * This method is called when the TESTAR requests the state of the SUT.
	 * Here you can add additional information to the SUT's state or write your
	 * own state fetching routine.
	 *
	 * Here we don't change the default behaviour, but we add one more step to
	 * create a JSON file from the state information.
	 *
	 * @return  the current state of the SUT with attached oracle.
	 */
	@Override
	protected State getState(SUT system) throws StateBuildException {
		State state = super.getState(system);
		// Creating a JSON file with information about widgets and their location on the screenshot:
		if(settings.get(ConfigTags.Mode) == Modes.Generate)
			JsonUtils.createWidgetInfoJsonFile(state);

		return state;
	}
	
	
	//CheckBox from project configuration panel
	private boolean isUnrecognizedCheckBox(Widget w) {
		if(w.parent()!=null &&
				w.get(Tags.Role).toString().contains("UIAText") &&
				w.parent().get(Tags.Role).toString().contains("ListItem")) {
			return true;
		}
		return false;
	}

	/**
	 * Select one of the available actions using an action selection algorithm (for example random action selection)
	 *
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

