/*
 * *
 * COPYRIGHT (2017):                                                                     *
 * Universitat Politecnica de Valencia                                                   *
 * Camino de Vera, s/n                                                                   *
 * 46022 Valencia, Spain                                                                 *
 * www.upv.es                                                                            *
 *                                                                                       *
 * D I S C L A I M E R:                                                                  *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)     *
 * in the context of the TESTAR Proof of Concept project:                                *
 * "UPV, Programa de Prueba de Concepto 2014, SP20141402"                                *
 * This software is distributed FREE of charge under the TESTAR license, as an open      *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *                                                                                        *
 * *
 *
 */

/*
 *  @author (base) Sebastian Bauersfeld
 *  @author Govert Buijs
 */
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;
import static org.fruit.monkey.ConfigTags.OutputDir;
import static org.fruit.monkey.ConfigTags.SequenceLength;
import static org.fruit.monkey.ConfigTags.Sequences;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.fruit.Pair;
import org.fruit.alayer.Action;
import org.fruit.alayer.Role;
import org.fruit.alayer.Roles;
import org.fruit.alayer.SUT;
import org.fruit.alayer.Shape;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.CompoundAction;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.actions.WdAttributeAction;
import org.fruit.alayer.actions.WdCloseTabAction;
import org.fruit.alayer.actions.WdHistoryBackAction;
import org.fruit.alayer.actions.WdSubmitAction;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.NoSuchTagException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.webdriver.CanvasDimensions;
import org.fruit.alayer.webdriver.WdDriver;
import org.fruit.alayer.webdriver.WdElement;
import org.fruit.alayer.webdriver.WdProtocolUtil;
import org.fruit.alayer.webdriver.WdWidget;
import org.fruit.alayer.webdriver.enums.WdRoles;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Main;
import org.fruit.monkey.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testar.actions.ActionBase;
import org.testar.actions.MatchType;
import org.testar.actions.checks.ActionCheck;
import org.testar.actions.filters.ActionFilter;
import org.testar.actions.selects.ActionSelect;
import org.testar.actions.selects.ActionSelectSequence;
import org.testar.protocols.CoverageProtocol;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.upv.staq.testar.NativeLinker;
import nl.ou.testar.HtmlReporting.HtmlSequenceReport;
import nl.ou.testar.SimpleGuiStateGraph.GuiStateGraphWithVisitedActions;

public class ProtocolWebdriverGWT extends CoverageProtocol {
    private static Logger logger = LoggerFactory.getLogger(ProtocolWebdriverGWT.class);
    private static final String LOG_TAG_TESTAR = "testar";
    private static final Pattern nonASCII = Pattern.compile("[^\\x00-\\x7f]");
    private static final Tag<String> ABSTRACT_ID = Tags.Abstract_R_T_P_ID;

    private static final int MAX_RETRIES = 1;

    private enum UIActionType {Typeable, Clickable, None}

    // Classes that are deemed clickable by the web framework
    private static List<String> clickableClasses = Arrays.asList(
            // Dropdown op top right
            "selectItemLiteText",
            // Menu items on the left
            "etreeCell", "etreeCellSelected", "etreeCellSelectedOver",
            // Checkboxes
            "checkboxFalse", "checkboxFalseOver", "checkboxTrue", "checkboxTrueOver",
            // Tiles
            "showcaseTileIcon",
            // Scrolling stuff
            "vScrollStart", "vScrollEnd",
            // general
            "clickable",
            // search
            "g2k_glyphicons-search",
            // help
            "g2k_help_icon",
            // buttons
            "button", "buttonOver", "imgButton", "imgButtonOver", "toolStripButton", "headerButton", "tabButtonTopSelected", "tabButtonTop",
            // side menu
            "sideMenuClickable", "sidemenuLink", "sidemenuButton", "sectionHeaderclosed", "sectionHeaderopened");

    private static List<String> excludeClickableClasses = Arrays.asList("circleButtonItemInactive");

    private static List<String> clickableIcons = Arrays.asList("connector_closed_middle.gif", "connector_opened_end.gif");

    // Disallow links and pages with these extensions
    // Set to null to ignore this feature
    private static List<String> deniedExtensions = Arrays.asList("pdf", "jpg", "png", "jsp");

    // Define a whitelist of allowed domains for links and pages
    // An empty list will be filled with the domain from the sut connector
    // Set to null to ignore this feature
    private static List<String> domainsAllowed = Arrays.asList("192.168.1.33:8080", "192.168.1.22:8080");

    // If true, follow links opened in new tabs
    // If false, stay with the original (ignore links opened in new tabs)
    private static boolean followLinks = true;

    // URL + form name, username input id + value, password input id + value
    // Set login to null to disable this feature
    private static Pair<String, String> login = Pair.from("https://login.awo.ou.nl/SSO/login", "OUinloggen");
    private static Pair<String, String> username = Pair.from("username", "");
    private static Pair<String, String> password = Pair.from("password", "");

    // List of attributes to identify and close policy popups
    // Set to null to disable this feature
    private static Map<String, String> policyAttributes = new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;

