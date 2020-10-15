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

import org.fruit.alayer.Action;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.monkey.Settings;
import org.testar.protocols.DesktopProtocol;

import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.fruit.alayer.windows.WinProcess;
import org.testar.jacoco.JacocoReportReader;
import org.testar.jacoco.MBeanClient;
import org.testar.OutputStructure;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import com.google.common.io.Files;

import org.fruit.Util;
import org.fruit.monkey.ConfigTags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.devices.AWTKeyboard;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.devices.Keyboard;
import org.fruit.alayer.Tags;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;

import java.io.FileWriter;

import org.fruit.alayer.*;

import org.fruit.monkey.Main;

/**
 * This protocol together with the settings provides a specific behavior to test Spaghetti
 * We will use Java Access Bridge settings (AccessBridgeEnabled = true) for widget tree extraction
 *
 * It uses Random Selection algorithm.
 */
public class Protocol_spaghetti_purerandom extends DesktopProtocol {
	
	private long startSequenceTime;
	private String reportTimeDir;

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
		
		// SwingSet2: Requires Java Access Bridge
		System.out.println("Are we running Java Access Bridge ? " + settings.get(ConfigTags.AccessBridgeEnabled, false));
		
		// TESTAR will execute the SUT with Java
		// We need this to add JMX parameters properly (-Dcom.sun.management.jmxremote.port=5000)
		WinProcess.java_execution = true;
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

		 //The super method returns a ONLY actions for killing unwanted processes if needed, or bringing the SUT to
		 //the foreground. You should add all other actions here yourself.
		 // These "special" actions are prioritized over the normal GUI actions in selectAction() / preSelectAction().
		 Set<Action> actions = super.deriveActions(system,state);

		 // To derive actions (such as clicks, drag&drop, typing ...) we should first create an action compiler.
		 StdActionCompiler ac = new AnnotatingActionCompiler();

		 /**
		  * Specific Action Derivation for Spaghetti SUT
		  * To avoid deriving actions on non-desired widgets
		  * 
		  * Optional : iterate through top level widgets based on Z-index
		  * for(Widget w : getTopWidgets(state))
		  * If selected also change it for all Spaghetti protocols
		  */

		 // iterate through all widgets
		 for(Widget w : state){

			 if(w.get(Enabled, true) && !w.get(Blocked, false)){ // only consider enabled and non-blocked widgets

				 if (!blackListed(w)){  // do not build actions for tabu widgets  

					 // left clicks
					 if(isClickable(w) && (isUnfiltered(w) || whiteListed(w))) {
						 actions.add(ac.leftClickAt(w));
					 }

					 // type into text boxes
					 if(isTypeable(w) && (isUnfiltered(w) || whiteListed(w))) {
						 actions.add(ac.clickTypeInto(w, this.getRandomText(w), true));
					 }

					 // GENERIC: All swing apps
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
	 * super.selectAction(state, actions) updates information to the HTML sequence report
	 *
	 * @param state the SUT's current state
	 * @param actions the set of derived actions
	 * @return  the selected action (non-null!)
	 */
	@Override
	protected Action selectAction(State state, Set<Action> actions){
		// RandomSelector: Desktop protocol will return a random action
		return(super.selectAction(state, actions));
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
			//compressOutputFile();

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
	}
}
