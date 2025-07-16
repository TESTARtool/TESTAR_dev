/***************************************************************************************************
 *
 * Copyright (c) 2019 - 2024 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2019 - 2024 Open Universiteit - www.ou.nl
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

import org.testar.DerivedActions;
import org.testar.environment.Environment;
import org.testar.managers.InputDataManager;
import org.testar.monkey.Drag;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.windows.UIATags;

import java.util.HashSet;
import java.util.Set;

import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;

public class DesktopProtocol extends GenericUtilsProtocol {
    //Attributes for adding slide actions
    protected static double SCROLL_ARROW_SIZE = 36; // sliding arrows
    protected static double SCROLL_THICK = 16; //scroll thickness
    
    /**
     * This method is invoked each time the TESTAR starts the SUT to generate a new sequence.
     * This can be used for example for bypassing a login screen by filling the username and password
     * or bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
     * the SUT's configuration files etc.)
     */
    @Override
    protected void beginSequence(SUT system, State state) {
    	super.beginSequence(system, state);
    	
    	double displayScale = Environment.getInstance().getDisplayScale(state.child(0).get(Tags.HWND, (long)0));
    	
    	mouse.setCursorDisplayScale(displayScale);
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

    	// For Qt applications, block elements outside the modal window
    	if(state.childCount() > 0 && state.child(0).get(UIATags.UIAFrameworkId, "").equals("Qt")) {
    		// Obtain the modal element. By default the main window
    		Widget modalWindow = state.child(0);
    		for(Widget w : state) {
    			if (w.get(UIATags.UIAIsWindowModal, false)) {
    				modalWindow = w;
    				break; // exit loop once modal is found
    			}
    		}

    		// If the modal element exists, mark elements outside modal as blocked
    		if(modalWindow != null) {
    			for(Widget w : state) {
    				if (!isQtElementVisibleOnModalScreen(w, modalWindow)) {
    					w.set(Tags.Blocked, true);
    				}
    			}
    		}
    	}

    	return state;
    }

    private boolean isQtElementVisibleOnModalScreen(Widget w, Widget modalWidget) {
    	Rect elementRect = (Rect) w.get(Tags.Shape, Rect.from(0,0,0,0));
    	Rect modalRect = (Rect) modalWidget.get(Tags.Shape, Rect.from(0,0,0,0));

    	double elementRight = elementRect.x() + elementRect.width();
    	double elementBottom = elementRect.y() + elementRect.height();
    	double modalRight = modalRect.x() + modalRect.width();
    	double modalBottom = modalRect.y() + modalRect.height();

    	// Check if element is completely visible inside the modal
    	return elementRect.x() >= modalRect.x() && 
    			elementRight <= modalRight &&
    			elementRect.y() >= modalRect.y() && 
    			elementBottom <= modalBottom;
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

        //The super method returns a ONLY actions for killing unwanted processes if needed, or bringing the SUT to
        //the foreground. You should add all other actions here yourself.
        // These "special" actions are prioritized over the normal GUI actions in selectAction() / preSelectAction().
        return super.deriveActions(system,state);
    }

    /**
     * Iterating through all widgets of the given state. 
     *
     * Adding derived actions into the given set of actions and returning the modified set of actions.
     *
     * @param actions
     * @param state
     * @return
     */
    protected DerivedActions deriveClickTypeScrollActionsFromAllWidgets(Set<Action> actions, State state){

        Set<Action> filteredActions = new HashSet<>();
        DerivedActions derived = new DerivedActions(actions, filteredActions);

        // Iterate through all widgets of the state to find all possible actions that TESTAR can click on
        for(Widget w : state){
            derived = getMultipleActionsFromWidget(w,derived);
        }
        return derived;
    }

    /**
     * Iterating top level widgets of the given state. 
     * These are elements like Menus or emerging Windows. 
     *
     * Adding derived top level actions into the given set of actions and returning the modified set of actions.
     * 
     * @param actions
     * @param state
     * @return
     */
    protected DerivedActions deriveClickTypeScrollActionsFromTopLevelWidgets(Set<Action> actions, State state){

        Set<Action> filteredActions = new HashSet<>();
        DerivedActions derived = new DerivedActions(actions, filteredActions);

        // Iterate through top level widgets of the state trying to execute more interesting actions
        for(Widget w : getTopWidgets(state)){
            derived = getMultipleActionsFromWidget(w,derived);
        }
        return derived;
    }

    /**
     * Given a widget, check if it is possible to derive an available or filter action on it. 
     * If widget is enabled and unfiltered, derive only one available action. 
     * Current Action priority: Typeable > Clickable > Draggable.
     * 
     * @param w
     * @param derived
     * @return
     */
    private DerivedActions getActionFromWidget(Widget w, DerivedActions derived){
        // To derive actions (such as clicks, drag&drop, typing ...) we should first create an action compiler.
        StdActionCompiler ac = new AnnotatingActionCompiler();

        if(w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAMenu")){
            // filtering out actions on menu-containers (that would add an action in the middle of the menu)
            return derived; // skip this widget
        }

        // Only consider enabled and non-blocked widgets
        if(!w.get(Enabled, true) || w.get(Blocked, false)) {
            // widget not enabled or is blocked:
            return derived; // skip this widget
        }else{

            // Do not build actions for widgets on the blacklist
            // The blackListed widgets are those that have been filtered during the SPY mode with the
            //CAPS_LOCK + SHIFT + Click clickfilter functionality.
            if (blackListed(w)) {
            	if(isTypeable(w)) {
            		derived.addFilteredAction(ac.clickTypeInto(w, InputDataManager.getRandomTextInputData(w), true));
            	} else {
            		derived.addFilteredAction(ac.leftClickAt(w));
            	}
                return derived;
            }else{

                //For widgets that are:
                // - typeable
                // and
                // - unFiltered by any of the regular expressions in the Filter-tab, or
                // - whitelisted using the clickfilter functionality in SPY mode (CAPS_LOCK + SHIFT + CNTR + Click)
                // We want to create actions that consist of typing into them
                if(isTypeable(w)){
                    if(isUnfiltered(w) || whiteListed(w)) {
                        //Create a type action with the Action Compiler, and add it to the set of derived actions
                        derived.addAvailableAction(ac.clickTypeInto(w, InputDataManager.getRandomTextInputData(w), true));
                        return derived;
                    }else{
                        // Filtered and not white listed:
                        derived.addFilteredAction(ac.clickTypeInto(w, InputDataManager.getRandomTextInputData(w), true));
                        return derived;
                    }
                }

                //For widgets that are:
                // - clickable
                // and
                // - unFiltered by any of the regular expressions in the Filter-tab, or
                // - whitelisted using the clickfilter functionality in SPY mode (CAPS_LOCK + SHIFT + CNTR + Click)
                // We want to create actions that consist of left clicking on them
                if(isClickable(w)) {
                    if((isUnfiltered(w) || whiteListed(w))){
                        //Create a left click action with the Action Compiler, and add it to the set of derived actions
                        derived.addAvailableAction(ac.leftClickAt(w));
                        return derived;
                    }else{
                        // Filtered and not white listed:
                        derived.addFilteredAction(ac.leftClickAt(w));
                        return derived;
                    }
                }

                //Add sliding actions (like scroll, drag and drop) to the derived actions
                //method defined below.
                Drag[] drags = null;
                //If there are scroll (drags/drops) actions possible
                if((drags = w.scrollDrags(SCROLL_ARROW_SIZE,SCROLL_THICK)) != null) {
                    derived = addSlidingActions(derived, ac, drags, w);
                    return derived;
                }
            }
        }
        return derived;
    }
    
    /**
     * Given a widget, check if it is possible to derive an available or filter multiple actions on it. 
     * If widget is enabled and unfiltered, derive all possible actions.
     * 
     * @param w
     * @param derived
     * @return
     */
    private DerivedActions getMultipleActionsFromWidget(Widget w, DerivedActions derived){
        // To derive actions (such as clicks, drag&drop, typing ...) we should first create an action compiler.
        StdActionCompiler ac = new AnnotatingActionCompiler();

        if(w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAMenu")){
            // filtering out actions on menu-containers (that would add an action in the middle of the menu)
            return derived; // skip this widget
        }

        // Only consider enabled and non-blocked widgets
        if(!w.get(Enabled, true) || w.get(Blocked, false)) {
            // widget not enabled or is blocked:
            return derived; // skip this widget
        }else{

            // Do not build actions for widgets on the blacklist
            // The blackListed widgets are those that have been filtered during the SPY mode with the
            //CAPS_LOCK + SHIFT + Click clickfilter functionality.
            if (blackListed(w)) {
            	if(isTypeable(w)) {
            		derived.addFilteredAction(ac.clickTypeInto(w, InputDataManager.getRandomTextInputData(w), true));
            	} else {
            		derived.addFilteredAction(ac.leftClickAt(w));
            	}
                return derived;
            }else{

                //For widgets that are:
                // - typeable
                // and
                // - unFiltered by any of the regular expressions in the Filter-tab, or
                // - whitelisted using the clickfilter functionality in SPY mode (CAPS_LOCK + SHIFT + CNTR + Click)
                // We want to create actions that consist of typing into them
                if(isTypeable(w)){
                    if(isUnfiltered(w) || whiteListed(w)) {
                        //Create a type action with the Action Compiler, and add it to the set of derived actions
                        derived.addAvailableAction(ac.clickTypeInto(w, InputDataManager.getRandomTextInputData(w), true));
                    }else{
                        // Filtered and not white listed:
                        derived.addFilteredAction(ac.clickTypeInto(w, InputDataManager.getRandomTextInputData(w), true));
                    }
                }

                //For widgets that are:
                // - clickable
                // and
                // - unFiltered by any of the regular expressions in the Filter-tab, or
                // - whitelisted using the clickfilter functionality in SPY mode (CAPS_LOCK + SHIFT + CNTR + Click)
                // We want to create actions that consist of left clicking on them
                if(isClickable(w)) {
                    if((isUnfiltered(w) || whiteListed(w))){
                        //Create a left click action with the Action Compiler, and add it to the set of derived actions
                        derived.addAvailableAction(ac.leftClickAt(w));
                    }else{
                        // Filtered and not white listed:
                        derived.addFilteredAction(ac.leftClickAt(w));
                    }
                }

                //Add sliding actions (like scroll, drag and drop) to the derived actions
                //method defined below.
                Drag[] drags = null;
                //If there are scroll (drags/drops) actions possible
                if((drags = w.scrollDrags(SCROLL_ARROW_SIZE,SCROLL_THICK)) != null) {
                    derived = addSlidingActions(derived, ac, drags, w);
                }
            }
        }
        return derived;
    }

    /**
     * Adding derived actions into the given set of actions and returning the modified set of actions.
     *
     * @param actions
     * @param system
     * @param state
     * @return
     */
    @Deprecated
    protected Set<Action> deriveClickTypeScrollActionsFromAllWidgetsOfState(Set<Action> actions, SUT system, State state){
        // To derive actions (such as clicks, drag&drop, typing ...) we should first create an action compiler.
        StdActionCompiler ac = new AnnotatingActionCompiler();

        // To find all possible actions that TESTAR can click on we should iterate through all widgets of the state.
        for(Widget w : state){

            if(w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAMenu")){
                // filtering out actions on menu-containers (that would add an action in the middle of the menu)
                continue; // skip this iteration of the for-loop
            }

            // Only consider enabled and non-blocked widgets
            if(w.get(Enabled, true) && !w.get(Blocked, false)){

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

                    //For widgets that are:
                    // - typeable
                    // and
                    // - unFiltered by any of the regular expressions in the Filter-tab, or
                    // - whitelisted using the clickfilter functionality in SPY mode (CAPS_LOCK + SHIFT + CNTR + Click)
                    // We want to create actions that consist of typing into them
                    if(isTypeable(w) && (isUnfiltered(w) || whiteListed(w))) {
                        //Create a type action with the Action Compiler, and add it to the set of derived actions
                        actions.add(ac.clickTypeInto(w, InputDataManager.getRandomTextInputData(w), true));
                    }
                    //Add sliding actions (like scroll, drag and drop) to the derived actions
                    //method defined below.
                    addSlidingActions(actions,ac,SCROLL_ARROW_SIZE,SCROLL_THICK,w);
                }
            }
        }
        return actions;
    }

    /**
     * Adding derived actions into the given set of actions and returning the modified set of actions.
     *
     * @param actions
     * @param system
     * @param state
     * @return
     */
    @Deprecated
    protected Set<Action> deriveClickTypeScrollActionsFromTopLevelWidgets(Set<Action> actions, SUT system, State state){
        // To derive actions (such as clicks, drag&drop, typing ...) we should first create an action compiler.
        StdActionCompiler ac = new AnnotatingActionCompiler();

        // iterate through top level widgets based on Z-index:
        for(Widget w : getTopWidgets(state)){

            if(w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAMenu")){
                // filtering out actions on menu-containers (that would add an action in the middle of the menu)
                continue; // skip this iteration of the for-loop
            }

            // Only consider enabled and non-blocked widgets
            if(w.get(Enabled, true) && !w.get(Blocked, false)){

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

                    //For widgets that are:
                    // - typeable
                    // and
                    // - unFiltered by any of the regular expressions in the Filter-tab, or
                    // - whitelisted using the clickfilter functionality in SPY mode (CAPS_LOCK + SHIFT + CNTR + Click)
                    // We want to create actions that consist of typing into them
                    if(isTypeable(w) && (isUnfiltered(w) || whiteListed(w))) {
                        //Create a type action with the Action Compiler, and add it to the set of derived actions
                        actions.add(ac.clickTypeInto(w, InputDataManager.getRandomTextInputData(w), true));
                    }
                    //Add sliding actions (like scroll, drag and drop) to the derived actions
                    //method defined below.
                    addSlidingActions(actions,ac,SCROLL_ARROW_SIZE,SCROLL_THICK,w);
                }
            }
        }
        return actions;
    }
}