        {
            put("class", "iAgreeButton");
        }
    };

    private static int derivedActionsTimesCalled = 0;
    private HtmlSequenceReport htmlReport;
    private GuiStateGraphWithVisitedActions stateGraphWithVisitedActions;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private List<ActionSelectSequence> actionSelectSequences;
    private List<ActionFilter> actionFilters;
    private List<ActionCheck> actionChecks;
    private ListIterator<ActionSelect> actionSelectIterator = null;
    private String actionSelectSequenceDescription = null;

    // 
    private Map<Action, WidgetElement> actionWidgetMap = null;
    private Map<String, WidgetElement> widgetTree = null;
    private Map<String, WidgetElement> widgetList = null;

    // Test settings
    private String testarSetup;
    private String testRun;

    private Date startDate;

    /**
     * Called once during the life time of TESTAR This method can be used to perform
     * initial setup work
     *
     * @param settings the current TESTAR settings as specified by the user.
     */
    protected void initialize(Settings settings) {
        // Start date must be set first
        startDate = new Date();

        NativeLinker.addWdDriverOS();
        super.initialize(settings);
        ensureDomainsAllowed();

        // Propagate followLinks setting
        WdDriver.followLinks = followLinks;

        // Override ProtocolUtil to allow WebDriver screenshots
        protocolUtil = new WdProtocolUtil();

        testarSetup = settings.get(ConfigTags.TestarSetup);
        testRun = settings.get(ConfigTags.TestarTestRun);

        // Set logging
        MDC.put(LOG_TAG_TESTAR, testarSetup + "_" + testRun);

        logger.info("Start sequence: {}", sequenceCount());

        // Initialize the HTML sequence report
        htmlReport = new HtmlSequenceReport(settings.get(OutputDir) + File.separator + testarSetup + "_" + testRun + "_TESTAR" + "_s"
                + settings.get(Sequences) + "_a" + settings.get(SequenceLength) + "_" + sdf.format(startDate) + ".html");
        // Initialize simple GUI state graph
        stateGraphWithVisitedActions = new GuiStateGraphWithVisitedActions();
        // Initialize action state model counter
        actionWidgetMap = new HashMap<>();
        widgetList = new HashMap<>();
        widgetTree = new HashMap<>();

        // Action selection and filtering
        actionSelectSequences = readJsonFiles("selects", ActionSelectSequence.class);
        logger.info("Action selects sequences(s)");
        if (actionSelectSequences != null) {
            for (ActionSelectSequence actionSelectSequence : actionSelectSequences) {
                logger.info("  Action sequence '{}'", actionSelectSequence.getDescription());
                logger.info("  Id: {}", actionSelectSequence.getId());
                for (ActionSelect actionSelect : actionSelectSequence.getActionSelects()) {
                    logger.info("    Action Select: {}", actionSelect.stringRepresentation());
                }
            }
        }
        actionFilters = readJsonFiles("filters", ActionFilter.class);
        logger.info("Action filter(s)");
        if (actionFilters != null) {
            logger.info("  Filter");
            for (ActionFilter actionFilter : actionFilters) {
                logger.info("  Action Filter: {}", actionFilter.stringRepresentation());
            }
        }

        actionChecks = readJsonFiles("checks", ActionCheck.class);
        logger.info("Action check(s)");
        if (actionChecks != null) {
            logger.info("  Check");
            for (ActionCheck actionCheck: actionChecks) {
                logger.info("  Action Check: {}", actionCheck.stringRepresentation());
            }
        }
    }

    @Override
    public void closeTestSession() {
        logger.info("Close test session - {} - {}", testarSetup, testRun);
        super.closeTestSession();

        // Printer counters
        Path actionDataFile = Paths
                .get(settings.get(OutputDir) + File.separator + testarSetup + "_" + testRun + "_actionselect" + "_s"
                        + settings.get(Sequences) + "_a" + settings.get(SequenceLength) + "_" + sdf.format(startDate) + ".txt");
        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(actionDataFile, CREATE))) {
            // Dump the coverage data
            out.write("Counters\n".getBytes());
            out.write("Filter counter(s)\n".getBytes());
            if (actionFilters != null) {
                for (ActionFilter actionFilter : actionFilters) {
                    out.write(String.format("  %s: %d\n", actionFilter.getId(), actionFilter.getCounter())
                            .getBytes());
                }
            }
            out.write("Action selects counter(s)\n".getBytes());
            if (actionSelectSequences != null) {
                for (ActionSelectSequence actionSelectSequence : actionSelectSequences) {
                    out.write(
                            String.format(" Action sequence '%s' (%s)\n", actionSelectSequence.getId(), actionSelectSequence.getDescription()).getBytes());
                    for (ActionSelect actionSelect : actionSelectSequence.getActionSelects()) {
                        switch (actionSelect.getSelectType()) {
                        case ATTRIBUTES:
                            out.write(String.format("  %s: %d\n", actionSelect.getAttributes(), actionSelect.getCounter())
                                    .getBytes());
                            break;
                        case NAME:
                            out.write(String.format("  %s: %d\n", actionSelect.getWidgetName(), actionSelect.getCounter())
                                    .getBytes());
                            break;
                        case NAME_ROLE:
                            out.write(String.format("  %s %s: %d\n", actionSelect.getWidgetName(), actionSelect.getRoleName(), actionSelect.getCounter())
                                    .getBytes());
                            break;
                        }
                    }
                }
            }
            out.write("Action check counter(s)\n".getBytes());
            if (actionChecks != null) {
                for (ActionCheck actionCheck : actionChecks) {
                    out.write(String.format("  %s: %d\n", actionCheck.getId(), actionCheck.getCounter())
                                .getBytes());
                }
            }
        } catch (IOException e) {
            logger.error("Failed to handle action select data: " + e.getMessage(), e);
            // Can't do anything here
        }
        actionSelectSequences = null;
        actionFilters = null;
        actionChecks = null;
        
        
        // Save the widget tree
        saveWidgetTree();
        widgetTree = null;
        
        // Log the widget list
        saveWidgetList();
        widgetList = null;

    }

    /**
     * This method is called when TESTAR starts the System Under Test (SUT). The
     * method should take care of 1) starting the SUT (you can use TESTAR's settings
     * obtainable from <code>settings()</code> to find out what executable to run)
     * 2) bringing the system into a specific start state which is identical on each
     * start (e.g. one has to delete or restore the SUT's configuratio files etc.)
     * 3) waiting until the system is fully loaded and ready to be tested (with
     * large systems, you might have to wait several seconds until they have
     * finished loading)
     *
     * @return a started SUT, ready to be tested.
     */
    protected SUT startSystem() throws SystemStartException {
        SUT sut = super.startSystem();

        // See remarks in WdMouse
        mouse = sut.get(Tags.StandardMouse);

        return sut;
    }

    /**
     * This method is invoked each time the TESTAR starts the SUT to generate a new
     * sequence. This can be used for example for bypassing a login screen by
     * filling the username and password or bringing the system into a specific
     * start state which is identical on each start (e.g. one has to delete or
     * restore the SUT's configuration files etc.)
     */
    protected void beginSequence(SUT system, State state) {
        super.beginSequence(system, state);
    }

    /**
     * This method is called when TESTAR requests the state of the SUT. Here you can
     * add additional information to the SUT's state or write your own state
     * fetching routine. The state should have attached an oracle (TagName:
     * <code>Tags.OracleVerdict</code>) which describes whether the state is
     * erroneous and if so why.
     *
     * @return the current state of the SUT with attached oracle.
     */
    protected State getState(SUT system) throws StateBuildException {
        State state = null;
        logger.info("getState - start");
        if (actionSelectIterator != null && actionSelectIterator.hasNext()) {
            logger.info("getState: select next from action select");
            ActionSelect nextActionSelect = actionSelectIterator.next();
            for (int retries = 0; retries < MAX_RETRIES; retries++) {
                logger.info("getState: retries: {}", retries);
                state = super.getState(system);
                logger.info("getState: retrieved new state");
                List<Widget> widgets = new ArrayList<>();
                for (Widget w : state) {
                    widgets.add(w);
                }
                Set<Widget> nextWidgets = findWidgets(widgets, nextActionSelect);
                if (!nextWidgets.isEmpty()) {
                    // Found widgets
                    break;
                }
                if (nextActionSelect.getMaxWait() != null) {
                    long waitTime = nextActionSelect.getMaxWait() / MAX_RETRIES;
                    logger.info("getState: wait {}ms for selection of widget {}", waitTime,
                            nextActionSelect.getWidgetName());
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
            }
            logger.info("getState: select previous from action select");
            actionSelectIterator.previous();
        }
        if (state == null) {
            state = super.getState(system);
        }
        logger.info("getState - end");

        return state;
    }

    /**
     * This is a helper method used by the default implementation of
     * <code>buildState()</code> It examines the SUT's current state and returns an
     * oracle verdict.
     *
     * @return oracle verdict, which determines whether the state is erroneous and
     *         why.
     */
    protected Verdict getVerdict(State state) {

        Verdict verdict = super.getVerdict(state); // by urueda
        // system crashes, non-responsiveness and suspicious titles automatically
        // detected!

        // -----------------------------------------------------------------------------
        // MORE SOPHISTICATED ORACLES CAN BE PROGRAMMED HERE (the sky is the limit ;-)
        // -----------------------------------------------------------------------------

        // ... YOU MAY WANT TO CHECK YOUR CUSTOM ORACLES HERE ...

        return verdict;
    }

    /**
     * This method is used by TESTAR to determine the set of currently available
     * actions. You can use the SUT's current state, analyze the widgets and their
     * properties to create a set of sensible actions, such as: "Click every Button
     * which is enabled" etc. The return value is supposed to be non-null. If the
     * returned set is empty, TESTAR will stop generation of the current action and
     * continue with the next one.
     *
     * @param system the SUT
     * @param state  the SUT's current state
     * @return a set of actions
     */
    protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
        logger.info("deriveActions - called {} times", derivedActionsTimesCalled++);
        // Kill unwanted processes, force SUT to foreground
        super.deriveActions(system, state);

        // create an action compiler, which helps us create actions
        // such as clicks, drag&drop, typing ...
        StdActionCompiler ac = new AnnotatingActionCompiler();

        // Clear action widget map. This mapping is used to correlate action to widget in actionSelect() method.
        actionWidgetMap.clear();
        
//        updateWidgetTree(state);

        // Check if forced actions are needed to stay within allowed domains
        Set<Action> forcedActions = detectForcedActions(state, ac);
        if (forcedActions != null && forcedActions.size() > 0) {
            return forcedActions;
        }

        Set<Action> actions = new HashSet<>();
        List<Widget> widgets = new ArrayList<>();
        for (Widget w : state) {
            widgets.add(w);
        }
        logger.info("deriveActions - start with {} widgets", widgets.size());
        // Filter widgets
        if (actionFilters != null) {
            List<Widget> filteredWidgets = applyFilters(widgets);
            // if there are widgets filtered use them
            if (!filteredWidgets.isEmpty()) {
                widgets = filteredWidgets;
                logger.info("deriveActions - widgets filtered");
            }
        }
        logger.info("deriveActions - after filter {} widgets", widgets.size());
//        for (Widget widget: widgets) {
//            logWidget("deriveActions_all", widget);
//        }

        // iterate through all widgets
        List<Widget> selectableWidgets = widgets.stream()
                                                .filter(widget -> getUIActionType(widget) != UIActionType.None)
                                                .collect(Collectors.toList());
        logger.info("deriveActions - only selectable widgets: {}", selectableWidgets.size());
        List<Widget> actionWidgets = new ArrayList<>(selectableWidgets);
        logger.info("=== SELECTABLE - START ===");
        for (Widget widget: selectableWidgets) {
            logWidget("deriveActions", widget);
        }
        logger.info("=== SELECTABLE - START ===");
        actionWidgets.addAll(widgets.stream()
                .filter(widget -> getUIActionType(widget) == UIActionType.None && hasWidgetText(widget))
                .collect(Collectors.toList()));
        logger.info("deriveActions - action widgets with text widgets: {}", actionWidgets.size());
        for (Widget widget: actionWidgets) {
            logWidget("deriveActions", widget);
        }
 
      updateWidgetList(selectableWidgets);

        // ----------------------
        // BUILD CUSTOM ACTIONS
        // ----------------------
        logger.info("=== ACTION SELECTS - check if action select in progress");
        if (actionSelectIterator != null) {
            logger.info("  = Continue with next from action sequence {}", actionSelectSequenceDescription);
            if (actionSelectIterator.hasNext()) {
                ActionSelect actionSelect = actionSelectIterator.next();
                Set<Widget> selectedWidgets = findWidgets(actionWidgets, actionSelect);
                if (!selectedWidgets.isEmpty()) {
                    logger.info("  = Selected widget {}", actionSelect.stringRepresentation());
                    Widget widget = selectWidget(selectedWidgets);
                    actionSelect.incrementCounter();

                    Action action = createAction(ac, actionSelect, widget);
                    if (action != null) {
                        actions.add(action);
                        actionWidgetMap.put(action, createOrGetWidgetElement(widget));
                        logger.info("deriveActions - next of sequence - selected an action for widget {}",
                                widget.get(Tags.Title, ""));
                        action.tags().forEach(tag -> logger.info("action {}: {}", tag.name(), action.get(tag)));
                        return actions;
                    } else {
                        logger.info("  = Sequence '{}' not completed, could not find widget {}",
                                actionSelectSequenceDescription, actionSelect.getWidgetName());
                    }
                } else {
                    logger.info("  = Sequence '{}' not completed, could not find widget {}",
                            actionSelectSequenceDescription, actionSelect.getWidgetName());
                }
            } else {
                logger.info("  = Sequence '{}' completed!!!", actionSelectSequenceDescription);
            }
        } else {
            logger.info("=== ACTION SELECTS - no action select in progress");
        }

        actionSelectIterator = null;
        actionSelectSequenceDescription = null;

        if (actionSelectSequences != null) {
            logger.info("=== ACTION SELECTS - start ({})", actionSelectSequences.size());
            for (ActionSelectSequence actionSelectSequence : actionSelectSequences) {
                int randomNum = ThreadLocalRandom.current().nextInt(0, 100);
                if (randomNum <= actionSelectSequence.getProbability()) {
                    logger.info(" == Sequence '{}' selected: prob: {} <= {}", actionSelectSequence.getDescription(),
                            randomNum, actionSelectSequence.getProbability());
                    ListIterator<ActionSelect> iter = actionSelectSequence.getActionSelects().listIterator();
                    if (iter.hasNext()) {
                        ActionSelect actionSelect = iter.next();
                        
                        logger.info("  = Search for widget {}", actionSelect.stringRepresentation());
                        Set<Widget> selectedWidgets = findWidgets(actionWidgets, actionSelect);
                        if (!selectedWidgets.isEmpty()) {
                            logger.info("  = Selected widget {}", actionSelect.stringRepresentation());
                            Widget widget = selectWidget(selectedWidgets);
                            Action action = createAction(ac, actionSelect, widget);
                            if (action != null) {
                                actionSelectIterator = iter;
                                actionSelect.incrementCounter();
                                actionSelectSequence.incrementCounter();
                                actionSelectSequenceDescription = actionSelectSequence.getDescription();
                                actions.add(action);
                                actionWidgetMap.put(action, createOrGetWidgetElement(widget));
                                logger.info("deriveActions - new sequence - selected an action for widget {}",
                                        widget.get(Tags.Title, ""));
                                action.tags().forEach(tag -> logger.info("action {}: {}", tag.name(), action.get(tag)));
                                return actions;
                            }
                        }
                    }
                }
            }
            logger.info("=== ACTION SELECTS - no actions selected ===");
        }

        // Do normal handling
        logger.info("deriveActions - No actions selects here, do normal handling");

        // iterate through all widgets
        for (Widget widget : selectableWidgets) {
            switch (getUIActionType(widget)) {
            case Typeable: {
                Action action = ac.clickTypeInto(widget, this.getRandomText(widget), true); 
                actions.add(action);
                actionWidgetMap.put(action, createOrGetWidgetElement(widget));
                break;
            }
            case Clickable: {
                Action action = ac.leftClickAt(widget); 
                actions.add(action);
                actionWidgetMap.put(action, createOrGetWidgetElement(widget));
                break;
            }
            case None:
                break;
            }
        }

        logger.info("deriveActions - returned actions: {}", actions.size());
        return actions;
    }

    /**
     * Log widget.
     *
     * @param prefix the prefix text for each log line
     * @param widget the widget
     */
    private void logWidget(String prefix, Widget widget) {
        logWidget(prefix, widget, 0);
    }

    /**
     * Log information about the widget and its children.
     *
     * @param prefix the prefix text for each log line
     * @param widget the widget
     * @param indent the indentation
     */
    private void logWidget(String prefix, Widget widget, int indent) {
        String identation = "";
        for (int i=0; i < (indent*4); i++) {
            identation += " ";
        }
        logger.info("{} - {} Abstract ID : {}", prefix, identation, widget.get(ABSTRACT_ID, ""));
        logger.info("{} - {} title       : {}", prefix, identation, widget.get(Tags.Title, ""));
        logger.info("{} - {} tooltip     : {}", prefix, identation, widget.get(Tags.ToolTipText, ""));
        logger.info("{} - {} role        : {}", prefix, identation, widget.get(Tags.Role, Roles.Widget));
        logger.info("{} - {} UIActionType: {}", prefix, identation, getUIActionType(widget));
        WdElement element = ((WdWidget) widget).element;
        logger.info("{} - {} name        : {}", prefix, identation, element.name);
        logger.info("{} - {} display     : {}", prefix, identation, element.display);
        logger.info("{} - {} textContent : {}", prefix, identation, element.textContent);
        logger.info("{} - {} tagName     : {}", prefix, identation, element.tagName);
        logger.info("{} - {} type        : {}", prefix, identation, element.type);
        logger.info("{} - {} helpText    : {}", prefix, identation, element.helpText);
        logger.info("{} - {} attributeMap: {}", prefix, identation, element.attributeMap);
        logger.info("{} - {} clickable   : {}", prefix, identation, element.isClickable);
        logger.info("{} - {} cssClasses  : {}", prefix, identation, element.cssClasses);
        try {
            logger.info("{} - {} shape       : {}", prefix, identation, widget.get(Tags.Shape));
        } catch (NoSuchTagException e) {
            logger.info("{} - {} shape       : <unknown>", prefix, identation);
        }
        logger.info("{} - ---------------------------------------", prefix);
        indent++;
        for (int i=0; i < widget.childCount(); i++) {
            logWidget(prefix, widget.child(i), indent);
        }

    }

    /**
     * Determine if this widget is typeable, clickable or neither.
     *
     * @param widget the widget
     * @return the UI action type
     */
    private UIActionType getUIActionType(Widget widget) {
        if (!widget.get(Enabled, true) || blackListed(widget)) {
            return UIActionType.None;
        }

        if (isAtBrowserCanvas(widget)) {
            if (isTypeable(widget)) {
                return UIActionType.Typeable;
            } else if ((isClickable(widget) && !isLinkDenied(widget))
                    || isClickableIcon(widget)) {
                return UIActionType.Clickable;
            }
        }
        return UIActionType.None;
    }

    /* (non-Javadoc)
     * @see es.upv.staq.testar.protocols.ClickFilterLayerProtocol#whiteListed(org.fruit.alayer.Widget)
     */
    public boolean whiteListed(Widget widget) {
        return super.whiteListed(widget);
    }

    private WidgetElement createOrGetWidgetElement(Widget widget) {
        if (widgetList.containsKey(widget.get(ABSTRACT_ID))) {
            WidgetElement widgetElement = widgetList.get(widget.get(ABSTRACT_ID));
//            logger.debug("Found widget {} in widget list", widgetElement.getTitle());
            return widgetElement;
        }
        
        WidgetElement widgetElement = new WidgetElement(widget);
        widgetList.put(widget.get(ABSTRACT_ID), widgetElement);
//        logger.debug("Widget {} not found in widget list, created one", widgetElement.getTitle());
        return widgetElement;
    }

   /*
     * Check the state if we need to force an action
     */
    private Set<Action> detectForcedActions(State state, StdActionCompiler ac) {
        logger.debug("detectForcedActions: enter");
        Set<Action> actions = detectForcedDeniedUrl();
        if (actions != null && actions.size() > 0) {
            return actions;
        }

        logger.debug("detectForcedActions: detect forced logins");
        actions = detectForcedLogin(state);
        if (actions != null && actions.size() > 0) {
            return actions;
        }

        logger.debug("detectForcedActions: detect forced popup");
        actions = detectForcedPopupClick(state, ac);
        if (actions != null && actions.size() > 0) {
            return actions;
        }
        logger.debug("detectForcedActions: return nothing");

        return null;
    }

    /*
     * Detect and perform login if defined
     */
    private Set<Action> detectForcedLogin(State state) {
        if (login == null || username == null || password == null) {
            return null;
        }

        // Check if the current page is a login page
        String currentUrl = WdDriver.getCurrentUrl();
        if (currentUrl.startsWith(login.left())) {
            CompoundAction.Builder builder = new CompoundAction.Builder();
            // Set username and password
            for (Widget widget : state) {
                WdWidget wdWidget = (WdWidget) widget;
                if (username.left().equals(wdWidget.getAttribute("id"))) {
                    builder.add(new WdAttributeAction(username.left(), "value", username.right()), 1);
                } else if (password.left().equals(wdWidget.getAttribute("id"))) {
                    builder.add(new WdAttributeAction(password.left(), "value", password.right()), 1);
                }
            }
            // Submit form
            builder.add(new WdSubmitAction(login.right()), 1);
            return new HashSet<>(Collections.singletonList(builder.build()));
        }

        return null;
    }

    /*
     * Force closing of Policies Popup
     */
    private Set<Action> detectForcedPopupClick(State state, StdActionCompiler ac) {
        if (policyAttributes == null || policyAttributes.size() == 0) {
            return null;
        }

        for (Widget widget : state) {
            if (!widget.get(Enabled, true) || widget.get(Blocked, false)) {
                continue;
            }

            WdElement element = ((WdWidget) widget).element;
            boolean isPopup = true;
            for (Map.Entry<String, String> entry : policyAttributes.entrySet()) {
                String attribute = element.attributeMap.get(entry.getKey());
                isPopup &= entry.getValue().equals(attribute);
            }
            if (isPopup) {
                return new HashSet<>(Collections.singletonList(ac.leftClickAt(widget)));
            }
        }

        return null;
    }

    /*
     * Force back action due to disallowed domain or extension
     */
    private Set<Action> detectForcedDeniedUrl() {
        String currentUrl = WdDriver.getCurrentUrl();
        logger.debug("detectForcedDeniedUrl: currentURL=" + currentUrl);
        // Don't get caught in PDFs etc. and non-whitelisted domains
        if (isUrlDenied(currentUrl) || isExtensionDenied(currentUrl)) {
            // If opened in new tab, close it
            if (WdDriver.getWindowHandles().size() > 1) {
                return new HashSet<>(Collections.singletonList(new WdCloseTabAction()));
            }
            // Single tab, go back to previous page
            else {
                return new HashSet<>(Collections.singletonList(new WdHistoryBackAction()));
            }
        }

        return null;
    }

    /*
     * Check if the current address has a denied extension (PDF etc.)
     */
    private boolean isExtensionDenied(String currentUrl) {
        // If the current page doesn't have an extension, always allow
        if (!currentUrl.contains(".")) {
            return false;
        }

        if (deniedExtensions == null || deniedExtensions.size() == 0) {
            return false;
        }

        // Deny if the extension is in the list
        String ext = currentUrl.substring(currentUrl.lastIndexOf(".") + 1);
        ext = ext.replace("/", "").toLowerCase();
        return deniedExtensions.contains(ext);
    }

    /*
     * Check if the URL is denied
     */
    private boolean isUrlDenied(String currentUrl) {
        if (currentUrl.startsWith("mailto:")) {
            return true;
        }

        // Always allow local file
        if (currentUrl.startsWith("file:///")) {
            return false;
        }

        // User wants to allow all
        if (domainsAllowed == null) {
            return false;
        }

        // Only allow pre-approved domains
        String domain = getDomain(currentUrl);
        boolean isUrlDenied = !domainsAllowed.contains(domain);
        logger.debug("is URL {} denied: {}: ", isUrlDenied);
        return isUrlDenied;
    }

    /*
     * Check if the widget has a denied URL as hyperlink
     */
    private boolean isLinkDenied(Widget widget) {
        String linkUrl = widget.get(Tags.ValuePattern, "");

        // Not a link or local file, allow
        if (linkUrl == null || linkUrl.startsWith("file:///")) {
            return false;
        }

        // Mail link, deny
        if (linkUrl.startsWith("mailto:")) {
            return true;
        }

        // Not a web link (or link to the same domain), allow
        if (!(linkUrl.startsWith("https://") || linkUrl.startsWith("http://"))) {
            return false;
        }

        // User wants to allow all
        if (domainsAllowed == null) {
            return false;
        }

        // Only allow pre-approved domains if
        String domain = getDomain(linkUrl);
        return !domainsAllowed.contains(domain);
    }

    /*
     * Get the domain from a full URL
     */
    private String getDomain(String url) {
        if (url == null) {
            return null;
        }

        // When serving from file, 'domain' is filesystem
        if (url.startsWith("file://")) {
            return "file://";
        }

        url = url.replace("https://", "").replace("http://", "").replace("file://", "");
        return (url.split("/")[0]).split("\\?")[0];
    }

    /*
     * If domainsAllowed not set, allow the domain from the SUT Connector
     */
    private void ensureDomainsAllowed() {
        // Not required or already defined
        if (domainsAllowed == null || domainsAllowed.size() > 0) {
            return;
        }

        String[] parts = settings().get(ConfigTags.SUTConnectorValue).split(" ");
        String url = parts[parts.length - 1].replace("\"", "");
        domainsAllowed = Arrays.asList(getDomain(url));
    }

    /*
     * We need to check if click position is within the canvas
     */
    private boolean isAtBrowserCanvas(Widget widget) {
        Shape shape = widget.get(Tags.Shape, null);
        if (shape == null) {
            return false;
        }

        // Widget must be completely visible on viewport for screenshots
        return shape.x() >= 0 && shape.x() + shape.width() < CanvasDimensions.getCanvasWidth() && shape.y() > 0
                && shape.y() + shape.height() < CanvasDimensions.getInnerHeight();
    }

    /* (non-Javadoc)
     * @see org.fruit.monkey.DefaultProtocol#isClickable(org.fruit.alayer.Widget)
     */
    protected boolean isClickable(Widget widget) {
        WdElement element = ((WdWidget) widget).element;
        if (!Collections.disjoint(element.cssClasses, excludeClickableClasses)) {
            return false;
        }

        Role role = widget.get(Tags.Role, Roles.Widget);
        if (Role.isOneOf(role, NativeLinker.getNativeClickableRoles())) {
            /*
             * // Input type are special... if (role.equals(WdRoles.WdINPUT)) { String type
             * = ((WdWidget) widget).element.type; return
             * WdRoles.clickableInputTypes().contains(type); }
             */
            return true;
        }


        if (element.isClickable) {
            return true;
        }

        Set<String> clickSet = new HashSet<>(clickableClasses);
        clickSet.retainAll(element.cssClasses);
        return clickSet.size() > 0;
    }

    /**
     * Checks if it is a clickable icon.
     *
     * @param widget the widget
     * @return true, if it is a clickable icon
     */
    protected boolean isClickableIcon(Widget widget) {
        WdElement element = ((WdWidget) widget).element;

        if (element.attributeMap != null) {
            for (Entry<String, String> attribute : element.attributeMap.entrySet()) {
                for (String clickableIcon : clickableIcons) {
                    if (StringUtils.containsIgnoreCase(attribute.getValue(), clickableIcon)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.fruit.monkey.DefaultProtocol#isTypeable(org.fruit.alayer.Widget)
     */
    protected boolean isTypeable(Widget widget) {
        Role role = widget.get(Tags.Role, Roles.Widget);
        if (Role.isOneOf(role, NativeLinker.getNativeTypeableRoles())) {
            // Input type are special...
            if (role.equals(WdRoles.WdINPUT)) {
                String type = ((WdWidget) widget).element.type;
                return WdRoles.typeableInputTypes().contains(type);
            }
            return true;
        }

        return false;
    }

    /**
     * Check if a filter can be applied, if so select the widgets.
     *
     * @param widgets the available widgets
     * @return the list of filtered widgets
     */
    private List<Widget> applyFilters(List<Widget> widgets) {
        logger.info("=== ACTION FILTERS - start ({})", actionFilters.size());
        List<Widget> filteredWidgets = new ArrayList<>();
        for (ActionFilter actionFilter : actionFilters) {
            logger.info(" == Find widget {}", actionFilter.getWidgetName());
            Set<Widget> filterWidgets = findWidgets(widgets, actionFilter);
            if (!filterWidgets.isEmpty()) {
                for (Widget parentWidget : filterWidgets) {
                    logger.info(" == Found filter widget : {}", parentWidget.get(Tags.Title));
                    for (Widget w : widgets) {
                        try {
                            if (isParentOf(parentWidget, w)) {
                                filteredWidgets.add(w);
                                logger.info("  = Added widget {}", w.get(Tags.Title));
                            }
                        } catch (NoSuchTagException e) {
                            logger.error(" Failed to add Title=" + w.get(Tags.Title), e);
                        }
                    }
                }
                actionFilter.incrementCounter();
                logger.info("=== ACTION FILTERS - widgets filtered");
                return filteredWidgets;
            }
        }
        logger.info("=== ACTION FILTERS - no widget filtered");
        return filteredWidgets;

    }

    /**
     * Create an action.
     *
     * @param ac the ac
     * @param actionSelect the action select
     * @param widget the widget
     * @return the action
     */
    private Action createAction(StdActionCompiler ac, ActionSelect actionSelect, Widget widget) {

        if (widget == null) {
            return null;
        }
        if (actionSelect.getActionType() != null) {
            switch (actionSelect.getActionType()) {
            case LEFTCLICK:
                return ac.leftClickAt(widget);
            case TYPEINTO:
                String fieldValue = generateValue(actionSelect.getValue());
                logger.info("   - Enter value: '{}'", fieldValue);
                return ac.clickTypeInto(widget, fieldValue, /* replaceText */ true);
            }
        }
        return null;
    }

    /**
     * Select one of the possible actions (e.g. at random)
     *
     * @param state   the SUT's current state
     * @param actions the set of available actions as computed by
     *                <code>buildActionsSet()</code>
     * @return the selected action (non-null!)
     */
    protected Action selectAction(State state, Set<Action> actions) {
        // Call select action from coverage protocol and indicate this protocol implements his own select action method.
        super.selectAction(state, actions, true);

        logger.info(" - select an action");

        try {
            htmlReport.addState(state, actions, stateGraphWithVisitedActions.getConcreteIdsOfUnvisitedActions(state));
        } catch(Exception e){
            // catching null for the first state or any new state, when unvisited actions is still null
            htmlReport.addState(state, actions);
        }

        // Call the preSelectAction method from the AbstractProtocol so that, if
        // necessary,
        // unwanted processes are killed and SUT is put into foreground.
        Action a = preSelectAction(state, actions);
        if (a != null) {
            // returning pre-selected action
        } else {
            // if no preSelected actions are needed, then implement your own action
            // selection strategy
            // Maintaining memory of visited states and selected actions, and selecting
            // randomly from unvisited actions:
            a = stateGraphWithVisitedActions.selectAction(state, actions);
            // a = RandomActionSelector.selectAction(actions);
        }
        htmlReport.addSelectedAction(state.get(Tags.ScreenshotPath), a);

        logger.info(" - selected action:");
        Action selectedAction = a;
        selectedAction.tags().forEach(tag -> logger.info("   tag {}: {}", tag.name(), selectedAction.get(tag)));

        if (actionWidgetMap.containsKey(a)) {
            logger.info(" - action found in widgetList");
            WidgetElement widgetElement = actionWidgetMap.get(a);
            widgetElement.incrementSelected();
            try {
                if (actionChecks != null && !actionChecks.isEmpty()) {
                    Widget widget = widgetElement.getWidget();
                    for (ActionCheck actionCheck: actionChecks) {
                        if (actionCheck.getWidgetIds() != null && actionCheck.getWidgetIds().contains(widget.get(ABSTRACT_ID))) {
                            actionCheck.incrementCounter();
                            logger.info(" # Found action check: {}", actionCheck.stringRepresentation());
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(" # Failed to find action check for widget: " + e.getMessage(), e);
            }
        } else {
            logger.info(" - action *not* found in widgetList");
        }

        logger.info(" - action selected");
        return a;
    }

    /**
     * Execute the selected action.
     *
     * @param system the SUT
     * @param state  the SUT's current state
     * @param action the action to execute
     * @return whether or not the execution succeeded
     */
    protected boolean executeAction(SUT system, State state, Action action) {
        boolean executed = super.executeAction(system, state, action);

        if (executed && actionSelectIterator != null) {
            logger.info("  = Wait before continue with next from action sequence {}", actionSelectSequenceDescription);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }

        return executed;
    }

    /**
     * TESTAR uses this method to determine when to stop the generation of actions
     * for the current sequence. You could stop the sequence's generation after a
     * given amount of executed actions or after a specific time etc.
     *
     * @return if <code>true</code> continue generation, else stop
     */
    protected boolean moreActions(State state) {
        return super.moreActions(state);
    }

    /**
     * This method is invoked each time after TESTAR finished the generation of a
     * sequence.
     */
    protected void finishSequence() {
        super.finishSequence();
    }

    /**
     * TESTAR uses this method to determine when to stop the entire test. You could
     * stop the test after a given amount of generated sequences or after a specific
     * time etc.
     *
     * @return if <code>true</code> continue test, else stop
     */
    protected boolean moreSequences() {
        return super.moreSequences();
    }

    /**
     * Find widgets.
     *
     * @param widgets the list of available widgets in the current state
     * @param actionBase the ActionSelect/ActionFilter/ActionCheck to find
     * @return the sets the
     */
    private Set<Widget> findWidgets(List<Widget> widgets, ActionBase actionBase) {
        Set<Widget> widgetsFound = new HashSet<>();
        if (actionBase.getWidgetIds() != null && !actionBase.getWidgetIds().isEmpty()) {
            for (Widget widget: widgets) {
                if (actionBase.getWidgetIds().contains(widget.get(ABSTRACT_ID))) {
                    widgetsFound.add(widget); 
                }
            }
        }
        if (!widgetsFound.isEmpty()) {
            return widgetsFound;
        }
        
        switch (actionBase.getSelectType()) {
        case NAME:
//            logger.info("  = Search for widget {}", actionBase.getWidgetName());
            widgetsFound = getWidgetsByName(widgets, actionBase.getWidgetName(),
                    actionBase.getWidgetNameMatchType(), actionBase.getNeighbours());
            break;
        case NAME_ROLE:
//            logger.info("  = Search for widget {} with role {}", actionBase.getWidgetName(), actionBase.getRoleName());
            widgetsFound = getWidgetsByName(widgets, actionBase.getWidgetName(),
                    actionBase.getWidgetNameMatchType(), actionBase.getRoleName(), actionBase.getNeighbours());
            break;
        case ATTRIBUTES:
//            logger.info("  = Search for widget with attributes {}", actionBase.getAttributes());
            widgetsFound = getWidgetsByAttributes(widgets, actionBase.getAttributes(), actionBase.getNeighbours());
            break;
        default:
            widgetsFound = new HashSet<>();
        }
//        Set<Widget> actionableWidgets = new HashSet<>();
        for (Widget widget: widgetsFound) {
//            if (actionBase instanceof ActionFilter
//             || getUIActionType(widget) != UIActionType.None) {
//                // widget is typeable or clickable
//                actionableWidgets.add(widget);
                String abstractId = widget.get(ABSTRACT_ID, "");
                if (!abstractId.isEmpty() && actionBase.getWidgetIds() != null && !actionBase.getWidgetIds().contains(abstractId)) {
                    actionBase.addWidgetId(abstractId);
                }
//            }
        }
        return widgetsFound;
    }

    /**
     * Gets the widget.
     *
     * @param widgets             the list of available widgets
     * @param widgetName          the widget name
     * @param widgetNameMatchType the widget name match type
     * @return a set of widgets, null if no widgets found
     */
    private Set<Widget> getWidgetsByName(List<Widget> widgets, String widgetName, MatchType widgetNameMatchType) {
        return getWidgetsByName(widgets, widgetName, widgetNameMatchType, null, null);
    }

    /**
     * Gets the widget.
     *
     * @param widgets             the list of available widgets
     * @param widgetName          the widget name
     * @param widgetNameMatchType the widget name match type
     * @param neighbours          the neighbours
     * @return the widget
     */
    private Set<Widget> getWidgetsByName(List<Widget> widgets, String widgetName, MatchType widgetNameMatchType,
            List<String> neighbours) {
        return getWidgetsByName(widgets, widgetName, widgetNameMatchType, null, neighbours);
    }

    /**
     * Gets the widget(s) by name.
     *
     * @param availableWidgets    the available widgets
     * @param widgetName          the widget name
     * @param widgetNameMatchType the widget name match type
     * @param roleName            the role name
     * @param neighbours          the neighbours
     * @return a set of widgets, empty set when no widgets could be found
     */
    private Set<Widget> getWidgetsByName(List<Widget> availableWidgets, String widgetName,
            MatchType widgetNameMatchType, String roleName, List<String> neighbours) {
        logger.debug("--- get widgets by name {} ---", widgetName);
        Set<Widget> selectedWidgets = new HashSet<>();

        // Check first if there are neighbours, because there can be more than one
        // widget with the same name and role. The
        // neighbours are the same for all, so do this only once.
        if (neighbours != null) {
            // Check if all neighbour widgets exist
            logger.debug("  - Check neighbours");
            for (String neighbour : neighbours) {
                Set<Widget> neighbourWidgets = getWidgetsByName(availableWidgets, neighbour, MatchType.EQUALS);
                if (neighbourWidgets.isEmpty()) {
                    logger.info("--- get widgets by name {} - neighbour {} not found", widgetName, neighbour);
                    return selectedWidgets;
                }
            }
            logger.debug("  - All neighbours found");
        }

        for (Widget widget : availableWidgets) {
            if (hasWidgetName(widget, widgetName, widgetNameMatchType)) {
                // Found a widget with title and/or tooltipText.
                if (roleName != null && !roleName.isEmpty()) {
                    // Get role
                    String widgetRoleName = getTagValue(widget, Tags.Role);
                    logger.debug("  - Check widget role {} against ", widgetRoleName, roleName);
                    if (!widgetRoleName.equals(roleName)) {
                        logger.info(
                                "--- get widgets by name {} - widget has not the requested role {} (has role {}) - continue with next",
                                widgetName, roleName, widgetRoleName);
                        continue;
                    } else {
                        logger.debug("  - Check on role not required");
                    }
                }
                // Widget found
                logger.debug("--- get widgets by name - {}, widget found, add to list", widgetName);
                selectedWidgets.add(widget);
            }
        }

        logger.debug("--- get widgets by name - {}, found {} widgets", widgetName, selectedWidgets.size());

        return selectedWidgets;
    }

    /**
     * Checks if the widget has a text.
     *
     * @param widget the widget
     * @return true, if successful
     */
    private boolean hasWidgetText(Widget widget) {
        String title = widget.get(Tags.Title, "");
        if (!title.isEmpty() && !title.startsWith("isc")) {
            return true; // has title
        }
        String tooltip = widget.get(Tags.ToolTipText, "");
        if (!tooltip.isEmpty()) {
            return true;
        }
        
        WdElement element = ((WdWidget) widget).element;

        if (element.name != null && !element.name.isEmpty() && !element.name.startsWith("isc_")) {
            logger.debug("hasWidgetName: has name: {}", element.name);
            return true;
        }

        if (element.textContent != null && !element.textContent.isEmpty()) {
            logger.debug("hasWidgetText: has textContent: {}", element.textContent);
            return true;
        }

        if (element.attributeMap != null) {
            for (Entry<String, String> attribute : element.attributeMap.entrySet()) {
                if (attribute.getKey().equals("aria-label") && attribute.getValue() != null && !attribute.getValue().isEmpty()) {
                    logger.debug("hasWidgetText: has aria-label: ({}:{})", attribute.getKey(), attribute.getValue());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Has the widget the requested widget name.
     *
     * @param widget the widget
     * @param widgetName the widget name
     * @param widgetNameMatchType how to match the widget name
     * @return true, if successful
     */
    private boolean hasWidgetName(Widget widget, String widgetName, MatchType widgetNameMatchType) {
        String title = widget.get(Tags.Title, "");
        String tooltip = widget.get(Tags.ToolTipText, "");
        
        if (matchWidgetName(widgetName, title, widgetNameMatchType)
                || matchWidgetName(widgetName, tooltip, widgetNameMatchType)) {
            return true;
        }
        WdElement element = ((WdWidget) widget).element;

        if (element.name != null) {
            if (matchWidgetName(widgetName, element.name, widgetNameMatchType)) {
                logger.debug("hasWidgetName: match {} by name: {}", widgetName, element.name);
                return true;
            }
        }
        if (element.textContent != null) {
            if (matchWidgetName(widgetName, element.textContent, widgetNameMatchType)) {
                logger.debug("hasWidgetName: match {} by textContent: {}", widgetName, element.textContent);
                return true;
            }
        }

        if (element.attributeMap != null) {
            for (Entry<String, String> attribute : element.attributeMap.entrySet()) {
                if (attribute.getKey().equals("aria-label") && matchWidgetName(widgetName, attribute.getValue(), widgetNameMatchType)) {
                    logger.debug("hasWidgetName: match {} by aria-label: ({}:{})", widgetName, attribute.getKey(), attribute.getValue());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Select one of the found widgets.
     *
     * @param widgets the widgets
     * @return the widget
     */
    private Widget selectWidget(Set<Widget> widgets) {
        if (widgets == null || widgets.isEmpty()) {
            return null;
        } else if (widgets.size() == 1) {
            Widget widget = widgets.iterator().next();
            logger.info("--- select widgets: {}, 1 widget found, return widget", widget.get(ABSTRACT_ID, "<unknown abstractId>"));
            return widget;
        } else if (widgets.size() > 1) {
            // Randomly select one of the widgets
            int randomIndex = ThreadLocalRandom.current().nextInt(0, widgets.size());
            logger.info("--- select widget: get widget randomly. Selected index {} of {}", randomIndex, widgets.size());
            for (Widget widget: widgets) {
                logger.info(" -- {} - {}", widget.get(ABSTRACT_ID, "<unknown abstractId>"), getUIActionType(widget));
            }
            return new ArrayList<>(widgets).get(randomIndex);
        }

        return null;
    }

    /**
     * Match widget name.
     *
     * @param widgetName the widget name
     * @param name       the name to match
     * @param matchType  the match type
     * @return true, if successful
     */
    private boolean matchWidgetName(String widgetName, String name, MatchType matchType) {
        switch (matchType) {
        case EQUALS:
            return name.equals(widgetName);
        case EQUALS_IC:
            return name.equalsIgnoreCase(widgetName);
        case CONTAINS:
            return name.contains(widgetName);
        case CONTAINS_IC:
            return StringUtils.containsIgnoreCase(name, widgetName);
        case STARTSWITH:
            return name.startsWith(widgetName);
        case STARTSWITCH_IC:
            return StringUtils.startsWithIgnoreCase(name, widgetName);
        default:
            return false;
        }
    }

    /**
     * Gets the widget(s) by attributes.
     *
     * @param availableWidgets the available widgets
     * @param attributes       the attributes
     * @param neighbours       the neighbours
     * @return a set of widgets, empty set when no widgets could be found
     */
    private Set<Widget> getWidgetsByAttributes(List<Widget> availableWidgets, Map<String, String> attributes,
            List<String> neighbours) {
        logger.debug("--- get widgets by attributes {} ---", attributes);
        Set<Widget> selectedWidgets = new HashSet<>();

        // Check first if there are neighbours, because there can be more than one
        // widget with the same name and role. The
        // neighbours are the same for all, so do this only once.
        if (neighbours != null) {
            // Check if all neighbour widgets exist
            logger.debug("  - Check neighbours");
            for (String neighbour : neighbours) {
                Set<Widget> neighbourWidgets = getWidgetsByName(availableWidgets, neighbour, MatchType.EQUALS);
                if (neighbourWidgets.isEmpty()) {
                    logger.info("--- get widgets by attributes {} - neighbour {} not found", attributes, neighbour);
                    return selectedWidgets;
                }
            }
            logger.debug("  - All neighbours found");
        }

        for (Widget widget : availableWidgets) {
            if (hasWidgetAttributes(widget, attributes)) {
                // Widget found
                logger.debug("--- get widgets by attributes - {}, widget found, add to list", attributes);
                selectedWidgets.add(widget);
            }
        }

        return selectedWidgets;
    }

    /**
     * Has the widget the requested attributes.
     *
     * @param widget the widget
     * @param attributes the attributes
     * @return true, if successful
     */
    private boolean hasWidgetAttributes(Widget widget, Map<String, String> attributes) {
        WdElement element = ((WdWidget) widget).element;

        if (element.attributeMap == null || element.attributeMap.isEmpty() || attributes == null || attributes.isEmpty()) {
            return false;
        }

        for (Entry<String, String> attribute : attributes.entrySet()) {
            if (!(element.attributeMap.containsKey(attribute.getKey())
                    && StringUtils.containsIgnoreCase(element.attributeMap.get(attribute.getKey()), attribute.getValue()))) {
                    return false;
            }
        }
        logger.debug("hasWidgetAttributes: attributes ({}) found", attributes);

        return true;
    }

    /**
     * Checks if parent is parent of child.
     *
     * @param parent the parent
     * @param child  the child
     * @return true, if is parent of
     */
    private boolean isParentOf(Widget parent, Widget child) {
        if (child.parent() == null) {
            return false;
        }
        if (parent == child.parent()) {
            logger.debug("isParentOf() = true");
            return true;
        }
        return isParentOf(parent, child.parent());
    }

    private String getTagValue(Widget w, Tag<?> tag) {
        String value = "";
        try {
            return w.get(tag).toString();
        } catch (NoSuchTagException e) {
            logger.debug("Tag {} not found", tag.name());
        } catch (Exception e) {
            logger.error(" -- Failed to get the tag: " + e.getMessage(), e);
        }

        return value;
    }

    private void updateWidgetList(List<Widget> widgets) {
        for (Widget widget: widgets) {
            createOrGetWidgetElement(widget);
        }
    }

    /**
     * Update widget tree.
     *
     * @param state the state
     */
//    private void updateWidgetTree(State state) {
//        // Check root of state widget tree
//        String rootId = state.get(ABSTRACT_ID);
//        if (!widgetTree.containsKey(rootId)) {
//            widgetTree.put(rootId, createOrGetWidgetElement(state));
//        }
//        // Check all the widgets of the state widget tree
//        for (Widget w : state) {
//            String id = w.get(ABSTRACT_ID);
//            if (!widgetTree.containsKey(id)) {
//                // Widget not yet in tree
//                widgetTree.put(id,  createOrGetWidgetElement(w));
//            }
//        }
//    }
    
    /**
     * Save widget tree.
     */
    private void saveWidgetTree() {
        Path p = Paths.get(settings.get(OutputDir) + File.separator + "tree_" + testarSetup + "_" + testRun + "_s"
                + settings.get(Sequences) + "_a" + settings.get(SequenceLength) + "_" + sdf.format(startDate) + ".gv");

        StringBuilder graph = new StringBuilder();
        if (widgetTree == null) {
            return;
        }
        for (WidgetElement widgetElement : widgetTree.values()) {
            Widget widget = widgetElement.getWidget();
            String abstractId = widget.get(ABSTRACT_ID, null);
            if (widget.parent() != null && abstractId != null) {
                graph.append(widget.parent().get(ABSTRACT_ID, "")).append(" -> ").append(abstractId).append("\n");
            }

            if (abstractId != null) {
                try {
                    graph.append(abstractId).append(" [");
                    String label = getLabel(widgetElement);
                    if (label != null && !label.isEmpty()) {
                        graph.append("label=\"").append(label).append("\"");
                    }
                    // Add color?
                    if (getUIActionType(widgetElement.getWidget()) != UIActionType.None) {
                        graph.append(" style=\"filled\" fillcolor=\"#A6CB45\"");
                    }
                    graph.append("];\n");
                } catch (Exception e) {
                    logger.error("Failed to create tree: " + e.getMessage());
                    // return;
                }
            }
        }
        if (graph.length() == 0) {
            logger.debug("No widget tree written, because no widgets found");
            return;
        } else {
            graph.insert(0, "digraph {\n");
            graph.append("}");
        }

        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(p, CREATE, APPEND))) {
            // iterate through all widgets
            byte[] data = graph.toString().getBytes();
            out.write(data, 0, data.length);
        } catch (IOException e) {
            logger.error("Failed to write widget tree: " + e.getMessage());
            return;
        }
    }

    /**
     * Save widget list to file
     */
    private void saveWidgetList() {
        if (widgetList == null) {
            return;
        }
        Path p = Paths.get(settings.get(OutputDir) + File.separator + testarSetup + "_" + testRun + "_widgetlist_s"
                + settings.get(Sequences) + "_a" + settings.get(SequenceLength) + "_" + sdf.format(startDate) + ".txt");

        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(p, CREATE, APPEND))) {
            int total = widgetList.size();
            int visited = (int) widgetList.values().stream().filter(element -> element.getTimesSelected() > 0).count();
            out.write(String.format("Widgets: %d, visited: %d, %d\n", total, visited, (visited * 100 / total))
                    .getBytes());
            for (WidgetElement widgetElement : widgetList.values()) {
                StringBuilder widgetRepresentation = new StringBuilder();
                widgetRepresentation.append("Widget: \n");
                Widget widget = widgetElement.getWidget();
                widgetRepresentation.append("Abstract ID  : ").append(widget.get(ABSTRACT_ID, "")).append("\n");
                widgetRepresentation.append("title        : ").append(widget.get(Tags.Title, "")).append("\n");
                widgetRepresentation.append("tooltip      : ").append(widget.get(Tags.ToolTipText, "")).append("\n");
                widgetRepresentation.append("role         : ").append(widget.get(Tags.Role, Roles.Widget)).append("\n");
                widgetRepresentation.append("UIActionType : ").append(getUIActionType(widget)).append("\n");
                WdElement element = ((WdWidget) widget).element;
                widgetRepresentation.append("name         : ").append(element.name).append("\n");
                widgetRepresentation.append("display      : ").append(element.display).append("\n");
                widgetRepresentation.append("textContent  : ").append(element.textContent).append("\n");
                widgetRepresentation.append("tagName      : ").append(element.tagName).append("\n");
                widgetRepresentation.append("type         : ").append(element.type).append("\n");
                widgetRepresentation.append("helpText     : ").append(element.helpText).append("\n");
                widgetRepresentation.append("attributeMap : ").append(element.attributeMap).append("\n");
                widgetRepresentation.append("clickable    : ").append(element.isClickable).append("\n");
                widgetRepresentation.append("cssClasses   : ").append(element.cssClasses).append("\n");
                try {
                    widgetRepresentation.append("shape        : ").append(widget.get(Tags.Shape)).append("\n");
                } catch (NoSuchTagException e) {
                    widgetRepresentation.append("shape        : <unknown>").append("\n");
                }
                widgetRepresentation.append("timesSelected: ").append(widgetElement.getTimesSelected()).append("\n");
                widgetRepresentation.append("-----------------------------------------------------------------").append("\n");
                out.write(widgetRepresentation.toString().getBytes());
            }
        } catch (Exception e) {
            logger.error("Failed to write widget tree: " + e.getMessage());
            return;
        }
    }

    /**
     * Gets the label of a widget.
     *
     * @param widgetElement the widget element
     * @return the label
     */
    private String getLabel(WidgetElement widgetElement) {
        StringBuilder label = new StringBuilder();
        Widget widget = widgetElement.getWidget();
        String title = widget.get(Tags.Title, "");
        WdElement element = ((WdWidget) widget).element;
        if (title.isEmpty() && element.name != null) {
            title = element.name;
        }
        if (title.isEmpty() && element.textContent != null) {
        }

        if (title.isEmpty() &&element.attributeMap != null && element.attributeMap.containsKey("aria-label")) {
            title = element.attributeMap.get("aria-label");
        }
            
        String formattedTitle = title.replaceAll("\\n", "#").replaceAll("##\\+", "#").replaceAll("\\r", "");
        if (formattedTitle.length() > 30) {
            formattedTitle = formattedTitle.substring(0, 30) + " ...";
        }
        label.append(nonASCII.matcher(formattedTitle).replaceAll(""));
        label.append("\\n").append(widget.get(ABSTRACT_ID));
        label.append("\\n").append(widget.get(Tags.Role, Roles.Widget).name());
        Shape shape = widget.get(Tags.Shape, null);
        if (shape != null) {
            label.append("\\n").append(shape.toString());
        }

        return label.toString();
    }

    /**
     * Read JSON files from a directory and convert them to specific objects and
     * return the object in a list
     *
     * @return the specific objects
     */
    private <T extends Object> List<T> readJsonFiles(String subDirectory, Class<T> type) {
        String directory = Main.getTestarDir() + "actions_" + testarSetup + File.separator + subDirectory;
        logger.info("Read JSON files from directory: {}", directory);
        try {
            // Find all JSON files in the <settings>/actions/<directory> directory and map
            // them to a specific object and
            // collect them in a list
            return Files.list(Paths.get(directory)).filter(path -> path.toString().endsWith(".json"))
                    .map(jsonFile -> convertJsonToObject(jsonFile, type)).filter(object -> Objects.nonNull(object))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Failed to read JSON files: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Generic method to convert a JSON file to a specific object.
     *
     * @param           <T> the generic type
     * @param path_file the JSON file
     * @param type      the specific object class
     * @return the specific objects in a list
     */
    private <T extends Object> T convertJsonToObject(Path path_file, Class<T> type) {
        try {
            byte[] jsonData = Files.readAllBytes(path_file);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonData, type);
        } catch (IOException e) {
            logger.error("Failed to read Json file " + path_file + ": " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Generate value.
     *
     * @param inputValue the input value
     * @return the string
     */
    private String generateValue(String inputValue) {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("testrun", String.format("%s %s", testarSetup, testRun));
        valuesMap.put("unique", String.format("%09d", ThreadLocalRandom.current().nextInt(0, 999999999)));
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        return sub.replace(inputValue);
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }
}
