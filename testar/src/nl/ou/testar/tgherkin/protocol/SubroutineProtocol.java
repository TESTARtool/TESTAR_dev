package nl.ou.testar.tgherkin.protocol;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;

import es.upv.staq.testar.protocols.ClickFilterLayerProtocol;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import nl.ou.testar.tgherkin.Utils;
import nl.ou.testar.tgherkin.model.Document;
import nl.ou.testar.tgherkin.model.ProtocolProxy;
import nl.ou.testar.utils.report.Reporter;
import org.fruit.Util;
import org.fruit.alayer.Action;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

/**
 * Abstract class responsible for executing a protocol enriched with a subroutine framework.
 *
 * @author Conny Hageluken
 * @Date October 2018
 */

public abstract class SubroutineProtocol extends ClickFilterLayerProtocol implements ProtocolProxy {

  /**
   * The text of address- and search bar in the Dutch version (see ENUM Browser).
   */
  private static String addressTitle = Browser.chromeNL.getAddressTitle();

  /**
   *  Subroutine Document.
   */
  private Document subroutine;

  /**
   * String with the name of the file with the source code of the subroutine document.
   */
  private String sourceFile;

  /**
   * String with the contents of the source code of the subroutine document.
   */
  private String sourceCode;

  /**
   * Data set with different subroutine options.
   */
  private HashMap<Integer,String[]> subroutineData = new HashMap<Integer,String[]>();

  /**
   * Index of currently running subroutine.
   */
  private int actualIndexSubD = 0;

  /**
   *   Boolean for process step activity.
   *  true: do not process subroutine step
   *  false: otherwise
   */
  private boolean actionSwitchOn;

  /**
   * Only valid in subroutine Mode.
   * true: actionSwitchOn is true
   * false: searching for more actions
   */
  private boolean subActionExec = false;

  private Action lastAction;

  /*
   * Reference to the folder of the actual protocol Class
   * Tags MyClassPath + ProtocolClass
   */
  private String protocolFolder = "";

  /*
   *  ActiveMode is Generate/GenerateDebug when in TESTAR Mode
   */
  private Modes activeMode;

  private static final double SCROLLARROWSIZE = 36; // sliding arrows (iexplorer)
  private static final double SCROLLTHICK = 16; // scroll thickness (iexplorer)

  /**
   * Constructor.
   */
  public SubroutineProtocol() {
    super();
  }

  /**
   *  Each browser (and locale!) uses different names for standard elements.
   */
  public enum Browser {

    // Dutch version
    explorerNL("address", "UIAEdit", "back", "close"),
    firefoxNL("voer zoekterm of adres in", "UIAEdit", "terug",null),
    chromeNL("Adres- en zoekbalk", "UIAEdit", "vorige", "sluiten"),

    // English version
    explorerEN("address", "UIAEdit", "back", "close"),
    firefoxEN("voer zoekterm of adres in", "UIAEdit", "terug",null),
    chromeEN("address and search bar", "UIAEdit", "back", "close");

    public String getAddressTitle() {
      return addressTitle;
    }

    private String addressTitle;
    private String addressRole;
    private String backTitle;
    private String closeTitle;

    Browser(String addressTitle, String addressRole, String backTitle, String closeTitle) {
      this.addressTitle = addressTitle;
      this.addressRole = addressRole;
      this.backTitle = backTitle;
      this.closeTitle = closeTitle;
    }
  }

  @Override
  /**
   * This method is used to perform initial setup work
   * for a subroutine protocol.
   * Called once during the lifetime of TESTAR
   * @param settings the current TESTAR settings as specified by the user.
   */
  protected void initialize(Settings settings){
    super.initialize(settings);
    protocolFolder = settings.get(ConfigTags.MyClassPath).get(0) + "/"
        + settings.get(ConfigTags.ProtocolClass).split("/")[0];
    activeMode = mode();
  }

