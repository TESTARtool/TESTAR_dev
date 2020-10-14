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
import java.io.IOException;
import java.util.Set;

import org.fruit.Util;
import org.fruit.alayer.Action;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.windows.WinProcess;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.devices.AWTKeyboard;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.devices.Keyboard;
import org.fruit.alayer.Tags;
import org.testar.OutputStructure;
import org.testar.jacoco.JacocoReportReader;
import org.testar.jacoco.MBeanClient;
import org.testar.protocols.DesktopProtocol;

import com.google.common.io.Files;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;


public class Protocol_swingset2_jacoco_action_statemodel_remote extends DesktopProtocol {

	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings  the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings){
		super.initialize(settings);

		// TESTAR will execute the SUT with Java
		// We need this to add JMX parameters properly (-Dcom.sun.management.jmxremote.port=5000)
		WinProcess.java_execution = true;
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

	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException{

		Set<Action> actions = super.deriveActions(system,state);
		// unwanted processes, force SUT to foreground, ... actions automatically derived!

		// create an action compiler, which helps us create actions, such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();

		//----------------------
		// BUILD CUSTOM ACTIONS
		//----------------------

		// iterate through all widgets
		for(Widget w : getTopWidgets(state)){

			if(w.get(Enabled, true) && !w.get(Blocked, false)){ // only consider enabled and non-blocked widgets

				if (!blackListed(w)){  // do not build actions for tabu widgets  

					// left clicks
					if(isClickable(w) && (isUnfiltered(w) || whiteListed(w))) {
						actions.add(ac.leftClickAt(w));
					}

					// type into text boxes
					if((isTypeable(w) && (isUnfiltered(w) || whiteListed(w))) && !isSourceCodeEditWidget(w)) {
						actions.add(ac.clickTypeInto(w, this.getRandomText(w), true));
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
	 * Execute the selected action.
	 * Extract and create JaCoCo coverage report (After each action JaCoCo report will be created).
	 */
	@Override
	protected boolean executeAction(SUT system, State state, Action action){
		boolean actionExecuted = super.executeAction(system, state, action);

		// Only create JaCoCo report for Generate Mode
		// Modify if desired
		if(settings.get(ConfigTags.Mode).equals(Modes.Generate)) {

			// Dump the jacoco report from the remote JVM and Get the name/path of this file
			try {
				System.out.println("Extract JaCoCO report for Action number: " + actionCount);
				String jacocoFile = MBeanClient.dumpJaCoCoActionStepReport(Integer.toString(actionCount));
				System.out.println("Extracted: " + new File(jacocoFile).getCanonicalPath());

				// Create JaCoCo report inside output\SUTexecuted folder
				String reportDir = new File(OutputStructure.outerLoopOutputDir).getCanonicalPath() 
						+ File.separator + "JaCoCo_reports"
						+ File.separator + OutputStructure.startInnerLoopDateString + "_" + OutputStructure.executedSUTname
						+ "_action_" + actionCount;

				// Launch JaCoCo report (build.xml) and overwrite desired parameters
				String antCommand = "cd jacoco && ant report"
						+ " -DjacocoFile=" + new File(jacocoFile).getCanonicalPath()
						+ " -DreportCoverageDir=" + reportDir;

				ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", antCommand);
				Process p = builder.start();
				p.waitFor();

				System.out.println("JaCoCo report created : " + reportDir);

				String coverageInfo = new JacocoReportReader(reportDir).obtainHTMLSummary();
				System.out.println(coverageInfo);

			} catch (Exception e) {
				System.out.println("ERROR Creating JaCoCo covergae for specific action: " + actionCount);
			}

		}

		return actionExecuted;
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
			String jacocoFile = dumpAndGetJacocoSequenceFileName();

			// Create the output Jacoco report
			createJacocoSequenceReport(jacocoFile);

		}

		super.finishSequence();
	}

	/**
	 * TESTAR has finished executing the actions
	 * Call MBeanClient to dump a jacoco.exec file
	 * 
	 * @return
	 */
	private String dumpAndGetJacocoSequenceFileName() {
		// Default name (generated by default by JaCoCo)
		String jacocoFile = "jacoco.exec";

		try {
			System.out.println("Extract JaCoCO report with MBeanClient...");
			jacocoFile = MBeanClient.dumpJaCoCoSequenceReport();
			System.out.println("Extracted: " + new File(jacocoFile).getCanonicalPath());
		} catch (Exception e) {
			System.out.println("ERROR: MBeanClient was not able to dump the JaCoCo exec report");
		}

		return jacocoFile;
	}

	/**
	 * With the dumped jacocoFile (typical jacoco.exec) 
	 * and the build.xml file (that includes a reference to the .class SUT files).
	 * 
	 * Create the JaCoCo report files.
	 * 
	 * @param jacocoFile
	 */
	private void createJacocoSequenceReport(String jacocoFile) {
		try {
			// JaCoCo report inside output\SUTexecuted folder
			String reportDir = new File(OutputStructure.outerLoopOutputDir).getCanonicalPath() 
					+ File.separator + "JaCoCo_reports"
					+ File.separator + OutputStructure.startInnerLoopDateString + "_" + OutputStructure.executedSUTname;

			// Launch JaCoCo report (build.xml) and overwrite desired parameters
			String antCommand = "cd jacoco && ant report"
					+ " -DjacocoFile=" + new File(jacocoFile).getCanonicalPath()
					+ " -DreportCoverageDir=" + reportDir;

			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", antCommand);
			Process p = builder.start();
			p.waitFor();

			System.out.println("JaCoCo report created : " + reportDir);

			String coverageInfo = new JacocoReportReader(reportDir).obtainHTMLSummary();
			System.out.println(coverageInfo);

		} catch (IOException | InterruptedException e) {
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
}
