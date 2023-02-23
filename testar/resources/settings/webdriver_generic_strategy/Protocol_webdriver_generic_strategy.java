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

 import org.apache.commons.lang.SerializationUtils;
 import org.testar.RandomActionSelector;
 import org.testar.SutVisualization;
 import org.testar.managers.InputDataManager;
 import org.testar.monkey.ConfigTags;
 import org.testar.monkey.DefaultProtocol;
 import org.testar.monkey.Settings;
 import org.testar.monkey.alayer.*;
 import org.testar.monkey.alayer.Shape;
 import org.testar.monkey.alayer.actions.*;
 import org.testar.monkey.alayer.devices.KBKeys;
 import org.testar.monkey.alayer.exceptions.ActionBuildException;
 import org.testar.monkey.alayer.exceptions.StateBuildException;
 import org.testar.monkey.alayer.webdriver.enums.WdTags;
 import org.testar.protocols.WebdriverProtocol;
 import parsing.ParseUtil;

 import java.awt.*;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Set;

 import static org.testar.monkey.alayer.Tags.Blocked;
 import static org.testar.monkey.alayer.Tags.Enabled;
 import static org.testar.monkey.alayer.webdriver.Constants.scrollArrowSize;
 import static org.testar.monkey.alayer.webdriver.Constants.scrollThick;

 public class Protocol_webdriver_generic_strategy extends WebdriverProtocol
{
    private ParseUtil               parseUtil;
    private Map<String, Integer>    actionsExecuted      = new HashMap<String, Integer>();
    private boolean                 UseSingleFill;
    
    @Override
    protected void initialize(Settings settings)
    {
        super.initialize(settings);
        parseUtil = new ParseUtil(settings.get(ConfigTags.StrategyFile));
        UseSingleFill = settings.get(ConfigTags.UseSingleFill);
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
            if(isForm(widget) && formContainsNonVisibleWidgetsBelow(widget, widget))
            {
                Action pageDown = ac.hitKey(KBKeys.VK_PAGE_DOWN);
                pageDown.set(Tags.OriginWidget, widget);
                pageDown.set(Tags.Role, ActionRoles.HitKeyScrollDownAction);
                actions.add(pageDown);
            }

//            if(UseSingleFill)
//            {
//                // fill forms actions
//                if (isAtBrowserCanvas(widget) && isForm(widget))
//                {
//                    String protocol = settings.get(ConfigTags.ProtocolClass, "");
//                    Action formFillingAction = new WdFillFormAction(ac, widget, protocol.substring(0, protocol.lastIndexOf('/')));
//                    if(!(formFillingAction instanceof NOP))// do nothing with NOP actions - the form was not actionable
//                        actions.add(formFillingAction);
//
//                }
//            }
            
            // only consider enabled and non-tabu widgets
            if (!widget.get(Enabled, true)) {
                continue;
            }
            // The blackListed widgets are those that have been filtered during the SPY mode with the
            //CAPS_LOCK + SHIFT + Click clickfilter functionality.
            if(blackListed(widget)){
                if(isTypeable(widget)){
                    filteredActions.add(ac.clickTypeInto(widget, InputDataManager.getRandomTextInputData(widget), true));
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
            
            // type into text boxes
            if (isAtBrowserCanvas(widget) && isTypeable(widget))
            {
                if(whiteListed(widget) || isUnfiltered(widget))
                {
                    actions.add(ac.clickTypeInto(widget, InputDataManager.getRandomTextInputData(widget), true));
                }else{
                    // filtered and not white listed:
                    filteredActions.add(ac.clickTypeInto(widget, InputDataManager.getRandomTextInputData(widget), true));
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
        
        //if(actions.isEmpty()) {
        //	return new HashSet<>(Collections.singletonList(new WdHistoryBackAction()));
        //}
        
        // If we have forced actions, prioritize and filter the other ones
        if (forcedActions != null && forcedActions.size() > 0) {
            filteredActions = actions;
            actions = forcedActions;
        }

//        if(UseSingleFill && actions.size() > 1)
//        {
//            Set<Action> fillFormActions = new HashSet<>();
//            for (Action action : actions)
//            {
//                if(action instanceof WdFillFormAction)
//                    fillFormActions.add(action);
//            }
//            actions = fillFormActions;
//        }

        //Showing the grey dots for filtered actions if visualization is on:
        if(visualizationOn || mode() == Modes.Spy) SutVisualization.visualizeFilteredActions(cv, state, filteredActions);
        
        return actions;
    }
    
    @Override
    protected Action selectAction(State state, Set<Action> actions)
    {
//        if(UseSingleFill)
//            return RandomActionSelector.selectAction(actions); //the list only contains FillFormActions

        //clone the action
        Action selectedAction = (Action) SerializationUtils.clone(parseUtil.selectAction(state, actions, actionsExecuted));
        
        if(DefaultProtocol.lastExecutedAction != null)
        {
            state.set(Tags.PreviousAction, DefaultProtocol.lastExecutedAction);
            state.set(Tags.PreviousActionID, DefaultProtocol.lastExecutedAction.get(Tags.AbstractIDCustom, null));
        }
        
        String actionID = selectedAction.get(Tags.AbstractIDCustom);
        Integer timesUsed = actionsExecuted.getOrDefault(actionID, 0); //get the use count for the action
        actionsExecuted.put(actionID, timesUsed + 1); //increase by one
        
        return selectedAction;
    }

    private boolean formContainsNonVisibleWidgetsBelow(Widget formWidget, Widget mainWidget)
    {
        boolean areaBelow = false;

        // If the widget is not at browser canvas
        if(!isAtBrowserCanvas(formWidget)) {
            Shape widgetShape = formWidget.get(Tags.Shape, null);
            Shape stateShape = mainWidget.get(Tags.Shape, null);
            if (widgetShape != null && stateShape != null) {
                Rectangle stateRect = new java.awt.Rectangle((int)stateShape.x(), (int)stateShape.y(), (int)stateShape.width(), (int)stateShape.height());
                Rectangle widgetRect = new java.awt.Rectangle((int)widgetShape.x(), (int)widgetShape.y(), (int)widgetShape.width(), (int)widgetShape.height());
                // Check if is contains area below the form
                areaBelow = isAreaBelow(widgetRect, stateRect);
            }
        }

        if(formWidget.childCount() > 0) {
            // Iterate through the form element widgets
            for(int i = 0; i < formWidget.childCount(); i++) {
                areaBelow = areaBelow || formContainsNonVisibleWidgetsBelow(formWidget.child(i), mainWidget);
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

    @Override
    protected void closeTestSession()
    {
        super.closeTestSession();
        if(settings.get(ConfigTags.Mode).equals(Modes.Generate))
        {
            compressOutputRunFolder();
            copyOutputToNewFolderUsingIpAddress("N:");
        }
    }
}
