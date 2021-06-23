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

import java.io.File;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.fruit.Pair;

import nl.ou.testar.RandomActionSelector;
import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.monkey.ConfigTags;
import org.testar.OutputStructure;
import org.testar.protocols.DesktopProtocol;

import nl.ou.testar.StateModel.Difference.StateModelDifferenceManager;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;

/**
 * This is a small change to Desktop Generic Protocol to use the learned state model for
 * improved action selection algorithm.
 *
 * Please note, that this requires state model learning to be enabled in the test settings
 * (or in Setting Dialog user interface of TESTAR).
 *
 *  It only changes the selectAction() method.
 */
public class Protocol_desktop_generic_statemodel extends DesktopProtocol {

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
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException{

		//The super method returns a ONLY actions for killing unwanted processes if needed, or bringing the SUT to
		//the foreground. You should add all other actions here yourself.
		// These "special" actions are prioritized over the normal GUI actions in selectAction() / preSelectAction().
		Set<Action> actions = super.deriveActions(system,state);


		// Derive left-click actions, click and type actions, and scroll actions from
		// top level (highest Z-index) widgets of the GUI:
		actions = deriveClickTypeScrollActionsFromTopLevelWidgets(actions, system, state);

		if(actions.isEmpty()){
			// If the top level widgets did not have any executable widgets, try all widgets:
//			System.out.println("No actions from top level widgets, changing to all widgets.");
			// Derive left-click actions, click and type actions, and scroll actions from
			// all widgets of the GUI:
			actions = deriveClickTypeScrollActionsFromAllWidgetsOfState(actions, system, state);
		}

		//return the set of derived actions
		return actions;
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
			System.out.println("State model based action selection did not find an action. Using random action selection.");
			// if state model fails, use random (default would call preSelectAction() again, causing double actions HTML report):
			retAction = RandomActionSelector.selectAction(actions);
		}
		return retAction;
	}
	
	/**
	 * All TESTAR test sequence sessions are closed (State Model + OrientDB included)
	 * We can start other connection to create State Model Difference Report
	 */
	@Override
	protected void closeTestSession() {
		super.closeTestSession();
		
		try {
			if(settings.get(ConfigTags.Mode) == Modes.Generate && settings.get(ConfigTags.StateModelDifferenceAutomaticReport, false)) {
				// Define State Model versions we want to compare
				String currentApplicationName = settings.get(ConfigTags.ApplicationName,"");
				String currentVersion = settings.get(ConfigTags.ApplicationVersion,"");
				Pair<String,String> currentStateModel = new Pair<>(currentApplicationName, currentVersion);

				// We are going to compare same Application
				String previousApplicationName = currentApplicationName;
				
				// Do we want to automatically compare in this way ?
				// Or access to database and check all existing versions < currentVersion ?
				String previousVersion = "";
				if(StringUtils.isNumeric(currentVersion)) {
					previousVersion = String.valueOf(Integer.parseInt(currentVersion) - 1);
				}
				else if (Pattern.matches("([0-9]*)\\.([0-9]*)", currentVersion)) {
					previousVersion = String.valueOf(Double.parseDouble(currentVersion) - 1);
				}
				else {
					System.out.println("WARNING: State Model Difference could not calculate previous application version automatically");
				}
				
				Pair<String,String> previousStateModel = new Pair<>(previousApplicationName, previousVersion);
				
				/* This is an option to use setting parameter to specify previous model
				String previousApplicationName = settings.get(ConfigTags.PreviousApplicationName,"");
				String previousVersion = settings.get(ConfigTags.PreviousApplicationVersion,"");
				Pair<String,String> previousStateModel = new Pair<>(previousApplicationName, previousVersion);
				*/

				// Obtain Database Configuration, from Settings by default
				Config config = new Config();
				config.setConnectionType(settings.get(ConfigTags.DataStoreType,""));
				config.setServer(settings.get(ConfigTags.DataStoreServer,""));
				config.setDatabaseDirectory(settings.get(ConfigTags.DataStoreDirectory,""));
				config.setDatabase(settings.get(ConfigTags.DataStoreDB,""));
				config.setUser(settings.get(ConfigTags.DataStoreUser,""));
				config.setPassword(settings.get(ConfigTags.DataStorePassword,""));

				// State Model Difference Report Directory Name
				String dirName = OutputStructure.outerLoopOutputDir  + File.separator + "StateModelDifference_"
						+ previousStateModel.left() + "_" + previousStateModel.right() + "_vs_"
						+ currentStateModel.left() + "_" + currentStateModel.right();

				// Execute the State Model Difference to create an HTML report
				StateModelDifferenceManager modelDifferenceManager = new StateModelDifferenceManager(config, dirName);
				modelDifferenceManager.calculateModelDifference(config, previousStateModel, currentStateModel);
			}
		} catch (Exception e) {
			System.out.println("ERROR: Trying to create an automatic State Model Difference");
			e.printStackTrace();
		}
	}

}

