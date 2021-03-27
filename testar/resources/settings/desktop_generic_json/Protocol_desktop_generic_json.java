/***************************************************************************************************
 *
 * Copyright (c) 2019 - 2021 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2019 - 2021 Open Universiteit - www.ou.nl
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

import nl.ou.testar.RandomActionSelector;
import nl.ou.testar.ScreenshotJsonFile.JsonUtils;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;

import java.util.Set;

import org.fruit.Pair;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.exceptions.*;
import org.fruit.alayer.windows.StateFetcher;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.testar.protocols.DesktopProtocol;

import es.upv.staq.testar.ProtocolUtil;

/**
 * This is a small change to Desktop Generic Protocol to create JSON files describing the widgets
 *  and their location into output/scrshots folder.
 *
 *  It only changes the getState() method.
 */
public class Protocol_desktop_generic_json extends DesktopProtocol {

    private Pair<String, Set<Action>> unvisitedActionsOfPreviousState;

    /**
     * Called once during the life time of TESTAR
     * This method can be used to perform initial setup work
     * @param   settings  the current TESTAR settings as specified by the user.
     */
    @Override
    protected void initialize(Settings settings){
        super.initialize(settings);
        // Not the best solution to deal with
        // https://stackoverflow.com/questions/34139450/getwindowrect-returns-a-size-including-invisible-borders
        StateFetcher.removeInvisibleBorders = true;
    }

    /**
     * This method is called when the TESTAR requests the state of the SUT.
     * Here you can add additional information to the SUT's state or write your
     * own state fetching routine.
     *
     * Here we don't change the default behaviour, but we add one more step to
     * create a JSON file from the state information.
     *
     * @return  the current state of the SUT with attached oracle.
     */
    @Override
    protected State getState(SUT system) throws StateBuildException {
        State previousState = latestState;
        State state = super.getState(system);

        // Take a screenshot for each widget of the state
        for(Widget widget : state) {
            // Ignore the widget Process
            if(widget.get(Tags.Role, Roles.Process).equals(Roles.Process)) {continue;}
            ProtocolUtil.getWidgetShot(state, widget);
        }

        // Creating a JSON file with information about widgets and their location on the screenshot:
        if(settings.get(ConfigTags.Mode) == Modes.Generate) {
            // Create the basic JSON File
            //JsonUtils.createWidgetInfoJsonFile(state);

            // Create the JSON File with the "destinationState" information
            if(lastExecutedAction != null && lastExecutedAction.get(Tags.OriginWidget, null) != null) {
                Widget executedWidget = lastExecutedAction.get(Tags.OriginWidget);
                JsonUtils.createWidgetInfoPreviousStateJsonFile(state, previousState, executedWidget);

                // If we have the previous unvisited actions from the previous state, 
                // update the "destinationState" information as "unvisited"
                if(unvisitedActionsOfPreviousState != null && unvisitedActionsOfPreviousState.left().equals(previousState.get(Tags.ConcreteIDCustom))) {
                    JsonUtils.updateUnvisitedActionsOfPreviousState(previousState, unvisitedActionsOfPreviousState.right());
                }
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

        //The super method returns a ONLY actions for killing unwanted processes if needed, or bringing the SUT to
        //the foreground. You should add all other actions here yourself.
        // These "special" actions are prioritized over the normal GUI actions in selectAction() / preSelectAction().
        Set<Action> actions = super.deriveActions(system,state);

        getClickActionFromWidgetsOfState(actions, state);

        //return the set of derived actions
        return actions;
    }

    private Set<Action> getClickActionFromWidgetsOfState(Set<Action> actions, State state){
        // To derive actions (such as clicks, drag&drop, typing ...) we should first create an action compiler.
        StdActionCompiler ac = new AnnotatingActionCompiler();

        // Iterate through top level widgets of the state trying to execute more interesting actions
        //for(Widget w : getTopWidgets(state)){

        // To find all possible actions that TESTAR can click on we should iterate through all widgets of the state.
        for(Widget w : state){

            if(w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAMenu")){
                // filtering out actions on menu-containers (that would add an action in the middle of the menu)
                continue;
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
                }
            }
        }

        return actions;
    }

    /**
     * Take a screenshot of the widget executed by the action. 
     * 
     * @param state
     * @param action
     */
    @Override
    protected void getActionScreenshot(State state, Action action) {
        // Disable the action screenshots (none will appear in the HTML report)
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

        unvisitedActionsOfPreviousState = new Pair<>(state.get(Tags.ConcreteIDCustom), actions);

        //Call the preSelectAction method from the AbstractProtocol so that, if necessary,
        //unwanted processes are killed and SUT is put into foreground.
        Action retAction = preSelectAction(state, actions);
        if (retAction== null) {
            //if no preSelected actions are needed, then implement your own action selection strategy
            //using the action selector of the state model:
            retAction = stateModelManager.getAbstractActionToExecute(actions);
        }
        if(retAction==null) {
            System.out.println("State model based action selection did not find an action. Using random action selection.");
            // if state model fails, use random (default would call preSelectAction() again, causing double actions HTML report):
            retAction = RandomActionSelector.selectAction(actions);
        }
        return retAction;
    }

}
