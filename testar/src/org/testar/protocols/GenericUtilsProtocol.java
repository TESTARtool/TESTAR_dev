/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2021 Open Universiteit - www.ou.nl
 * Copyright (c) 2020 - 2021 Universitat Politecnica de Valencia - www.upv.es
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


package org.testar.protocols;

import es.upv.staq.testar.NativeLinker;
import es.upv.staq.testar.protocols.ClickFilterLayerProtocol;
import es.upv.staq.testar.serialisation.LogSerialiser;

import org.apache.commons.io.FileUtils;
import nl.ou.testar.DerivedActions;
import org.fruit.Drag;
import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.ActionRoles;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.NOP;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Main;
import org.testar.OutputStructure;
import org.testar.jacoco.JacocoFilesCreator;
import org.testar.jacoco.MergeJacocoFiles;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.fruit.alayer.Tags.Title;

public class GenericUtilsProtocol extends ClickFilterLayerProtocol {

    /**
     * This method waits until the widget with a matching Tag value (case sensitive) is found or the retry limit is reached.
     * If a matching widget is found, left mouse button is clicked on it and return value is true.
     * Else returns false
     *
     * @param tag for example: org.fruit.alayer.Tags.Title
     * @param value
     * @param state
     * @param system needed for updating the state between retries
     * @param maxNumberOfRetries int number of times
     * @param waitBetween double in seconds
     * @return
     */
    protected boolean waitAndLeftClickWidgetWithMatchingTag(Tag<?> tag, String value, State state, SUT system, int maxNumberOfRetries, double waitBetween){
        int numberOfRetries = 0;
        while(numberOfRetries<maxNumberOfRetries){
            //looking for a widget with matching tag value:
            Widget widget = getWidgetWithMatchingTag(tag,value,state);
            if(widget!=null){
                StdActionCompiler ac = new AnnotatingActionCompiler();
                //System.out.println("DEBUG: left mouse click on a widget with "+tag.toString()+"=" + value);
                executeAction(system,state,ac.leftClickAt(widget));
                // is waiting needed after the action has been executed?
                return true;
            }
            else{
                Util.pause(waitBetween);
                state = getState(system);
                numberOfRetries++;
            }
        }
        System.out.println("Matching widget was not found, "+tag.toString()+"=" + value);
        printTagValuesOfWidgets(tag,state);
        return false;
    }

    /**
     * This method waits until the widget with a matching Tag value (case sensitive) is found or the retry limit is reached.
     * If a matching widget is found, left mouse button is clicked on it, the given text is typed into it, and return value is true.
     * Else returns false
     *
     * @param tag for example: org.fruit.alayer.Tags.Title
     * @param value
     * @param textToType types the given text by replacing the existing text
     * @param state
     * @param system needed for updating the state between retries
     * @param maxNumberOfRetries int number of times
     * @param waitBetween double in seconds
     * @return
     */
    protected boolean waitLeftClickAndTypeIntoWidgetWithMatchingTag(Tag<?> tag, String value, String textToType, State state, SUT system, int maxNumberOfRetries, double waitBetween){
        int numberOfRetries = 0;
        while(numberOfRetries<maxNumberOfRetries){
            //looking for a widget with matching tag value:
            Widget widget = getWidgetWithMatchingTag(tag,value,state);
            if(widget!=null){
                StdActionCompiler ac = new AnnotatingActionCompiler();
                executeAction(system,state,ac.clickTypeInto(widget, textToType, true));
                // is waiting needed after the action has been executed?
                return true;
            }
            else{
                Util.pause(waitBetween);
                state = getState(system);
                numberOfRetries++;
            }
        }
        System.out.println("Matching widget was not found, "+tag.toString()+"=" + value);
        printTagValuesOfWidgets(tag,state);
        return false;
    }
    
