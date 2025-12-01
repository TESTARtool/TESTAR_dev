/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2023 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 - 2023 Open Universiteit - www.ou.nl
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
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
import org.testar.reporting.ReportManager;
import org.testar.screenshotjson.JsonUtils;

/**
 * This protocol is used to test TESTAR by executing a gradle CI workflow.
 * 
 * ".github/workflows/gradle.yml"
 */
public class Protocol_test_gradle_workflow_desktop_generic extends DesktopProtocol {

	// The JsonUtils should create this state JSON file by default
	// But if TESTAR loads a state without children, this file will not be created
	boolean jsonCreated = true;

	@Override
	protected State getState(SUT system) throws StateBuildException {
		State state = super.getState(system);

		// Creating a JSON file with information about widgets and their location on the screenshot:
		if(settings.get(ConfigTags.Mode) == Modes.Generate && settings.get(ConfigTags.CreateWidgetInfoJsonFile))
			jsonCreated = JsonUtils.createWidgetInfoJsonFile(state);

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

	/**
	 * This methods is called after each test sequence, allowing for example using external profiling software on the SUT
	 *
	 * super.postSequenceProcessing() is adding test verdict into the HTML sequence report
	 */
	@Override
	protected void postSequenceProcessing() {
		super.postSequenceProcessing();

		// If OnlySaveFaultySequences is enabled and the sequence verdict is OK, sequence_ok must not exist
		if(settings().get(ConfigTags.OnlySaveFaultySequences) && (getFinalVerdict()).severity() == Verdict.OK.severity()) {
			String sequencesOkFolderName = OutputStructure.outerLoopOutputDir + File.separator + "sequences_ok";
			File sequencesOkFolder = null;
			try {
				sequencesOkFolder = new File(sequencesOkFolderName).getCanonicalFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Assert.isTrue(!sequencesOkFolder.exists());
		}
		// Else, if OnlySaveFaultySequences enabled and sequence verdict is a failure,
		// Or if OnlySaveFaultySequences disabled,
		// sequence must have generated a .testar file
		else {
			Assert.isTrue(getGeneratedSequenceName().endsWith(".testar"));
			Assert.isTrue(new File(getGeneratedSequenceName()).exists());
		}

		// Verify the JsonUtils created a JSON State file
		if(jsonCreated) {
			File screenshotsFolder = null;
			try {
				screenshotsFolder = new File(OutputStructure.screenshotsOutputDir).getCanonicalFile();
				File[] subdirectories = screenshotsFolder.listFiles(File::isDirectory);
				Assert.isTrue(subdirectories.length > 0, "TESTAR screenshotsFolder did not contains a screenshot sequence directory");

				File[] jsonFileList = subdirectories[0].listFiles((dir, name) -> name.endsWith(".json"));
				if (jsonFileList != null) {
					Arrays.stream(jsonFileList).forEach(file -> System.out.println(file.getName()));
				}
				Assert.isTrue(jsonFileList.length > 0, "TESTAR screenshotsFolder did not create a JSON file using JsonUtils feature");
			} catch(IOException e) {
				Assert.isTrue(screenshotsFolder != null, "TESTAR screenshotsFolder did not exists");
			}
		}

		// Verify html and txt report files were created
		Assert.isTrue(reportManager instanceof ReportManager);
		File htmlReportFile = new File(((ReportManager)reportManager).getReportFileName().concat("_" + getFinalVerdict().verdictSeverityTitle() + ".html"));
		File txtReportFile = new File(((ReportManager)reportManager).getReportFileName().concat("_" + getFinalVerdict().verdictSeverityTitle() + ".txt"));
		System.out.println("htmlReportFile: " + htmlReportFile.getPath());
		System.out.println("txtReportFile: " + txtReportFile.getPath());
		Assert.isTrue(htmlReportFile.exists());
		Assert.isTrue(txtReportFile.exists());

		// Verify report information
		Assert.isTrue(fileContains("<h1>TESTAR execution sequence report for sequence 1</h1>", htmlReportFile));
		Assert.isTrue(fileContains("TESTAR execution sequence report for sequence 1", txtReportFile));

		Assert.isTrue(fileContains("<h2>Test verdict for this sequence:", htmlReportFile));
		Assert.isTrue(fileContains("Test verdict for this sequence:", txtReportFile));
	}

	private boolean fileContains(String searchText, File file) {
		try (Scanner scanner = new Scanner(file)) {
			// Read the content of the file line by line
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				// Check if the line contains the specific text
				if (line.contains(searchText)) {
					return true;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
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
