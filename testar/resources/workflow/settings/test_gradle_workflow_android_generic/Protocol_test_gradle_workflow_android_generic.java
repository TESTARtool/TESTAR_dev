/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 - 2025 Open Universiteit - www.ou.nl
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

import org.apache.commons.io.FileUtils;
import org.testar.OutputStructure;
import org.testar.managers.InputDataManager;
import org.testar.monkey.Assert;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Main;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.exceptions.*;
import org.testar.monkey.alayer.android.actions.*;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import org.testar.protocols.AndroidProtocol;
import org.testar.reporting.ReportManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;

public class Protocol_test_gradle_workflow_android_generic extends AndroidProtocol {

	/**
	 * This method is called when the TESTAR requests the state of the SUT.
	 * Here you can add additional information to the SUT's state or write your
	 * own state fetching routine. The state should have attached an oracle
	 * (TagName: <code>Tags.OracleVerdict</code>) which describes whether the
	 * state is erroneous and if so why.
	 * @return  the current state of the SUT with attached oracle.
	 */
	@Override
	protected State getState(SUT system) throws StateBuildException {
		State state = super.getState(system);

		// DEBUG: The widgets from the emulator
		for(Widget w : state) {
			String debug = String.format("Widget: '%s' ", w.get(Tags.Title, ""));
			if(w.get(Tags.Shape, null) != null) {
				debug = debug.concat(String.format("with Shape: %s", w.get(Tags.Shape, null)));
			}
			System.out.println(debug);
		}

		return state;
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

		// create an action compiler, which helps us create actions
		// such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();

		// iterate through all widgets
		for (Widget widget : state) {

			// Ignore widgets that are not whitelisted or that are filtred
			if(!(whiteListed(widget) || isUnfiltered(widget))) {
				continue;
			}

			// type into text boxes
			if (isTypeable(widget)) {
				String randomInput = InputDataManager.getRandomTextInputData(widget);
				actions.add(new AndroidActionType(state, widget, randomInput));
			}

			// left clicks, but ignore links outside domain
			if (isClickable(widget)) {
				actions.add(new AndroidActionClick(state, widget));
			}

			if (isLongClickable(widget)) {
				actions.add(new AndroidActionLongClick(state, widget));
			}
		}

		return actions;
	}

	/**
	 * This methods is called after each test sequence, allowing for example using external profiling software on the SUT
	 *
	 * super.postSequenceProcessing() is adding test verdict into the HTML sequence report
	 */
	@Override
	protected void postSequenceProcessing() {
		super.postSequenceProcessing(); // Finish Reports

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

	private boolean folderIsEmpty(Path path) {
		if (Files.isDirectory(path)) {
			try (DirectoryStream<Path> directory = Files.newDirectoryStream(path)) {
				return !directory.iterator().hasNext();
			} catch (IOException e) {
				return false;
			}
		}
		return false;
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
