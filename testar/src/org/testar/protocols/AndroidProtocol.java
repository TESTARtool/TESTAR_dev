/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2022 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 - 2022 Open Universiteit - www.ou.nl
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

import org.testar.plugin.NativeLinker;
import org.testar.reporting.HtmlSequenceReport;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.ConfigTags;
import org.testar.settings.Settings;
import org.testar.OutputStructure;
import org.testar.monkey.alayer.android.actions.AndroidBackAction;
import org.testar.monkey.alayer.android.enums.AndroidTags;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AndroidProtocol extends GenericUtilsProtocol {
    //Attributes for adding slide actions
    protected static double SCROLL_ARROW_SIZE = 36; // sliding arrows
    protected static double SCROLL_THICK = 16; //scroll thickness
    protected HtmlSequenceReport htmlReport;
    protected State latestState;

    /**
     * Called once during the life time of TESTAR
     * This method can be used to perform initial setup work
     * @param   settings  the current TESTAR settings as specified by the user.
     */
    @Override
    protected void initialize(Settings settings){
        NativeLinker.addAndroidOS();
        super.initialize(settings);
    }

    /**
     * This methods is called before each test sequence, allowing for example using external profiling software on the SUT
     */
    @Override
    protected void preSequencePreparations() {
        //initializing the HTML sequence report:
        htmlReport = new HtmlSequenceReport();
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
        //Spy mode didn't use the html report
        if(settings.get(ConfigTags.Mode) == Modes.Spy) {
            return super.getState(system);
        }

        latestState = super.getState(system);

        //adding state to the HTML sequence report:
        htmlReport.addState(latestState);
        return latestState;
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
        // The super method returns a ONLY actions for killing unwanted processes if needed, or bringing the SUT to
        // the foreground. You should add all other actions here yourself.
        // These "special" actions are prioritized over the normal GUI actions in selectAction() / preSelectAction().
        return super.deriveActions(system,state);
    }

    // Modified isClickable to look at the clickable attribute passed by Appium.
    @Override
    protected boolean isClickable(Widget w) {
//        return (w.get(AndroidTags.AndroidClassName, "").equals("android.widget.ImageButton")
//                || w.get(AndroidTags.AndroidClassName, "").equals("android.widget.Button")
//                || w.get(AndroidTags.AndroidClassName, "").equals("android.widget.CheckBox")
//                || w.get(AndroidTags.AndroidClassName, "").equals("android.widget.TextView") &&
//                w.get(AndroidTags.AndroidClickable, false));
        return(w.get(AndroidTags.AndroidClickable, false) && !(w.get(AndroidTags.AndroidClassName, "").equals("android.widget.EditText")) && w.get(AndroidTags.AndroidEnabled));
    }

    @Override
    protected boolean isTypeable(Widget w) {
        return (w.get(AndroidTags.AndroidClassName, "").equals("android.widget.EditText"));
    }

    protected boolean isScrollable(Widget w) {
        return(w.get(AndroidTags.AndroidScrollable, false));
    }

    protected boolean isLongClickable(Widget w) {
        return(w.get(AndroidTags.AndroidLongClickable, false));
    }

    /**
     * Overwriting to add HTML report writing into it
     *
     * @param state
     * @param actions
     * @return
     */
    @Override
    protected Set<Action> preSelectAction(SUT system, State state, Set<Action> actions){

        Widget topWidget = state.root().child(0);

        if (actions.size() == 0) {
            Action backAction = new AndroidBackAction(state, topWidget);
            buildStateActionsIdentifiers(state, Collections.singleton(backAction));
            htmlReport.addActions(actions);
            return new HashSet<>(Collections.singletonList(backAction));
        }

        // adding available actions into the HTML report:
        htmlReport.addActions(actions);
        return(super.preSelectAction(system, state, actions));
    }

    /**
     * Select one of the available actions (e.g. at random)
     * @param state the SUT's current state
     * @param actions the set of derived actions
     * @return  the selected action (non-null!)
     */
    @Override
    protected Action selectAction(State state, Set<Action> actions){
        return super.selectAction(state, actions);
    }

    /**
     * Execute the selected action.
     * @param system the SUT
     * @param state the SUT's current state
     * @param action the action to execute
     * @return whether or not the execution succeeded
     */
    @Override
    protected boolean executeAction(SUT system, State state, Action action){
        // Calls the super.executeAction where the specific behavior is defined on what to do when Android is the environment
        // adding the action that is going to be executed into HTML report:
        htmlReport.addSelectedAction(state, action);
        return super.executeAction(system, state, action);
    }

    /**
     * This methods is called after each test sequence, allowing for example using external profiling software on the SUT
     */
    @Override
    protected void postSequenceProcessing() {
        htmlReport.addTestVerdict(getFinalVerdict());

        String sequencesPath = getGeneratedSequenceName();
        try {
            sequencesPath = new File(getGeneratedSequenceName()).getCanonicalPath();
        }catch (Exception e) {}

        String status = (getFinalVerdict()).verdictSeverityTitle();
        String statusInfo = (getFinalVerdict()).info();

        statusInfo = statusInfo.replace("\n"+Verdict.OK.info(), "");

        //Timestamp(generated by logger) SUTname Mode SequenceFileObject Status "StatusInfo"
        INDEXLOG.info(OutputStructure.executedSUTname
                + " " + settings.get(ConfigTags.Mode, mode())
                + " " + sequencesPath
                + " " + status + " \"" + statusInfo + "\"" );

        htmlReport.close();
    }

    @Override
    protected void closeTestSession() {
        super.closeTestSession();
        NativeLinker.cleanAndroidOS();
    }

}