    /**
     * This method waits until the widget with a matching Tag value (case sensitive) is found or the retry limit is reached.
     * If a matching widget is found, left mouse button is clicked on it, the given text is pasted into it, and return value is true.
     * Else returns false
     *
     * @param tag for example: org.fruit.alayer.Tags.Title
     * @param value
     * @param textToPaste paste the given text by replacing the existing text
     * @param state
     * @param system needed for updating the state between retries
     * @param maxNumberOfRetries int number of times
     * @param waitBetween double in seconds
     * @return
     */
    protected boolean waitLeftClickAndPasteIntoWidgetWithMatchingTag(Tag<?> tag, String value, String textToPaste, State state, SUT system, int maxNumberOfRetries, double waitBetween){
    	int numberOfRetries = 0;
    	while(numberOfRetries<maxNumberOfRetries){
    		//looking for a widget with matching tag value:
    		Widget widget = getWidgetWithMatchingTag(tag,value,state);
    		if(widget!=null){
    			StdActionCompiler ac = new AnnotatingActionCompiler();
    			executeAction(system, state, ac.pasteTextInto(widget, textToPaste, true));
    			// is waiting needed after the action has been executed?
    			return true;
    		}
    		else{
    			Util.pause(waitBetween);
    			state = getState(system);
    			numberOfRetries++;
    		}
    	}
    	System.out.println("Matching widget was not found, "+tag.toString()+"=" + value);
    	printTagValuesOfWidgets(tag,state);
    	return false;
    }

    /**
     * Iterates the widgets of the state until a widget with matching tag value is found.
     * The value is case sensitive.
     *
     * @param tag
     * @param value
     * @param state
     * @return the matching widget if found, null if not found
     */
    protected Widget getWidgetWithMatchingTag(Tag<?> tag, String value, State state){
        for(Widget widget:state){
            if(widget.get(tag, null)==null){
                // this widget did not have a value for the given tag
            }
            else if(widget.get(tag, null).toString().equals(value)){
                return widget;
            }
            else if(widget.get(tag, null).toString().contains(value)) {
                return widget;
            }
        }
        return null;
    }


    /**
     * Prints to system out all the widgets that have some value in the given tag.
     *
     * @param tag
     * @param state
     */
    protected void printTagValuesOfWidgets(Tag<?> tag, State state){
        for(Widget widget:state){
            if(widget.get(tag, null)==null){
                // this widget did not have a value for the given tag
            }
            else{
                System.out.println(tag.toString()+"=" + widget.get(tag, null).toString()+ "; Description of the widget="+widget.get(Tags.Desc, ""));
            }
        }
    }


    /**
     * Adds sliding actions (like scroll, drag and drop) to the given Set of Actions
     * @param actions
     * @param ac
     * @param scrollArrowSize
     * @param scrollThick
     * @param widget
     */
    @Deprecated
    protected void addSlidingActions(Set<Action> actions, StdActionCompiler ac, double scrollArrowSize, double scrollThick, Widget widget){
        Drag[] drags = null;
        //If there are scroll (drags/drops) actions possible
        if((drags = widget.scrollDrags(scrollArrowSize,scrollThick)) != null){
            //For each possible drag, create an action and add it to the derived actions
            for (Drag drag : drags){
                //Create a slide action with the Action Compiler, and add it to the set of derived actions
                actions.add(ac.slideFromTo(
                        new AbsolutePosition(Point.from(drag.getFromX(),drag.getFromY())),
                        new AbsolutePosition(Point.from(drag.getToX(),drag.getToY())),
                        widget
                ));

            }
        }
    }

    @Deprecated
    protected void addSlidingActions(Set<Action> actions, StdActionCompiler ac, double scrollArrowSize, double scrollThick, Widget widget, State state){
        addSlidingActions(actions, ac, scrollArrowSize, scrollThick, widget);
    }

