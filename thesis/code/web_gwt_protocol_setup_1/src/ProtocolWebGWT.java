import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;
import static org.fruit.alayer.windows.UIARoles.UIAButton;
import static org.fruit.alayer.windows.UIARoles.UIACheckBox;
import static org.fruit.alayer.windows.UIARoles.UIAComboBox;
import static org.fruit.alayer.windows.UIARoles.UIACustomControl;
import static org.fruit.alayer.windows.UIARoles.UIADataItem;
import static org.fruit.alayer.windows.UIARoles.UIAGroup;
import static org.fruit.alayer.windows.UIARoles.UIAHyperlink;
import static org.fruit.alayer.windows.UIARoles.UIAImage;
import static org.fruit.alayer.windows.UIARoles.UIAList;
import static org.fruit.alayer.windows.UIARoles.UIAListItem;
import static org.fruit.alayer.windows.UIARoles.UIAMenu;
import static org.fruit.alayer.windows.UIARoles.UIAMenuItem;
import static org.fruit.alayer.windows.UIARoles.UIARadioButton;
import static org.fruit.alayer.windows.UIARoles.UIAScrollBar;
import static org.fruit.alayer.windows.UIARoles.UIASlider;
import static org.fruit.alayer.windows.UIARoles.UIASpinner;
import static org.fruit.alayer.windows.UIARoles.UIASplitButton;
import static org.fruit.alayer.windows.UIARoles.UIATabItem;
import static org.fruit.alayer.windows.UIARoles.UIAText;
import static org.fruit.alayer.windows.UIARoles.UIATree;
import static org.fruit.alayer.windows.UIARoles.UIATreeItem;
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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.fruit.Drag; // by urueda
import org.fruit.alayer.AbsolutePosition;
import org.fruit.alayer.Action;
import org.fruit.alayer.Point;
import org.fruit.alayer.Rect;
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
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.NoSuchTagException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.windows.UIATags;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Main;
import org.fruit.monkey.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testar.actions.MatchType;
import org.testar.actions.filters.ActionFilter;
import org.testar.actions.selects.ActionSelect;
import org.testar.actions.selects.ActionSelectSequence;
import org.testar.coverage.CoverageCounter;
import org.testar.coverage.CoverageData;
import org.testar.coverage.CoverageReader;
import org.testar.coverage.RemoteExecutor;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.upv.staq.testar.NativeLinker;
import es.upv.staq.testar.protocols.ClickFilterLayerProtocol;
import nl.ou.testar.SimpleGuiStateGraph.GuiStateGraphWithVisitedActions;

public class ProtocolWebGWT extends ClickFilterLayerProtocol {
  private static Logger logger = LoggerFactory.getLogger("WEBGWT");

  private static final String LOG_TAG_TESTAR = "testar";

  private static final int MAX_RETRIES = 5;

  private GuiStateGraphWithVisitedActions stateGraphWithVisitedActions;

  // platform: Windows7 -> we expect Mozilla Firefox or Microsoft Internet
  // Explorer
  private static Role webText; // browser dependent
  private static double browser_toolbar_filter;

  private static final double scrollArrowSize = 36; // sliding arrows (iexplorer)
  private static final double scrollThick = 16; // scroll thickness (iexplorer)

  private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");

  private List<ActionSelectSequence> actionSelectSequences;
  private List<ActionFilter> actionFilters;
  private ListIterator<ActionSelect> actionSelectIterator = null;
  private String actionSelectSequenceDescription = null;

  private int defaultPort = 22;
  private Map<Integer, CoverageData> testCoverageData;
  private CoverageReader coverageReader;

  private int numberOfActions;
  private Timer timer;
  private int minute = -1;

  // Test settings
  private String testarSetup;
  private String testRun;

  private String startDate;

  private TimerTask coverageTimerTask;

