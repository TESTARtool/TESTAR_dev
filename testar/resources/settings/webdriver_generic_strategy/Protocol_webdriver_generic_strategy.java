/**
* Copyright (c) 2018 - 2021 Open Universiteit - www.ou.nl
* Copyright (c) 2019 - 2021 Universitat Politecnica de Valencia - www.upv.es
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
*
*/

import org.testar.CodingManager;
import org.testar.OutputStructure;
import org.testar.RandomActionSelector;
import org.testar.SutVisualization;
import org.testar.managers.InputDataManager;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.DefaultProtocol;
import org.testar.monkey.Main;
import org.testar.settings.Settings;
import org.testar.monkey.alayer.Shape;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.ActionRoles;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.devices.KBKeys;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.plugin.NativeLinker;
import org.testar.plugin.OperatingSystems;
import org.testar.protocols.WebdriverProtocol;
import parsing.ParseUtil;
import writers.CSVFileWriter;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static org.testar.OutputStructure.outerLoopName;
import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;

public class Protocol_webdriver_generic_strategy extends WebdriverProtocol
{
    private ParseUtil               parseUtil;
    private RandomActionSelector    selector;
    private boolean useRandom = false;
    private Map<String, Integer>    actionsExecuted      = new HashMap<String, Integer>();
    private Map<String, Integer>    debugActionsExecuted      = new HashMap<String, Integer>();
    private ArrayList<String> operatingSystems = new ArrayList<>();
    private String startTimestamp = "";
    private String start_epoch = "";
    private String end_epoch = "";
    private int numFieldsFilled = -1;

    @Override
    protected void buildStateActionsIdentifiers(State state, Set<Action> actions)
    {
        CodingManager.buildIDs(state, actions);
        for(Action action : actions)
        {
            // Radio buttons are special form elements
            // We only want to select one radio button widget per group
            // For this reason, we use the widget web name property to identify all actions in a group as the same
            if(action.get(Tags.OriginWidget) != null && action.get(Tags.OriginWidget).get(WdTags.WebType, "").equals("radio"))
            {

                Widget widget = action.get(Tags.OriginWidget);
                String widgetWebName = widget.get(WdTags.WebName, "");

                String collisionId = CodingManager.lowCollisionID(state.get(Tags.AbstractIDCustom)
                        + widgetWebName
                        + action.get(Tags.Role));

                String radioActionAbstractId = CodingManager.ID_PREFIX_ACTION
                        + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM
                        + collisionId;

                action.set(Tags.AbstractIDCustom, radioActionAbstractId);
            }
            // For other elements, use the widget abstract identifier to identify independent actions
            // The widget abstract identifier relies on the AbstractStateAttributes (WebWidgetId)
            else if (action.get(Tags.OriginWidget) != null)
            {
                Widget widget = action.get(Tags.OriginWidget);

                String collisionId = CodingManager.lowCollisionID(state.get(Tags.AbstractIDCustom)
                        + widget.get(Tags.AbstractIDCustom)
                        + action.get(Tags.Role));

                String elementActionAbstractId = CodingManager.ID_PREFIX_ACTION
                        + CodingManager.ID_PREFIX_ABSTRACT_CUSTOM
                        + collisionId;

                action.set(Tags.AbstractIDCustom, elementActionAbstractId);
            }
        }
    }

    @Override
    protected void initialize(Settings settings)
    {
        super.initialize(settings);

        useRandom = settings.get(ConfigTags.StrategyFile).equals("");
        if (useRandom)
            selector = new RandomActionSelector();
        else
            parseUtil = new ParseUtil(settings.get(ConfigTags.StrategyFile));
    
        for(OperatingSystems OS : NativeLinker.getPLATFORM_OS())
            operatingSystems.add(OS.toString());
    }

    @Override
    protected void beginSequence(SUT system, State state)
    {
        super.beginSequence(system, state);
        state.remove(Tags.PreviousAction);
        state.remove(Tags.PreviousActionID);
        startTimestamp = Long.toString(System.currentTimeMillis());
    }
    
    @Override
    protected State getState(SUT system) throws StateBuildException
    {
        State state = super.getState(system);
        if(latestState == null)
            state.set(Tags.StateChanged, true);
        else
        {
            String previousStateID = latestState.get(Tags.AbstractIDCustom);
            boolean stateChanged = ! previousStateID.equals(state.get(Tags.AbstractIDCustom));
            state.set(Tags.StateChanged, stateChanged);
        }
        
        return state;
    }
    