    /**
     * Adds sliding actions into available actions of the DerivedActions for the given widget
     * and returns DerivedActions after that
     *
     * @param derived
     * @param ac
     * @param drags
     * @param widget
     * @return DerivedActions with added sliding actions in the available actions
     */
    protected DerivedActions addSlidingActions(DerivedActions derived, StdActionCompiler ac, Drag[] drags, Widget widget){

            //TODO creates multiple drag actions for one widget?
            //For each possible drag, create an action and add it to the derived actions
            for (Drag drag : drags){
                //Create a slide action with the Action Compiler, and add it to the set of derived actions
                derived.addAvailableAction(ac.slideFromTo(
                        new AbsolutePosition(Point.from(drag.getFromX(),drag.getFromY())),
                        new AbsolutePosition(Point.from(drag.getToX(),drag.getToY())),
                        widget
                ));

            }
        return derived;
    }

    /**
     * Check whether a widget is clickable
     * @param w
     * @return
     */
    protected boolean isClickable(Widget w){
        Role role = w.get(Tags.Role, Roles.Widget);
        if(Role.isOneOf(role, NativeLinker.getNativeClickableRoles()))
            return true;
        return false;
    }

    /**
     * Check whether a widget is typeable
     * @param w
     * @return
     */
    protected boolean isTypeable(Widget w){
        return NativeLinker.isNativeTypeable(w);
    }

    /**
     * Check whether widget w should be filtered based on
     * its title (matching the regular expression of the Dialog --> clickFilterPattern)
     * that is cannot be hit
     * @param w
     * @return
     */
    protected boolean isUnfiltered(Widget w){
        //Check whether the widget can be hit
        // If not, it should be filtered
        if(!Util.hitTest(w, 0.5, 0.5))
            return false;

        //Check whether the widget has an empty title or no title
        //If it has, it is unfiltered
        //Because it cannot match the regular expression of the Action Filter.
        String title = w.get(Title, "");
        if (title == null || title.isEmpty())
            return true;

        //If no clickFilterPattern exists, then create it
        //Get the clickFilterPattern from the regular expression provided by the tester in the Dialog
        if (this.clickFilterPattern == null)
            this.clickFilterPattern = Pattern.compile(settings().get(ConfigTags.ClickFilter), Pattern.UNICODE_CHARACTER_CLASS);

        //Check whether the title matches any of the clickFilterPatterns
        Matcher m = this.clickFilterMatchers.get(title);
        if (m == null){
            m = this.clickFilterPattern.matcher(title);
            this.clickFilterMatchers.put(title, m);
        }
        return !m.matches();
    }

    /**
     * Return a list of widgets that have the maximal Zindex
     * @param state
     * @return
     */
    protected List<Widget> getTopWidgets(State state){
        List<Widget> topWidgets = new ArrayList<>();
        double maxZIndex = state.get(Tags.MaxZIndex);
        for (Widget w : state)
            if (w.get(Tags.ZIndex) == maxZIndex)
                topWidgets.add(w);
        return topWidgets;
    }

    protected boolean isNOP(Action action){
        String as = action.toString();
        if (as != null && as.equals(NOP.NOP_ID))
            return true;
        else
            return false;
    }

    protected boolean isESC(Action action){
        Role r = action.get(Tags.Role, null);
        if (r != null && r.isA(ActionRoles.HitKey)){
            String desc = action.get(Tags.Desc, null);
            if (desc != null && desc.contains("VK_ESCAPE"))
                return true;
        }
        return false;
    }
    
