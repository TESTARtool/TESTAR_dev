
/***************************************************************************************************
 *
 * Copyright (c) 2025 - 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2025 - 2026 Open Universiteit - www.ou.nl
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
import org.testar.oracles.OracleSelection;
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

    enum AUTH {
        CREDENTIALS,
        PICK_ACCOUNT,
        INVALID
    }

    private String XPATH_FILTER_FILE = "/android_digioffice_xpath_filter.txt";

    private String VERDICT_LIST_FILE = "/android_digioffice_verdict_list.txt";
    private List<String> listErrorVerdictInfo = new ArrayList<>();

    private List<Oracle> extendedOraclesList = new ArrayList<>();

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

    /**
     * This methods is called before each test sequence, before startSystem(),
     * allowing for example using external profiling software on the SUT
     *
     * HTML sequence report will be initialized in the
     * super.preSequencePreparations() for each sequence
     */
    @Override
    protected void preSequencePreparations() {
        super.preSequencePreparations();
        extendedOraclesList = OracleSelection.loadExtendedOracles(settings.get(ConfigTags.ExtendedOracles));
    }

    @Override
    protected SUT startSystem() throws SystemStartException {
        if(!checkDigiOfficeEnvironmentVariables()) {
            System.err.println("Skipping DigiOffice login: missing env vars.");
            return super.startSystem();
        }

        SUT system = super.startSystem();

        // Load the DigiOffice name and URL for the account endpoint
        initialLoadEndpoitAccount(system);

        // Now the DigiOffice loads the authentication
        // Wait until detected the corresponding authentication method
        AUTH auth =  checkAuthenticationMethodMicrosoft(system);

        if(AUTH.CREDENTIALS.equals(auth)){
            typekMicrosoftUserCredentials(system);
            Util.pause(5);
            typekMicrosofPasswordCredentials(system);
        } else if(AUTH.PICK_ACCOUNT.equals(auth)){
            clickMicrosoftPickAccount(system);
            Util.pause(5);
            pressEnterToContinueMicrosoftPickAccount(system);
        } else {
            System.err.println("Invalid Authentication process");
            return system;
        }

        // Finally wait until the DigiOffice state after the login appears
        waitDigiOfficeStateAppears(system);

        return system;
    }

    private boolean checkDigiOfficeEnvironmentVariables() {
        try {
            String endpoint = System.getenv("digioffice_endpoint");
            String user = System.getenv("digioffice_user");
            String password = System.getenv("digioffice_password");
            boolean ok = true;

            if (endpoint == null || endpoint.isBlank()) {
                System.err.println("The 'digioffice_endpoint' environment variable is not defined (or is blank)");
                ok = false;
            }
            if (user == null || user.isBlank()) {
                System.err.println("The 'digioffice_user' environment variable is not defined (or is blank)");
                ok = false;
            }
            if (password == null || password.isBlank()) {
                System.err.println("The 'digioffice_password' environment variable is not defined (or is blank)");
                ok = false;
            }
            return ok;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    private void initialLoadEndpoitAccount(SUT system) {
        State initialState = getState(system);

        // Type Verbinding Naam and Verbinding URL
        for (Widget w : initialState) {
            // Verbinding Naam
            if (w.get(AndroidTags.AndroidAccessibilityId, "").contains("Input Field")
                    && w.get(AndroidTags.AndroidResourceId, "").contains("name-input")) {
                System.out.println("Typing Verbinding Naam...");
                Action typeVerbindingNaam = new AndroidActionType(initialState, w, "testar");
                typeVerbindingNaam.run(system, initialState, 0.1);
            }

            // Verbinding URL
            if (w.get(AndroidTags.AndroidAccessibilityId, "").contains("Input Field")
                    && w.get(AndroidTags.AndroidResourceId, "").contains("endpoint-input")) {
                System.out.println("Typing Verbinding URL...");
                Action typeVerbindingUrl = new AndroidActionType(initialState, w, System.getenv("digioffice_endpoint"));
                typeVerbindingUrl.run(system, initialState, 0.1);
            }
        }

        // Click Verbinding button is being enabled
        Util.pause(2);
        initialState = getState(system);
        for (Widget w : initialState) {
            // Verbinding Naam
            if (w.get(AndroidTags.AndroidText, "").contains("Voeg verbinding toe")) {
                System.out.println("Clicking 'Voeg verbinding toe' button...");
                Action clickVerbindingButton = new AndroidActionClick(initialState, w);
                clickVerbindingButton.run(system, initialState, 0.1);
            }
        }
    }

    private AUTH checkAuthenticationMethodMicrosoft(SUT system) {
        for (int attempt = 0; attempt < 60; attempt++) {
            System.out.println("Waiting Microsoft Sign In method... attempt: " + attempt);
            Util.pause(2);

            State state = getState(system);
            for (Widget w : state) {
                if (w.get(AndroidTags.AndroidHint, "").contains("Enter your email or phone Sign in")) {
                    System.out.println("Detected Microsoft credentials Sign In method");
                    return AUTH.CREDENTIALS;
                }
                if (w.get(AndroidTags.AndroidHint, "").contains("Pick an account")) {
                    System.out.println("Detected Microsoft pick account Sign In method");
                    return AUTH.PICK_ACCOUNT;
                }
            }
        }
        return AUTH.INVALID;
    } 

    private void typekMicrosoftUserCredentials(SUT system) {
        for (int attempt = 0; attempt < 60; attempt++) {
            State state = getState(system);
            for (Widget w : state) {
                if (w.get(AndroidTags.AndroidHint, "").contains("Enter your email or phone Sign in")) {
                    System.out.println("Typing in 'Enter your email or phone Sign in' field...");
                    Action typeMicrosoftUser = new AndroidActionType(state, w, System.getenv("digioffice_user"));
                    typeMicrosoftUser.run(system, state, 0.1);
                    Util.pause(2);
                    AndroidAppiumFramework.pressKeyEvent(new KeyEvent(AndroidKey.ENTER));
                    return;
                }
            }
        }
    }

    private void typekMicrosofPasswordCredentials(SUT system) {
        for (int attempt = 0; attempt < 60; attempt++) {
            State state = getState(system);
            for (Widget w : state) {
                if (w.get(AndroidTags.AndroidHint, "").contains("Enter the password for " + System.getenv("digioffice_user"))) {
                    System.out.println("Typing in 'Enter the password' field...");
                    Action typeMicrosoftPassword = new AndroidActionType(state, w, System.getenv("digioffice_password"));
                    typeMicrosoftPassword.run(system, state, 0.1);
                    Util.pause(2);
                    AndroidAppiumFramework.pressKeyEvent(new KeyEvent(AndroidKey.ENTER));
                    return;
                }
            }
        }
    }

    private void clickMicrosoftPickAccount(SUT system) {
        for (int attempt = 0; attempt < 60; attempt++) {
            State state = getState(system);
            for (Widget w : state) {
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
            State state = getState(system);
            for (Widget w : state) {
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
        Oracle duplicatedViewGroupOracle = new AndroidDigiOfficeInvariantDuplicatedViewGroup();
        Verdict duplicatedViewGroupVerdict = duplicatedViewGroupOracle.getVerdict(state);
        if (shouldReturnVerdict(duplicatedViewGroupVerdict)) {
            return duplicatedViewGroupVerdict;
        }

        // 4) Header Is Not Empty Not NA
        Oracle headerOracle = new AndroidDigiOfficeHeaderIsNotEmptyNotNA();
        Verdict headerVerdict = headerOracle.getVerdict(state);
        if (shouldReturnVerdict(headerVerdict)) {
            return headerVerdict;
        }

        // 5) Person Name Is Not Empty Not NA
        Oracle personNameOracle = new AndroidDigiOfficePersonNameIsNotEmptyNotNA();
        Verdict personNameVerdict = personNameOracle.getVerdict(state);
        if (shouldReturnVerdict(personNameVerdict)) {
            return personNameVerdict;
        }

        // 6) Company Name Is Not Empty Not NA
        Oracle companyNameOracle = new AndroidDigiOfficeCompanyNameIsNotEmptyNotNA();
        Verdict companyNameVerdict = companyNameOracle.getVerdict(state);
        if (shouldReturnVerdict(companyNameVerdict)) {
            return companyNameVerdict;
        }

        // 7) Widget Is Not Empty
        Oracle widgetIsNotEmptyOracle = new AndroidDigiOfficeWidgetIsNotEmpty();
        Verdict widgetIsNotEmptyVerdict = widgetIsNotEmptyOracle.getVerdict(state);
        if (shouldReturnVerdict(widgetIsNotEmptyVerdict)) {
            return widgetIsNotEmptyVerdict;
        }

        // 8) Phone Text Is Not Valid
        Oracle phoneOracle = new AndroidDigiOfficePhoneTextIsNotValid();
        Verdict phoneVerdict = phoneOracle.getVerdict(state);
        if (shouldReturnVerdict(phoneVerdict)) {
            return phoneVerdict;
        }

        // 9) Mobile Text Is Not Valid
        Oracle mobileOracle = new AndroidDigiOfficeMobileTextIsNotValid();
        Verdict mobileVerdict = mobileOracle.getVerdict(state);
        if (shouldReturnVerdict(mobileVerdict)) {
            return mobileVerdict;
        }

        // 10) Email Text Is Not Valid
        Oracle emailOracle = new AndroidDigiOfficeEmailTextIsNotValid();
        Verdict emailVerdict = emailOracle.getVerdict(state);
        if (shouldReturnVerdict(emailVerdict)) {
            return emailVerdict;
        }

        // 11) Website Text Is Not Valid
        Oracle websiteOracle = new AndroidDigiOfficeWebsiteTextIsNotValid();
        Verdict websiteVerdict = websiteOracle.getVerdict(state);
        if (shouldReturnVerdict(websiteVerdict)) {
            return websiteVerdict;
        }

        // 12) Search bar contains clear option
        Oracle searchClearOracle = new AndroidDigiOfficeSearchBarContainsClear();
        Verdict searchClearVerdict = searchClearOracle.getVerdict(state);
        if (shouldReturnVerdict(searchClearVerdict)) {
            return searchClearVerdict;
        }

        // "ExtendedOracles" offered by TESTAR in the test.settings or Oracles GUI
        // dialog
        for (Oracle extendedOracle : extendedOraclesList) {
            Verdict extendedVerdict = extendedOracle.getVerdict(state);

            // If the Custom Verdict is not OK and was not detected in a previous sequence
            // return verdict with failure state
            if (shouldReturnVerdict(extendedVerdict)) {
                return extendedVerdict;
            }

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

class AndroidDigiOfficeInvariantDuplicatedViewGroup implements Oracle {

    @Override
    public void initialize() {
    }

    @Override
    public Verdict getVerdict(State state) {
        Verdict finalVerdict = Verdict.OK;

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

            Verdict duplicatedVerdict = new Verdict(
                    Verdict.Severity.WARNING_DUPLICATED_RESOURCE_ISSUE,
                    verdictMsg, visualizer);
            finalVerdict = finalVerdict.join(duplicatedVerdict);
        }

        return finalVerdict;
    }

}

class AndroidDigiOfficeHeaderIsNotEmptyNotNA implements Oracle {

    // Matches any resId that ends with:
    // header-title
    private static final java.util.regex.Pattern HEADER_WIDGET_ID_PATTERN = java.util.regex.Pattern
            .compile(".*header-title.*");

    private boolean isInvalidValue(String value) {
        if (value.trim().isEmpty())
            return true;

        String upperValue = value.trim().toUpperCase(java.util.Locale.ROOT);

        return upperValue.equals("-")
                || upperValue.contains("N/A")
                || upperValue.contains("N\\A");
    }

    @Override
    public void initialize() {
    }

    @Override
    public Verdict getVerdict(State state) {
        Verdict finalVerdict = Verdict.OK;

        for (Widget w : state) {
            String resId = w.get(AndroidTags.AndroidResourceId, "");

            if (!HEADER_WIDGET_ID_PATTERN.matcher(resId).matches()) {
                continue;
            }

            String value = w.get(AndroidTags.AndroidText, "");

            if (isInvalidValue(value)) {
                String verdictMsg = String.format(
                        "Detected Header with invalid content (resId=%s, value='%s') %s",
                        resId, value, w.get(AndroidTags.AndroidXpath));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Collections.singletonList(w)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict headerVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                finalVerdict = finalVerdict.join(headerVerdict);
            }
        }

        return finalVerdict;
    }
}

class AndroidDigiOfficePersonNameIsNotEmptyNotNA implements Oracle {

    // Matches any resId that ends with:
    // detail-name-text
    // detail-contact-person-<number>-text
    private static final java.util.regex.Pattern PERSON_WIDGET_ID_PATTERN = java.util.regex.Pattern
            .compile(".*detail-(name|contact-person-\\d+)-text.*");

    private boolean isInvalidValue(String value) {
        if (value.trim().isEmpty())
            return true;

        String upperValue = value.trim().toUpperCase(java.util.Locale.ROOT);

        return upperValue.equals("-")
                || upperValue.contains("N/A")
                || upperValue.contains("N\\A");
    }

    @Override
    public void initialize() {
    }

    @Override
    public Verdict getVerdict(State state) {
        Verdict finalVerdict = Verdict.OK;

        for (Widget w : state) {
            String resId = w.get(AndroidTags.AndroidResourceId, "");

            if (!PERSON_WIDGET_ID_PATTERN.matcher(resId).matches()) {
                continue;
            }

            String value = w.get(AndroidTags.AndroidAccessibilityId, "");

            if (isInvalidValue(value)) {
                String verdictMsg = String.format(
                        "Detected Person name with invalid content (resId=%s, value='%s') %s",
                        resId, value, w.get(AndroidTags.AndroidXpath));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Collections.singletonList(w)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict personNameVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                finalVerdict = finalVerdict.join(personNameVerdict);
            }
        }

        return finalVerdict;
    }
}

class AndroidDigiOfficeCompanyNameIsNotEmptyNotNA implements Oracle {

    // Matches any resId that ends with:
    // detail-company-name-text
    private static final java.util.regex.Pattern COMPANY_WIDGET_ID_PATTERN = java.util.regex.Pattern
            .compile(".*detail-(company-name)-text.*");

    private boolean isInvalidValue(String value) {
        if (value.trim().isEmpty())
            return true;

        String upperValue = value.trim().toUpperCase(java.util.Locale.ROOT);

        return upperValue.equals("-")
                || upperValue.contains("N/A")
                || upperValue.contains("N\\A");
    }

    @Override
    public void initialize() {
    }

    @Override
    public Verdict getVerdict(State state) {
        Verdict finalVerdict = Verdict.OK;

        for (Widget w : state) {
            String resId = w.get(AndroidTags.AndroidResourceId, "");

            if (!COMPANY_WIDGET_ID_PATTERN.matcher(resId).matches()) {
                continue;
            }

            String value = w.get(AndroidTags.AndroidAccessibilityId, "");

            if (isInvalidValue(value)) {
                String verdictMsg = String.format(
                        "Detected Company name with invalid content (resId=%s, value='%s') %s",
                        resId, value, w.get(AndroidTags.AndroidXpath));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Collections.singletonList(w)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict companyNameVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                finalVerdict = finalVerdict.join(companyNameVerdict);
            }
        }

        return finalVerdict;
    }
}

class AndroidDigiOfficeWidgetIsNotEmpty implements Oracle {

    // Matches any resId that ends with:
    // person-detail-contact-person-0-secondary-text
    // person-detail-contact-person-0-tertiary-text
    // contact-person-detail-function-text
    private static final java.util.regex.Pattern WIDGET_ID_PATTERN = java.util.regex.Pattern
            .compile(".*(detail-contact-person-\\d+-(secondary|tertiary)-text|person-detail-function-text).*");

    private boolean isInvalidValue(String value) {
        if (value.trim().isEmpty())
            return true;
        return value.trim().equals("-");
    }

    @Override
    public void initialize() {
    }

    @Override
    public Verdict getVerdict(State state) {
        Verdict finalVerdict = Verdict.OK;

        for (Widget w : state) {
            String resId = w.get(AndroidTags.AndroidResourceId, "");

            if (!WIDGET_ID_PATTERN.matcher(resId).matches()) {
                continue;
            }

            String value = w.get(AndroidTags.AndroidAccessibilityId, "");

            if (isInvalidValue(value)) {
                String verdictMsg = String.format(
                        "Detected widget with invalid content (resId=%s, value='%s') %s",
                        resId, value, w.get(AndroidTags.AndroidXpath));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Collections.singletonList(w)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict widgetEmptyVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                finalVerdict = finalVerdict.join(widgetEmptyVerdict);
            }
        }

        return finalVerdict;
    }
}

class AndroidDigiOfficePhoneTextIsNotValid implements Oracle {

    // Matches any resId that ends with:
    // person-detail-phone-text
    // relation-detail-phone-text
    // contact-person-detail-phone-text
    // contact-person-detail-relation-phone-text
    private static final java.util.regex.Pattern PHONE_WIDGET_ID_PATTERN = java.util.regex.Pattern
            .compile(".*(detail|detail-relation)-phone-text.*");

    private boolean isInvalidValue(String value) {
        if (value.trim().isEmpty())
            return true; // empty is invalid
        if (value.trim().equals("-"))
            return false; // dash char is allowed

        String upperValue = value.trim().toUpperCase(java.util.Locale.ROOT);

        // No alphabetic characters allowed
        return java.util.regex.Pattern.compile(".*[A-Z].*").matcher(upperValue).matches();
    }

    @Override
    public void initialize() {
    }

    @Override
    public Verdict getVerdict(State state) {
        Verdict finalVerdict = Verdict.OK;

        for (Widget w : state) {
            String resId = w.get(AndroidTags.AndroidResourceId, "");

            if (!PHONE_WIDGET_ID_PATTERN.matcher(resId).matches()) {
                continue;
            }

            String value = w.get(AndroidTags.AndroidAccessibilityId, "");

            if (isInvalidValue(value)) {
                String verdictMsg = String.format(
                        "Detected Phone text with invalid content (resId=%s, value='%s') %s",
                        resId, value, w.get(AndroidTags.AndroidXpath));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Collections.singletonList(w)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict phoneTextVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                finalVerdict = finalVerdict.join(phoneTextVerdict);
            }
        }

        return finalVerdict;
    }
}

class AndroidDigiOfficeMobileTextIsNotValid implements Oracle {

    // Matches any resId that ends with:
    // person-detail-mobile-text
    // relation-detail-mobile-text
    // contact-person-detail-mobile-text
    // contact-person-detail-relation-mobile-text
    private static final java.util.regex.Pattern MOBILE_WIDGET_ID_PATTERN = java.util.regex.Pattern
            .compile(".*(detail|detail-relation)-mobile-text.*");

    private boolean isInvalidValue(String value) {
        if (value.trim().isEmpty())
            return true; // empty is invalid
        if (value.trim().equals("-"))
            return false; // dash char is allowed

        String upperValue = value.trim().toUpperCase(java.util.Locale.ROOT);

        // No alphabetic characters allowed
        return java.util.regex.Pattern.compile(".*[A-Z].*").matcher(upperValue).matches();
    }

    @Override
    public void initialize() {
    }

    @Override
    public Verdict getVerdict(State state) {
        Verdict finalVerdict = Verdict.OK;

        for (Widget w : state) {
            String resId = w.get(AndroidTags.AndroidResourceId, "");

            if (!MOBILE_WIDGET_ID_PATTERN.matcher(resId).matches()) {
                continue;
            }

            String value = w.get(AndroidTags.AndroidAccessibilityId, "");

            if (isInvalidValue(value)) {
                String verdictMsg = String.format(
                        "Detected Mobile text with invalid content (resId=%s, value='%s') %s",
                        resId, value, w.get(AndroidTags.AndroidXpath));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Collections.singletonList(w)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict mobileTextVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                finalVerdict = finalVerdict.join(mobileTextVerdict);
            }
        }

        return finalVerdict;
    }
}

class AndroidDigiOfficeEmailTextIsNotValid implements Oracle {

    // Matches any resId that ends with:
    // person-detail-email-text
    // relation-detail-email-text
    // contact-person-detail-email-text
    // contact-person-detail-relation-email-text
    private static final java.util.regex.Pattern EMAIL_WIDGET_ID_PATTERN = java.util.regex.Pattern
            .compile(".*(detail|detail-relation)-email-text.*");

    private boolean isInvalidValue(String value) {
        if (value.trim().isEmpty())
            return true; // empty is invalid
        if (value.trim().equals("-"))
            return false; // dash char is allowed

        String upperValue = value.trim().toUpperCase(java.util.Locale.ROOT);

        // Only email format allowed
        return !(java.util.regex.Pattern.compile(".*[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}.*")
                .matcher(upperValue).matches());
    }

    @Override
    public void initialize() {
    }

    @Override
    public Verdict getVerdict(State state) {
        Verdict finalVerdict = Verdict.OK;

        for (Widget w : state) {
            String resId = w.get(AndroidTags.AndroidResourceId, "");

            if (!EMAIL_WIDGET_ID_PATTERN.matcher(resId).matches()) {
                continue;
            }

            String value = w.get(AndroidTags.AndroidAccessibilityId, "");

            if (isInvalidValue(value)) {
                String verdictMsg = String.format(
                        "Detected Email text with invalid content (resId=%s, value='%s') %s",
                        resId, value, w.get(AndroidTags.AndroidXpath));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Collections.singletonList(w)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict emailTextVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                finalVerdict = finalVerdict.join(emailTextVerdict);
            }
        }

        return finalVerdict;
    }
}

class AndroidDigiOfficeWebsiteTextIsNotValid implements Oracle {

    // Matches any resId that ends with:
    // person-detail-website-text
    // relation-detail-website-text
    // contact-person-detail-website-text
    // contact-person-detail-relation-website-text
    private static final java.util.regex.Pattern WEBSITE_WIDGET_ID_PATTERN = java.util.regex.Pattern
            .compile(".*(detail|detail-relation)-website-text.*");

    private boolean isInvalidValue(String value) {
        if (value.trim().isEmpty())
            return true;

        String upperValue = value.trim().toUpperCase(java.util.Locale.ROOT);

        return upperValue.contains("N/A")
                || upperValue.contains("N\\A");
    }

    @Override
    public void initialize() {
    }

    @Override
    public Verdict getVerdict(State state) {
        Verdict finalVerdict = Verdict.OK;

        for (Widget w : state) {
            String resId = w.get(AndroidTags.AndroidResourceId, "");

            if (!WEBSITE_WIDGET_ID_PATTERN.matcher(resId).matches()) {
                continue;
            }

            String value = w.get(AndroidTags.AndroidAccessibilityId, "");

            if (isInvalidValue(value)) {
                String verdictMsg = String.format(
                        "Detected Website text with invalid content (resId=%s, value='%s') %s",
                        resId, value, w.get(AndroidTags.AndroidXpath));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Collections.singletonList(w)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict websiteTextVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                finalVerdict = finalVerdict.join(websiteTextVerdict);
            }
        }

        return finalVerdict;
    }
}

class AndroidDigiOfficeSearchBarContainsClear implements Oracle {

    private static final java.util.regex.Pattern SEARCH_WIDGET_ID_PATTERN = java.util.regex.Pattern
            .compile(".*list-search$");

    private static final java.util.regex.Pattern CLEAR_WIDGET_ID_PATTERN = java.util.regex.Pattern
            .compile(".*list-clear.*");

    @Override
    public void initialize() {
    }

    @Override
    public Verdict getVerdict(State state) {
        Verdict finalVerdict = Verdict.OK;

        for (Widget w : state) {
            String resId = w.get(AndroidTags.AndroidResourceId, "");

            if (!SEARCH_WIDGET_ID_PATTERN.matcher(resId).matches()) {
                continue;
            }

            if (!containsClearRecursive(w, CLEAR_WIDGET_ID_PATTERN)) {
                String verdictMsg = String.format(
                        "Detected Search element without clear option (resId=%s) %s",
                        resId,
                        w.get(AndroidTags.AndroidXpath, ""));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(java.util.Collections.singletonList(w)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict searchBarVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                finalVerdict = finalVerdict.join(searchBarVerdict);
            }
        }

        return finalVerdict;
    }

    private boolean containsClearRecursive(Widget w, java.util.regex.Pattern clearPattern) {
        String id = w.get(AndroidTags.AndroidResourceId, "");
        if (clearPattern.matcher(id).matches())
            return true;

        for (int i = 0; i < w.childCount(); i++) {
            if (containsClearRecursive(w.child(i), clearPattern)) {
                return true;
            }
        }
        return false;
    }
}
