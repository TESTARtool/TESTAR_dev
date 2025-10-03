/**
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2025 Universitat Politecnica de Valencia - www.upv.es
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

import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.testar.OutputStructure;
import org.testar.SutVisualization;
import org.testar.managers.InputDataManager;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.exceptions.ActionBuildException;
import org.testar.monkey.alayer.exceptions.StateBuildException;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.WdElement;
import org.testar.monkey.alayer.webdriver.WdWidget;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.oracles.web.performance.NetworkMonitor;
import org.testar.oracles.web.performance.NetworkRecord;
import org.testar.oracles.web.performance.NetworkSummary;
import org.testar.plugin.NativeLinker;
import org.testar.protocols.WebdriverProtocol;
import org.testar.settings.Settings;
import org.testar.monkey.Main;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.testar.monkey.alayer.Tags.Blocked;
import static org.testar.monkey.alayer.Tags.Enabled;

/**
 * Protocol with functional oracles examples to detect:
    Oracle idea X: performance issues
 */
public class Protocol_webdriver_performance_digioffice extends WebdriverProtocol {

    private List<String> listErrorVerdictInfo = new ArrayList<>();

    private NetworkMonitor monitor;

    /**
     * Called once during the life time of TESTAR
     * This method can be used to perform initial setup work
     * @param   settings  the current TESTAR settings as specified by the user.
     */
    @Override
    protected void initialize(Settings settings){

        if (settings.get(org.testar.monkey.ConfigTags.AlwaysCompile))
        {
            try {
                // bat file that uses tscon.exe to disconnect without stop GUI session
                File disconnectBatFile = new File(Main.settingsDir + File.separator + "webdriver_performance_digioffice" + File.separator + "disconnectRDP.bat").getCanonicalFile();

                // Launch and disconnect from RDP session
                // This will prompt the UAC permission window if enabled in the System
                if(disconnectBatFile.exists()) {
                    System.out.println("Running: " + disconnectBatFile);
                    Runtime.getRuntime().exec("cmd /c start \"\" " + disconnectBatFile);
                } else {
                    System.out.println("THIS BAT DOES NOT EXIST: " + disconnectBatFile);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            // Wait because disconnect from system modifies internal Screen resolution
            Util.pause(30);
        }
        super.initialize(settings);
    }


    /**
     * This method is called before the first test sequence, allowing for example setting up the test environment
     */
    @Override
    protected void initTestSession() {
        super.initTestSession();
        listErrorVerdictInfo = new ArrayList<>();
    }

    /**
     * This method is called when TESTAR starts the System Under Test (SUT). The method should
     * take care of
     * 1) starting the SUT (you can use TESTAR's settings obtainable from <code>settings()</code> to find
     * out what executable to run)
     * 2) bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
     * the SUT's configuratio files etc.)
     * 3) waiting until the system is fully loaded and ready to be tested (with large systems, you might have to wait several
     * seconds until they have finished loading)
     *
     * @return a started SUT, ready to be tested.
     */
    @Override
    protected SUT startSystem() throws SystemStartException {
        SUT system = super.startSystem();

        // This is to silent the devtools console debug printing
        Logger.getLogger("org.openqa.selenium").setLevel(Level.WARNING);
        Logger.getLogger("org.openqa.selenium.devtools").setLevel(Level.WARNING);
        Logger.getLogger("org.openqa.selenium.devtools.Connection").setLevel(Level.WARNING);

        DevTools devTools = ((HasDevTools) WdDriver.getRemoteWebDriver()).getDevTools();
        monitor = new NetworkMonitor(devTools);
        monitor.startMonitor(OutputStructure.startInnerLoopDateString);
        monitor.beginMeasurement("loadSystem"); // Measure the system load performance

        return system;
    }

    /**
     * This method is called when the TESTAR requests the state of the SUT.
     * Here you can add additional information to the SUT's state or write your
     * own state fetching routine. The state should have attached an oracle
     * (TagName: <code>Tags.OracleVerdict</code>) which describes whether the
     * state is erroneous and if so why.
     *
     * super.getState(system) puts the state information also to the HTML sequence report
     *
     * @return  the current state of the SUT with attached oracle.
     */
    @Override
    protected State getState(SUT system) throws StateBuildException {
        // First, measure network performance
        monitor.awaitNetworkIdle(Duration.ofMillis(500), Duration.ofSeconds(30));
        // Second, get state GUI info
        State state = super.getState(system);
        return state;
    }

    /**
     * This is a helper method used by the default implementation of <code>buildState()</code>
     * It examines the SUT's current state and returns an oracle verdict.
     *
     * @return oracle verdict, which determines whether the state is erroneous and why.
     */
    @Override
    protected Verdict getVerdict(State state) {
        // Obtain suspicious title verdicts
        Verdict verdict = super.getVerdict(state);

        NetworkSummary networkSummary = monitor.getSummary();
        List<NetworkRecord> networkRecordList = monitor.getCurrentFinishedActionRecords();
        monitor.logSummaryAndRequests();
        monitor.endMeasurement();

        // 1 - Oracle for web items performance issues
        Verdict itemPerformanceVerdict = itemPerformanceVerdict(state, networkRecordList);
        if (shouldReturnVerdict(itemPerformanceVerdict)) return itemPerformanceVerdict;

        // 2 - Oracle for duplicated web resources at network level
        Verdict duplicatedResourceVerdict = duplicatedResourceVerdict(state, networkRecordList);
        if (shouldReturnVerdict(duplicatedResourceVerdict)) return duplicatedResourceVerdict;

        // 3 - Oracle for high web items size at network level

        // 4 - Oracle for web state performance issues
        Verdict statePerformanceVerdict = statePerformanceVerdict(state, networkSummary);
        if (shouldReturnVerdict(statePerformanceVerdict)) return statePerformanceVerdict;

        return verdict;
    }

    private Verdict itemPerformanceVerdict(State state, List<NetworkRecord> networkRecordList) {
        Verdict groupItemsPerformanceVerdict = Verdict.OK;
        long itemThreshold = 1000L; // 1 second for each web item

        // Iterate through all web items because multiple might have performance issues
        for (NetworkRecord r : networkRecordList) {
            if(r.durationMs > itemThreshold) {
                // Create the Verdict and the description for a single item
                String performanceMsg = "Performance issue loading the web network item '" + r.url + "'";
                Verdict singleItemVerdict = new Verdict(Verdict.Severity.WARNING_RESOURCE_PERFORMANCE_ISSUE, performanceMsg);

                String itemDescription = "Item load duration " + r.durationMs + "ms above threshold " + itemThreshold + "ms \n";
                itemDescription = itemDescription.concat(r.toLine() + "\n");
                singleItemVerdict.setDescription(itemDescription);

                // Join the single item Verdict with the possible group of affected items
                groupItemsPerformanceVerdict = groupItemsPerformanceVerdict.join(singleItemVerdict);
            }
        }

        return groupItemsPerformanceVerdict;
    }

    private Verdict duplicatedResourceVerdict(State state, List<NetworkRecord> networkRecordList) {
        // Use method + url to detect duplicated network resources
        Function<NetworkRecord, String> keyFn = r -> (String.valueOf(r.method) + " " + r.url);

        // Count occurrences by (method + url)
        Map<String, Long> counts = networkRecordList.stream()
                .filter(r -> r != null && r.url != null)
                .collect(Collectors.groupingBy(keyFn, Collectors.counting()));

        // Determine duplicates (method + url) in encounter order
        List<String> duplicatedResourcesOrdered = networkRecordList.stream()
                .filter(r -> r != null && r.url != null)
                .map(keyFn)
                .filter(k -> counts.getOrDefault(k, 0L) > 1)
                .distinct() // preserves encounter order
                .collect(Collectors.toList());

        if (duplicatedResourcesOrdered.isEmpty()) {
            return Verdict.OK;
        }

        // All records whose (method + url) is duplicated (keep duplicates, preserve order)
        List<NetworkRecord> duplicatedNetworkItems = networkRecordList.stream()
                .filter(Objects::nonNull)
                .filter(r -> r.url != null && counts.getOrDefault(keyFn.apply(r), 0L) > 1)
                .collect(Collectors.toList());

        // Build verdict message with (method + url) and their counts
        String stateId = state.get(WdTags.WebHref, state.get(WdTags.WebTitle, state.get(Tags.ConcreteID, "")));
        String msg = duplicatedResourcesOrdered.stream()
                //.map(k -> k + " (x" + counts.get(k) + ")") // key (method + url) and count
                .map(k -> k) // key (method + url)
                .collect(Collectors.joining(", "));

        Verdict duplicatedResourceVerdict = new Verdict(
                Verdict.Severity.WARNING_DUPLICATED_RESOURCE_ISSUE,
                "Web state '" + stateId + "' contains duplicate network resources: " + msg
                );

        // Description with each duplicated record on its own line
        StringBuilder duplicatedDescription = new StringBuilder();
        for (NetworkRecord r : duplicatedNetworkItems) {
            duplicatedDescription.append(r.toLine()).append("\n");
        }
        duplicatedResourceVerdict.setDescription(duplicatedDescription.toString());

        return duplicatedResourceVerdict;
    }

    private Verdict statePerformanceVerdict(State state, NetworkSummary networkSummary) {
        long stateThreshold = 5000L; // 5 second for each web item
        if(networkSummary.stateDurationMs > stateThreshold) {
            String stateId = state.get(WdTags.WebHref, state.get(WdTags.WebTitle, state.get(Tags.ConcreteID, "")));
            String performanceMsg = "Performance issue loading web network items in the web state '" + stateId + "'";
            Verdict statePerformanceVerdict = new Verdict(Verdict.Severity.WARNING_STATE_PERFORMANCE_ISSUE, performanceMsg);

            String stateDescription = "State load duration " + networkSummary.stateDurationMs + "ms above threshold " + stateThreshold + "ms \n";
            stateDescription = stateDescription.concat(monitor.getSummaryAndRequestsString());
            statePerformanceVerdict.setDescription(stateDescription);

            return statePerformanceVerdict;
        } else {
            return Verdict.OK;
        }
    }

    /**
     * We want to return the verdict if it is not OK, 
     * and not on the detected failures list (it's a new failure). 
     * 
     * @param verdict
     * @return
     */
    private boolean shouldReturnVerdict(Verdict verdict) {
        return verdict != Verdict.OK && listErrorVerdictInfo.stream().noneMatch(info -> info.contains(verdict.info().replace("\n", " ")));
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
        // Kill unwanted processes, force SUT to foreground
        Set<Action> actions = super.deriveActions(system, state);
        Set<Action> filteredActions = new HashSet<>();

        // create an action compiler, which helps us create actions
        // such as clicks, drag&drop, typing ...
        StdActionCompiler ac = new AnnotatingActionCompiler();

        // Check if forced actions are needed to stay within allowed domains
        Set<Action> forcedActions = detectForcedActions(state, ac);

        // iterate through all widgets
        for (Widget widget : state) {
            if(isAtBrowserCanvas(widget) && isMenuItemClickable(widget)) {
                actions.add(ac.leftClickAt(widget));
            }

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
            addSlidingActions(actions, ac, widget);

            // If the element is blocked, Testar can't click on or type in the widget
            if (widget.get(Blocked, false) && !widget.get(WdTags.WebIsShadow, false)) {
                continue;
            }

            // type into text boxes
            if (isAtBrowserCanvas(widget) && isTypeable(widget)) {
                if(whiteListed(widget) || isUnfiltered(widget)){
                    actions.add(ac.clickTypeInto(widget, InputDataManager.getRandomTextInputData(widget), true));
                    actions.add(ac.pasteTextInto(widget, InputDataManager.getRandomTextFromCustomInputDataFile(System.getProperty("user.dir") + "/settings/custom_input_data.txt"), true));
                }else{
                    // filtered and not white listed:
                    filteredActions.add(ac.clickTypeInto(widget, InputDataManager.getRandomTextInputData(widget), true));
                }
            }

            // left clicks, but ignore links outside domain
            if (isAtBrowserCanvas(widget) && isClickable(widget)) {
                if(whiteListed(widget) || isUnfiltered(widget)){
                    if (!isLinkDenied(widget)) {
                        actions.add(ac.leftClickAt(widget));
                        //actions.add(ac.rightClickAt(widget));
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
    protected boolean isClickable(Widget widget) {
        // If the web widget contains the "readonly" attribute, we do not want to derive type actions
        if(widget.get(WdTags.WebAttributeMap, null) != null && widget.get(WdTags.WebAttributeMap).containsKey("disabled")) {
            return false;
        }

        if(widget.get(WdTags.WebAttributeMap, null) != null && widget.get(WdTags.WebAttributeMap).containsKey("display") && widget.get(WdTags.WebAttributeMap).get("display").contains("none")){
            return false;
        }

        if(widget.get(WdTags.WebIsDisabled, false)) return false;

        Role role = widget.get(Tags.Role, Roles.Widget);
        if (Role.isOneOf(role, NativeLinker.getNativeClickableRoles())) {
            // Input type are special...
            if (role.equals(WdRoles.WdINPUT)) {
                String type = ((WdWidget) widget).element.type;
                return WdRoles.clickableInputTypes().contains(type);
            }
            return true;
        }

        WdElement element = ((WdWidget) widget).element;
        if (element.isClickable) {
            return true;
        }

        Set<String> clickSet = new HashSet<>(clickableClasses);
        clickSet.retainAll(element.cssClasses);
        return clickSet.size() > 0;
    }

    private boolean isMenuItemClickable(Widget widget) {
        if(widget.get(WdTags.WebCssClasses, "").contains("[item,")) {
            return true;
        }
        return false;
    }

    @Override
    protected boolean isTypeable(Widget widget) {
        //If the web widget contains the "readonly" attribute, we do not want to derive type actions
        if(widget.get(WdTags.WebAttributeMap, null) != null && widget.get(WdTags.WebAttributeMap).containsKey("readonly")) {
            return false;
        }

        Role role = widget.get(Tags.Role, Roles.Widget);
        if (Role.isOneOf(role, NativeLinker.getNativeTypeableRoles())) {

            // Specific class="input" for parasoft SUT
            if(widget.get(WdTags.WebCssClasses, "").contains("input")) {
                return true;
            }

            // Input type are special...
            if (role.equals(WdRoles.WdINPUT)) {
                String type = ((WdWidget) widget).element.type;
                return WdRoles.typeableInputTypes().contains(type.toLowerCase());
            }
            return true;
        }

        return false;
    }

    /**
     * Execute the selected action.
     *
     * @param system the SUT
     * @param state  the SUT's current state
     * @param action the action to execute
     * @return whether or not the execution succeeded
     */
    @Override
    protected boolean executeAction(SUT system, State state, Action action) {
        monitor.beginMeasurement(action.get(Tags.ConcreteID)); // Measure the action execution performance
        return super.executeAction(system, state, action);
    }

    /**
     * This method is invoked each time after TESTAR finished the generation of a sequence.
     */
    @Override
    protected void finishSequence() {
        super.finishSequence();
        // If the final Verdict is not OK and the verdict is not saved in the list
        // This is a new run fail verdict
        if(getFinalVerdict().severity() > Verdict.Severity.OK.getValue() && !listErrorVerdictInfo.contains(getFinalVerdict().info().replace("\n", " "))) {
            listErrorVerdictInfo.add(getFinalVerdict().info().replace("\n", " "));
        }
    }

}
