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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import nl.ou.testar.ActionSelectionUtils;
import nl.ou.testar.PrioritizeNewActionsSelector;
import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.*;
import org.fruit.monkey.Settings;
import org.testar.protocols.JavaSwingProtocol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.fruit.Util;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.windows.AccessBridgeFetcher;
import org.fruit.alayer.windows.UIATags;
import org.fruit.alayer.windows.WinProcess;
import org.fruit.monkey.ConfigTags;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.CompoundAction;
import org.fruit.alayer.actions.KeyDown;
import org.fruit.alayer.actions.KeyUp;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.devices.AWTKeyboard;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.devices.Keyboard;
import org.testar.OutputStructure;
import org.testar.jacoco.JacocoReportReader;
import org.testar.jacoco.MBeanClient;

import com.google.common.io.Files;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;

import java.io.FileWriter;

import org.fruit.monkey.Main;

/**
 * This protocol together with the settings provides a specific behavior to test rachota
 * We will use Java Access Bridge settings (AccessBridgeEnabled = true) for widget tree extraction
 *
 * It uses PrioritizeNewActionsSelector algorithm.
 */
public class Protocol_rachota_prioritize_new_actions extends JavaSwingProtocol {
	
	private long startSequenceTime;
	private String reportTimeDir;

	// PrioritizeNewActionsSelector: Instead of random, we will prioritize new actions for action selection
	private PrioritizeNewActionsSelector selector = new PrioritizeNewActionsSelector();
	
	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 * @param   settings  the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings){
		
		// For experimental purposes we need to disconnect from Windows Remote Desktop
		// without close the GUI session.
		/*try {
			// bat file that uses tscon.exe to disconnect without stop GUI session
			File disconnectBatFile = new File(Main.settingsDir + File.separator + "disconnectRDP.bat").getCanonicalFile();

			// Launch and disconnect from RDP session
			// This will prompt the UAC permission window if enabled in the System
			if(disconnectBatFile.exists()) {
				System.out.println("Running: " + disconnectBatFile);
				Runtime.getRuntime().exec("cmd /c start \"\" " + disconnectBatFile);
			} else {
				System.out.println("THIS BAT DOES NOT EXIST: " + disconnectBatFile);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		// Wait because disconnect from system modifies internal Screen resolution
		Util.pause(30);*/
		
		super.initialize(settings);
		
		// rachota: Requires Java Access Bridge
		System.out.println("Are we running Java Access Bridge ? " + settings.get(ConfigTags.AccessBridgeEnabled, false));
		
		// TESTAR will execute the SUT with Java
		// We need this to add JMX parameters properly (-Dcom.sun.management.jmxremote.port=5000)
		WinProcess.java_execution = true;
		
		// Enable the inspection of internal cells on Java Swing Tables
		AccessBridgeFetcher.swingJavaTableDescend = true;
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
		try{
			reportTimeDir = new File(OutputStructure.outerLoopOutputDir).getCanonicalPath();
			//myWriter = new FileWriter(reportTimeDir + "/_sequenceTimeUntilActions.txt");
		} catch (Exception e) {
				System.out.println("sequenceTimeUntilActions.txt can not be created " );
				e.printStackTrace();
		}
		super.beginSequence(system, state);

		// rachota: predefined action to deal with initial pop-up question
		// If stopSystem configuration works (delete rachota folder) we will not need this
		for(Widget w : state) {
			if(w.get(Tags.Title,"").contains("Rachota is already running or it was not")) {
				waitAndLeftClickWidgetWithMatchingTag(Tags.Title, "Yes", state, system, 5, 1);
			}
		}

		// Wait and update the state
		Util.pause(10);
		state = super.getState(system);
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

		 // To derive actions (such as clicks, drag&drop, typing ...) we should first create an action compiler.
		 StdActionCompiler ac = new AnnotatingActionCompiler();

		 /**
		  * Specific Action Derivation for rachota SUT
		  * To avoid deriving actions on non-desired widgets
		  * 
		  * Optional : for(Widget w : state)
		  * If selected also change it for all rachota protocols
		  */

		 // iterate through top level widgets
		 for(Widget w : getTopWidgets(state)){

			 if(w.get(Enabled, true) && !w.get(Blocked, false)){ // only consider enabled and non-blocked widgets

				 if (!blackListed(w)){  // do not build actions for tabu widgets  

					 // left clicks
					 if((isClickable(w)) && (isUnfiltered(w) || whiteListed(w))) {
						 actions.add(ac.leftClickAt(w));
					 }

					 // rachota: left clicks on specific widgets
					 if((isEditToClickWidget(w) || isCalendarTextWidget(w)) && (isUnfiltered(w) || whiteListed(w))) {
						 actions.add(ac.leftClickAt(w));
					 }

					 // left click in Table Cells
					 if(isTableCell(w) && (isUnfiltered(w) || whiteListed(w))) {
						 actions.add(ac.leftClickAt(w));
					 }

					 // rachota: use spinboxes
					 if(isSpinBoxWidget(w) && (isUnfiltered(w) || whiteListed(w))) {
						 addIncreaseDecreaseActions(w, actions, ac);
					 }

					 // type into text boxes
					 // rachota: edit widgets have tooltiptext (isEditableWidget)
					 if((isTypeable(w) && (isUnfiltered(w) || whiteListed(w))) && isEditableWidget(w)) {
						 actions.add(ac.clickTypeInto(w, this.getRandomText(w), true));
					 }

					 // GENERIC: All swing apps
					 /** Force actions on some widgets with a wrong accessibility **/
					 // Optional feature, comment out this changes if your Swing applications doesn't need it

					 // Tree List elements have plain "text" items have child nodes
					 // We need to derive a click action on them
					 if(w.get(Tags.Role).toString().contains("Tree")) {
						 forceWidgetTreeClickAction(w, actions);
					 }
					 // Combo Box elements also have List Elements
					 // Lists elements needs a special derivation to check widgets visibility
					 if(w.get(Tags.Role).toString().contains("List")) {
						 forceListElemetsClickAction(w, actions);
					 }
					 /** End of Force action **/
				 }
			 }
		 }

		 // PrioritizeNewActionsSelector: pick prioritized actions
		 actions = selector.getPrioritizedActions(actions);

		 return actions;

	 }

	 /**
	  * Rachota:
	  * Some Edit widgets allow click actions
	  */
	 private boolean isEditToClickWidget(Widget w) {
		 if(w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAEdit")){
			 return w.get(Tags.ToolTipText,"").contains("Mouse click");
		 }
		 return false;
	 }

	 /**
	  * Rachota:
	  * Seems that interactive Edit elements have tool type text with instructions
	  * Then if ToolTipText exists, the widget is interactive
	  */
	 private boolean isEditableWidget(Widget w) {
		 String toolTipText = w.get(Tags.ToolTipText,"");
		 return !toolTipText.trim().isEmpty() && !toolTipText.contains("text/plain") && !toolTipText.contains("Mouse click");
	 }

	 /**
	  * Rachota + Swing:
	  * Check if it is a Table Cell widget
	  */
	 private boolean isTableCell(Widget w) {
		 return w.get(UIATags.UIAAutomationId, "").contains("TableCell");
	 }

	 /**
	  * Rachota:
	  * Tricky way to check if current text widgets is a potential calendar number
	  */
	 private boolean isCalendarTextWidget(Widget w) {
		 if(w.parent() != null && w.parent().get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAPane")) {
			 int calendarDay = getNumericInt(w.get(Tags.Title, ""));
			 if(0 < calendarDay && calendarDay < 31){
				 return true;
			 }
		 }
		 return false;
	 }

	 private int getNumericInt(String strNum) {
		 if (strNum == null) {
			 return -1;
		 }
		 try {
			 return Integer.parseInt(strNum);
		 } catch (NumberFormatException nfe) {
			 return -1;
		 }
	 }

	 /**
	  * Rachota + Swing:
	  * Check if it is a Spinner widget
	  */
	 private boolean isSpinBoxWidget(Widget w) {
		 return w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIASpinner");
	 }

	 /**
	  * Rachota + Swing:
	  * SpinBox widgets buttons seems that do not exist as unique element,
	  * derive click + keyboard action to increase or decrease
	  */
	 private void addIncreaseDecreaseActions(Widget w, Set<Action> actions, StdActionCompiler ac) {
		 Action increase = new CompoundAction.Builder()   
				 .add(ac.leftClickAt(w), 0.5) // Click for focus 
				 .add(new KeyDown(KBKeys.VK_UP),0.5) // Press Up Arrow keyboard
				 .add(new KeyUp(KBKeys.VK_UP),0.5).build(); // Release Keyboard

		 increase.set(Tags.Role, Roles.Button);
		 increase.set(Tags.OriginWidget, w);
		 increase.set(Tags.Desc, "Increase Spinner");

		 Action decrease = new CompoundAction.Builder()   
				 .add(ac.leftClickAt(w), 0.5) // Click for focus 
				 .add(new KeyDown(KBKeys.VK_DOWN),0.5) // Press Down Arrow keyboard
				 .add(new KeyUp(KBKeys.VK_DOWN),0.5).build(); // Release Keyboard

		 decrease.set(Tags.Role, Roles.Button);
		 decrease.set(Tags.OriginWidget, w);
		 decrease.set(Tags.Desc, "Decrease Spinner");

		 actions.add(increase);
		 actions.add(decrease);
	 }

	 /**
	  * Iterate through the child of the specified widget to derive a click Action
	  */
	 private void forceWidgetTreeClickAction(Widget w, Set<Action> actions) {
		 StdActionCompiler ac = new AnnotatingActionCompiler();
		 actions.add(ac.leftClickAt(w));
		 w.set(Tags.ActionSet, actions);
		 for(int i = 0; i<w.childCount(); i++) {
			 forceWidgetTreeClickAction(w.child(i), actions);
		 }
	 }

	 /**
	  * Derive a click Action on visible List dropdown elements
	  */
	 public void forceListElemetsClickAction(Widget w, Set<Action> actions) {
		 if(!Objects.isNull(w.parent())) {
			 Widget parentContainer = w.parent();
			 Rect visibleContainer = Rect.from(parentContainer.get(Tags.Shape).x(), parentContainer.get(Tags.Shape).y(),
					 parentContainer.get(Tags.Shape).width(), parentContainer.get(Tags.Shape).height());

			 forceComboBoxClickAction(w, visibleContainer, actions);
		 }
	 }

	 /**
	  * Derive a click Action if widget rect bounds are inside the visible container
	  */
	 public void forceComboBoxClickAction(Widget w, Rect visibleContainer, Set<Action> actions) {
		 StdActionCompiler ac = new AnnotatingActionCompiler();
		 try {
			 Rect widgetContainer = Rect.from(w.get(Tags.Shape).x(), w.get(Tags.Shape).y(),
					 w.get(Tags.Shape).width(), w.get(Tags.Shape).height());

			 if(Rect.contains(visibleContainer, widgetContainer)) {
				 actions.add(ac.leftClickAt(w));
				 w.set(Tags.ActionSet, actions);
			 }

			 for(int i = 0; i<w.childCount(); i++) {
				 forceComboBoxClickAction(w.child(i), visibleContainer, actions);
			 }
		 } catch(Exception e) {}
	 }

	 /**
	 * Select one of the available actions using an action selection algorithm (for example random action selection)
	 *
	 * super.selectAction(state, actions) updates information to the HTML sequence report
	 *
	 * @param state the SUT's current state
	 * @param actions the set of derived actions
	 * @return  the selected action (non-null!)
	 */
	@Override
	protected Action selectAction(State state, Set<Action> actions){
		// PrioritizeNewActionsSelector: we select randomly one of the prioritize actions
		Action action = super.selectAction(state, actions);
		return(action);
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
				
				// Write sequence duration to CLI and to file
				long  sequenceDurationSoFar = System.currentTimeMillis() - startSequenceTime;
				System.out.println();
				System.out.println("Elapsed time until action " + actionCount + ": " + sequenceDurationSoFar);
	
				long minutes = (sequenceDurationSoFar / 1000)  / 60;
				int seconds = (int)((sequenceDurationSoFar / 1000) % 60);
				System.out.println("Elapsed time until action " + actionCount + ": " + + minutes + " minutes, "+ seconds + " seconds.");
				System.out.println();
				// Write sequence duration to file
				try {
					FileWriter myWriter = new FileWriter(reportTimeDir + "/" + OutputStructure.startInnerLoopDateString + "_" + OutputStructure.executedSUTname + "_actionTimeStamps.txt", true);
					//myWriter.write("Elapsed time until action " + actionCount + " : " + minutes + " minutes, " + seconds + " seconds   (" + sequenceDurationSoFar + " mili). \r\n");
					myWriter.write(sequenceDurationSoFar + "\r\n");
					myWriter.close();
					System.out.println("Wrote time so far to file." + reportTimeDir + "/_sequenceTimeUntilAction.txt");
				} catch (IOException e) {
					System.out.println("An error occurred.");
					e.printStackTrace();
				}
				
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
			
			//TODO: Disabled by default, we also need to delete original folder after compression
			//Compress JaCoCo files
			compressOutputFile();

		}
 
		super.finishSequence();
		
		// Write sequence duration to CLI and to file
		long  sequenceDuration = System.currentTimeMillis() - startSequenceTime;
		System.out.println();
		System.out.println("Sequence duration: " + sequenceDuration);
		long minutes = (sequenceDuration / 1000)  / 60;
		int seconds = (int)((sequenceDuration / 1000) % 60);
		System.out.println("Sequence duration: " + minutes + " minutes, "+ seconds + " seconds.");
		System.out.println();
		try {
			String reportDir = new File(OutputStructure.outerLoopOutputDir).getCanonicalPath();//  + File.separator;
			FileWriter myWriter = new FileWriter(reportDir + "/" + OutputStructure.startInnerLoopDateString + "_" + OutputStructure.executedSUTname + "_sequenceDuration.txt");
			myWriter.write("Sequence duration: " + minutes + " minutes, " + seconds + " seconds.   (" + sequenceDuration + " mili)");
			myWriter.close();
			System.out.println("Wrote time to file." + reportDir + "/_sequenceDuration.txt");
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
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
	 * Compress desired folder
	 * https://www.baeldung.com/java-compress-and-uncompress
	 * 
	 * @return
	 */
	private boolean compressOutputFile() {
		String originalFolder = "";
		try {
			originalFolder = new File(OutputStructure.outerLoopOutputDir).getCanonicalPath() + File.separator + "JaCoCo_reports";
			System.out.println("Compressing folder... " + originalFolder);

			String compressedFile = new File(OutputStructure.outerLoopOutputDir).getCanonicalPath() + File.separator + "JacocoReportCompress.zip";

			FileOutputStream fos = new FileOutputStream(compressedFile);
			ZipOutputStream zipOut = new ZipOutputStream(fos);
			File fileToZip = new File(originalFolder);

			zipFile(fileToZip, fileToZip.getName(), zipOut);
			zipOut.close();
			fos.close();

			System.out.println("OK! Compressed successfully : " + compressedFile);

			return true;
		} catch (Exception e) {
			System.out.println("ERROR Compressing folder: " + originalFolder);
			e.printStackTrace();
		}
		return false;
	}

	private void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
		if (fileToZip.isHidden()) {
			return;
		}
		if (fileToZip.isDirectory()) {
			if (fileName.endsWith("/")) {
				zipOut.putNextEntry(new ZipEntry(fileName));
				zipOut.closeEntry();
			} else {
				zipOut.putNextEntry(new ZipEntry(fileName + "/"));
				zipOut.closeEntry();
			}
			File[] children = fileToZip.listFiles();
			for (File childFile : children) {
				zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
			}
			return;
		}
		FileInputStream fis = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zipOut.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}
		fis.close();
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

		String rachotaPath = "C:\\Users\\testar\\.rachota";

		// Delete rachota files then next sequence will have same initial state without tasks
		if(new File(rachotaPath).exists()) {
			try {
				FileUtils.deleteDirectory(new File(rachotaPath));
			} catch(Exception e) {System.out.println("ERROR deleting rachota folder");}
		}
	}
}
