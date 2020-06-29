/***************************************************************************************************
 *
 * Copyright (c) 2013, 2014, 2015, 2016, 2017, 2018, 2019 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018, 2019 Open Universiteit - www.ou.nl
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


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.fruit.Assert;
import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.*;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.exceptions.*;
import org.fruit.monkey.Settings;
import org.testar.protocols.DesktopProtocol;

import static org.fruit.alayer.Tags.*;

/**
 * This protocol provides default TESTAR behaviour to test Windows desktop applications.
 * <p>
 * It uses random action selection algorithm.
 */
public class Protocol_desktop_generic extends DesktopProtocol {
    public final class WidgetClick extends TaggableBase implements Action {
        private static final long serialVersionUID = 259015065012204913L;
        private final String widgetPath;

        public WidgetClick(String widgetPath) {
            Assert.notNull(widgetPath);
            this.widgetPath = widgetPath;
        }

        public String toString() {
            return "Widget Click " + this.widgetPath;
        }

        @Override
        public String toString(Role... discardParameters) {
            return toString();
        }

        public void run(SUT system, State state, double duration) {
            try {
                Assert.notNull(system);
                Util.pause(duration);
                if (state == null) {
                    state = getState(system);
                }

                Action action = getActionByWidgetPath(this.widgetPath, state);
                action.run(system, state, duration);
            } catch (NoSuchTagException tue) {
                throw new ActionFailedException(tue);
            }
        }

        @Override
        public String toShortString() {
            return toString();
        }

        @Override
        public String toParametersString() {
            return toString();
        }

        protected Action getActionByWidgetPath(String widgetPath, State state) throws ActionBuildException {
            StdActionCompiler ac = new AnnotatingActionCompiler();

            List<Action> actions = new ArrayList<>();

            for (Widget w : state) {
                if (w.get(Path, "").contains(widgetPath)) {
                    actions.add(ac.leftClickAt(w));
                }
            }

            if (actions.size() == 0) {
                throw new ActionBuildException("Could not find action by path: " + widgetPath);
            } else if (actions.size() > 1) {
                throw new ActionBuildException("Too many actions found by path: " + widgetPath);
            }

            return actions.get(0);
        }

    }

    /**
     * Called once during the life time of TESTAR
     * This method can be used to perform initial setup work
     *
     * @param settings the current TESTAR settings as specified by the user.
     */
    @Override
    protected void initialize(Settings settings) {
        super.initialize(settings);
    }

    /**
     * This methods is called before each test sequence, before startSystem(),
     * allowing for example using external profiling software on the SUT
     * <p>
     * HTML sequence report will be initialized in the super.preSequencePreparations() for each sequence
     */
    @Override
    protected void preSequencePreparations() {
        super.preSequencePreparations();
    }

    /**
     * This method is called when TESTAR starts the System Under Test (SUT). The method should
     * take care of
     * 1) starting the SUT (you can use TESTAR's settings obtainable from <code>settings()</code> to find
     * out what executable to run)
     * 2) waiting until the system is fully loaded and ready to be tested (with large systems, you might have to wait several
     * seconds until they have finished loading)
     *
     * @return a started SUT, ready to be tested.
     */
    @Override
    protected SUT startSystem() throws SystemStartException {
        return super.startSystem();
    }

    /**
     * This method is invoked each time the TESTAR starts the SUT to generate a new sequence.
     * This can be used for example for bypassing a login screen by filling the username and password
     * or bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
     * the SUT's configuration files etc.)
     */
    @Override
    protected void beginSequence(SUT system, State state) {
        super.beginSequence(system, state);

        new CompoundAction.Builder()
                // Click text area
                .add(new WidgetClick("[0, 0, 0, 2]"), 0.1)
                // Type "x = 10"
                .add(new Type("x"), 0.1)
                .add(new KeyDown(KBKeys.VK_SPACE), 0.1)
                .add(new KeyDown(KBKeys.VK_EQUALS), 0.1)
                .add(new KeyDown(KBKeys.VK_SPACE), 0.1)
                .add(new Type("10"), 0.1)
                // Run Python program
                .add(new KeyDown(KBKeys.VK_F5), 0.1)
                .add(new KeyUp(KBKeys.VK_F5), 0.1)
                // Type name for new file
                .add(new Type("example"), 0.1)
                .add(new KeyDown(KBKeys.VK_ENTER), 0.1)
                .build()
                .run(system, null, 0.1);
    }

