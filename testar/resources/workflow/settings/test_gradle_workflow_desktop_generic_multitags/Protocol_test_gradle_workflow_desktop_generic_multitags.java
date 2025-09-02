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


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.testar.monkey.Assert;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Main;
import org.testar.monkey.RuntimeControlsProtocol.Modes;
import org.testar.DerivedActions;
import org.testar.OutputStructure;
import org.testar.SutVisualization;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.protocols.DesktopProtocol;

/**
 * This protocol is used to test TESTAR by executing a gradle CI workflow.
 * 
 * ".github/workflows/gradle.yml"
 */
public class Protocol_test_gradle_workflow_desktop_generic_multitags extends DesktopProtocol {
	
	/**
	 * This method is invoked each time the TESTAR starts the SUT to generate a new sequence.
	 * This can be used for example for bypassing a login screen by filling the username and password
	 * or bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
	 * the SUT's configuration files etc.)
	 */
	@Override
	protected void beginSequence(SUT system, State state){
		super.beginSequence(system, state);
		Map<String, String> mapNotepad= new HashMap<String, String>();
		mapNotepad.put("Role", "UIAMenuItem");
		mapNotepad.put("Title", "Format");

		// Verify that Font widget is not in the GUI before clicking matching tag
		boolean formatWidgetFound = false;
		for(Widget w : state) {
			if(w.get(Tags.Title, "").contains("Font")) {
				formatWidgetFound = true;
			}
		}
		Assert.isTrue(!formatWidgetFound);

		waitAndLeftClickWidgetWithMatchingTags(mapNotepad, state, system, 5, 1.0);

		// Update the state to obtain the new widgets
		state = getState(system);

		// Verify that now Font widget is in the GUI because matching tag feature works
		for(Widget w : state) {
			if(w.get(Tags.Title, "").contains("Font")) {
				formatWidgetFound = true;
			}
		}
		Assert.isTrue(formatWidgetFound);
	}

	@Override
	protected State getState(SUT system) throws StateBuildException {
		State state = super.getState(system);

		// DEBUG: That widgets have screen bounds in the GUI of the remote server
		for(Widget w : state) {
			String debug = String.format("Widget: '%s' ", w.get(Tags.Title, ""));
			if(w.get(Tags.Shape, null) != null) {
				debug = debug.concat(String.format("with Shape: %s", w.get(Tags.Shape, null)));
			}
			System.out.println(debug);
		}

		return state;
	}

	@Override
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {

		//The super method returns a ONLY actions for killing unwanted processes if needed, or bringing the SUT to
		//the foreground. You should add all other actions here yourself.
		// These "special" actions are prioritized over the normal GUI actions in selectAction() / preSelectAction().
		Set<Action> actions = super.deriveActions(system,state);

		// Derive left-click actions, click and type actions, and scroll actions from
		// top level widgets of the GUI:
		DerivedActions derived = deriveClickTypeScrollActionsFromTopLevelWidgets(actions, state);

		if(derived.getAvailableActions().isEmpty()){
			// If the top level widgets did not have any executable widgets, try all widgets:
			// Derive left-click actions, click and type actions, and scroll actions from
			// all widgets of the GUI:
			derived = deriveClickTypeScrollActionsFromAllWidgets(actions, state);
		}

		Set<Action> filteredActions = derived.getFilteredActions();
		actions = derived.getAvailableActions();

		//Showing the grey dots for filtered actions if visualization is on:
		if(visualizationOn || mode() == Modes.Spy) SutVisualization.visualizeFilteredActions(cv, state, filteredActions);

		//return the set of derived actions
		return actions;
	}

	@Override
	protected void closeTestSession() {
		super.closeTestSession();
		try {
			File originalFolder = new File(OutputStructure.outerLoopOutputDir).getCanonicalFile();
			File artifactFolder = new File(Main.testarDir + settings.get(ConfigTags.ApplicationName,""));
			FileUtils.copyDirectory(originalFolder, artifactFolder);
		} catch(Exception e) {System.out.println("ERROR: Creating Artifact Folder");}
	}
}
