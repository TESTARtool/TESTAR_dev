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
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Main;
import org.fruit.monkey.Settings;
import org.testar.OutputStructure;
import org.testar.jacoco.JacocoReportReader;
import org.testar.jacoco.MBeanClient;
import org.testar.protocols.DesktopProtocol;

import es.upv.staq.testar.NativeLinker;

public class Protocol_desktop_codeo extends DesktopProtocol {

	// TODO: Make SUT path configurable via settings to allow remote API configuration/execution
	private static final String DEFAULT_CODEO = "java -jar "
			+ "C:\\sysgo\\opt\\codeo-6.2\\codeo\\plugins\\org.eclipse.equinox.launcher_1.3.200.v20160318-1642.jar --launcher.library C:\\sysgo\\opt\\codeo-6.2\\codeo\\plugins\\org.eclipse.equinox.launcher.win32.win32.x86_1.1.400.v20160518-1444 --launcher.GTK_version 2 -vmargs -Dsun.io.useCanonPrefixCache=false -Dosgi.requiredJavaVersion=1.8 -Xms256m -Xmx1024m";

	private static final String JACOCO_CODEO = "java"
			+ " -Dcom.sun.management.jmxremote.port=5000"
			+ " -Dcom.sun.management.jmxremote.authenticate=false"
			+ " -Dcom.sun.management.jmxremote.ssl=false"
			+ " -javaagent:jacoco_0.8.5\\lib\\jacocoagent.jar=jmx=true"
			+ " -jar"
			+ " C:\\sysgo\\opt\\codeo-6.2\\codeo\\plugins\\org.eclipse.equinox.launcher_1.3.200.v20160318-1642.jar --launcher.library C:\\sysgo\\opt\\codeo-6.2\\codeo\\plugins\\org.eclipse.equinox.launcher.win32.win32.x86_1.1.400.v20160518-1444 --launcher.GTK_version 2 -vmargs -Dsun.io.useCanonPrefixCache=false -Dosgi.requiredJavaVersion=1.8 -Xms256m -Xmx1024m";
	
	private double menubarFilter;
	
	/**
	 * Initialize TESTAR with the given settings:
	 *
	 * @param settings
	 */
	@Override
	protected void initialize(Settings settings) {
		super.initialize(settings);
		
		// TODO: Prepare a JaCoCo installation verification message
		//verifyJaCoCoInstallation();
		
		String sutExecution = DEFAULT_CODEO;
		
		// TODO: Add TESTAR ConfigSetting or separate in two protocols
		if(jacocoExecutionMode()) {sutExecution = JACOCO_CODEO;}
		
		try {
			Runtime.getRuntime().exec("cmd /c start \"\" " + sutExecution);
		} catch (IOException e) {
			System.out.println("ERROR: Trying to ejecute CODEO application via command line");
			e.printStackTrace();
		}

		// Not a beautiful way to deal with Workspace dialog 
		// TODO: Try to replace with a friendly verification method like predefined-actions
		// BUT: We need to start the SUT in this initialize method, before SUT_WINDOWS_TITLE attachment
		// (Here we do not have the widget tree yet - we can not State iteration)

		/*Util.pause(30);

		Keyboard kb = AWTKeyboard.build();
		kb.press(KBKeys.VK_SHIFT);
		kb.release(KBKeys.VK_SHIFT);
		kb.press(KBKeys.VK_SHIFT);
		kb.release(KBKeys.VK_SHIFT);
		kb.press(KBKeys.VK_ENTER);
		kb.release(KBKeys.VK_ENTER);

		Util.pause(30);*/
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
	 * Here you can put graceful shutdown sequence for your SUT
	 * @param system
	 */
	@Override
	protected void stopSystem(SUT system) {
		if(jacocoExecutionMode()) {
			prepareJaCoCoReport(system);
		} else {
			super.stopSystem(system);
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
					" " + new File(testResultsArtefactDirectory).getCanonicalPath();

			executeNodeJSQueryPKM(commandTestResults);
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
						" " + new File(stateModelArtefactDirectory).getCanonicalPath();

				executeNodeJSQueryPKM(commandStateModel);
			} catch (IOException e) {
				System.out.println("ERROR! Reading files to insert State Model Artefact");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Verify that JaCoCo is correctly installed and configured with TESTAT requirements.
	 */
	private void verifyJaCoCoInstallation() {
		if(!new File("jacoco_0.8.5").exists()) {
			String message = "Please download and extraxt JaCoCo 0.8.5 inside /testar/bin/jacoco_0.8.5 directory.";
			System.out.println(message);
			popupMessage(message);
		} else if(!new File("jacoco_0.8.5/build.xml").exists()) {
			String message = "Please copy /testar/bin/resources/settings/desktop_codeo/build.xml file inside /testar/bin/jacoco_0.8.5 directory";
			System.out.println(message);
			popupMessage(message);
		} 
		// TODO: Verify CODEO .class files (required for build.xml)
		/* else if () {} */
	}
	
	/**
	 * Check if we want to execute TESTAR with JaCoCo coverage functionality.
	 * @return true or false
	 */
	private boolean jacocoExecutionMode() {
		if(settings.get(ConfigTags.Mode) == Modes.Generate) {
			return false;
			// Check settings + return true;
		}
		return false;
	}
	
	/**
	 * Dump JaCoCo exec file from the JVM that contains the Agent.
	 * Stop the system.
	 * Prepare the JaCoCo report Artefact and his summarize information.
	 * 
	 * @param system
	 */
	private void prepareJaCoCoReport(SUT system) {
		String jacocoFile = "jacoco-client.exec";
		try {
			jacocoFile = MBeanClient.dumpJaCoCoReport();
		} catch (Exception e) {
			System.out.println("ERROR: MBeanClient was not able to dump the JaCoCo exec report");
		}

		// Not a beautiful way trying to close the SUT 
		// TODO: Implement graceful Actions to close SUT
		
		Keyboard kb = AWTKeyboard.build();

		kb.press(KBKeys.VK_ALT);
		kb.press(KBKeys.VK_F4);

		kb.release(KBKeys.VK_ALT);
		kb.release(KBKeys.VK_F4);

		Util.pause(10);

		kb.press(KBKeys.VK_SHIFT);
		kb.release(KBKeys.VK_SHIFT);

		kb.press(KBKeys.VK_ENTER);
		kb.release(KBKeys.VK_ENTER);

		Util.pause(10);

		super.stopSystem(system);

		try {

			String reportDir = new File(OutputStructure.outerLoopOutputDir).getCanonicalPath()
					+ File.separator + "JaCoCo_reports"
					+ File.separator + OutputStructure.startInnerLoopDateString + "_" + OutputStructure.executedSUTname;
			
			coverageDir.add(reportDir);

			//Launch JaCoCo report
			String antCommand = "cd jacoco_0.8.5 && ant report"
					+ " -DjacocoFile=" + new File(jacocoFile).getCanonicalPath()
					+ " -DreportCoverageDir=" + reportDir;

			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", antCommand);
			Process p = builder.start();
			p.waitFor();

			System.out.println("JaCoCo report created successfully!");

			String coverageInfo = new JacocoReportReader(reportDir).obtainHTMLSummary();
			coverageSummary.add(coverageInfo);

			System.out.println(coverageInfo);

		} catch (IOException | InterruptedException e) {
			System.out.println("ERROR: Creating JaCoCo report !");
			e.printStackTrace();
		}
	}

}