    /**
     * This method is called when the TESTAR requests the state of the SUT.
     * Here you can add additional information to the SUT's state or write your
     * own state fetching routine. The state should have attached an oracle
     * (TagName: <code>Tags.OracleVerdict</code>) which describes whether the
     * state is erroneous and if so why.
     * <p>
     * super.getState(system) puts the state information also to the HTML sequence report
     *
     * @return the current state of the SUT with attached oracle.
     */
    @Override
    protected State getState(SUT system) throws StateBuildException {
        return super.getState(system);
    }

    /**
     * The getVerdict methods implements the online state oracles that
     * examine the SUT's current state and returns an oracle verdict.
     *
     * @return oracle verdict, which determines whether the state is erroneous and why.
     */
    @Override
    protected Verdict getVerdict(State state) {
        // The super methods implements the implicit online state oracles for:
        // system crashes
        // non-responsiveness
        // suspicious titles
        Verdict verdict = super.getVerdict(state);

        //--------------------------------------------------------
        // MORE SOPHISTICATED STATE ORACLES CAN BE PROGRAMMED HERE
        //--------------------------------------------------------

        return verdict;
    }

    /**
     * This method is used by TESTAR to determine the set of currently available actions.
     * You can use the SUT's current state, analyze the widgets and their properties to create
     * a set of sensible actions, such as: "Click every Button which is enabled" etc.
     * The return value is supposed to be non-null. If the returned set is empty, TESTAR
     * will stop generation of the current action and continue with the next one.
     *
     * @param system the SUT
     * @param state  the SUT's current state
     * @return a set of actions
     */
    @Override
    protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {

        //The super method returns a ONLY actions for killing unwanted processes if needed, or bringing the SUT to
        //the foreground. You should add all other actions here yourself.
        // These "special" actions are prioritized over the normal GUI actions in selectAction() / preSelectAction().
        Set<Action> actions = super.deriveActions(system, state);


        // Derive left-click actions, click and type actions, and scroll actions from
        // top level (highest Z-index) widgets of the GUI:
        actions = deriveClickTypeScrollActionsFromTopLevelWidgets(actions, system, state);

        if (actions.isEmpty()) {
            // If the top level widgets did not have any executable widgets, try all widgets:
//			System.out.println("No actions from top level widgets, changing to all widgets.");
            // Derive left-click actions, click and type actions, and scroll actions from
            // all widgets of the GUI:
            actions = deriveClickTypeScrollActionsFromAllWidgetsOfState(actions, system, state);
        }

        //return the set of derived actions
        return actions;
    }

    /**
     * Select one of the available actions using an action selection algorithm (for example random action selection)
     * <p>
     * super.selectAction(state, actions) updates information to the HTML sequence report
     *
     * @param state   the SUT's current state
     * @param actions the set of derived actions
     * @return the selected action (non-null!)
     */
    @Override
    protected Action selectAction(State state, Set<Action> actions) {
        return (super.selectAction(state, actions));
    }

    /**
     * Execute the selected action.
     * <p>
     * super.executeAction(system, state, action) is updating the HTML sequence report with selected action
     *
     * @param system the SUT
     * @param state  the SUT's current state
     * @param action the action to execute
     * @return whether or not the execution succeeded
     */
    @Override
    protected boolean executeAction(SUT system, State state, Action action) {
        return super.executeAction(system, state, action);
    }

    /**
     * TESTAR uses this method to determine when to stop the generation of actions for the
     * current sequence. You can stop deriving more actions after:
     * - a specified amount of executed actions, which is specified through the SequenceLength setting, or
     * - after a specific time, that is set in the MaxTime setting
     *
     * @return if <code>true</code> continue generation, else stop
     */
    @Override
    protected boolean moreActions(State state) {
        return super.moreActions(state);
    }


    /**
     * TESTAR uses this method to determine when to stop the entire test sequence
     * You could stop the test after:
     * - a specified amount of sequences, which is specified through the Sequences setting, or
     * - after a specific time, that is set in the MaxTime setting
     *
     * @return if <code>true</code> continue test, else stop
     */
    @Override
    protected boolean moreSequences() {
        return super.moreSequences();
    }

    /**
     * Here you can put graceful shutdown sequence for your SUT
     *
     * @param system
     */
    @Override
    protected void stopSystem(SUT system) {
        super.stopSystem(system);
    }

    /**
     * This methods is called after each test sequence, allowing for example using external profiling software on the SUT
     * <p>
     * super.postSequenceProcessing() is adding test verdict into the HTML sequence report
     */
    @Override
    protected void postSequenceProcessing() {
        super.postSequenceProcessing();
    }
}
