/***************************************************************************************************
 *
 * Copyright (c) 2020 Open Universiteit - www.ou.nl
 * Copyright (c) 2020 Universitat Politecnica de Valencia - www.upv.es
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
import nl.ou.testar.StateModel.Difference.StateModelDifferenceManager;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.fruit.Drag;
import org.fruit.Pair;
import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.ActionRoles;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.NOP;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Main;
import org.testar.OutputStructure;
import org.testar.json.object.StateModelDifferenceJsonObject;
import org.testar.pkm.PkmRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    protected void addSlidingActions(Set<Action> actions, StdActionCompiler ac, double scrollArrowSize, double scrollThick, Widget widget, State state){
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
     * UNDER DEVELOPMENT - Best option is to use manual State Model Difference from GUI - State Model tab.
     * 
     * Automatically calculate the State Model Difference report if
     * StateModelDifferenceAutomaticReport test.setting is enabled.
     * 
     * Prioritize test settings parameters (PreviousApplicationName & PreviousApplicationVersion).
     * 
     * If no PreviousApplicationName specified, use current ApplicationName.
     * If no PreviousApplicationVersion specified, try to decrease Integer or Double ApplicationVersion.
     */
    protected StateModelDifferenceJsonObject automaticStateModelDifference() {
    	
		// Create the JSON Object that contains the information about State Model Difference
		StateModelDifferenceJsonObject stateModelDifferenceJsonObject = new StateModelDifferenceJsonObject();
    	
    	if(settings.get(ConfigTags.Mode) == Modes.Generate 
    			&& settings.get(ConfigTags.StateModelEnabled, false)
    			&& settings.get(ConfigTags.StateModelDifferenceAutomaticReport, false)
    			&& !decoderExceptionThrown) {
    		
    		try {
    			// Define current State Model version
    			String currentApplicationName = settings.get(ConfigTags.ApplicationName,"");
    			String currentVersion = settings.get(ConfigTags.ApplicationVersion,"");
    			Pair<String,String> currentStateModel = new Pair<>(currentApplicationName, currentVersion);

    			// By default we determine that we want to compare same ApplicationName
    			String previousApplicationName = currentApplicationName;

    			// Check if user selects a specific Application Name of previous State Model
    			if(!settings.get(ConfigTags.PreviousApplicationName,"").isEmpty()) {
    				previousApplicationName = settings.get(ConfigTags.PreviousApplicationName,"");
    			}

    			String previousVersion = "";

    			// Check if user selects a specific Application Version of previous State Model
    			if(!settings.get(ConfigTags.PreviousApplicationVersion,"").isEmpty()) {
    				previousVersion = settings.get(ConfigTags.PreviousApplicationVersion,"");
    			} else {
    				// Try automatic calculate version decrease
    				// Do we want to automatically compare in this way ?
    				// Or access to database and check all existing versions < currentVersion ?
    				if(StringUtils.isNumeric(currentVersion)) {
    					previousVersion = String.valueOf(Integer.parseInt(currentVersion) - 1);
    				}
    				else if (Pattern.matches("([0-9]*)\\.([0-9]*)", currentVersion)) {
    					previousVersion = String.valueOf(Double.parseDouble(currentVersion) - 1);
    				}
    				else {
    					System.out.println("WARNING: State Model Difference could not calculate previous application version automatically");
    					System.out.println("Try to use manual State Model Difference");
    					
    					// We cannot obtain previous version, finish
    					return stateModelDifferenceJsonObject;
    				}
    			}

    			Pair<String,String> previousStateModel = new Pair<>(previousApplicationName, previousVersion);

    			// Obtain Database Configuration, from Settings by default
    			Config config = new Config();
    			config.setConnectionType(settings.get(ConfigTags.DataStoreType,""));
    			config.setServer(settings.get(ConfigTags.DataStoreServer,""));
    			config.setDatabaseDirectory(settings.get(ConfigTags.DataStoreDirectory,""));
    			config.setDatabase(settings.get(ConfigTags.DataStoreDB,""));
    			config.setUser(settings.get(ConfigTags.DataStoreUser,""));
    			config.setPassword(settings.get(ConfigTags.DataStorePassword,""));

    			// State Model Difference Report Directory Name
    			String dirName = OutputStructure.outerLoopOutputDir  + File.separator + "StateModelDifference_"
    					+ previousStateModel.left() + "_" + previousStateModel.right() + "_vs_"
    					+ currentStateModel.left() + "_" + currentStateModel.right();

    			// Execute the State Model Difference to create an HTML report
    			StateModelDifferenceManager modelDifferenceManager = new StateModelDifferenceManager(config, dirName);
    			modelDifferenceManager.calculateModelDifference(config, previousStateModel, currentStateModel);
    			
    			// Update State Model Difference JSON Object with the Difference Report information
    			stateModelDifferenceJsonObject.setPreviousStateModelAppName(previousApplicationName);
    			stateModelDifferenceJsonObject.setPreviousStateModelAppVersion(previousVersion);
    			stateModelDifferenceJsonObject.setExistsPreviousStateModel(modelDifferenceManager.existsPreviousStateModel());
    			stateModelDifferenceJsonObject.setNumberDisappearedAbstractStates(modelDifferenceManager.getNumberDisappearedAbstractStates());
    			stateModelDifferenceJsonObject.setNumberNewAbstractStates(modelDifferenceManager.getNumberNewAbstractStates());
    			stateModelDifferenceJsonObject.setStateModelDifferenceReport(dirName);

    		} catch (Exception e) {
    			System.err.println("ERROR: Trying to create an automatic State Model Difference");
    			e.printStackTrace();
    		}
    	}
    	
    	return stateModelDifferenceJsonObject;
    }
    
    /**
     * Insert TestResults Artefact in the PKM and return the ArtefactId. 
     * 
     * @param artefactTestResults
     * @return artefactId
     */
    protected String insertTestResultsPKM(String artefactTestResults) {
    	// TODO: Allow Record mode when Listening mode implemented
    	if(settings.get(ConfigTags.Mode) == Modes.Generate && !decoderExceptionThrown) {
    	    
    	    String artefactIdTestResults = PkmRequest.postArtefactTestResults(settings, artefactTestResults);
    	    
    	    if(artefactIdTestResults != "TestResultsErrorArtefactId") {
    	        return artefactIdTestResults;
    	    }
    	    
    	    decoderExceptionThrown = true;
    	    System.err.println("ERROR! Trying to Insert TestResults Artefact");
    	}
    	
    	return "TestResultsErrorArtefactId";
    }
    
    /**
     * Insert StateModel Artefact in the PKM and return the ArtefactId. 
     * 
     * @param artefactStateModel
     * @return artefactId
     */
    protected String insertStateModelPKM(String artefactStateModel) {
        // TODO: Allow Record mode when Listening mode implemented
        if(settings.get(ConfigTags.Mode) == Modes.Generate && !decoderExceptionThrown) {
            
            String artefactIdStateModel = PkmRequest.postArtefactStateModel(settings, artefactStateModel);
            
            if(artefactIdStateModel != "StateModelErrorArtefactId") {
                return artefactIdStateModel;
            }
            
            decoderExceptionThrown = true;
            System.err.println("ERROR! Trying to Insert StateModel Artefact");
        }
        
        return "StateModelErrorArtefactId";
    }
	
    /**
     * DECODER needs a Map to have a relation between the TESTAR TestResults output results 
     * and the TESTAR TestResults ArtefactId. 
     * This method renames TESTAR TestResults OutputRunFolder with ArtefactId name.
     * 
     * @param artefactIdTestResults
     */
    protected void updateOutputRunFolder(String artefactIdTestResults) {

        // If we are not in Generate Mode we do not want to move the output folder
        if(settings.get(ConfigTags.Mode) != Modes.Generate || decoderExceptionThrown) {
            return;
        }
        try {
            File artefactOutputFolder = new File(Main.testarDir + artefactIdTestResults + File.separator + "output" + File.separator);
            File outputRunFile = new File(OutputStructure.outerLoopOutputDir);
            FileUtils.moveDirectory(outputRunFile, artefactOutputFolder);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ERROR moving OutputRunFolder with ArtefactId");
        }

        // If permanent JettyServer exists, copy the folder inside resources directory
        try {
            String jettyPath = "JettyServer" + File.separator + "bin" + File.separator + "resources" + File.separator;
            if(new File(Main.testarDir + jettyPath).exists()) {
                FileUtils.copyDirectory(new File(Main.testarDir + artefactIdTestResults), new File(Main.testarDir + jettyPath + File.separator + artefactIdTestResults + File.separator));
            }
        } catch(Exception e) {
            System.out.println("ERROR preparing JettyServer resources folder");
        }
    }
}
