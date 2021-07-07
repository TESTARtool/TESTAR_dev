/***************************************************************************************************
 *
 * Copyright (c) 2020 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 Open Universiteit - www.ou.nl
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

import nl.ou.testar.RandomActionSelector;
import nl.ou.testar.ReinforcementLearning.ReinforcementLearningSettings;
import nl.ou.testar.ReinforcementLearning.ActionSelectors.ReinforcementLearningActionSelector;
import nl.ou.testar.ReinforcementLearning.Policies.GreedyPolicy;
import nl.ou.testar.ReinforcementLearning.Policies.Policy;
import nl.ou.testar.ReinforcementLearning.Policies.PolicyFactory;
import nl.ou.testar.StateModel.ActionSelection.ActionSelector;

import org.fruit.alayer.Action;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.monkey.Settings;
import org.testar.protocols.experiments.SpaghettiProtocol;
import org.testar.settings.ExtendedSettingsFactory;

import java.util.Set;
import org.fruit.alayer.windows.WinProcess;
import org.testar.OutputStructure;
import java.io.File;
import java.io.IOException;
import org.fruit.monkey.ConfigTags;
import java.io.FileWriter;

/**
 * This protocol together with the settings provides a specific behavior to test Spaghetti
 * We will use Java Access Bridge settings (AccessBridgeEnabled = true) for widget tree extraction
 *
 * It uses Reinforcement Learning Action Selection algorithm
 * Check test.setting - Reinforcement learning settings
 */
public class Protocol_spaghetti_reinforcement_learning extends SpaghettiProtocol {

    private ActionSelector actionSelector = null;
    private Policy policy = null;

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings  the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings){

		// Disconnect from Windows Remote Desktop, without close the GUI session
		// User will need to disable or accept UAC permission prompt message
		//disconnectRDP();
	    
        //Create Abstract Model with Reinforcement Learning Implementation
        settings.set(ConfigTags.StateModelReinforcementLearningEnabled, true);
        
        // Extended settings framework, set ConfigTags settings with XML framework values 
        // test.setting -> ExtendedSettingsFile
        ReinforcementLearningSettings rlXmlSetting = ExtendedSettingsFactory.createReinforcementLearningSettings();
        settings = rlXmlSetting.updateXMLSettings(settings);

        policy = PolicyFactory.getPolicy(settings);
        actionSelector = new ReinforcementLearningActionSelector(policy);

		super.initialize(settings);

		// Spaghetti: Requires Java Access Bridge
		System.out.println("Are we running Java Access Bridge ? " + settings.get(ConfigTags.AccessBridgeEnabled, false));

		// TESTAR will execute the SUT with Java
		// We need this to add JMX parameters properly (-Dcom.sun.management.jmxremote.port=5000)
		WinProcess.java_execution = true;

		// Copy "bin/settings/protocolName/build.xml" file to "bin/jacoco/build.xml"
		copyJacocoBuildFile();
		
		startRunTime = System.currentTimeMillis();
	}

	/**
	 * This method is invoked each time the TESTAR starts the SUT to generate a new sequence.
	 * This can be used for example for bypassing a login screen by filling the username and password
	 * or bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
	 * the SUT's configuration files etc.)
	 */
	@Override
	protected void beginSequence(SUT system, State state){
		startSequenceTime = System.currentTimeMillis();
		super.beginSequence(system, state);
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
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException{

		//The super method returns a ONLY actions for killing unwanted processes if needed, or bringing the SUT to
		//the foreground. You should add all other actions here yourself.
		// These "special" actions are prioritized over the normal GUI actions in selectAction() / preSelectAction().
		Set<Action> actions = super.deriveActions(system,state);

		// Derive left-click actions, click and type actions, and scroll actions from
		// top level (highest Z-index) widgets of the GUI:
		actions = deriveClickTypeScrollActionsFromTopLevelWidgets(actions, state);

		if(actions.isEmpty()){
			// If the top level widgets did not have any executable widgets, try all widgets:
			// Derive left-click actions, click and type actions, and scroll actions from
			// all widgets of the GUI:
			actions = deriveClickTypeScrollActionsFromAllWidgetsOfState(actions, state);
		}

		//return the set of derived actions
		return actions;
	}

	/**
	 * Select one of the available actions using a reinforcement learning action selection algorithm
	 *
	 * Normally super.selectAction(state, actions) updates information to the HTML sequence report, but since we
	 * overwrite it, not always running it, we have take care of the HTML report here
	 *
	 * @param state the SUT's current state
	 * @param actions the set of derived actions
	 * @return the selected action (non-null!)
	 */
	@Override
	protected Action selectAction(final State state, final Set<Action> actions) {
	    //Call the preSelectAction method from the DefaultProtocol so that, if necessary,
	    //unwanted processes are killed and SUT is put into foreground.
	    final Action preSelectedAction = preSelectAction(state, actions);
	    if (preSelectedAction != null) {
	        return preSelectedAction;
	    }
	    Action modelAction = stateModelManager.getAbstractActionToExecute(actions);
	    if(modelAction==null) {
	        System.out.println("State model based action selection did not find an action. Using random action selection.");
	        // if state model fails, use random (default would call preSelectAction() again, causing double actions HTML report):
	        return RandomActionSelector.selectAction(actions);
	    }
	    return modelAction;
	}

	/**
	 * Execute the selected action.
	 * Extract and create JaCoCo coverage report (After each action JaCoCo report will be created).
	 */
	@Override
	protected boolean executeAction(SUT system, State state, Action action){
		boolean actionExecuted = super.executeAction(system, state, action);

		// Extract and create JaCoCo action coverage report for Generate Mode
		if(settings.get(ConfigTags.Mode).equals(Modes.Generate)) {
			extractJacocoActionReport();
		}

		return actionExecuted;
	}

	/**
	 * This method is invoked each time the TESTAR has reached the stop criteria for generating a sequence.
	 * This can be used for example for graceful shutdown of the SUT, maybe pressing "Close" or "Exit" button
	 */
	@Override
	protected void finishSequence() {
		// Extract and create JaCoCo sequence coverage report for Generate Mode
		if(settings.get(ConfigTags.Mode).equals(Modes.Generate)) {
			extractJacocoSequenceReport();
		}

		super.finishSequence();
	}

	/**
	 * This methods stops the SUT
	 *
	 * @param system
	 */
	@Override
	protected void stopSystem(SUT system) {
		super.stopSystem(system);

		// This is the default JaCoCo generated file, we dumped our desired file with MBeanClient (finishSequence)
		// In this protocol this one is residual, so just delete
		if(new File("jacoco.exec").exists()) {
			System.out.println("Deleted residual jacoco.exec file ? " + new File("jacoco.exec").delete());
		}
	}

	/**
	 * This method is called after the last sequence, to allow for example handling the reporting of the session
	 */
	@Override
	protected void closeTestSession() {
		super.closeTestSession();
		// Extract and create JaCoCo run coverage report for Generate Mode
		if(settings.get(ConfigTags.Mode).equals(Modes.Generate)) {
			extractJacocoRunReport();
			compressOutputRunFolder();
			copyOutputToNewFolderUsingIpAddress("N:");
		}
	}
}