  /**
   * Read data from the subroutine input file.
   * Name of the input file is stored in ConfigTag SubroutineData (see settings)
   * Data format (.csv) number|url|Tgherkin filename|exit url
   * Contents of the file will be stored in subroutineData HashMap
   * Contents can be adapted using panel Subroutine Data from main TESTAR menu
   */
  protected void readSubroutineDataInputfile() {
    File subroutineFile = new File(settings.get(ConfigTags.SubroutineData));
    int index = 0;
    try (Scanner in = new Scanner(subroutineFile)) {
      while (in.hasNextLine()) {
        String instring = in.nextLine();
        int n = instring.indexOf("|");
        String a = instring.substring(0, n);
        String b = instring.substring(n + 1);
        instring = b;
        n = instring.indexOf("|");
        b = instring.substring(0, n);
        String c = instring.substring(n + 1);

        if (a != null && b != null && c != null) {
          index++;
          String[] r = { a, b, c };
          subroutineData.put(index, r);
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Store contents of String sourceCode in Document subroutine.
   *
   */
  protected void initializeDocument() {
    try {
      sourceCode = Utils.readTgherkinSourceFile(sourceFile);
      subroutine = Utils.getDocument(sourceCode);
    } catch (Exception e) {
      e.printStackTrace();
    }
    // report header
    Report.report(null, null, null, settings().get(ConfigTags.GenerateTgherkinReport), false);
  }

  /**
   * Is a subroutine up and running?.
   * @return true if a subroutine is up and running
   */
  private boolean subroutineMode() {
    return (activeMode == Modes.Generate || activeMode == Modes.GenerateDebug)
        && subroutine != null;
  }

  /**
   * State is fulfilling criterion for running a subroutine.
   * @param state the SUT's current state
   * @return state is ready for subroutine action
   */
  protected boolean startState(State state) {
    return false;
  }

  /**
   * This method is invoked each time TESTAR starts to generate a new subroutine.
   * @param state the SUT's current state
   */
  protected void startSubroutine(State state) {
    getSubroutine().beginSequence();
  }

  /** Define a set of actions to be taken when switching from the subroutine to TESTAR.
   * @param state the SUT's current state
   * @return the available actions
  */
  protected Set<Action> finishState(State state) {
    return null;
  }

  /**
   * This method is invoked each time after TESTAR finishes the generation of a subroutine.
   * @param state the SUT's current state
   */
  protected void finishSubroutine(State state) {
    sourceCode = null;
    subroutine = null;
  }

  /**
   *  Process document step if actionSwitchOn is false.
   *  @return true if it is not possible to process a document step
   */
  private boolean chkActionSwitch() {
    if (forceKillProcess != null || forceToForeground || forceNextActionESC) {
      actionSwitchOn = true;
    } else {
      actionSwitchOn = false;
    }
    return actionSwitchOn;
  }
  
  /**
   * Default derivation of action steps.
   * @param system the SUT
   * @param state the SUT's current state
   * @return actions the actions to be taken
   */
  private Set<Action> defaultDeriveActions(SUT system, State state) {
    Set<Action> actions = super.deriveActions(system, state);

    // iterate through all (top) widgets
    StdActionCompiler ac = new UrlActionCompiler();

    for (Widget widget : getTopWidgets(state)) {

      // only consider enabled and non-blocked widgets
      if (widget.get(Enabled, true) && !widget.get(Blocked, false)) {

        // do not build actions for tabu widget
        if (blackListed(widget)) {
          continue;
        }

        // left clicks
        if (whiteListed(widget) || isClickable(widget)) {
          continue;
        }

        storeWidget(state.get(Tags.ConcreteID), widget);
        actions.add(ac.leftClickAt(widget));
      }

      // type into text boxes
      if (whiteListed(widget) || isTypeable(widget)) {
        storeWidget(state.get(Tags.ConcreteID), widget);
        actions.add(ac.clickTypeInto(widget, this.getRandomText(widget)));
      }

      // slides
      addSlidingActions(actions, ac, SCROLLARROWSIZE, SCROLLTHICK, widget);
    }
    return actions;
  }

  @Override
  /**
   * This method is used by TESTAR to determine the set of
   * currently available actions.
   * Derivation of actions: 
   * 1 – subroutine is not running
   *   if startState() is true
   *   - initialize Document for subroutine (startState)
   *   - add action from subroutine
   *   if startState()  is false
   *   - TESTAR remains in control
   * 2 – subroutine is running
   *   if more subroutine actions available => continue subroutine
   *   if no more subroutine actions available =>
   *   - initialize finish state action set (finishState)
   * @param system the SUT
   * @param state the SUT's current state
   * @return a set of actions
   * @throws ActionBuildException
   */
  protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
    Set<Action> actions = new HashSet<Action>();

    if (subroutine == null) {
      if (startState(state)) {
        System.out.println("Start state for subroutine");
        startSubroutine(state);
        actions = subroutine.deriveActions(this);
      } else {
        System.out.println("Continue TESTAR");
        actions = defaultDeriveActions(system, state);
      }
    } else {
      if (!moreSubroutineActions(state)) {
        System.out.println("Finish state subroutine");
        finishSubroutine(state);
        actions = finishState(state);
      } else {
        System.out.println("Continue subroutine, step " + getActionCount());
        if (subroutineMode()) {
          if (settings().get(ConfigTags.ReportState)) {
            Reporter.getInstance().report(new StateReportItem(false, this));
          }
          Report.appendReportDetail(
              Report.IntegerColumn.PRE_GENERATED_DERIVED_ACTIONS, actions.size());
      
        }
        // if an action switch is on then do not process document step
        if (!chkActionSwitch()) {
          actions.addAll(subroutine.deriveActions(this));
        }
      }
    }
    return actions;
  }

  @Override
  /**
   * Get verdict.
   * This is a helper method used by the default implementation of <code>buildState()</code>
   * It examines the SUT's current state and returns an oracle verdict.
   * @param state the SUT's current state
   * @return oracle verdict, which determines whether the state is erroneous and why.
   */
  protected Verdict getVerdict(State state) {
    Verdict verdict = super.getVerdict(state);
    // activeMode is Generate or Generate Debug and subroutine document not empty
    // subActionExec is
    if (subroutineMode()) {
      if (subActionExec) {
        if (verdict.severity() < settings().get(ConfigTags.FaultThreshold)) {
          // no fault yet: determine document verdict
          verdict = subroutine.getVerdict(this);
        }
      }
      Report.appendReportDetail(Report.StringColumn.VERDICT, verdict.toString());
    }
    activeMode = mode();
    return verdict;
  }

  @Override
  /**
   * Select one of the possible actions (e.g. at random)
   * @param state the SUT's current state
   * @param actions the set of available actions
   * @return the selected action (non-null!)
   */
  protected Action selectAction(State state, Set<Action> actions) {
    Action action = super.selectAction(state, actions);

    if (subroutineMode() && action != null) {
      String data = Util.toString((Object)action.get(Tags.Desc, null));
      Report.appendReportDetail(Report.StringColumn.SELECTED_ACTION,data);
      data = action.toString();
      data = data.replaceAll("(\\r|\\n|\\t)", "");
      Report.appendReportDetail(Report.StringColumn.SELECTED_ACTION_DETAILS,data);
    }
    return action;
  }

  @Override
  /**
   * Execute the selected action.
   * Determine whether or not the execution succeeded
   * @param system the SUT
   * @param state the SUT's current state
   * @param action the action to execute
   * @return whether or not the execution succeeded
   */
  protected boolean executeAction(SUT system, State state, Action action) {

    if (subroutineMode() && !actionSwitchOn) {
      subActionExec = true;
    }
    lastAction = action;
    return super.executeAction(system, state, action);
  }

  /**
  * Determine whether there are more subroutine actions to be executed.
  * @param state the SUT's current state
  * @return whether there are more subroutine actions to be executed
  */
  protected boolean moreSubroutineActions(State state) {
    if (subroutineMode()) {
      subActionExec = false;
      Report.appendReportDetail(Report.IntegerColumn.SEQUENCE_NR,sequenceCount());
      Report.appendReportDetail(Report.IntegerColumn.ACTION_NR,actionCount() - 1);
      Report.report(state, lastAction, graphDB, 
          settings().get(ConfigTags.GenerateTgherkinReport), 
          settings().get(ConfigTags.StoreTgherkinReport));
      if (!subroutine.moreActions(this)) {
        return false;
      } else {
        return true;
      }
    }
    return false;
  }

  @Override
  /**
   * This method is invoked each time after TESTAR finishes the generation of a sequence.
   * In this case, the first action will be to finish the subroutine, 
   * and in a next step the sequence will be finished
   * @param recordedSequence the recorded sequence
   */
  protected void finishSequence(File recordedSequence) {
    if (subroutineMode()) {
      finishSubroutine(state);
    } else {
      super.finishSequence(recordedSequence);
    }
  }
  
  // Implement protocolProxy methods, needed when working with Document class

  /**
   * Retrieve configuration settings.
   * @return settings
   */
  public Settings getSettings() {
    return super.settings();
  }

  /**
   * Retrieve state.
   * @return state
   */
  public State getState() {
    return state;
  }

  @Override
  /**
   * Retrieve Tgherkin source code.
   * @return Tgherkin source code
   */
  public String getTgherkinSourceCode() {
    return sourceCode;
  }

  @Override
  /**
   * Check whether widget is not filtered.
   * change visibility from protected to public
   * @param widget widget involved
   * @return true if widget is not filtered, otherwise false
   */
  public boolean isUnfiltered(Widget w) {
    return super.isUnfiltered(w);
  }

  @Override
  /**
   * Check whether widget is clickable.
   * change visibility from protected to public
   * @param widget widget involved
   * @return true if widget is clickable, otherwise false
   */
  public boolean isClickable(Widget w) {
    return super.isClickable(w);
  }

  @Override
  /**
   * Check whether widget is typeable.
   * change visibility from protected to public
   * @param widget widget involved
   * @return true if widget is typeable, otherwise false
   */
  public boolean isTypeable(Widget w) {
    return super.isTypeable(w);
  }

  @Override
  /**
   * Retrieve random text.
   * change visibility from protected to public
   * @param widget widget involved
   * @return generated random text
   */
  public String getRandomText(Widget w) {
    return super.getRandomText(w);
  }

  @Override
  /**
   * Retrieve top widgets.
   * change visibility from protected to public
   * @param state the SUT's current state
   * @return list of top widgets
   */
  public List<Widget> getTopWidgets(State state) {
    return super.getTopWidgets(state);
  }

  @Override
  /**
   * Store widget.
   * change visibility from protected to public
   * @param stateId state identifier
   * @param widget to be stored widget
   */
  public void storeWidget(String stateId, Widget widget) {
    super.storeWidget(stateId, widget);
  }

  /**
   * Retrieve sequence count.
   * @return sequence count
   */
  public int getSequenceCount() {
    return super.sequenceCount();
  }

  /**
   * Retrieve action count.
   * @return action count
   */
  public int getActionCount() {
    return super.actionCount();
  }
  
  /**
   * Retrieve addressTitle.
   * @return addressTitle
   */
  // getters and setters

  public static String getAddressTitle() {
    return addressTitle;
  }
  
  /**
   * Retrieve subroutine.
   * @return subroutine
   */  

  public Document getSubroutine() {
    return subroutine;
  }

  /**
   * Retrieve protocolFolder.
   * @return protocolFolder
   */  

  public String getProtocolFolder() {
    return protocolFolder;
  }
  
  /**
   * Retrieve subroutineData.
   * @return subroutineData
   */  

  public HashMap<Integer, String[]> getSubData() {
    return subroutineData;
  }
  
  /**
   * Retrieve actualIndexSD.
   * @return actualIndexSD
   */  

  public Integer getActualIndexSubD() {
    return actualIndexSubD;
  }
 
  /**
   * Set sourceCode.
   * @param sourceCode String with contents of source code of the subroutine document 
   */

  public void setSourceCode(String sourceCode) {
    this.sourceCode = sourceCode;
  }

  /**
   * Set sourceFile.
   * @param sourceFile String with name of file with source code of the subroutine document
   */

  public void setSourceFile(String sourceFile) {
    this.sourceFile = sourceFile;
  }

  /**
   * Set actualIndexSD.
   * @param actualIndexSD Index of currently running subroutine
   */


  public void setActualIndexSD(Integer actualIndexSD) {
    this.actualIndexSubD = actualIndexSD;
  }
}
