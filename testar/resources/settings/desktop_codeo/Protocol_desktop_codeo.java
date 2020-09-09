/***************************************************************************************************
 *
 * Copyright (c) 2019, 2020 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2019, 2020 Open Universiteit - www.ou.nl
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
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.devices.AWTKeyboard;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.devices.Keyboard;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.windows.WinProcess;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Main;
import org.fruit.monkey.Settings;
import org.testar.OutputStructure;
import org.testar.jacoco.JacocoReportReader;
import org.testar.jacoco.MBeanClient;
import org.testar.protocols.DesktopProtocol;

import es.upv.staq.testar.NativeLinker;

public class Protocol_desktop_codeo extends DesktopProtocol {

	private double menubarFilter;
	
	/**
	 * Initialize TESTAR with the given settings:
	 *
	 * @param settings
	 */
	@Override
	protected void initialize(Settings settings) {
		super.initialize(settings);
		
		// Set desired License
		licenseSUT = new CODEOLicense();
		
		// TESTAR will execute the SUT with Java
		// We need this to add JMX parameters properly (-Dcom.sun.management.jmxremote.port=5000)
		WinProcess.codeo_execution = true;
		
		// TODO: Prepare a JaCoCo installation verification message
		//verifyJaCoCoInstallation();
	}
	
	/**
	 * This method is called when TESTAR starts the System Under Test (SUT). The method should
	 * take care of
	 *   1) starting the SUT (you can use TESTAR's settings obtainable from <code>settings()</code> to find
	 *      out what executable to run)
	 *   2) waiting until the system is fully loaded and ready to be tested (with large systems, you might have to wait several
	 *      seconds until they have finished loading)
	 * @return  a started SUT, ready to be tested.
	 */
	@Override
	protected SUT startSystem() throws SystemStartException {
		
		SUT system = super.startSystem();
		
		// Not a beautiful way to deal with Workspace dialog 
		// TODO: Try to replace with a friendly verification method like predefined-actions
		// BUT: We need to start the SUT in this initialize method, before SUT_WINDOWS_TITLE attachment
		// (Here we do not have the widget tree yet - we can not State iteration)

		/*Keyboard kb = AWTKeyboard.build();
		kb.press(KBKeys.VK_SHIFT);
		kb.release(KBKeys.VK_SHIFT);
		kb.press(KBKeys.VK_SHIFT);
		kb.release(KBKeys.VK_SHIFT);
		kb.press(KBKeys.VK_ENTER);
		kb.release(KBKeys.VK_ENTER);

		Util.pause(30);*/
		return system;
	}
	
	/**
	 * Verify that JaCoCo is correctly installed and configured with TESTAT requirements.
	 */
	private void verifyJaCoCoInstallation() {
		if(!new File("jacoco").exists()) {
			String message = "Please download and extraxt JaCoCo inside /testar/bin/jacoco directory.";
			System.out.println(message);
			popupMessage(message);
		} else if(!new File("jacoco/build.xml").exists()) {
			String message = "Please copy /testar/bin/resources/settings/desktop_codeo/build.xml file inside /testar/bin/jacoco directory";
			System.out.println(message);
			popupMessage(message);
		} 
		// TODO: Verify CODEO .class files (required for build.xml)
		/* else if () {} */
	}
	
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
		for(Widget w : state){
            Role role = w.get(Tags.Role, Roles.Widget);
            if(Role.isOneOf(role, new Role[]{NativeLinker.getNativeRole("UIAMenuBar")})) {
            	menubarFilter = w.get(Tags.Shape,null).y() + w.get(Tags.Shape,null).height();
            	break;
            }
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
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {

		Set<Action> actions = super.deriveActions(system,state);

		// To derive actions (such as clicks, drag&drop, typing ...) we should first create an action compiler.
		StdActionCompiler ac = new AnnotatingActionCompiler();

		// To find all possible actions that TESTAR can click on we should iterate through all widgets of the state.
		for(Widget w : state){

			if(w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAMenu")){
				// filtering out actions on menu-containers (that would add an action in the middle of the menu)
				continue; // skip this iteration of the for-loop
			}

			// Only consider enabled and non-blocked widgets
			if(w.get(Tags.Enabled, true) && !w.get(Tags.Blocked, false)){

				// Do not build actions for widgets on the blacklist
				// The blackListed widgets are those that have been filtered during the SPY mode with the
				//CAPS_LOCK + SHIFT + Click clickfilter functionality.
				if (!blackListed(w)){

					//For widgets that are:
					// - clickable
					// and
					// - unFiltered by any of the regular expressions in the Filter-tab, or
					// - whitelisted using the clickfilter functionality in SPY mode (CAPS_LOCK + SHIFT + CNTR + Click)
					// We want to create actions that consist of left clicking on them
					if(isClickable(w) && (isUnfiltered(w) || whiteListed(w))) {
						//Create a left click action with the Action Compiler, and add it to the set of derived actions
						actions.add(ac.leftClickAt(w));
					}

					/*else if(isClickable(w) && isUnrecognizedCheckBox(w) && (isUnfiltered(w) || whiteListed(w))) {
						//Create a left click action with the Action Compiler, and add it to the set of derived actions
						actions.add(ac.leftClickAt(w, 0.05, 0.5));
					}*/

					//For widgets that are:
					// - typeable
					// and
					// - unFiltered by any of the regular expressions in the Filter-tab, or
					// - whitelisted using the clickfilter functionality in SPY mode (CAPS_LOCK + SHIFT + CNTR + Click)
					// We want to create actions that consist of typing into them
					if(isTypeable(w) && (isUnfiltered(w) || whiteListed(w))) {
						//Create a type action with the Action Compiler, and add it to the set of derived actions
						actions.add(ac.clickTypeInto(w, this.getRandomText(w), true));
					}

					//Add sliding actions (like scroll, drag and drop) to the derived actions
					//method defined below.
					//addSlidingActions(actions, ac, SCROLL_ARROW_SIZE,SCROLL_THICK, w.parent(), state);

				}
			}
		}
		return actions;
	}

	@Override
	protected boolean isUnfiltered(Widget w) {
		Shape shape = w.get(Tags.Shape, null);
		if (shape != null && shape.y() < menubarFilter && !w.get(Tags.Desc,"").contains("CODEO")) {
			return false;
		}

		return super.isUnfiltered(w);
	}

	@Override
	protected boolean isTypeable(Widget w) {
		if(w.get(Tags.Role, Roles.Widget).toString().contains("UIAText")) {
			return false;
		}
		return super.isTypeable(w);
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
	 * This method is invoked each time the TESTAR has reached the stop criteria for generating a sequence.
	 * This can be used for example for graceful shutdown of the SUT, maybe pressing "Close" or "Exit" button
	 */
	@Override
	protected void finishSequence() {

		// Only create JaCoCo report for Generate Mode
		// Modify if desired
		if(settings.get(ConfigTags.Mode).equals(Modes.Generate)) {

			// Dump the jacoco report from the remote JVM and Get the name/path of this file
			// And Create the output Jacoco report
			prepareJaCoCoReport();
		}

		super.finishSequence();
	}
	
	/**
	 * Dump JaCoCo exec file from the JVM that contains the Agent.
	 * Prepare the JaCoCo report Artefact and his summarize information.
	 * 
	 * @param system
	 */
	private void prepareJaCoCoReport() {
		String jacocoFile = "jacoco-client.exec";
		try {
			System.out.println("Extract JaCoCO report with MBeanClient...");
			jacocoFile = MBeanClient.dumpJaCoCoReport();
			System.out.println("Extracted: " + new File(jacocoFile).getCanonicalPath());
		} catch (Exception e) {
			System.out.println("ERROR: MBeanClient was not able to dump the JaCoCo exec report");
		}

		/**
		 * With the dumped jacocoFile (typical jacoco.exec) 
		 * and the build.xml file (that includes a reference to the .class SUT files).
		 * 
		 * Create the JaCoCo report files.
		 */
		
		try {

			// Create JaCoCo report inside output\SUTexecuted folder
			String reportDir = new File(OutputStructure.outerLoopOutputDir).getCanonicalPath()
					+ File.separator + "JaCoCo_reports"
					+ File.separator + OutputStructure.startInnerLoopDateString + "_" + OutputStructure.executedSUTname;
			
			// Update DECODER Artefact information
			coverageDir.add(reportDir);

			// Launch JaCoCo report (build.xml) and overwrite desired parameters
			String antCommand = "cd jacoco && ant report"
					+ " -DjacocoFile=" + new File(jacocoFile).getCanonicalPath()
					+ " -DreportCoverageDir=" + reportDir;

			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", antCommand);
			Process p = builder.start();
			p.waitFor();

			System.out.println("JaCoCo report created successfully!");

			// Update DECODER Artefact information
			String coverageInfo = new JacocoReportReader(reportDir).obtainHTMLSummary();
			coverageSummary.add(coverageInfo);

			System.out.println(coverageInfo);

		} catch (IOException | InterruptedException e) {
			System.out.println("ERROR: Creating JaCoCo report !");
			e.printStackTrace();
		}
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
	 * All TESTAR test sequence sessions are closed (State Model + OrientDB included)
	 * We can start other connection to create State Model Difference Report
	 */
	@Override
	protected void closeTestSession() {
		super.closeTestSession();

		automaticStateModelDifference();

		installNodePackages(new HashSet<>(Arrays.asList("mongodb", "ajv")));

		try {
			// Prepare the NodeJS command to insert the Test Results Artefact
			String insertTestResultsJS = Main.settingsDir + "validate_and_insert_testar_test_results.js";
			String insertTestResultsSchema = Main.settingsDir + "TESTAR_TestResults_Schema.json";
			String commandTestResults = "node" +
					" " + new File(insertTestResultsJS).getCanonicalPath() +
					" " + new File(insertTestResultsSchema).getCanonicalPath() +
					" " + new File(testResultsArtefactDirectory).getCanonicalPath() +
					" " + settings.get(ConfigTags.PKMaddress) +
					" " + settings.get(ConfigTags.PKMport);

			// Execute the NodeJS query and obtain the TestResults ArtefactId
			String artefactIdTestResults = executeNodeJSQueryPKM(commandTestResults);
			// Update the JSON Map that we will use to launch TESTAR HttpReportServer web service
			updateTestResultsJsonMap(artefactIdTestResults);
		} catch (IOException e) {
			System.out.println("ERROR! Reading files to insert Test Result Artefacts");
			e.printStackTrace();
		}

		if(settings.get(ConfigTags.StateModelEnabled, false)) {
			try {
				// Prepare the NodeJS command to insert the State Model Artefact
				String insertStateModelJS = Main.settingsDir + "validate_and_insert_testar_state_model.js";
				String insertStateModelSchema = Main.settingsDir + "TESTAR_StateModel_Schema.json";
				String commandStateModel = "node" +
						" " + new File(insertStateModelJS).getCanonicalPath() +
						" " + new File(insertStateModelSchema).getCanonicalPath() +
						" " + new File(stateModelArtefactDirectory).getCanonicalPath() +
						" " + settings.get(ConfigTags.PKMaddress) +
						" " + settings.get(ConfigTags.PKMport);

				String artefactIdStateModel = executeNodeJSQueryPKM(commandStateModel);
			} catch (IOException e) {
				System.out.println("ERROR! Reading files to insert State Model Artefact");
				e.printStackTrace();
			}
		}
	}

}

/**
 *  Helper class to customize SUT License as desires
 */
class CODEOLicense {
	
	String sutTitle = "CODEO";
	String sutName = "PikeOS Certified Hypervisor Eclipse-based CODEO";
	String sutUrl = "https://www.sysgo.com/products/pikeos-hypervisor/eclipse-based-codeo";
	String product = "SYSGO";
	boolean isOpenSource = false;

	public CODEOLicense () { 
		// Create object for JSON Artefact purposes
	}
}
