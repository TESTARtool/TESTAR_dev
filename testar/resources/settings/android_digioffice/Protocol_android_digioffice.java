
/***************************************************************************************************
 *
 * Copyright (c) 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2025 Open Universiteit - www.ou.nl
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

import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.testar.managers.InputDataManager;
import org.testar.settings.Settings;

import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;

import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.exceptions.*;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.oracles.Oracle;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Main;
import org.testar.monkey.Pair;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.android.AndroidAppiumFramework;
import org.testar.monkey.alayer.android.actions.*;
import org.testar.monkey.alayer.android.enums.AndroidRoles;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import org.testar.protocols.AndroidProtocol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Protocol_android_digioffice extends AndroidProtocol {

    private String XPATH_FILTER_FILE = "/android_digioffice_xpath_filter.txt";

    private String VERDICT_LIST_FILE = "/android_digioffice_verdict_list.txt";
    private List<String> listErrorVerdictInfo = new ArrayList<>();

    /**
     * Called once during the life time of TESTAR
     * This method can be used to perform initial setup work
     * 
     * @param settings the current TESTAR settings as specified by the user.
     */
    @Override
    protected void initialize(Settings settings) {
        super.initialize(settings);

        XPATH_FILTER_FILE = Settings.getSettingsPath() + "/android_digioffice_xpath_filter.txt";
    }

    @Override
    protected SUT startSystem() throws SystemStartException {
        SUT system = super.startSystem();

        // Courtesy wait seconds, maybe not necessary
        Util.pause(2);

        // Load the DigiOffice name and URL for the account endpoint
        initialLoadEndpoitAccount(system);

        // Now the DigiOffice loads the authentication
        // And makes a transition to Microsoft double verification login
        clickMicrosoftPickAccount(system);
        Util.pause(5); // TODO: Make press enter more reliable and delete this pause
        pressEnterToContinueMicrosoftPickAccount(system);

        // Finally wait until the DigiOffice state after the login appears
        waitDigiOfficeStateAppears(system);

        // Courtesy wait seconds, maybe not necessary
        Util.pause(2);

        return system;
    }

    private void initialLoadEndpoitAccount(SUT system) {
        State initialState = getState(system);

        // Type Verbinding Naam and Verbinding URL
        for (Widget w : initialState) {
            // Verbinding Naam
            if (w.get(AndroidTags.AndroidAccessibilityId, "").contains("Input Field")
                    && w.parent() != null
                    && w.parent().parent() != null
                    && w.parent().parent().get(AndroidTags.AndroidNodeIndex, -1) == 1) {
                System.out.println("Typing Verbinding Naam...");
                Action typeVerbindingNaam = new AndroidActionType(initialState, w, "testar");
                typeVerbindingNaam.run(system, initialState, 0.1);
            }

            // Verbinding URL
            if (w.get(AndroidTags.AndroidAccessibilityId, "").contains("Input Field")
                    && w.parent() != null
                    && w.parent().parent() != null
                    && w.parent().parent().get(AndroidTags.AndroidNodeIndex, -1) == 2) {
                System.out.println("Typing Verbinding URL...");
                Action typeVerbindingUrl = new AndroidActionType(initialState, w, System.getenv("digioffice_endpoint"));
                typeVerbindingUrl.run(system, initialState, 0.1);
            }
        }

        // Click Verbinding button
        for (Widget w : initialState) {
            // Verbinding Naam
            if (w.get(AndroidTags.AndroidText, "").contains("Voeg verbinding toe")) {
                System.out.println("Clicking 'Voeg verbinding toe' button...");
                Action clickVerbindingButton = new AndroidActionClick(initialState, w);
                clickVerbindingButton.run(system, initialState, 0.1);
            }
        }
    }

    private void clickMicrosoftPickAccount(SUT system) {
        for (int attempt = 0; attempt < 60; attempt++) {
            System.out.println("Waiting Microsoft 'Pick an account' button... attempt: " + attempt);
            Util.pause(2);

            State state = getState(system);
            for (Widget w : state) {
                // Click Microsoft Sign In - Pick and account button
                if (w.get(AndroidTags.AndroidHint, "").contains("Pick an account")) {
                    System.out.println("Clicking 'Pick an account' button...");
                    Action clickPickAnAccount = new AndroidActionClick(state, w);
                    clickPickAnAccount.run(system, state, 0.1);
                    return;
                }
            }
        }
    }

    private void pressEnterToContinueMicrosoftPickAccount(SUT system) {
        for (int attempt = 0; attempt < 60; attempt++) {
            System.out.println("Waiting to press ENTER to continue... attempt: " + attempt);
            Util.pause(2);

            State state = getState(system);
            for (Widget w : state) {
                // Microsoft WebKit continue
                if (w.get(AndroidTags.AndroidClassName, "").contains("webkit.WebView")
                        && w.get(AndroidTags.AndroidText, "").contains("Sign in to your account")) {
                    System.out.println("Press ENTER to continue...");
                    AndroidAppiumFramework.pressKeyEvent(new KeyEvent(AndroidKey.ENTER));
                    return;
                }
            }
        }
    }

    private void waitDigiOfficeStateAppears(SUT system) {
        for (int attempt = 0; attempt < 60; attempt++) {
            System.out.println("Waiting to DigiOffice state after login appears... attempt: " + attempt);
            Util.pause(2);

            State state = getState(system);
            for (Widget w : state) {
                // DigiOffice state after login appears
                if (w.get(AndroidTags.AndroidClassName, "").contains("widget.TextView")
                        && w.get(AndroidTags.AndroidText, "").contains("DigiOffice")
                        && w.get(AndroidTags.AndroidPackageName, "").contains("com.digioffice.app")) {
                    System.out.println("DigiOffice state is ready!");
                    return;
                }
            }
        }
    }

    /**
     * This method is invoked each time the TESTAR starts the SUT to generate a new
     * sequence.
     * This can be used for example for bypassing a login screen by filling the
     * username and password
     * or bringing the system into a specific start state which is identical on each
     * start (e.g. one has to delete or restore
     * the SUT's configuration files etc.)
     */
    @Override
    protected void beginSequence(SUT system, State state) {
        super.beginSequence(system, state);

        // Load known verdict list
        VERDICT_LIST_FILE = Settings.getSettingsPath() + "/android_digioffice_verdict_list.txt";
        loadVerdictListFile();
    }

    private void loadVerdictListFile() {
        ObjectInputStream ois = null;
        try {
            File verdictListFile = new File(VERDICT_LIST_FILE);
            if (verdictListFile.exists()) {
                FileInputStream fis = new FileInputStream(verdictListFile);
                ois = new ObjectInputStream(fis);
                listErrorVerdictInfo = (List) ois.readObject();
                ois.close();
                return;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        listErrorVerdictInfo = new ArrayList<>();
    }

    /**
     * This method is called when the TESTAR requests the state of the SUT.
     * Here you can add additional information to the SUT's state or write your
     * own state fetching routine. The state should have attached an oracle
     * (TagName: <code>Tags.OracleVerdict</code>) which describes whether the
     * state is erroneous and if so why.
     * 
     * @return the current state of the SUT with attached oracle.
     */
    @Override
    protected State getState(SUT system) throws StateBuildException {
        State state = super.getState(system);

        Widget chooseActionModal = state;

        for (Widget widget : state) {
            // When allowInvisibleElements setting is enabled, some Android sources might be
            // overlapped
            // This makes the default hit tester logic detect that the widgets are obscured
            widget.set(Tags.HitTester, Util.TrueTester);

            if (widget.get(AndroidTags.AndroidAccessibilityId, "").contains("Choose an action")) {
                chooseActionModal = widget;
            }
        }

        if (!(chooseActionModal instanceof State)) {
            for (Widget widget : state) {
                if (!isSonOfWidget(chooseActionModal, widget)) {
                    // Make hit tester false if a modal exists and the widget is not son of it
                    widget.set(Tags.HitTester, Util.FalseTester);
                }
            }
        }

        return state;
    }

    private boolean isSonOfWidget(Widget parent, Widget widget) {
        if (widget.parent() == null)
            return false;
        else if (widget.parent().equals(parent))
            return true;
        else
            return isSonOfWidget(parent, widget.parent());
    }

    /**
     * The getVerdict methods implements the online state oracles that
     * examine the SUT's current state and returns an oracle verdict.
     * 
     * @return oracle verdict, which determines whether the state is erroneous and
     *         why.
     */
    @Override
    protected Verdict getVerdict(State state) {

        // 1) If we unexpectedly navigated to the emulator default activity
        if (state.get(AndroidTags.AndroidActivity, "").contains("NexusLauncherActivity")) {
            // We assume the DigiOffice app has crashed
            return new Verdict(Verdict.Severity.UNEXPECTEDCLOSE,
                    "Android app offline! Closed Unexpectedly! I assume it crashed!");
        }

        // 2) The super methods implements the implicit online state oracles for
        // suspicious tags (exception, error messages)
        Verdict verdict = super.getVerdict(state);
        if (shouldReturnVerdict(verdict)) {
            return verdict;
        }

        // 3) Custom invariant oracle for duplicated elements
        Oracle duplicatedViewGroupOracle = new AndroidInvariantDuplicatedViewGroup();
        Verdict duplicatedViewGroupVerdict = duplicatedViewGroupOracle.getVerdict(state);
        if (shouldReturnVerdict(duplicatedViewGroupVerdict)) {
            return duplicatedViewGroupVerdict;
        }

        // 4) Clickable element without any textual description
        Oracle clickableWithoutDescriptionOracle = new AndroidClickableElementWithoutDescription();
        Verdict clickableWithoutDescriptionVerdict = clickableWithoutDescriptionOracle.getVerdict(state);
        if (shouldReturnVerdict(clickableWithoutDescriptionVerdict)) {
            return clickableWithoutDescriptionVerdict;
        }

        return Verdict.OK;
    }

    /**
     * We want to return the verdict if it is not OK,
     * and not on the detected failures list (it's a new failure).
     */
    private boolean shouldReturnVerdict(Verdict verdict) {
        return verdict.severity() > Verdict.Severity.OK.getValue()
                && listErrorVerdictInfo.stream().noneMatch(info -> info.contains(verdict.info().replace("\n", " ")));
    }

    /**
     * This method is used by TESTAR to determine the set of currently available
     * actions.
     * You can use the SUT's current state, analyze the widgets and their properties
     * to create
     * a set of sensible actions, such as: "Click every Button which is enabled"
     * etc.
     * The return value is supposed to be non-null. If the returned set is empty,
     * TESTAR
     * will stop generation of the current action and continue with the next one.
     * 
     * @param system the SUT
     * @param state  the SUT's current state
     * @return a set of actions
     */
    @Override
    protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
        // The super method returns a ONLY actions for killing unwanted processes if
        // needed, or bringing the SUT to
        // the foreground. You should add all other actions here yourself.
        // These "special" actions are prioritized over the normal GUI actions in
        // selectAction() / preSelectAction().
        Set<Action> actions = super.deriveActions(system, state);

        // create an action compiler, which helps us create actions
        // such as clicks, drag&drop, typing ...
        StdActionCompiler ac = new AnnotatingActionCompiler();

        // boolean which ensures only the first encountered widget will get scroll
        // action.
        boolean oneScroll = true;

        // iterate through all widgets
        for (Widget widget : state) {

            // Ignore widgets that are not filtered
            if (!isUnfiltered(widget)) {
                continue;
            }

            // type into text boxes
            if (isTypeable(widget)) {
                String randomInput = InputDataManager.getRandomTextInputData(widget);
                actions.add(new AndroidActionType(state, widget, randomInput));
            }

            // left clicks
            if (isClickable(widget)) {
                actions.add(new AndroidActionClick(state, widget));
            }

            // Scroll action
            if (oneScroll) {
                if (isScrollable(widget)) {
                    actions.add(new AndroidActionScroll(state, widget));
                    oneScroll = false;
                }
            }

            if (isLongClickable(widget)) {
                actions.add(new AndroidActionLongClick(state, widget));
            }
        }

        // Add system calls
        // Workaround to pass a Android widget to the systemActions, otherwise will
        // complain about not being able to set
        // Tags. Additionally creating an SystemAction interface will not work as the
        // returned type must be Action.
        Widget topWidget = state.root().child(0);
        Boolean checkAddSystemActions = settings.get(ConfigTags.UseSystemActions, false);

        if (checkAddSystemActions) {
            actions.add(
                    // System orientation swap
                    new AndroidSystemActionOrientation(state, topWidget));

            actions.add(
                    // Receive a call
                    new AndroidSystemActionCall(state, topWidget));

            actions.add(
                    // Receive text message
                    new AndroidSystemActionText(state, topWidget));
        }

        return actions;
    }

    @Override
    protected boolean isUnfiltered(Widget w) {
        String widgetXPath = w.get(AndroidTags.AndroidXpath, "");
        if (widgetXPath != null && !widgetXPath.isEmpty()) {
            Set<String> xpathsToFilter = getFilteredXPaths();
            if (xpathsToFilter.contains(widgetXPath)) {
                return false;
            }
        }

        return super.isUnfiltered(w);
    }

    private Set<String> getFilteredXPaths() {
        Set<String> filteredXPaths = Collections.emptySet();

        try {
            List<String> lines = Files.readAllLines(Paths.get(XPATH_FILTER_FILE), StandardCharsets.UTF_8);
            Set<String> result = new HashSet<>();
            for (String line : lines) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    result.add(trimmed);
                }
            }
            filteredXPaths = Collections.unmodifiableSet(result);
        } catch (IOException e) {
            // Do nothing if the file is not readable
        }

        return filteredXPaths;
    }

    @Override
    protected Set<Action> preSelectAction(SUT system, State state, Set<Action> actions) {
        Set<Action> actionsToReturn = super.preSelectAction(system, state, actions);

        // If we are not anymore in the DigiOffice state
        String domainPackage = "com.digioffice.app";
        if (!widgetTreePackageMatches(state, domainPackage)) {

            if (lastExecutedAction != null && lastExecutedAction.get(Tags.OriginWidget, null) != null) {
                Widget lastExecutedWidget = lastExecutedAction.get(Tags.OriginWidget);
                String xPath = lastExecutedWidget.get(AndroidTags.AndroidXpath, "");
                // First, we add the xpath to the filtering file to avoid subsequent executions
                if (xPath != null && !xPath.isEmpty()) {
                    try {
                        Files.write(
                                Paths.get(XPATH_FILTER_FILE),
                                (xPath + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                                StandardOpenOption.CREATE,
                                StandardOpenOption.APPEND);
                    } catch (IOException e) {
                        System.err
                                .println("Failed to write XPath to file " + XPATH_FILTER_FILE + ": " + e.getMessage());
                    }
                }
            }

            // Second, we force a navigate back action to return to DigiOffice state
            Widget topWidget = state.root().child(0);
            Action backAction = new AndroidBackAction(state, topWidget);
            buildStateActionsIdentifiers(state, Collections.singleton(backAction));
            actionsToReturn = new HashSet<>(Collections.singletonList(backAction));
        }

        return actionsToReturn;
    }

    private boolean widgetTreePackageMatches(State state, String packageName) {
        for (Widget w : state) {
            String widgetPackageName = w.get(AndroidTags.AndroidPackageName, "");
            if (!widgetPackageName.isEmpty() && !widgetPackageName.equals(packageName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Select one of the available actions using an action selection algorithm (for
     * example random action selection)
     *
     * @param state   the SUT's current state
     * @param actions the set of derived actions
     * @return the selected action (non-null!)
     */
    @Override
    protected Action selectAction(State state, Set<Action> actions) {
        return super.selectAction(state, actions);
    }

    /**
     * This method is invoked each time after TESTAR finished the generation of a
     * sequence.
     */
    @Override
    protected void finishSequence() {
        super.finishSequence();
        // If the final Verdict is not OK and the verdict is not saved in the list
        // This is a new run fail verdict
        if (getFinalVerdict().severity() > Verdict.Severity.OK.getValue()
                && !listErrorVerdictInfo.contains(getFinalVerdict().info().replace("\n", " "))) {
            listErrorVerdictInfo.add(getFinalVerdict().info().replace("\n", " "));
            saveVerdictListFile();
        }
    }

    private void saveVerdictListFile() {
        try (FileOutputStream fos = new FileOutputStream(VERDICT_LIST_FILE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            oos.writeObject(listErrorVerdictInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

class AndroidInvariantDuplicatedViewGroup implements Oracle {

    @Override
    public void initialize() {
    }

    @Override
    public Verdict getVerdict(State state) {
        // 1) Join clickable AndroidViewGroup with non-empty accessibility id and a
        // parent
        List<Widget> viewGroupList = new ArrayList<>();
        for (Widget w : state) {
            if (w.get(Tags.Role, Roles.Widget).equals(AndroidRoles.AndroidViewGroup)
                    && !w.get(AndroidTags.AndroidAccessibilityId, "").isEmpty()
                    && w.get(AndroidTags.AndroidClickable, false)
                    && w.parent() != null) {
                viewGroupList.add(w);
            }
        }

        // 2) Group by parent and detect duplicates of AndroidAccessibilityId
        Map<Widget, Map<String, List<Widget>>> parentToWidgets = new HashMap<>();

        for (Widget w : viewGroupList) {
            Widget parent = w.parent();
            String accessId = w.get(AndroidTags.AndroidAccessibilityId, "");

            Map<String, List<Widget>> idToWidgets = parentToWidgets.computeIfAbsent(parent, p -> new HashMap<>());

            List<Widget> widgetsForId = idToWidgets.computeIfAbsent(accessId, id -> new ArrayList<>());

            widgetsForId.add(w);
        }

        // Now collect those with size > 1
        List<Widget> duplicatedViewGroupWidgets = new ArrayList<>();
        Set<String> duplicatedAccessIds = new HashSet<>();

        for (Map<String, List<Widget>> idToWidgets : parentToWidgets.values()) {
            for (Map.Entry<String, List<Widget>> entry : idToWidgets.entrySet()) {
                if (entry.getValue().size() > 1) {
                    duplicatedAccessIds.add(entry.getKey());
                    duplicatedViewGroupWidgets.addAll(entry.getValue());
                }
            }
        }

        // If the list of duplicated accessId list is not empty
        if (!duplicatedAccessIds.isEmpty()) {
            String verdictMsg = String.format(
                    "Detected duplicate accessibility IDs in ViewGroup(s) for widgets: %s ",
                    duplicatedAccessIds);

            Visualizer visualizer = new RegionsVisualizer(
                    getRedPen(),
                    getWidgetRegions(duplicatedViewGroupWidgets),
                    "Invariant Fault",
                    0.5, 0.5);

            return new Verdict(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT, verdictMsg, visualizer);
        }

        return Verdict.OK;
    }

}

class AndroidClickableElementWithoutDescription implements Oracle {

    @Override
    public void initialize() {
    }

    @Override
    public Verdict getVerdict(State state) {

        for (Widget w : state) {
            if (w.get(AndroidTags.AndroidClickable, false)
                    && w.get(AndroidTags.AndroidDisplayed, false)
                    && w.get(AndroidTags.AndroidAccessibilityId, "").trim().isEmpty()
                    && w.get(AndroidTags.AndroidText, "").trim().isEmpty()
                    && w.get(AndroidTags.AndroidHint, "").trim().isEmpty()) {

                String verdictMsg = String.format("Detected Clickable widget without any description: %s",
                        w.get(AndroidTags.AndroidXpath));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Arrays.asList(w)),
                        "Accessibility Fault",
                        0.5, 0.5);

                return new Verdict(Verdict.Severity.WARNING_ACCESSIBILITY_FAULT, verdictMsg, visualizer);
            }
        }

        return Verdict.OK;
    }

}