    @Override
    protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
        // Kill unwanted processes, force SUT to foreground
        Set<Action> actions = super.deriveActions(system, state);
        Set<Action> filteredActions = new HashSet<>();
        
        // create an action compiler, which helps us create actions
        // such as clicks, drag&drop, typing ...
        StdActionCompiler ac = new AnnotatingActionCompiler();
        
        // Check if forced actions are needed to stay within allowed domains
        Set<Action> forcedActions = detectForcedActions(state, ac);

        // iterate through all widgets
        for (Widget widget : state)
        {
            // Add the possibility to page down (scroll down) if a widget of the form is not visible below
            if(isForm(widget) && formContainsNonVisibleSubmitButtonBelow(widget))
            {
                Action pageDown = ac.hitKey(KBKeys.VK_PAGE_DOWN);
                pageDown.set(Tags.OriginWidget, widget);
                pageDown.set(Tags.Role, ActionRoles.HitKeyScrollDownAction);
                actions.add(pageDown);
            }
            
            // only consider enabled and non-tabu widgets
            if (!widget.get(Enabled, true)) {
                continue;
            }
            // The blackListed widgets are those that have been filtered during the SPY mode with the
            //CAPS_LOCK + SHIFT + Click clickfilter functionality.
            if(blackListed(widget)){
                if(isTypeable(widget)){
                    filteredActions.add(ac.pasteTextInto(widget, InputDataManager.getRandomTextInputData(widget), true));
                } else {
                    filteredActions.add(ac.leftClickAt(widget));
                }
                continue;
            }
            
            // slides can happen, even though the widget might be blocked
//            addSlidingActions(actions, ac, scrollArrowSize, scrollThick, widget);

            // If the element is blocked, Testar can't click on or type in the widget
            if (widget.get(Blocked, false) && !widget.get(WdTags.WebIsShadow, false)) {
                continue;
            }

            if(isAtBrowserCanvas(widget))
            {
                String webType = widget.get(WdTags.WebType, "");
                if(webType.equalsIgnoreCase("date"))
                {
                    actions.add(ac.clickAndTypeText(widget, 0.1,0.5, InputDataManager.getRandomDateNumber()));
                    continue;
                }
                else if(webType.equalsIgnoreCase("datetime-local"))
                {
                    actions.add(ac.clickAndTypeText(widget, 0.1,0.5, InputDataManager.getRandomDateTimeNumber()));
                    continue;
                }
                else if(webType.equalsIgnoreCase("number"))
                {
                    actions.add(ac.clickAndTypeText(widget, 0.1, 0.5, InputDataManager.getRandomNumberInput()));
                    continue;
                }
                else if(webType.equalsIgnoreCase("time"))
                {
                    actions.add(ac.clickAndTypeText(widget, 0.1, 0.5, InputDataManager.getRandomTimeNumber()));
                    continue;
                }
                else if(webType.equalsIgnoreCase("week"))
                {
                    actions.add(ac.clickAndTypeText(widget, InputDataManager.getRandomWeekNumber()));
                    continue;
                }
                else if(webType.equalsIgnoreCase("checkbox"))
                {
                    actions.add(ac.leftClickAt(widget));
                    continue;
                }
                else if(webType.equalsIgnoreCase("radio"))
                {
                    actions.add(ac.leftClickAt(widget));
                    continue;
                }
                else if(webType.toLowerCase().contains("range"))
                {
                    double[] values = new double[] {0.1, 0.2, 0.3, 0.4, 0.6, 0.7, 0.8, 0.9, 1.0}; //no 0.5
                    Random r = new Random();
                    actions.add(ac.leftClickAt(widget, values[r.nextInt(values.length)], 0.5));
                    continue;
                }
            }
            
            // type into text boxes
            if (isAtBrowserCanvas(widget) && isTypeable(widget))
            {
                if(whiteListed(widget) || isUnfiltered(widget))
                {
                    actions.add(ac.pasteTextInto(widget, InputDataManager.getRandomTextInputData(widget), true));
                }else{
                    // filtered and not white listed:
                    filteredActions.add(ac.pasteTextInto(widget, InputDataManager.getRandomTextInputData(widget), true));
                }
            }
            
            // left clicks, but ignore links outside domain
            if (isAtBrowserCanvas(widget) && isClickable(widget)) {
                if(whiteListed(widget) || isUnfiltered(widget)){
                    if (!isLinkDenied(widget) && widget.get(WdTags.WebType, "").equalsIgnoreCase("submit")) {
                        actions.add(ac.leftClickAt(widget));
                    }else{
                        // link denied:
                        filteredActions.add(ac.leftClickAt(widget));
                    }
                }else{
                    // filtered and not white listed:
                    filteredActions.add(ac.leftClickAt(widget));
                }
            }
        }
        