    /**
     * Disconnect from Windows Remote Desktop without close the GUI session.
     */
    protected void disconnectRDP() {
		try {
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
		Util.pause(60);
    }
    
	// Java Coverage: It may happen that the SUT and its JVM unexpectedly close or stop responding
	// we use this variable to store after each action the last correct coverage
	private String lastCorrectJacocoCoverageFile = "";
	private String lastActionMergedCoverageFile = "";

	// Java Coverage: Save all JaCoCo sequence reports, to merge them at the end of the execution
	private Set<String> jacocoFiles = new HashSet<>();
	
	protected long startSequenceTime;
	protected long startRunTime;
	
	/**
	 * Copy settings protocolName build.xml file to jacoco directory
	 * Example: "bin/settings/protocolName/build.xml" file to "bin/jacoco/build.xml"
	 */
	protected void copyJacocoBuildFile() {
		// JaCoCo: Move settings protocolName build.xml file to jacoco directory
		try {
			// Copy "bin/settings/protocolName/build.xml" file to "bin/jacoco/build.xml"
			String protocolName = settings.get(ConfigTags.ProtocolClass,"").split("/")[0];
			File originalBuildFile = new File(Main.settingsDir + File.separator + protocolName + File.separator + "build.xml").getCanonicalFile();
			System.out.println("originalBuildFile: " + originalBuildFile);
			if(originalBuildFile.exists()) {
				File destbuildFile = new File(Main.testarDir + File.separator + "jacoco" + File.separator + "build.xml").getCanonicalFile();
				System.out.println("destbuildFile: " + destbuildFile);
				if(destbuildFile.exists()) {
					destbuildFile.delete();
				}
				FileUtils.copyFile(originalBuildFile, destbuildFile);
			}
		} catch (Exception e) {
			LogSerialiser.log("ERROR Trying to move settings protocolName build.xml file to jacoco directory",
			        LogSerialiser.LogLevel.Info);
			System.err.println("ERROR Trying to move settings protocolName build.xml file to jacoco directory");
		}
	}
	
	/**
	 * Extract and create JaCoCo action coverage report.
	 */
	protected void extractJacocoActionReport() {
	    try {
	        System.out.println("Extracting JaCoCo report for Action number: " + actionCount);

	        // Dump the JaCoCo Action report from the remote JVM
	        // Example: jacoco-upm_sequence_1_action_3.exec
	        String jacocoFileAction = JacocoFilesCreator.dumpAndGetJacocoActionFileName(Integer.toString(actionCount));

	        // If is not empty, save as last correct file in case the SUT crashes executing next actions
	        if(!jacocoFileAction.isEmpty() && new File(jacocoFileAction).exists()) {
	            lastCorrectJacocoCoverageFile = jacocoFileAction;
	        }

	        // Create the output JaCoCo Action report (Ex: "jacoco_reports/upm_sequence_1_action_3/report_jacoco.csv")
	        // And get a string that represents obtained coverage
	        String actionCoverage = JacocoFilesCreator.createJacocoActionReport(jacocoFileAction, Integer.toString(actionCount));
	        long  actionTime = System.currentTimeMillis() - startSequenceTime;
	        writeCoverageFile("Sequence | " + OutputStructure.sequenceInnerLoopCount +
	                " | actionnr | " + actionCount +
	                " | time | " + actionTime +
	                " | " + actionCoverage);

	        extractJacocoActionMergedReport(jacocoFileAction);

	    } catch (Exception e) {
	        LogSerialiser.log("ERROR Creating JaCoCo coverage for specific action: " + actionCount,
	                LogSerialiser.LogLevel.Info);
	        System.err.println("ERROR Creating JaCoCo coverage for specific action: " + actionCount);
	    }
	    extractStateModelMetrics();
	}

	/**
	 * Prepare and create the continuous action merge coverage report. 
	 * Always merge current action coverage with previous one (even previous sequences). 
	 * 
	 * @param jacocoFileAction
	 */
	private void extractJacocoActionMergedReport(String jacocoFileAction) {
	    // First one will not exists
	    if(lastActionMergedCoverageFile.isEmpty()) lastActionMergedCoverageFile = jacocoFileAction;

	    try {
	        System.out.println("Extracting JaCoCo Merged report for Action number: " + actionCount);
	        
	        if(new File(jacocoFileAction).length() > 0 && new File(lastActionMergedCoverageFile).length() > 0) {
	            String actionMergedJacocoFilename = OutputStructure.outerLoopOutputDir + File.separator + 
	                    "merged-jacoco-" + OutputStructure.executedSUTname +
	                    "_sequence_" + OutputStructure.sequenceInnerLoopCount +
	                    "_action_" + actionCount;
	            File actionMergedJacocoFile = new File(actionMergedJacocoFilename);

	            Set<String> filesToMerge = new HashSet<>(Arrays.asList(lastActionMergedCoverageFile, jacocoFileAction));
	            
	            MergeJacocoFiles mergeActionsJacoco = new MergeJacocoFiles();
	            mergeActionsJacoco.testarExecuteMojo(new ArrayList<>(filesToMerge), actionMergedJacocoFile);

	            long  runTime = System.currentTimeMillis() - startRunTime;
	            String iterativeActionMerge = JacocoFilesCreator.createJacocoActionMergedReport(actionMergedJacocoFile.getCanonicalPath(), Integer.toString(actionCount));
	            writeMergedCoverageFile("Merged Sequence | " + OutputStructure.sequenceInnerLoopCount +
	                    " | actionnr | " + actionCount +
	                    " | time | " + runTime +
	                    " | " + iterativeActionMerge);

	            lastActionMergedCoverageFile = actionMergedJacocoFilename;
	        }
	    } catch (Exception e) {
	        LogSerialiser.log("ERROR Creating JaCoCo Iterative Merged Coverage for specific action: " + actionCount,
	                LogSerialiser.LogLevel.Info);
	        System.err.println("ERROR Creating JaCoCo Iterative Merged Coverage for specific action: " + actionCount);
	    }
	}
	
	/**
	 * Extract and create JaCoCo sequence coverage report.
	 */
	protected void extractJacocoSequenceReport() {
		// Dump the sequence JaCoCo report from the remote JVM
		// Example: jacoco-upm_sequence_1.exec
		String jacocoFile = JacocoFilesCreator.dumpAndGetJacocoSequenceFileName();
		if(!jacocoFile.isEmpty() && new File(jacocoFile).exists()) {
			lastCorrectJacocoCoverageFile = jacocoFile;
		}

		// Add lastCorrectJacocoCoverageFile file to this set list, for merging at the end of the TESTAR run
		// If everything works correctly will be the sequence report, if not, last correct action jacoco.exec file
		jacocoFiles.add(lastCorrectJacocoCoverageFile);

		// Create the output JaCoCo report (Ex: "jacoco_reports/upm_sequence_1/report_jacoco.csv")
		// And get a string that represents obtained coverage
		String sequenceCoverage = JacocoFilesCreator.createJacocoSequenceReport(jacocoFile);
		long  sequenceTime = System.currentTimeMillis() - startSequenceTime;
		writeCoverageFile("SequenceTotal | " + OutputStructure.sequenceInnerLoopCount +
		        " | actionnr | " + actionCount +
		        " | time | " + sequenceTime +
		        " | " + sequenceCoverage);
		
		// reset value
		lastCorrectJacocoCoverageFile = "";
	}
	
	/**
	 * Merge all JaCoCo sequences files and create a run coverage merged report
	 */
	protected void extractJacocoRunReport() {
		try {
			// Merge all jacoco.exec sequence files
			MergeJacocoFiles mergeJacoco = new MergeJacocoFiles();
			File mergedJacocoFile = new File(OutputStructure.outerLoopOutputDir + File.separator + "jacoco_merged.exec");
			mergeJacoco.testarExecuteMojo(new ArrayList<>(jacocoFiles), mergedJacocoFile);
			
			// Then create the report that contains the coverage of all executed sequences (Ex: jacoco_reports/TOTAL_MERGED/report_jacoco.csv)
			// And get a string that represents obtained coverage
			String runCoverage = JacocoFilesCreator.createJacocoMergedReport(mergedJacocoFile.getCanonicalPath());
			long  runTime = System.currentTimeMillis() - startRunTime;
			writeCoverageFile("RunTotal | time | " + runTime + " | " + runCoverage);
		} catch (Exception e) {
			System.err.println(e.getMessage());
	        LogSerialiser.log("ERROR: Trying to MergeMojo feature with JaCoCo Files",
	                    LogSerialiser.LogLevel.Info);
			System.err.println("ERROR: Trying to MergeMojo feature with JaCoCo Files");
		}
	}
	
	/**
	 * Write the action coverage inside a coverage text file
	 * 
	 * @param coverageInformation
	 */
	private void writeCoverageFile(String coverageInformation) {
	    try {
	        String reportCoverageFile = new File(OutputStructure.outerLoopOutputDir).getCanonicalPath() + File.separator 
	                + OutputStructure.outerLoopName + "_coverageMetrics.txt";
	        FileWriter myWriter = new FileWriter(reportCoverageFile, true);
	        myWriter.write(coverageInformation + "\r\n");
	        myWriter.close();
	    } catch (IOException e) {
	        LogSerialiser.log("ERROR: Writing Coverage Metrics inside coverageMetrics text file",
	                LogSerialiser.LogLevel.Info);
	        System.err.println("ERROR: Writing Coverage Metrics inside coverageMetrics text file");
	        e.printStackTrace();
	    }
	}

	/**
	 * Write the action merged coverage inside a coverage text file
	 * 
	 * @param coverageMergedInformation
	 */
	private void writeMergedCoverageFile(String coverageMergedInformation) {
	    try {
	        String reportMergedCoverageFile = new File(OutputStructure.outerLoopOutputDir).getCanonicalPath() + File.separator 
	                + OutputStructure.outerLoopName + "_coverageMetricsMerged.txt";
	        FileWriter myWriter = new FileWriter(reportMergedCoverageFile, true);
	        myWriter.write(coverageMergedInformation + "\r\n");
	        myWriter.close();
	    } catch (IOException e) {
	        LogSerialiser.log("ERROR: Writing Merged Coverage Metrics inside coverageMetrics text file",
	                LogSerialiser.LogLevel.Info);
	        System.err.println("ERROR: Writing Merged Coverage Metrics inside coverageMetrics text file");
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Compress TESTAR output run report folder
	 */
	protected void compressOutputRunFolder() {
	    Util.compressFolder(OutputStructure.outerLoopOutputDir, Main.outputDir, OutputStructure.outerLoopName);
	}
	
	/**
	 * Obtain the IP address of the current host to create a folder inside destFolder, 
	 * then copy all TESTAR output run results inside created folder. 
	 * 
	 * This is an utility method intended to copy output results inside a file server shared folder, 
	 * used to save data of TESTAR experiments. 
	 * 
	 * @param destFolder
	 */
	protected void copyOutputToNewFolderUsingIpAddress(String destFolder) {
	    // Obtain the ip address of the host
	    // https://stackoverflow.com/a/38342964
	    String ipAddress = "127.0.0.1";
	    try(final DatagramSocket socket = new DatagramSocket()){
	        socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
	        ipAddress = socket.getLocalAddress().getHostAddress();
	    } catch (SocketException | UnknownHostException e) {
	        LogSerialiser.log("ERROR copyOutputToNewFolderUsingIpAddress: Obtaining host ip address",
	                LogSerialiser.LogLevel.Info);
	        System.err.println("ERROR copyOutputToNewFolderUsingIpAddress: Obtaining host ip address");
	        e.printStackTrace();
	    }

	    // Create a new directory inside desired destination using the ipAddress as name
	    String folderIpAddress = destFolder + File.separator + ipAddress + File.separator + settings.get(ConfigTags.ApplicationName, "");
	    try {
	        Files.createDirectories(Paths.get(folderIpAddress));
	    } catch (IOException e) {
	        LogSerialiser.log("ERROR copyOutputToNewFolderUsingIpAddress: Creating new folder with ip name",
	                LogSerialiser.LogLevel.Info);
	        System.err.println("ERROR copyOutputToNewFolderUsingIpAddress: Creating new folder with ip name");
	        e.printStackTrace();
	        return;
	    }
	    
	    // Copy run zip file to desired ip address output folder
	    File outputZipFile = new File(Main.outputDir + File.separator + OutputStructure.outerLoopName + ".zip");
	    try {
	        if(outputZipFile.exists()) {
	            File fileIpAddressOutput = new File(folderIpAddress + File.separator + ipAddress + "_" + outputZipFile.getName());
	            FileUtils.copyFile(outputZipFile, fileIpAddressOutput);
	            System.out.println(String.format("Sucessfull copy %s to %s", outputZipFile, fileIpAddressOutput));
	        }
	    } catch (IOException e) {
	        LogSerialiser.log("ERROR copyOutputToNewFolderUsingIpAddress: ERROR ZIP : " + outputZipFile,
	                LogSerialiser.LogLevel.Info);
	        System.err.println("ERROR copyOutputToNewFolderUsingIpAddress: ERROR ZIP : " + outputZipFile);
	        e.printStackTrace();
	    }

	    copyCoverageMetricsToFolder(destFolder, ipAddress);
	    copyStateModelMetricsToFolder(destFolder, ipAddress);
	}

	/**
	 * Copy Coverage Metrics file inside the destination Folder. 
	 * 
	 * @param destFolder
	 * @param ipAddress
	 */
	private void copyCoverageMetricsToFolder(String destFolder, String ipAddress) {
	    // Create a new directory inside desired destination to store all metrics
	    String metricsFolder = destFolder + File.separator + "metrics" + File.separator + settings.get(ConfigTags.ApplicationName, "");
	    try {
	        Files.createDirectories(Paths.get(metricsFolder));
	    } catch (IOException e) {
	        LogSerialiser.log("ERROR copyCoverageMetricsToFolder: Creating new folder for metrics : " + metricsFolder,
	                LogSerialiser.LogLevel.Info);
	        System.err.println("ERROR copyCoverageMetricsToFolder: Creating new folder for metrics : " + metricsFolder);
	        e.printStackTrace();
	        return;
	    }

	    File srcMetrics = new File(OutputStructure.outerLoopOutputDir + File.separator + OutputStructure.outerLoopName + "_coverageMetrics.txt");
	    File destMetrics = new File(metricsFolder + File.separator + ipAddress + "_" + OutputStructure.outerLoopName + "_coverageMetrics.txt");
	    try {
	        FileUtils.copyFile(srcMetrics, destMetrics);
	        System.out.println(String.format("Sucessfull copy %s to %s", srcMetrics, destMetrics));
	    } catch (IOException e) {
	        LogSerialiser.log("ERROR copyCoverageMetricsToFolder: ERROR Metrics : " + destMetrics,
	                LogSerialiser.LogLevel.Info);
	        System.err.println("ERROR copyCoverageMetricsToFolder: ERROR Metrics : " + destMetrics);
	        e.printStackTrace();
	    }

	    File srcMergedMetrics = new File(OutputStructure.outerLoopOutputDir + File.separator + OutputStructure.outerLoopName + "_coverageMetricsMerged.txt");
	    File destMergedMetrics = new File(metricsFolder + File.separator + ipAddress + "_" + OutputStructure.outerLoopName + "_coverageMetricsMerged.txt");
	    try {
	        FileUtils.copyFile(srcMergedMetrics, destMergedMetrics);
	        System.out.println(String.format("Sucessfull copy %s to %s", srcMergedMetrics, destMergedMetrics));
	    } catch (IOException e) {
	        LogSerialiser.log("ERROR copyCoverageMetricsToFolder: ERROR Merged Metrics : " + destMergedMetrics,
	                LogSerialiser.LogLevel.Info);
	        System.err.println("ERROR copyCoverageMetricsToFolder: ERROR Merged Metrics : " + destMergedMetrics);
	        e.printStackTrace();
	    }
	}

	protected void extractStateModelMetrics() {
	    String resultAbstractStates = "AbstractStates " + stateModelManager.queryStateModel("select count(*) from AbstractState");
	    String resultAbstractActions = "AbstractActions " + stateModelManager.queryStateModel("select count(*) from AbstractAction");
	    String resultUnvisitedActions = "UnvisitedActions " + stateModelManager.queryStateModel("select count(*) from UnvisitedAbstractAction");
	    String resultConcreteStates = "ConcreteStates " + stateModelManager.queryStateModel("select count(*) from ConcreteState");
	    String resultConcreteActions = "ConcreteActions " + stateModelManager.queryStateModel("select count(*) from ConcreteAction");
	    writeStateModelMetrics("SequenceTotal | " + OutputStructure.sequenceInnerLoopCount +
	            " | actionnr | " + actionCount +
	            " | " + resultAbstractStates +
	            " | " + resultAbstractActions +
	            " | " + resultUnvisitedActions +
	            " | " + resultConcreteStates +
	            " | " + resultConcreteActions
	            );
	}

	/**
	 * Write the state model metrics inside a StateModelMetrics text file
	 * 
	 * @param coverageInformation
	 */
	private void writeStateModelMetrics(String statemodelInformation) {
	    try {
	        String reportCoverageFile = new File(OutputStructure.outerLoopOutputDir).getCanonicalPath() + File.separator 
	                + OutputStructure.outerLoopName + "_stateModelMetrics.txt";
	        FileWriter myWriter = new FileWriter(reportCoverageFile, true);
	        myWriter.write(statemodelInformation + "\r\n");
	        myWriter.close();
	    } catch (IOException e) {
	        LogSerialiser.log("ERROR: Writing StateModel Metrics inside stateModelMetrics text file",
	                LogSerialiser.LogLevel.Info);
	        System.err.println("ERROR: Writing StateModel Metrics inside stateModelMetrics text file");
	        e.printStackTrace();
	    }
	}

	/**
	 * Copy StateModel Metrics file inside the destination Folder. 
	 * 
	 * @param destFolder
	 * @param ipAddress
	 */
	private void copyStateModelMetricsToFolder(String destFolder, String ipAddress) {
	    // Create a new directory inside desired destination to store all metrics
	    String metricsFolder = destFolder + File.separator + "metrics" + File.separator + settings.get(ConfigTags.ApplicationName, "");
	    try {
	        Files.createDirectories(Paths.get(metricsFolder));
	    } catch (IOException e) {
	        LogSerialiser.log("ERROR copyStateModelMetricsToFolder: Creating new folder for metrics : " + metricsFolder,
	                LogSerialiser.LogLevel.Info);
	        System.err.println("ERROR copyStateModelMetricsToFolder: Creating new folder for metrics : " + metricsFolder);
	        e.printStackTrace();
	        return;
	    }

	    File srcMetrics = new File(OutputStructure.outerLoopOutputDir + File.separator + OutputStructure.outerLoopName + "_stateModelMetrics.txt");
	    File destMetrics = new File(metricsFolder + File.separator + ipAddress + "_" + OutputStructure.outerLoopName + "_stateModelMetrics.txt");
	    try {
	        FileUtils.copyFile(srcMetrics, destMetrics);
	        System.out.println(String.format("Sucessfull copy %s to %s", srcMetrics, destMetrics));
	    } catch (IOException e) {
	        LogSerialiser.log("ERROR copyStateModelMetricsToFolder: ERROR Metrics : " + destMetrics,
	                LogSerialiser.LogLevel.Info);
	        System.err.println("ERROR copyStateModelMetricsToFolder: ERROR Metrics : " + destMetrics);
	        e.printStackTrace();
	    }

	    File srcMergedMetrics = new File(OutputStructure.outerLoopOutputDir + File.separator + OutputStructure.outerLoopName + "_stateModelMetrics.txt");
	    File destMergedMetrics = new File(metricsFolder + File.separator + ipAddress + "_" + OutputStructure.outerLoopName + "_stateModelMetrics.txt");
	    try {
	        FileUtils.copyFile(srcMergedMetrics, destMergedMetrics);
	        System.out.println(String.format("Sucessfull copy %s to %s", srcMergedMetrics, destMergedMetrics));
	    } catch (IOException e) {
	        LogSerialiser.log("ERROR copyStateModelMetricsToFolder: ERROR Merged Metrics : " + destMergedMetrics,
	                LogSerialiser.LogLevel.Info);
	        System.err.println("ERROR copyStateModelMetricsToFolder: ERROR Merged Metrics : " + destMergedMetrics);
	        e.printStackTrace();
	    }
	}
}