  /**
   * Called once during the life time of TESTAR This method can be used to perform
   * initial setup work
   * 
   * @param settings the current TESTAR settings as specified by the user.
   */
  @Override
  protected void initialize(Settings settings) {
    testarSetup = settings.get(ConfigTags.TestarSetup);
    testRun = settings.get(ConfigTags.TestarTestRun);

    // Set logging
    MDC.put(LOG_TAG_TESTAR, testarSetup + "_" + testRun);

    logger.info("Start sequence: {}", sequenceCount());

    stateGraphWithVisitedActions = new GuiStateGraphWithVisitedActions();

    super.initialize(settings);
    initBrowser();

    if (mode() == Modes.Generate) {
      // Collect coverage data
      testCoverageData = new HashMap<>();

      // Reset coverage data
      int coveragePort = defaultPort;
      try {
        coveragePort = Integer.valueOf(settings.get(ConfigTags.CoverageServerPort));
      } catch (NumberFormatException e) {
        logger.error("Failed to determine coverage server port -> use default 22: " + e.getMessage());
      }
      coverageReader = new CoverageReader(settings.get(ConfigTags.CoverageServerUsername),
          settings.get(ConfigTags.CoverageServerHostname), coveragePort,
          settings.get(ConfigTags.CoverageServerKeyFile));
      try {
        coverageReader.resetCoverageData(settings.get(ConfigTags.CoverageCommandReset));
      } catch (IOException e) {
        logger.error("Failed to reset coverage data: " + e.getMessage(), e);
      }
      // Restart SUT
      RemoteExecutor remoteExecutor = null;
      try {
        int sutPort = defaultPort;
        try {
          sutPort = Integer.valueOf(settings.get(ConfigTags.SutServerPort));
        } catch (NumberFormatException e) {
          logger.error("Failed to determine sut server port -> use default 22: " + e.getMessage());
        }
        remoteExecutor = new RemoteExecutor(settings.get(ConfigTags.SutServerHostname), sutPort,
            settings.get(ConfigTags.SutServerUsername), null, settings.get(ConfigTags.SutServerKeyFile));
        remoteExecutor.connectWithKeys();
        String result = remoteExecutor.execCommand(settings.get(ConfigTags.SutServerRestart));
        logger.debug("Command result:\n{}", result);

      } catch (IOException e) {
        logger.error("Failed to restart SUT on " + settings.get(ConfigTags.SutServerHostname) + ": " + e.getMessage(),
            e);
      } finally {
        if (remoteExecutor != null) {
          remoteExecutor.disconnect();
        }
      }

      // Schedule every minute retrieval of coverage data
      startDate = sdf.format(new Date());
      settings.get(Sequences);
      Path coverageDataFile = Paths
          .get(settings.get(OutputDir) + File.separator + testarSetup + "_" + testRun + "_coverage" + "_s"
              + settings.get(Sequences) + "_a" + settings.get(SequenceLength) + "_" + startDate + ".txt");

      numberOfActions = 0;
      timer = new Timer();
      coverageTimerTask = new TimerTask() {
        public void run() {
          minute++;
          if (mode() == Modes.Quit || !moreSequences()) {
            logger.info("TIMER: Finish protocol timer.");
            timer.cancel();
            return;
          }
          logger.info("TIMER: Retrieve coverage data");
          try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(coverageDataFile, CREATE, APPEND))) {
            // Dump the coverage data
            coverageReader.dumpCoverageData(settings.get(ConfigTags.CoverageCommandDump));
            String xmlFilename = String.format("/extra/tests/%s/%s/report_%s_%03d.xml", testarSetup, testRun, testRun,
                minute);
            String reportCommand = settings.get(ConfigTags.CoverageCommandXmlReport) + xmlFilename;
            // Retrieve the coverage data
            CoverageData coverageData = coverageReader.retrieveCoverageData(sequenceCount(), numberOfActions, reportCommand,
                xmlFilename);
            // Store the coverage data - internal memory and on disk
            if (coverageData != null) {
              testCoverageData.put(minute, coverageData);
              // Get branch and line coverage, the other ones are also available here and
              // could also be used if needed
              int branchCoverage = coverageData.get(CoverageCounter.BRANCH).getCoveragePercentage();
              int lineCoverage = coverageData.get(CoverageCounter.LINE).getCoveragePercentage();
              String data = String.format("%d,%d,%d,%d,%d\n", minute, sequenceCount(), numberOfActions, branchCoverage,
                  lineCoverage);
              // Write data
              out.write(data.getBytes());
            } else {
              String data = String.format("%d,%d,%d,,\n", minute, sequenceCount(), numberOfActions);
              // Write data
              out.write(data.getBytes());
            }
          } catch (IOException e) {
            logger.error("Failed to handle coverage data: " + e.getMessage(), e);
            // Can't do anything here
          }
          logger.info("TIMER: log memory usage");
          logMemoryUsage();
        }
      };
      timer.scheduleAtFixedRate(coverageTimerTask, 0, 60 * 1000);
    }
    // Action selection and filtering
    actionSelectSequences = readJsonFiles("selects", ActionSelectSequence.class);
    logger.info("Action selects sequences(s)");
    if (actionSelectSequences != null) {
      for (ActionSelectSequence actionSelectSequence : actionSelectSequences) {
        logger.info("  Action sequence '{}'", actionSelectSequence.getDescription());
        for (ActionSelect actionSelect : actionSelectSequence.getActionSelects()) {
          logger.info("    Widget name      : {}", actionSelect.getWidgetName());
          logger.info("    Widget name type : {}", actionSelect.getWidgetNameMatchType());
          logger.info("    Role name        : {}", actionSelect.getRoleName());
          logger.info("    Neighbour widgets: {}", actionSelect.getNeighbours());
          logger.info("    Action type      : {}", actionSelect.getActionType());
          logger.info("    Value            : {}", actionSelect.getValue());
          logger.info("");
        }
      }
    }
    actionFilters = readJsonFiles("filters", ActionFilter.class);
    logger.info("Action filter(s)");
    if (actionFilters != null) {
      logger.info("  Filter");
      for (ActionFilter actionFilter : actionFilters) {
        logger.info("    Widget name     : {}", actionFilter.getWidgetName());
        logger.info("    Widget name type: {}", actionFilter.getWidgetNameMatchType());
        logger.info("    Neighbours      : {}", actionFilter.getNeighbours());
        logger.info("");
      }
    }
  }

  @Override
  public void closeTestSession() {
    logger.info("Close test session - {} - {}", testarSetup, testRun);
    super.closeTestSession();

    if (mode() == Modes.Generate) {
      logger.info("Cancel timer - {} - {}", testarSetup, testRun);
      coverageTimerTask.cancel();
      timer.cancel();
      // Create an HTML report of the coverage data for analysing purposes
      String htmlDir = String.format("/extra/tests/%s/%s/report_html_%s", testarSetup, testRun, testRun);
      String reportCommand = settings.get(ConfigTags.CoverageCommandHtmlReport) + htmlDir;
      try {
        coverageReader.executeRemoteCommand(reportCommand);
      } catch (IOException e) {
        logger.error("Failed to create HTML report: " + e.getMessage(), e);
      }
      // Create a chart of the data
      // TODO
    }

    // Printer counters
    Path actionDataFile = Paths
        .get(settings.get(OutputDir) + File.separator + testarSetup + "_" + testRun + "_actionselect" + "_s"
            + settings.get(Sequences) + "_a" + settings.get(SequenceLength) + "_" + startDate + ".txt");
    try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(actionDataFile, CREATE))) {
      // Dump the coverage data
      out.write("Counters\n".getBytes());
      out.write("Filter counter(s)\n".getBytes());
      if (actionFilters != null) {
        for (ActionFilter actionFilter : actionFilters) {
          out.write(String.format("  %s: %d\n", actionFilter.getWidgetName(), actionFilter.getCounter()).getBytes());
        }
      }
      out.write("Action selects counter(s)\n".getBytes());
      if (actionSelectSequences != null) {
        for (ActionSelectSequence actionSelectSequence : actionSelectSequences) {
          out.write(String.format(" Action sequence '%s'\n", actionSelectSequence.getDescription()).getBytes());
          for (ActionSelect actionSelect : actionSelectSequence.getActionSelects()) {
            out.write(String.format("  %s: %d\n", actionSelect.getWidgetName(), actionSelect.getCounter()).getBytes());
          }
        }
      }
    } catch (IOException e) {
      logger.error("Failed to handle action select data: " + e.getMessage(), e);
      // Can't do anything here
    }
    actionSelectSequences = null;
    actionFilters = null;
  }

  // check browser
  private void initBrowser() {
    webText = NativeLinker.getNativeRole("UIAEdit"); // just init with some value
    String sutPath = settings().get(ConfigTags.SUTConnectorValue);
    if (sutPath.contains("iexplore.exe"))
      webText = NativeLinker.getNativeRole("UIAEdit");
    else if (sutPath.contains("firefox"))
      webText = NativeLinker.getNativeRole("UIAText");
  }

  /**
   * This method is invoked each time TESTAR starts to generate a new sequence
   */
  protected void beginSequence(SUT system, State state) {

    super.beginSequence(system, state);
  }

  /**
   * This method is invoked each time after TESTAR finished the generation of a
   * sequence.
   */
  protected void finishSequence() {

    super.finishSequence();
    logger.info("Finish sequence: {}", sequenceCount());
//      MDC.clear();
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

    return sut;
  }

  @Override
  protected void stopSystem(SUT system) {
    super.stopSystem(system);
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
        state = super.getState(system);
        Set<Widget> nextWidgets = getWidgets(getTopWidgets(state), nextActionSelect.getWidgetName(),
            nextActionSelect.getWidgetNameMatchType());
        if (nextWidgets.isEmpty() && nextActionSelect.getMaxWait() != null) {
          logger.info("getState: wait {}ms for selection of widget {}", nextActionSelect.getMaxWait(),
              nextActionSelect.getWidgetName());
          try {
            Thread.sleep(nextActionSelect.getMaxWait() / MAX_RETRIES);
          } catch (InterruptedException e) {
            // ignore
          }
        } else {
          logger.info("getState: found widget {}, return state", nextActionSelect.getWidgetName());
          break;
        }
      }
      logger.info("getState: select previous from action select");
      actionSelectIterator.previous();
    }
    if (state == null) {
      state = super.getState(system);
    }

    for (Widget w : state) {
      Role role = w.get(Tags.Role, Roles.Widget);
      if (Role.isOneOf(role, new Role[] { NativeLinker.getNativeRole("UIAToolBar") }))
//              browser_toolbar_filter = w.get(Tags.Shape,null).y() + w.get(Tags.Shape,null).height();
        browser_toolbar_filter = 0;
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

  private static int times = 0;

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

    logger.info("deriveActions - called {} times", times++);
    super.deriveActions(system, state); // by urueda
    // unwanted processes, force SUT to foreground, ... actions automatically
    // derived!

    // create an action compiler, which helps us create actions, such as clicks,
    // drag&drop, typing ...
    StdActionCompiler ac = new AnnotatingActionCompiler();

    Set<Action> actions = new HashSet<>();
    List<Widget> widgets = getTopWidgets(state);
    logger.info("derivateActions - start with {} widgets", widgets.size());
    // Filter widgets
    if (actionFilters != null) {
      List<Widget> filteredWidgets = applyFilters(widgets);
      // if there are widgets filtered use them
      if (!filteredWidgets.isEmpty()) {
        widgets = filteredWidgets;
        logger.info("derivateActions - after filter {} widgets", widgets.size());
      }
    }

    // ----------------------
    // BUILD CUSTOM ACTIONS
    // ----------------------
    logger.info("=== ACTION SELECTS - check if action select in progress");
    if (actionSelectIterator != null) {
      logger.info("  = Continue with next from action sequence {}", actionSelectSequenceDescription);
      if (actionSelectIterator.hasNext()) {
        ActionSelect actionSelect = actionSelectIterator.next();
        logger.info("  = Search for widget {}", actionSelect.getWidgetName());
        Set<Widget> selectedWidgets = getWidgets(widgets, actionSelect.getWidgetName(), actionSelect.getRoleName(),
            actionSelect.getWidgetNameMatchType(), actionSelect.getNeighbours());
        if (!selectedWidgets.isEmpty()) {
          logger.info("  = Selected widget {}", actionSelect.getWidgetName());
          Widget widget = selectWidget(selectedWidgets);
          actionSelect.incrementCounter();

          Action action = getAction(ac, actionSelect, widget);
          if (action != null) {
            actions.add(action);
            logger.info("derivateActions - next of sequence - selected an action for widget {}", widget.get(Tags.Title, ""));
            action.tags().forEach(tag -> logger.info("action {}: {}", tag.name(), action.get(tag)));
            return actions;
          } else {
            logger.info("  = Sequence '{}' not completed, could not find widget {}", actionSelectSequenceDescription,
                actionSelect.getWidgetName());
          }
        } else {
          logger.info("  = Sequence '{}' not completed, could not find widget {}", actionSelectSequenceDescription,
              actionSelect.getWidgetName());
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
          logger.info(" == Sequence '{}' selected: prob: {} <= {}", actionSelectSequence.getDescription(), randomNum,
              actionSelectSequence.getProbability());
          ListIterator<ActionSelect> iter = actionSelectSequence.getActionSelects().listIterator();
          if (iter.hasNext()) {
            ActionSelect actionSelect = iter.next();
            logger.info("  = Search for widget {}", actionSelect.getWidgetName());
            Set<Widget> selectedWidgets = getWidgets(widgets, actionSelect.getWidgetName(), actionSelect.getRoleName(),
                actionSelect.getWidgetNameMatchType(), actionSelect.getNeighbours());
            if (!selectedWidgets.isEmpty()) {
              logger.info("  = Selected widget {}", actionSelect.getWidgetName());
              Widget widget = selectWidget(selectedWidgets);
              Action action = getAction(ac, actionSelect, widget);
              if (action != null) {
                actionSelectIterator = iter;
                actionSelect.incrementCounter();
                actionSelectSequence.incrementCounter();
                actionSelectSequenceDescription = actionSelectSequence.getDescription();
                actions.add(action);
                logger.info("derivateActions - new sequence - selected an action for widget {}", widget.get(Tags.Title, ""));
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
//    // iterate through all widgets
//    actions = super.deriveActions(system, state); // by urueda
    for (Widget w : widgets) {
      if (w.get(Enabled, true) && !w.get(Blocked, false)) { // only consider enabled and non-blocked
                                                            // widgets
        if (!blackListed(w)) { // do not build actions for tabu widgets
          logger.debug("Enabled and not blocked widget {}", w.toString());
          logger.debug("  tag {}: {}", Tags.Title.name(), w.get(Tags.Title, ""));
          logger.debug("  tag {}: {}", Tags.Role.name(), w.get(Tags.Role, Roles.Widget));
          logger.debug("  tag {}: {}", Tags.Shape.name(), w.get(Tags.Shape, Rect.from(0, 0, 0, 0)));
          logger.debug("  tag {}: {}", UIATags.UIAName.name(), w.get(UIATags.UIAName, ""));

          // left clicks
          if (isClickable(w)) {
            Action action = ac.leftClickAt(w);
            actions.add(action);
            action.tags().forEach(tag -> logger.info("action {}: {}", tag.name(), action.get(tag)));
          }

          // type into text boxes
          if (isTypeable(w)) {
            logger.debug("    Widget is typeable");
            Action action = ac.clickTypeInto(w, this.getRandomText(w), /* replaceText */ true);
            actions.add(action);
            action.tags().forEach(tag -> logger.info("action {}: {}", tag.name(), action.get(tag)));
          }
          // slides
          addSlidingActions(actions, ac, scrollArrowSize, scrollThick, w, state);
        }
      }
    }
    logger.info("Finished deriveActions() - return actions");

    return actions;
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
      Set<Widget> filterWidgets = getWidgets(widgets, actionFilter.getWidgetName(), actionFilter.getWidgetNameMatchType(),
          actionFilter.getNeighbours());
      if (!filterWidgets.isEmpty()) {
        Widget filterWidget = selectWidget(filterWidgets);
        logger.info(" == Found filter widget : {}", filterWidget.get(Tags.Title));
        for (Widget w : widgets) {
          if (w.get(Enabled, true) && !w.get(Blocked, false)) { // only consider enabled and non-blocked widgets
            if (!blackListed(w)) { // do not build actions for tabu widgets
              try {
                if (isParentOf(filterWidget, w)) {
                  if (whiteListed(w) || isClickable(w)) {
                    filteredWidgets.add(w);
                    logger.info("  = Added widget {}", w.get(Tags.Title));
                  }
                }
              } catch (NoSuchTagException e) {
                logger.error(" Failed to add Title=" + w.get(Tags.Title), e);
              }
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

  private Action getAction(StdActionCompiler ac, ActionSelect actionSelect, Widget widget) {

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
   * Adds sliding actions (like scroll, drag and drop) to the given Set of Actions
   * 
   * @param actions
   * @param ac
   * @param scrollArrowSize
   * @param scrollThick
   * @param w
   */
  protected void addSlidingActions(Set<Action> actions, StdActionCompiler ac, double scrollArrowSize,
      double scrollThick, Widget w, State state) {
    Drag[] drags = null;
    // If there are scroll (drags/drops) actions possible
    if ((drags = w.scrollDrags(scrollArrowSize, scrollThick)) != null) {
      // For each possible drag, create an action and add it to the derived actions
      for (Drag drag : drags) {
        // Store the widget in the Graphdatabase
//              storeWidget(state.get(Tags.ConcreteID), w);
        // Create a slide action with the Action Compiler, and add it to the set of
        // derived actions
        actions.add(ac.slideFromTo(new AbsolutePosition(Point.from(drag.getFromX(), drag.getFromY())),
            new AbsolutePosition(Point.from(drag.getToX(), drag.getToY()))));
      }
    }
  }

  /**
   * Gets the widget.
   *
   * @param widgets             the list of available widgets
   * @param widgetName          the widget name
   * @param widgetNameMatchType the widget name match type
   * @return a set of widgets, null if no widgets found
   */
  private Set<Widget> getWidgets(List<Widget> widgets, String widgetName, MatchType widgetNameMatchType) {
    return getWidgets(widgets, widgetName, null, widgetNameMatchType, null);
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
  private Set<Widget> getWidgets(List<Widget> widgets, String widgetName, MatchType widgetNameMatchType,
      List<String> neighbours) {
    return getWidgets(widgets, widgetName, null, widgetNameMatchType, neighbours);
  }

  /**
   * Gets the widget(s).
   *
   * @param availableWidgets the available widgets
   * @param widgetName          the widget name
   * @param roleName            the role name
   * @param widgetNameMatchType the widget name match type
   * @param neighbours          the neighbours
   * @return a set of widgets, empty set when no widgets could be found
   */
  private Set<Widget> getWidgets(List<Widget> availableWidgets, String widgetName, String roleName, MatchType widgetNameMatchType,
      List<String> neighbours) {
    logger.debug("--- get widgets {} ---", widgetName);
    Set<Widget> selectedWidgets = new HashSet<>();

    // Check first if there are neighbours, because there can be more than one
    // widget with the same name and role. The
    // neighbours are the same for all, so do this only once.
    if (neighbours != null) {
      // Check if all neighbour widgets exist
      logger.debug("  - Check neighbours");
      for (String neighbour : neighbours) {
        Set<Widget> neighbourWidgets = getWidgets(availableWidgets, neighbour, MatchType.EQUALS);
        if (neighbourWidgets.isEmpty()) {
          logger.info("--- get widgets {} - neighbour {} not found", widgetName, neighbour);
          return selectedWidgets;
        }
      }
      logger.debug("  - All neighbours found");
    }

    for (Widget widget : availableWidgets) {
      if (hasWidgetName(widget, Arrays.asList(widgetName), widgetNameMatchType)) {
        // Found a widget with title and/or tooltipText.
        if (roleName != null && !roleName.isEmpty()) {
          // Get role
          String widgetRoleName = getTagValue(widget, Tags.Role);
          logger.debug("  - Check widget role {} against ", widgetRoleName, roleName);
          if (!widgetRoleName.equals(roleName)) {
            logger.info("--- get widgets {} - widget has not the requested role {} (has role {}) - continue with next",
                widgetName, roleName, widgetRoleName);
            continue;
          } else {
            logger.debug("  - Check on role not required");
          }
        }
        // Widget found
        logger.debug("--- get widgets - {}, widget found, add to list", widgetName);
        selectedWidgets.add(widget);
      }
    }

    return selectedWidgets;
  }

  private boolean hasWidgetName(Widget widget, List<String> widgetNames, MatchType widgetNameMatchType) {
    String title = widget.get(Tags.Title, "");
    String tooltip = widget.get(Tags.ToolTipText, "");
    if (title.isEmpty() && tooltip.isEmpty()) {
      // widget has no title or tooltip text
      return false;
    }
    // Some titles are very long, use only first part for logging
    String titleString = title.length() < 25 ? title : title.substring(0, 25);
    logger.debug(" -- check widget with title={}, tooltip={}", titleString, tooltip);

    for (String widgetName: widgetNames) {
      if (matchWidgetName(widgetName, title, widgetNameMatchType)
          || matchWidgetName(widgetName, tooltip, widgetNameMatchType)) {
        return true;
      }
    }
    return false;
  }

  private Widget selectWidget(Set<Widget> widgets) {
    if (widgets == null || widgets.isEmpty()) {
      return null;
    } else if (widgets.size() == 1) {
      logger.info("--- select widgets: {}, 1 widget found, return widget");
      return widgets.iterator().next();
    } else if (widgets.size() > 1) {
      // Randomly select one of the widgets
      int randomIndex = ThreadLocalRandom.current().nextInt(0, widgets.size());

      logger.info("--- select widget: get widget randomly. Selected index {} of {}", randomIndex, widgets.size());
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

//  private boolean isParentOf(Widget parent, Widget child, List<String> filterNames) {
//    Widget p = child.parent();
//    if (logger.isDebugEnabled()) {
//      logger.debug(" parent  : {}", parent);
//      logger.debug(" title   : {}", parent.get(Tags.Title, ""));
//      logger.debug(" w       : {}", child);
//      logger.debug(" title   : {}", child.get(Tags.Title, ""));
//      logger.debug(" w.parent: {}", p);
//      logger.debug(" title   : {}", p.get(Tags.Title, ""));
//    }
//    if (p == null) {
//      return false;
//    }
//    if (p == parent) {
//      return true;
//    }
//    return isParentOf(parent, p, filterNames);
//  }

  /**
   * Checks if parent is parent of child.
   *
   * @param parent the parent
   * @param child  the child
   * @return true, if is parent of
   */
  private boolean isParentOf(Widget parent, Widget child) {
    Widget p = child.parent();
    if (logger.isDebugEnabled()) {
      logger.debug(" parent  : {}", parent);
      logger.debug(" title   : {}", parent.get(Tags.Title, ""));
      logger.debug(" w       : {}", child);
      logger.debug(" title   : {}", child.get(Tags.Title, ""));
      logger.debug(" w.parent: {}", p);
      logger.debug(" title   : {}", p.get(Tags.Title, ""));
    }
    if (p == null) {
      return false;
    }
    if (p == parent) {
      return true;
    }
    return isParentOf(parent, p);
  }

  private String getTagValue(Widget w, Tag<?> tag) {
    String value = "";
    try {
      return w.get(tag).toString();
    } catch (NoSuchTagException e) {
      logger.debug("Tag {} not found", tag.name());
    } catch (Exception e) {
      logger.error(" -- Failed to get title: " + e.getMessage(), e);
    }

    return value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.fruit.monkey.DefaultProtocol#isClickable(org.fruit.alayer.Widget)
   */
  @Override
  public boolean isClickable(Widget w) {
    if (isAtBrowserCanvas(w)) {
      logger.debug("  - is at browser canvas");
      Role role = w.get(Tags.Role, Roles.Widget);
      logger.debug("Role: " + role);
      if (Role.isOneOf(role, getNativeClickableRoles())) {
        logger.debug("  - has Role that is native clickable");
        try {
          logger.debug("  - on foreground = " + w.get(Tags.Foreground));
        } catch (Exception e) {
          logger.debug("  - failed to retrieve Tags.Foreground");
        }
        boolean isUnfiltered = isUnfiltered(w);
        if (isUnfiltered) {
          logger.debug("  - is unfiltered");
        } else {
          logger.debug("  - is filtered");
        }
        return isUnfiltered;
      }
      logger.debug("  - has role that is *not* native clickable");
      return false;
    } else {
      logger.debug("  Widget is *not* at browser canvas");
      return false;
    }
  }

  /**
   * Gets all roles that correspond to elements that can be clicked.
   * 
   * @return All roles that correspond to elements that can be clicked.
   */
  public Role[] getNativeClickableRoles() {
    return new Role[] { UIAMenu, UIAMenuItem, UIAButton, UIACheckBox, UIARadioButton, UIAComboBox, UIAList, UIAListItem,
        UIATabItem, UIAHyperlink, UIADataItem, UIATree, UIATreeItem, UIASlider, UIASpinner, UIAScrollBar,
        UIASplitButton, UIACustomControl, UIAGroup, UIAImage, UIAText }; // be careful on custom
                                                                         // control (we do not
                                                                         // know what
    // they are)
//      return new Role[] { UIAMenu, UIAMenuItem, UIAButton, UIACheckBox, UIARadioButton, UIAComboBox, UIAList,
//              UIAListItem, UIATabItem, UIAHyperlink, UIADataItem, UIATree, UIATreeItem, UIASlider, UIASpinner,
//              UIAScrollBar, UIASplitButton, UIACustomControl }; // be careful on custom control (we do not know what
//                                                                // they are)
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.fruit.monkey.DefaultProtocol#isTypeable(org.fruit.alayer.Widget)
   */
  @Override
  public boolean isTypeable(Widget w) {
    if (!isAtBrowserCanvas(w))
      return false;

    Role role = w.get(Tags.Role, null);
    if (role != null && Role.isOneOf(role, webText))
      return isUnfiltered(w);

    return false;
  }

  /**
   * Checks if is at browser canvas.
   *
   * @param w the w
   * @return true, if is at browser canvas
   */
  private boolean isAtBrowserCanvas(Widget w) {
    Shape shape = w.get(Tags.Shape, null);
    if (shape != null && shape.y() > browser_toolbar_filter)
      return true;
    else
      return false;
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
    if (mode() == Modes.Generate) {
      numberOfActions++;
    }

    logger.info(" - select an action");

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

    logger.info(" - selected action:");
    Action selectedAction = a;
    selectedAction.tags().forEach(tag -> logger.info("   tag {}: {}", tag.name(), selectedAction.get(tag)));

    logger.info(" - action selected");
    return a;
  }

  final static double MAX_ACTION_WAIT_FRAME = 1.0; // (seconds)

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
   * TESTAR uses this method to determine when to stop the entire test. You could
   * stop the test after a given amount of generated sequences or after a specific
   * time etc.
   * 
   * @return if <code>true</code> continue test, else stop
   */
  protected boolean moreSequences() {

    return super.moreSequences();

  }

  public boolean blackListed(Widget widget) {
    return super.blackListed(widget);
  }

  /**
   * Read JSON files from a directory and convert them to specific objects and
   * return the object in a list
   *
   * @return the specific objects
   */
  private <T extends Object> List<T> readJsonFiles(String subDirectory, Class<T> type) {
    logger.info("Read JSON files from directory");
    try {
      // Find all JSON files in the <settings>/actions/<directory> directory and map
      // them to a specific object and
      // collect them in a list
      return Files.list(Paths.get(Main.getTestarDir() + "actions" + File.separator + subDirectory))
          .filter(path -> path.toString().endsWith(".json")).map(jsonFile -> convertJsonToObject(jsonFile, type))
          .filter(object -> Objects.nonNull(object)).collect(Collectors.toList());
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

  private void logMemoryUsage() {
    // Get the Java runtime
    Runtime runtime = Runtime.getRuntime();
    // Run the garbage collector
    runtime.gc();
    // Calculate the used memory
    long memory = runtime.totalMemory() - runtime.freeMemory();
    logger.info("Used memory is megabytes: {}MB ({})", bytesToMegabytes(memory), memory);
  }

  private static long bytesToMegabytes(long bytes) {
    return bytes / (1024L * 1024L);
  }
}
