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

import java.io.File;
import java.io.IOException;
import java.util.Set;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.windows.WinProcess;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.testar.OutputStructure;
import org.testar.protocols.DesktopProtocol;

import es.upv.staq.testar.NativeLinker;

public class Protocol_desktop_codeo extends DesktopProtocol {

    private double menubarFilter;

    /**
     * Initialize TESTAR with the given settings:
     *
     * @param settings
     */
    @Override
    protected void initialize(Settings settings) {
        super.initialize(settings);

        // Set desired License
        licenseSUT = new CODEOLicense();

        // TESTAR will execute the SUT with Java
        // We need this to add JMX parameters properly (-Dcom.sun.management.jmxremote.port=5000)
        WinProcess.codeo_execution = true;

        // CODEO takes his time to start, wait 30s by default
        WinProcess.codeo_pause = 30;

        // Copy "bin/settings/protocolName/build.xml" file to "bin/jacoco/build.xml"
        copyJacocoBuildFile();
    }

    /**
     * This method is called when TESTAR starts the System Under Test (SUT). The method should
     * take care of
     *   1) starting the SUT (you can use TESTAR's settings obtainable from <code>settings()</code> to find
     *      out what executable to run)
     *   2) waiting until the system is fully loaded and ready to be tested (with large systems, you might have to wait several
     *      seconds until they have finished loading)
     * @return  a started SUT, ready to be tested.
     */
    @Override
    protected SUT startSystem() throws SystemStartException {

        SUT system = super.startSystem();

        // Not a beautiful way to deal with Workspace dialog 

        /*Keyboard kb = AWTKeyboard.build();
		kb.press(KBKeys.VK_SHIFT);
		kb.release(KBKeys.VK_SHIFT);
		kb.press(KBKeys.VK_SHIFT);
		kb.release(KBKeys.VK_SHIFT);
		kb.press(KBKeys.VK_ENTER);
		kb.release(KBKeys.VK_ENTER);

		Util.pause(30);*/
        return system;
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
        for(Widget w : state){
            Role role = w.get(Tags.Role, Roles.Widget);
            if(Role.isOneOf(role, new Role[]{NativeLinker.getNativeRole("UIAMenuBar")})) {
                menubarFilter = w.get(Tags.Shape,null).y() + w.get(Tags.Shape,null).height();
                break;
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

        Set<Action> actions = super.deriveActions(system,state);

        // To derive actions (such as clicks, drag&drop, typing ...) we should first create an action compiler.
        StdActionCompiler ac = new AnnotatingActionCompiler();

        // To find all possible actions that TESTAR can click on we should iterate through all widgets of the state.
        for(Widget w : state){

            if(w.get(Tags.Role, Roles.Widget).toString().equalsIgnoreCase("UIAMenu")){
                // filtering out actions on menu-containers (that would add an action in the middle of the menu)
                continue; // skip this iteration of the for-loop
            }

            // Only consider enabled and non-blocked widgets
            if(w.get(Tags.Enabled, true) && !w.get(Tags.Blocked, false)){

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

                    /*else if(isClickable(w) && isUnrecognizedCheckBox(w) && (isUnfiltered(w) || whiteListed(w))) {
						//Create a left click action with the Action Compiler, and add it to the set of derived actions
						actions.add(ac.leftClickAt(w, 0.05, 0.5));
					}*/

                    //For widgets that are:
                    // - typeable
                    // and
                    // - unFiltered by any of the regular expressions in the Filter-tab, or
                    // - whitelisted using the clickfilter functionality in SPY mode (CAPS_LOCK + SHIFT + CNTR + Click)
                    // We want to create actions that consist of typing into them
                    if(isTypeable(w) && (isUnfiltered(w) || whiteListed(w))) {
                        //Create a type action with the Action Compiler, and add it to the set of derived actions
                        actions.add(ac.clickTypeInto(w, this.getRandomText(w), true));
                    }

                    //Add sliding actions (like scroll, drag and drop) to the derived actions
                    //method defined below.
                    //addSlidingActions(actions, ac, SCROLL_ARROW_SIZE,SCROLL_THICK, w.parent(), state);

                }
            }
        }
        return actions;
    }

    @Override
    protected boolean isUnfiltered(Widget w) {
        Shape shape = w.get(Tags.Shape, null);
        if (shape != null && shape.y() < menubarFilter && !w.get(Tags.Desc,"").contains("CODEO")) {
            return false;
        }

        return super.isUnfiltered(w);
    }

    @Override
    protected boolean isTypeable(Widget w) {
        if(w.get(Tags.Role, Roles.Widget).toString().contains("UIAText")) {
            return false;
        }
        return super.isTypeable(w);
    }

    //CheckBox from project configuration panel
    private boolean isUnrecognizedCheckBox(Widget w) {
        if(w.parent()!=null &&
                w.get(Tags.Role).toString().contains("UIAText") &&
                w.parent().get(Tags.Role).toString().contains("ListItem")) {
            return true;
        }
        return false;
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

        // Extract and create JaCoCo action coverage report for Generate Mode
        if(settings.get(ConfigTags.Mode).equals(Modes.Generate)) {
            String actionCoverageInfo = extractJacocoActionReport();
            coverageInformation.add(actionCoverageInfo);
        }
        return actionExecuted;
    }

    /**
     * This method is invoked each time the TESTAR has reached the stop criteria for generating a sequence.
     * This can be used for example for graceful shutdown of the SUT, maybe pressing "Close" or "Exit" button
     */
    @Override
    protected void finishSequence() {
        // Extract and create JaCoCo sequence coverage report for Generate Mode
        if(settings.get(ConfigTags.Mode).equals(Modes.Generate)) {
            extractJacocoSequenceReport();
        }

        try {
            // Update DECODER Coverage information
            String reportDir = new File(OutputStructure.outerLoopOutputDir).getCanonicalPath() + File.separator + "jacoco_reports";
            coverageDir.add(reportDir);
        } catch (IOException e) {
            coverageDir.add("ERROR: Addind Jacoco Coverage Directory");
        }

        super.finishSequence();
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

    /**
     * This method is called after the last sequence, to allow for example handling the reporting of the session
     */
    @Override
    protected void closeTestSession() {
        // Extract and create JaCoCo run coverage report for Generate Mode
        if(settings.get(ConfigTags.Mode).equals(Modes.Generate)) {
            extractJacocoRunReport();
        }

        // Execute DECODER PKM insertion
        super.closeTestSession();
    }

}

/**
 *  Helper class to customize SUT License as desires
 */
class CODEOLicense {

    String sutTitle = "CODEO";
    String sutName = "PikeOS Certified Hypervisor Eclipse-based CODEO";
    String sutUrl = "https://www.sysgo.com/products/pikeos-hypervisor/eclipse-based-codeo";
    String product = "SYSGO";
    boolean isOpenSource = false;

    public CODEOLicense () { 
        // Create object for JSON Artefact purposes
    }
}
