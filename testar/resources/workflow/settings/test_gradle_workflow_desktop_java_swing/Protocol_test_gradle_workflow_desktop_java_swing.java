/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2024 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 - 2024 Open Universiteit - www.ou.nl
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

import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.testar.monkey.Assert;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Main;
import org.testar.monkey.Util;
import org.testar.OutputStructure;
import org.testar.coverage.CodeCoverageManager;
import org.testar.managers.InputDataManager;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.protocols.DesktopProtocol;
import org.testar.settings.Settings;

/**
 * This protocol is used to test TESTAR by executing a gradle CI workflow.
 * 
 * ".github/workflows/gradle.yml"
 */
public class Protocol_test_gradle_workflow_desktop_java_swing extends DesktopProtocol {

	private CodeCoverageManager codeCoverageManager;

	@Override
	protected void initialize(Settings settings){
		super.initialize(settings);
		// Initialize the code coverage extractor using the test settings values
		codeCoverageManager = new CodeCoverageManager(settings);
	}

	@Override
	protected SUT startSystem() throws SystemStartException {
		SUT system = super.startSystem();
		// After launching the java swing application, wait a few seconds
		// Sometimes the windows container starts but the java swing widgets still loading
		Util.pause(5);
		return system;
	}

	@Override
	protected void beginSequence(SUT system, State state){
		// Before executing the first SUT action, extract the initial coverage
		codeCoverageManager.getActionCoverage("0");

		super.beginSequence(system, state);

		// Verify that Themes Java Swing widget is found in the GUI
		boolean themesJavaSwingWidgetFound = false;
		for(Widget w : state) {
			if(w.get(Tags.Title, "").contains("Themes")) {
				themesJavaSwingWidgetFound = true;
			}
		}
		Assert.isTrue(themesJavaSwingWidgetFound);
	}

	@Override
	protected State getState(SUT system) throws StateBuildException {
		State state = super.getState(system);

		// DEBUG: Top widgets that have screen bounds in the GUI of the remote server
		for(Widget w : getTopWidgets(state)) {
			String debug = String.format("Widget: '%s' ", w.get(Tags.Title, ""));
			if(w.get(Tags.Shape, null) != null) {
				debug = debug.concat(String.format("with Shape: %s", w.get(Tags.Shape, null)));
			}
			System.out.println(debug);
		}

		return state;
	}

	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException{

		Set<Action> actions = super.deriveActions(system,state);
		// unwanted processes, force SUT to foreground, ... actions automatically derived!

		// create an action compiler, which helps us create actions, such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();

		//----------------------
		// BUILD CUSTOM ACTIONS
		//----------------------

		// iterate through the top widgets of the state (used for menu items)
		for(Widget w : getTopWidgets(state)){

			if(w.get(Enabled, true) && !w.get(Blocked, false)){ // only consider enabled and non-blocked widgets

				if (!blackListed(w)){  // do not build actions for tabu widgets  

					// left clicks
					if(isClickable(w) && (isUnfiltered(w) || whiteListed(w))) {
						actions.add(ac.leftClickAt(w));
					}

					// type into text boxes
					if((isTypeable(w) && (isUnfiltered(w) || whiteListed(w))) && !isSourceCodeEditWidget(w)) {
						actions.add(ac.clickTypeInto(w, InputDataManager.getRandomTextInputData(w), true));
					}

					//Force actions on some widgets with a wrong accessibility
					//Optional, comment this changes if your Swing applications doesn't need it

					if(w.get(Tags.Role).toString().contains("Tree") ||
							w.get(Tags.Role).toString().contains("ComboBox") ||
							w.get(Tags.Role).toString().contains("List")) {
						widgetTree(w, actions);
					}
					//End of Force action

				}

			}

		}

		return actions;
	}

	/**
	 * SwingSet2 application contains a TabElement called "SourceCode"
	 * that internally contains UIAEdit widgets that are not modifiable.
	 * Because these widgets have the property ToolTipText with the value "text/html",
	 * use this Tag to recognize and ignore.
	 */
	private boolean isSourceCodeEditWidget(Widget w) {
		return w.get(Tags.ToolTipText, "").contains("text/html");
	}

