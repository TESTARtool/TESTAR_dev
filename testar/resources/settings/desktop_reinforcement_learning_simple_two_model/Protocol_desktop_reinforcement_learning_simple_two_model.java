/**
 * Copyright (c) 2018, 2019 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018, 2019 Open Universiteit - www.ou.nl
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * <p>
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
 */


import org.testar.RandomActionSelector;
import nl.ou.testar.ReinforcementLearning.ActionSelectors.ReinforcementLearningActionSelector;
import nl.ou.testar.ReinforcementLearning.Policies.GreedyPolicy;
import nl.ou.testar.ReinforcementLearning.Policies.Policy;
import nl.ou.testar.ReinforcementLearning.Policies.PolicyFactory;
import nl.ou.testar.ReinforcementLearning.ReinforcementLearningSettings;

import org.testar.statemodel.AbstractState;
import org.testar.statemodel.actionselector.ActionSelector;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.windows.WinProcess;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;
import org.testar.protocols.DesktopProtocol;
import org.testar.extendedsettings.ExtendedSettingsFactory;


import static org.testar.monkey.alayer.Tags.*;

import java.util.Set;

import javax.swing.AbstractAction;

/**
 * This is a small change to Desktop Generic Protocol to use reinforcement learning to improve action selection
 *
 *  It changes the initialize() and selectAction() methods.
 */
public class Protocol_desktop_reinforcement_learning_simple_two_model extends DesktopProtocol {
    private ActionSelector actionSelector = null;
    private Policy policy = null;

    private RandomActionSelector<State, Action> randomActionSelector = new RandomActionSelector<>();

    /**
     * Called once during the life time of TESTAR
     * This method can be used to perform initial setup work
     * @param settings the current TESTAR settings as specified by the user.
     */
    @Override
    protected void initialize(Settings settings) {
        // Disconnect from Windows Remote Desktop, without close the GUI session
        // User will need to disable or accept UAC permission prompt message
        //disconnectRDP();

        //Create Abstract Model with Reinforcement Learning Implementation
        settings.set(ConfigTags.StateModelReinforcementLearningEnabled, "sarsaModelManager");

        // Extended settings framework, set ConfigTags settings with XML framework values
        // test.setting -> ExtendedSettingsFile
        ReinforcementLearningSettings rlXmlSetting = ExtendedSettingsFactory.createReinforcementLearningSettings();
        settings = rlXmlSetting.updateXMLSettings(settings);

        policy = PolicyFactory.getPolicy(settings);
        actionSelector = new ReinforcementLearningActionSelector(policy);

        super.initialize(settings);

        // Spaghetti: Requires Java Access Bridge
        System.out.println("Are we running Java Access Bridge ? " + settings.get(ConfigTags.AccessBridgeEnabled, false));

        // TESTAR will execute the SUT with Java
        // We need this to add JMX parameters properly (-Dcom.sun.management.jmxremote.port=5000)
        WinProcess.java_execution = true;

        // Copy "bin/settings/protocolName/build.xml" file to "bin/jacoco/build.xml"
        copyJacocoBuildFile();

        startRunTime = System.currentTimeMillis();
    }

    /**
     * This method is used by TESTAR to determine the set of currently available actions.
     * You can use the SUT's current state, analyze the widgets and their properties to create
     * a set of sensible actions, such as: "Click every Button which is enabled" etc.
     * The return value is supposed to be non-null. If the returned set is empty, TESTAR
     * will stop generation of the current action and continue with the next one.
     * @param system the SUT
     * @param state the SUT's current state
     * @return a set of actions
     */
    @Override
    public Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
        //The super method returns a ONLY actions for killing unwanted processes if needed, or bringing the SUT to
        //the foreground. You should add all other actions here yourself.
        // These "special" actions are prioritized over the normal GUI actions in selectAction() / preSelectAction().
        Set<Action> actions = super.deriveActions(system, state);

        // To derive actions (such as clicks, drag&drop, typing ...) we should first create an action compiler.
        StdActionCompiler ac = new AnnotatingActionCompiler();

        // To find all possible actions that TESTAR can click on we should iterate through all widgets of the state.
        for(Widget w : getTopWidgets(state)){

            // Only consider enabled and non-blocked widgets
            if (w.get(Enabled, true) && !w.get(Blocked, false)) {

                // Do not build actions for widgets on the blacklist
                // The blackListed widgets are those that have been filtered during the SPY mode with the
                //CAPS_LOCK + SHIFT + Click clickfilter functionality.
                if (!blackListed(w)) {

                    //For widgets that are:
                    // - clickable
                    // and
                    // - unFiltered by any of the regular expressions in the Filter-tab, or
                    // - whitelisted using the clickfilter functionality in SPY mode (CAPS_LOCK + SHIFT + CNTR + Click)
                    // We want to create actions that consist of left clicking on them
                    if (isClickable(w) && (isUnfiltered(w) || whiteListed(w))) {
                        //Create a left click action with the Action Compiler, and add it to the set of derived actions
                        actions.add(ac.leftClickAt(w));
                    }

                    //For widgets that are:
                    // - typeable
                    // and
                    // - unFiltered by any of the regular expressions in the Filter-tab, or
                    // - whitelisted using the clickfilter functionality in SPY mode (CAPS_LOCK + SHIFT + CNTR + Click)
                    // We want to create actions that consist of typing into them
                    if (isTypeable(w) && (isUnfiltered(w) || whiteListed(w))) {
                        //Create a type action with the Action Compiler, and add it to the set of derived actions

                        // For Reinforcement Learning purposes, we will use temporally this text for type actions
                        actions.add(ac.clickTypeInto(w, "SameTypeStringText", true));
                    }
                }
            }
        }
        return actions;
    }

    /**
     * Select one of the available actions using a reinforcement learning action selection algorithm
     *
     * Normally super.selectAction(state, actions) updates information to the HTML sequence report, but since we
     * overwrite it, not always running it, we have take care of the HTML report here
     *
     * @param state the SUT's current state
     * @param actions the set of derived actions
     * @return the selected action (non-null!)
     */
    @Override
    public Action selectAction(SUT system, final State state, final Set<Action> actions) {
        //Call the preSelectAction method from the DefaultProtocol so that, if necessary,
        //unwanted processes are killed and SUT is put into foreground.
        final Action preSelectedAction = preSelectAction(system, state, actions).stream().findAny().get();
        if (preSelectedAction != null) {
            return preSelectedAction;
        }
        Action modelAction = stateModelManager.getAbstractActionToExecute(actions);
        if(modelAction==null) {
            System.out.println("State model based action selection did not find an action. Using random action selection.");
            // if state model fails, use random (default would call preSelectAction() again, causing double actions HTML report):
            return randomActionSelector.selectAction(state, actions);
        }
        return modelAction;
    }
}
