/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2022 Open Universiteit - www.ou.nl
 * Copyright (c) 2020 - 2022 Universitat Politecnica de Valencia - www.upv.es
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

import org.apache.commons.io.FileUtils;
import org.testar.DerivedActions;
import org.testar.OutputStructure;
import org.testar.monkey.Drag;
import org.testar.monkey.Main;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.ActionRoles;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.NOP;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.plugin.NativeLinker;
import org.testar.plugin.OperatingSystems;
import org.testar.monkey.ConfigTags;
import org.testar.protocols.experiments.WriterExperiments;
import org.testar.serialisation.LogSerialiser;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenericUtilsProtocol extends ClickFilterLayerProtocol {

    /**
     * Allows using the function with a tag name,
     * so the user does not need to know where in TESTAR package that specific tag is found.
     *
     * @param tagName
     * @param value
     * @param state
     * @param system
     * @param maxNumberOfRetries
     * @param waitBetween
     * @return
     */
    protected boolean waitAndLeftClickWidgetWithMatchingTag(String tagName, String value, State state, SUT system, int maxNumberOfRetries, double waitBetween){
    	// If the state has no children return false
    	// This may happen because the state has no GUI elements, for example a XML page
    	if(state.childCount() == 0) {
    		return false;
    	}

        if(NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER)){
            if(!tagName.startsWith("Web")){
                tagName = "Web"+tagName;
            }
        }
        for(Tag tag:state.child(0).tags()){
            if(tag.name().equalsIgnoreCase(tagName)){
                return waitAndLeftClickWidgetWithMatchingTag(tag,value,state,system,maxNumberOfRetries,waitBetween);
            }
        }
        System.out.println("Matching widget was not found, "+tagName+"=" + value);
        return false;
    }

    /**
     * This method waits until a widget with multiple matching tag values is found or the retry limit is reached.
     * If a widget that matches all specified tag values is found, the left mouse button is clicked on it and the
     * return value is true. Else returns false
     *
     * @param tagValues Map where the keys are tag names, and the values are the values these tags should have.
     * @param state
     * @param system needed for updating the state between retries
     * @param maxNumberOfRetries int number of times
     * @param waitBetween double in seconds
     */
    protected boolean waitAndLeftClickWidgetWithMatchingTags(Map<String,String> tagValues, State state, SUT system, int maxNumberOfRetries, double waitBetween) {
        int numberOfRetries = 0;
        while (numberOfRetries<maxNumberOfRetries){
            Widget widget = getWidgetWithMatchingTags(tagValues, state);
            if (widget != null) {
                StdActionCompiler ac = new AnnotatingActionCompiler();
                executeAction(system, state, ac.leftClickAt(widget));
                return true;
            }
            else {
                Util.pause(waitBetween);
                state = getState(system);
                numberOfRetries++;
            }
        }
        return false;
    }

    /**
     * This method waits until the widget with a matching Tag value (case sensitive) is found or the retry limit is reached.
     * If a matching widget is found, left mouse button is clicked on it and return value is true.
     * Else returns false
     *
     * @param tag for example: org.testar.alayer.Tags.Title
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
     * Allows using the function with a tag name,
     * so the user does not need to know where in TESTAR package that specific tag is found.
     *
     * @param tagName
     * @param value
     * @param textToType
     * @param state
     * @param system
     * @param maxNumberOfRetries
     * @param waitBetween
     * @return
     */
    protected boolean waitLeftClickAndTypeIntoWidgetWithMatchingTag(String tagName, String value, String textToType, State state, SUT system, int maxNumberOfRetries, double waitBetween){
    	// If the state has no children return false
    	// This may happen because the state has no GUI elements, for example a XML page
    	if(state.childCount() == 0) {
    		return false;
    	}

        if(NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER)){
            if(!tagName.startsWith("Web")){
                tagName = "Web"+tagName;
            }
        }
        for(Tag tag:state.child(0).tags()){
            if(tag.name().equalsIgnoreCase(tagName)){
                return waitLeftClickAndTypeIntoWidgetWithMatchingTag(tag,value,textToType,state,system,maxNumberOfRetries,waitBetween);
            }
        }
        System.out.println("Matching widget was not found, "+tagName+"=" + value);
        return false;
    }

    /**
     * This method waits until a widget matching multiple tag values is found or the retry limit is reached.
     * If a matching widget is found, the left mouse button is clicked on it, the given text is typed into it, and the return value is true.
     * Else it returns false
     *
     * @param tagValues Map where the keys are tag names, and the values are the values these tags should have.
     * @param textToType types the given text by replacing the existing text
     * @param state
     * @param system needed for updating the state between retries
     * @param maxNumberOfRetries int number of times
     * @param waitBetween double in seconds
     * @return
     */
    protected boolean waitLeftClickAndTypeIntoWidgetWithMatchingTags(Map<String,String> tagValues, String textToType, State state, SUT system, int maxNumberOfRetries, double waitBetween) {
        int numberOfRetries = 0;
        while(numberOfRetries<maxNumberOfRetries){
            Widget widget = getWidgetWithMatchingTags(tagValues,state);
            if(widget!=null){
                StdActionCompiler ac = new AnnotatingActionCompiler();
                executeAction(system,state,ac.clickTypeInto(widget, textToType, true));
                return true;
            }
            else {
                Util.pause(waitBetween);
                state = getState(system);
                numberOfRetries++;
            }
        }
        return false;
    }

    /**
     * This method waits until the widget with a matching Tag value (case sensitive) is found or the retry limit is reached.
     * If a matching widget is found, left mouse button is clicked on it, the given text is typed into it, and return value is true.
     * Else returns false
     *
     * @param tag for example: org.testar.alayer.Tags.Title
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
     *
     * Allows using the function with a tag name,
     * so the user does not need to know where in TESTAR package that specific tag is found.
     *
     * @param tagName
     * @param value
     * @param textToPaste
     * @param state
     * @param system
     * @param maxNumberOfRetries
     * @param waitBetween
     * @return
     */
    protected boolean waitLeftClickAndPasteIntoWidgetWithMatchingTag(String tagName, String value, String textToPaste, State state, SUT system, int maxNumberOfRetries, double waitBetween){
    	// If the state has no children return false
    	// This may happen because the state has no GUI elements, for example a XML page
    	if(state.childCount() == 0) {
    		return false;
    	}

        if(NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER)){
            if(!tagName.startsWith("Web")){
                tagName = "Web"+tagName;
            }
        }
        for(Tag tag:state.child(0).tags()){
            if(tag.name().equalsIgnoreCase(tagName)){
                return waitLeftClickAndPasteIntoWidgetWithMatchingTag(tag,value,textToPaste,state,system,maxNumberOfRetries,waitBetween);
            }
        }
        System.out.println("Matching widget was not found, "+tagName+"=" + value);
        return false;
    }

   /**
     * This method waits until a widget matching multiple tag values is found or the retry limit is reached.
     * If a matching widget is found, the left mouse button is clicked on it, the given text is pasted into it, and the return value is true.
     * Else it returns false
     *
     * @param tagValues Map where the keys are tag names, and the values are the values these tags should have.
     * @param textToPaste pastes the given text by replacing the existing text
     * @param state
     * @param system needed for updating the state between retries
     * @param maxNumberOfRetries int number of times
     * @param waitBetween double in seconds
     * @return
     */
    protected boolean waitLeftClickAndPasteIntoWidgetWithMatchingTags(Map<String,String> tagValues, String textToPaste, State state, SUT system, int maxNumberOfRetries, double waitBetween) {
        int numberOfRetries = 0;
        while(numberOfRetries<maxNumberOfRetries){
            Widget widget = getWidgetWithMatchingTags(tagValues,state);
            if(widget!=null){
                StdActionCompiler ac = new AnnotatingActionCompiler();
                executeAction(system,state,ac.pasteTextInto(widget, textToPaste, true));
                return true;
            }
            else {
                Util.pause(waitBetween);
                state = getState(system);
                numberOfRetries++;
            }
        }
        return false;
    }

    /**
     * This method waits until the widget with a matching Tag value (case sensitive) is found or the retry limit is reached.
     * If a matching widget is found, left mouse button is clicked on it, the given text is pasted into it, and return value is true.
     * Else returns false
     *
     * @param tag for example: org.testar.alayer.Tags.Title
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
     * Allows using the function with a tag name,
     * so the user does not need to know where in TESTAR package that specific tag is found.
     *
     * @param tagName
     * @param value
     * @param state
     * @return
     */
    protected Widget getWidgetWithMatchingTag(String tagName, String value, State state){
    	// If the state has no children return null
    	// This may happen because the state has no GUI elements, for example a XML page
    	if(state.childCount() == 0) {
    		return null;
    	}

        if(NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER)){
            if(!tagName.startsWith("Web")){
                tagName = "Web"+tagName;
            }
        }
        for(Tag tag:state.child(0).tags()){
            if(tag.name().equalsIgnoreCase(tagName)){
                return getWidgetWithMatchingTag(tag, value, state);
            }
        }
        return null;
    }

     /**
     * Finds a widget that matches all specified tag values
     *
     * @param tagValues A map of tags. The keys are the tag names and the values are the values these tags should have
     * @param state
     * @return
     */
    protected Widget getWidgetWithMatchingTags(Map<String,String> tagValues, State state) {
    	// If the state has no children return null
    	// This may happen because the state has no GUI elements, for example a XML page
    	if(state.childCount() == 0) {
    		return null;
    	}

        // First make a lookup table to find Tags for each tag name
        Map<String,Tag<?>> tagLookup = new HashMap<String,Tag<?>>();
        for (String tagName : tagValues.keySet()) {

            if(NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER) &&
            ! tagName.startsWith("Web") ) {
                tagName = "Web" + tagName;
            }

            boolean tagFound = false;

            for ( Tag tag : state.child(0).tags() ) {
                if ( tag.name().equalsIgnoreCase(tagName) ) {
                    tagLookup.put(tagName,tag);
                    tagFound = true;
                    break;
                }
            }
            if ( ! tagFound ) {
                System.out.println("Error: could not find tag for tag name " + tagName);
                return null;
            }
        }

        // Then check the tags of each widget to see if they match the tag values we are
        // looking for.
        for (Widget widget : state) {
            Vector<String> tagsFound = new Vector<String>();

            HashMap<String, String> webTagValues = new HashMap<>();
            webTagValues.putAll(tagValues);

            for (String tagName : tagValues.keySet()) {

            	if(NativeLinker.getPLATFORM_OS().contains(OperatingSystems.WEBDRIVER) &&
            			! tagName.startsWith("Web") ) {
            		String value = tagValues.get(tagName);
            		webTagValues.remove(tagName);
            		tagName = "Web" + tagName;
            		webTagValues.put(tagName, value);
            	}

                Tag tag = tagLookup.get(tagName);
                String value = webTagValues.get(tagName);
                if (    widget.get(tag, null) != null &&
                        widget.get(tag, null).toString().equals(value) )  {
                    tagsFound.add(tagName);
                }
            }

            if ( tagsFound.size() == webTagValues.keySet().size() ) {
                return widget;
            }
        }

        return null;
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

        Boolean isFiltered = false;

        for(String tagToFilter : settings.get(ConfigTags.TagsToFilter)){
            String tagValue = "";
            // First finding the Tag that matches the TagsToFilter string, then getting the value of that Tag:
            for(Tag tag : w.tags()){
                if(tag.name().equals(tagToFilter)){
                    tagValue = w.get(tag, "");
                    break;
                    //System.out.println("DEBUG: tag found, "+tagToFilter+"="+tagValue);
                }
            }

            //Check whether the Tag value is empty or null
            //If it is, it is unfiltered
            //Because it cannot match the regular expression of the Action Filter.
            if (tagValue == null || tagValue.isEmpty())
                continue; //no action, isFiltered is still false (cannot return directly if other Tags are checked)

            //If no clickFilterPattern exists, then create it
            //Get the clickFilterPattern from the regular expression provided by the tester in the Dialog
            if (this.clickFilterPattern == null)
                this.clickFilterPattern = Pattern.compile(settings().get(ConfigTags.ClickFilter), Pattern.UNICODE_CHARACTER_CLASS);

            //Check whether the title matches any of the clickFilterPatterns
            Matcher m = this.clickFilterMatchers.get(tagValue);
            if (m == null){
                m = this.clickFilterPattern.matcher(tagValue);
                this.clickFilterMatchers.put(tagValue, m);
            }
            isFiltered = m.matches();
            // if filtered, no need to check if it should be filtered multiple times:
            if(isFiltered) return(!isFiltered); //method is for is-UN-filtered

        }
        //method is for is-UN-filtered
        return !isFiltered;
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

    @Override
    protected Set<Action> preSelectAction(SUT system, State state, Set<Action> actions){
    	if(actions.isEmpty()) {
    		actions = retryDeriveAction(system, 5, 1);
    	}
    	return super.preSelectAction(system, state, actions);
    }

    /**
     * If SUT is slow rendering the GUI elements, this retry method may help to
     * to wait and obtain the SUT state and derive SUT actions.
     * User can indicate the number of retries and seconds to wait.
     *
     * @param system
     * @param maxRetries
     * @param waitingSeconds
     * @return
     */
    protected Set<Action> retryDeriveAction(SUT system, int maxRetries, int waitingSeconds){
    	for(int i = 0; i < maxRetries; i++) {
    		System.out.println("DEBUG: retryDeriveAction");
    		Util.pause(waitingSeconds);
    		State newState = getState(system);
    		Set<Action> newActions = deriveActions(system, newState);
    		if(!newActions.isEmpty()) {
    			return newActions;
    		}
    	}
    	return new HashSet<>();
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

        // Create a folder inside the centralized file server and copy the metrics results
    }

    /**
     * Obtain the IP address of the current host to create a folder inside destFolder,
     * then copy chosen TESTAR output file inside created folder.
     *
     * This is an utility method intended to copy output results inside a file server shared folder,
     * used to save data of TESTAR experiments.
     *
     * @param destFolder
     * @param outputFile
     */
    protected void copyOutputFileToNewFolderUsingIpAddress(String destFolder, File outputFile) {
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

        // Copy file to desired ip address output folder
//        File outputFile = new File(Main.outputDir + File.separator + filename);
        try {
            if(outputFile.exists()) {
                File fileIpAddressOutput = new File(folderIpAddress + File.separator + ipAddress + "_" + outputFile.getName());
                FileUtils.copyFile(outputFile, fileIpAddressOutput, true); //copy and replace
                System.out.println(String.format("Sucessfull copy %s to %s", outputFile, fileIpAddressOutput));
            }
        } catch (IOException e) {
            LogSerialiser.log("ERROR copyOutputFileToNewFolderUsingIpAddress: ERROR FILE : " + outputFile,
                    LogSerialiser.LogLevel.Info);
            System.err.println("ERROR copyOutputFileToNewFolderUsingIpAddress: ERROR FILE : " + outputFile);
            e.printStackTrace();
        }

        // Create a folder inside the centralized file server and copy the metrics results
    }
}