	//Force actions on Tree widgets with a wrong accessibility
	public void widgetTree(Widget w, Set<Action> actions) {
		StdActionCompiler ac = new AnnotatingActionCompiler();
		actions.add(ac.leftClickAt(w));
		w.set(Tags.ActionSet, actions);
		for(int i = 0; i<w.childCount(); i++) {
			widgetTree(w.child(i), actions);
		}
	}

	@Override
	protected boolean executeAction(SUT system, State state, Action action){
		boolean executed = super.executeAction(system, state, action);
		// After executing the SUT action, extract the action coverage
		codeCoverageManager.getActionCoverage(String.valueOf(actionCount));
		return executed;
	}

	@Override
	protected void finishSequence(){
		// Before finishing the sequence and closing the SUT, extract the sequence coverage
		codeCoverageManager.getSequenceCoverage();

		// Don't use the call SystemProcessHandling.killTestLaunchedProcesses before stopSystem
		// Invoking a jar app considers the java.exe a test launched process instead the main one

		// In fact, finishSequence is maybe the wrong moment to invoke killTestLaunchedProcesses
	}

	@Override
	protected void stopSystem(SUT system) {
		State state = super.getState(system);

		// Verify that the top Java Swing widget Open Menu Item is found
		Widget openMenuItemWidget = null;
		for(Widget w : getTopWidgets(state)) {
			if(w.get(Tags.Title, "").contains("Open")) {
				System.out.println("Found: " + w.get(Tags.Title) + ", enabled? " + w.get(Tags.Enabled));
				openMenuItemWidget = w;
			}
		}

		Assert.notNull(openMenuItemWidget); // Widget was found
		Assert.isTrue(!(openMenuItemWidget.get(Tags.Enabled))); // And Is Disabled by default

		super.finishSequence(); // call SystemProcessHandling.killTestLaunchedProcesses
		super.stopSystem(system);
	}

	@Override
	protected void closeTestSession() {
		super.closeTestSession();

		// Verify the jacoco coverage folder has been created
		String jacocoDirectory = OutputStructure.outerLoopOutputDir 
				+ File.separator + "coverage" 
				+ File.separator + "jacoco";
		Assert.isTrue(new File(jacocoDirectory).exists());

		// Verify the jacoco coverage files have been created
		String jacocoFiles = jacocoDirectory + File.separator 
				+ OutputStructure.startOuterLoopDateString + "_" 
				+ OutputStructure.executedSUTname;

		Assert.isTrue(new File(jacocoFiles + "_accumulative_ratio_coverage.txt").exists());
		Assert.isTrue(new File(jacocoFiles + "_sequence_1.exec").exists());
		Assert.isTrue(new File(jacocoFiles + "_sequence_1.csv").exists());
		Assert.isTrue(new File(jacocoFiles + "_sequence_1_action_0.exec").exists());
		Assert.isTrue(new File(jacocoFiles + "_sequence_1_action_0.csv").exists());
		Assert.isTrue(new File(jacocoFiles + "_sequence_1_action_1.exec").exists());
		Assert.isTrue(new File(jacocoFiles + "_sequence_1_action_1.csv").exists());
		Assert.isTrue(new File(jacocoFiles + "_sequence_1_action_1_merged.exec").exists());
		Assert.isTrue(new File(jacocoFiles + "_sequence_1_action_1_merged.csv").exists());

		Assert.isTrue(fileContains("FileChooserDemo", new File(jacocoFiles + "_sequence_1.csv")));
		Assert.isTrue(fileContains("SwingSet2", new File(jacocoFiles + "_sequence_1.csv")));
		Assert.isTrue(fileContains("ProgressBarDemo", new File(jacocoFiles + "_sequence_1.csv")));

		try {
			File originalFolder = new File(OutputStructure.outerLoopOutputDir).getCanonicalFile();
			File artifactFolder = new File(Main.testarDir + settings.get(ConfigTags.ApplicationName,""));
			FileUtils.copyDirectory(originalFolder, artifactFolder);
		} catch(Exception e) {System.out.println("ERROR: Creating Artifact Folder");}
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
}