        // If we have forced actions, prioritize and filter the other ones
        if (forcedActions != null && forcedActions.size() > 0) {
            filteredActions = actions;
            actions = forcedActions;
        }

        //Showing the grey dots for filtered actions if visualization is on:
        if(visualizationOn || mode() == Modes.Spy) SutVisualization.visualizeFilteredActions(cv, state, filteredActions);
        
        return actions;
    }
    
    @Override
    protected Action selectAction(State state, Set<Action> actions)
    {
        if(DefaultProtocol.lastExecutedAction != null)
        {
            state.set(Tags.PreviousAction, DefaultProtocol.lastExecutedAction);
            state.set(Tags.PreviousActionID, DefaultProtocol.lastExecutedAction.get(Tags.AbstractIDCustom, null));
        }

        Action selectedAction = (useRandom) ?
                selector.selectAction(state, actions):
                parseUtil.selectAction(state, actions, actionsExecuted, operatingSystems);
        
        String actionID = selectedAction.get(Tags.AbstractIDCustom);
        Integer timesUsed = actionsExecuted.getOrDefault(actionID, 0); //get the use count for the action
        actionsExecuted.put(actionID, timesUsed + 1); //increase by one

        String widgetPath = selectedAction.get(Tags.OriginWidget).get(Tags.Path).trim();
        String widgetDesc = selectedAction.get(Tags.OriginWidget).get(Tags.Desc);

        String identifier = widgetPath + ":" + widgetDesc;
        Integer timesDescUsed = debugActionsExecuted.getOrDefault(identifier, 0); //get the use count for the action
        debugActionsExecuted.put(identifier, timesDescUsed + 1); //increase by one

        return selectedAction;
    }

    private boolean formContainsNonVisibleSubmitButtonBelow(Widget formChildWidget)
    {
        boolean submitButtonBelow = false;

        // If the widget is not at browser canvas
        if(!isAtBrowserCanvas(formChildWidget)) {
            submitButtonBelow = formChildWidget.get(WdTags.WebType, "").equalsIgnoreCase("submit");
        }

        if(formChildWidget.childCount() > 0) {
            // Iterate through the form element widgets
            for(int i = 0; i < formChildWidget.childCount(); i++) {
                submitButtonBelow = submitButtonBelow || formContainsNonVisibleSubmitButtonBelow(formChildWidget.child(i));
            }
        }

        return submitButtonBelow;
    }

    private boolean formContainsNonVisibleWidgetsBelow(Widget formChildWidget)
    {
        boolean areaBelow = false;

        // If the widget is not at browser canvas
        if(!isAtBrowserCanvas(formChildWidget))
        {
            Shape widgetShape = formChildWidget.get(Tags.Shape, null);
            if (widgetShape != null && cv != null)
            {
                Rectangle canvasRect = new java.awt.Rectangle((int)cv.x(), (int)cv.y(), (int)cv.width(), (int)cv.height());
                Rectangle widgetRect = new java.awt.Rectangle((int)widgetShape.x(), (int)widgetShape.y(), (int)widgetShape.width(), (int)widgetShape.height());
                // Check if is contains area below the form
                areaBelow = isAreaBelow(widgetRect, canvasRect);
            }
        }

        if(formChildWidget.childCount() > 0) {
            // Iterate through the form element widgets
            for(int i = 0; i < formChildWidget.childCount(); i++) {
                areaBelow = areaBelow || formContainsNonVisibleWidgetsBelow(formChildWidget.child(i));
            }
        }

        return areaBelow;
    }

    /**
     * Hi, can you generate me a java method that compares two rectangles (R1 and R2)
     * and returns a boolean that indicates if R2 contains an area below R1?
     *
     * ChatGPT:
     * Sure, here is an example Java method that takes two Rectangle objects (r1 and r2)
     * and returns true if r2 contains an area below r1, and false otherwise:
     *
     * @param r1
     * @param r2
     * @return
     */
    private boolean isAreaBelow(Rectangle r1, Rectangle r2) {
        if (r1.getMaxY() < r2.getMaxY()) {
            // r1 is completely above r2
            return false;
        } else if (r1.getMinY() >= r2.getMaxY()) {
            // r1 is completely below r2
            return true;
        } else {
            // r1 intersects r2
            return r1.getMinY() < r2.getMaxY();
        }
    }

    /**
     * TESTAR uses this method to determine when to stop the generation of actions for the
     * current sequence. You can stop deriving more actions after:
     * - a specified amount of executed actions, which is specified through the SequenceLength setting, or
     * - after a specific time, that is set in the MaxTime setting
     * @return  if <code>true</code> continue generation, else stop
     */
    @Override
    protected boolean moreActions(State state)
    {
        for(Widget widget : state)
        {
            if(widget.get(WdTags.WebTextContent, "").equalsIgnoreCase("return to form"))
            {
                logFormValues(state);
                return false;
            }
        }
        return super.moreActions(state);
    }

    /**
     * Print the <li> web elements corresponding to the filled form values
     *
     * param state
     */
    private void logFormValues(State state)
    {
        numFieldsFilled = 0;
        try
        {
            FileWriter myWriter = new FileWriter(Main.outputDir + File.separator + outerLoopName + File.separator +"log_form_values.txt", true);

            myWriter.write("---------- FORM VALUES LOG----------");
            myWriter.write(System.getProperty("line.separator"));

            myWriter.write(WdDriver.getCurrentUrl());
            myWriter.write(System.getProperty("line.separator"));
            myWriter.write("No. actions: " + (actionCount-1));
            myWriter.write(System.getProperty("line.separator"));
            myWriter.write(System.getProperty("line.separator"));
            for(Widget w : state)
            {
                if(w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdLI))
                {
                    String webTextContent = w.get((WdTags.WebTextContent));
                    String strippedWebTextContent = webTextContent.replaceAll("\\s", ""); //remove all spaces
                    String[] splitWebTextContent = strippedWebTextContent.split("=");

                    if(splitWebTextContent[0].toLowerCase().contains("range") && !splitWebTextContent[1].equals("50"))
                        numFieldsFilled++;
                    else if(splitWebTextContent.length == 2)
                        numFieldsFilled++;

                    if(splitWebTextContent[0].equalsIgnoreCase("begin_epoch"))
                        start_epoch = splitWebTextContent[1];
                    else if(splitWebTextContent[0].equalsIgnoreCase("end_epoch"))
                        end_epoch = splitWebTextContent[1];

                    myWriter.write(webTextContent);
                    myWriter.write(System.getProperty("line.separator"));
                }
            }
            myWriter.close();
        }
        catch (IOException e)
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


    /**
     * This method is called after each test sequence, allowing for example using external profiling software on the SUT
     *
     * super.postSequenceProcessing() is adding test verdict into the HTML sequence report
     */
    @Override
    protected void postSequenceProcessing()
    {
        super.postSequenceProcessing();
        logActionCount(latestState);
        logResults();
    }

    private void logResults()
    {
        boolean createNewFile = true;

        CSVFileWriter csvWriter = new CSVFileWriter(Main.outputDir,
        settings.get(ConfigTags.ApplicationName,"application") + "_" + settings.get(ConfigTags.ApplicationVersion,"1"));

        String url = WdDriver.getCurrentUrl();
        String fieldCodes = url.substring(url.lastIndexOf('/')); //get the last part of the url, only works for one form
        int numFields = fieldCodes.length() / 3; //length should be a multiple of 3
        String submitSuccess = (DefaultProtocol.lastExecutedAction.get(Tags.OriginWidget).get(WdTags.WebType, "").equalsIgnoreCase("submit")) ? "yes" : "no";
        numFieldsFilled -= 5; //formstring, begin_epoch, end_epoch, delta_epoch, datetime


        csvWriter.addField("url", "URL", url);
//        csvWriter.addField("applicationName", "application name", settings.get(ConfigTags.ApplicationName, "application"));
//        csvWriter.addField("applicationVersion", "application version", settings.get(ConfigTags.ApplicationVersion,"1"));
//        csvWriter.addField("strategy", "strategy", (useRandom) ? "Random" : "Human strategy");
        csvWriter.addField("startDatetime", "start datetime", OutputStructure.startInnerLoopDateString);
        csvWriter.addField("timestampTestarStart", "timestamp TESTAR start", startTimestamp);
        csvWriter.addField("timestampTestarEnd", "timestamp TESTAR end", DefaultProtocol.lastExecutedAction.get(Tags.TimeStamp, null).toString());
//        csvWriter.addField("timestampServerStart", "timestamp server start", start_epoch);
//        csvWriter.addField("timestampServerEnd", "timestamp server end", end_epoch);
        csvWriter.addField("actionLimit", "action limit", Integer.toString(settings.get(ConfigTags.SequenceLength)));
        csvWriter.addField("numberOfFields", "number of fields", Integer.toString(numFields));
        csvWriter.addField("actionsExecuted", "actions executed", Integer.toString(actionCount-1));
        csvWriter.addField("numberOfFieldsFilled", "number of fields filled", Integer.toString(numFieldsFilled));
        csvWriter.addField("maxActionsPerField", "maximum actions per field", Integer.toString(Collections.max(actionsExecuted.values())));
        csvWriter.addField("submit", "successful submit", submitSuccess);
    
        if(createNewFile || csvWriter.fileIsEmpty()) //file empty or nonexistent
        {
            csvWriter.createFile();
            csvWriter.writeTitleRow();
        }

        csvWriter.writeCurrentRow();
    }

    /**
     * Print the <li> web elements corresponding to the filled form values
     *
     * param state
     */
    private void logActionCount(State state)
    {
    	try
    	{
    		// convert the map entries to a list
    		java.util.List<Map.Entry<String, Integer>> entryList = new ArrayList<>(debugActionsExecuted.entrySet());

    		// Courtesy of ChatGPT
    		// sort the list by the keys (which are String values)
    		Collections.sort(entryList, new Comparator<Map.Entry<String, Integer>>() {
    		    @Override
    		    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
    		        String[] parts1 = o1.getKey().split(":");
    		        String[] parts2 = o2.getKey().split(":");

    		        int[] arr1 = getIntArray(parts1[0]);
    		        int[] arr2 = getIntArray(parts2[0]);

    		        for (int i = 0; i < Math.min(arr1.length, arr2.length); i++) {
    		            if (arr1[i] != arr2[i]) {
    		                return arr1[i] - arr2[i];
    		            }
    		        }

    		        if (arr1.length != arr2.length) {
    		            return arr1.length - arr2.length;
    		        }

    		        return parts1[1].compareTo(parts2[1]);
    		    }

    		    private int[] getIntArray(String s) {
    		        String[] parts = s.replaceAll("\\[|\\]|\\s", "").split(",");
    		        int[] result = new int[parts.length];
    		        for (int i = 0; i < parts.length; i++) {
    		            result[i] = Integer.parseInt(parts[i]);
    		        }
    		        return result;
    		    }
    		});

    		FileWriter myWriter = new FileWriter(Main.outputDir + File.separator + outerLoopName + File.separator + "log_form_values.txt", true);

    		myWriter.write("---------- Actions Executed ----------");
    		myWriter.write(System.getProperty( "line.separator" ));
    		for (Map.Entry<String, Integer> entry : entryList) {
    			String line = entry.getKey() + " , " + entry.getValue();
    			myWriter.write(line);
    			myWriter.write(System.getProperty( "line.separator" ));
    		}
    		myWriter.close();
    	}
    	catch (IOException e)
    	{
    		System.out.println("An error occurred.");
    		e.printStackTrace();
    	}
    }

    @Override
    protected void closeTestSession()
    {
        super.closeTestSession();
        if(settings.get(ConfigTags.Mode).equals(Modes.Generate))
        {
//            compressOutputRunFolder();
//            copyOutputToNewFolderUsingIpAddress("N:");
//            copyOutputFileToNewFolderUsingIpAddress("N:", outputCsvFile);
        }
    }
}
