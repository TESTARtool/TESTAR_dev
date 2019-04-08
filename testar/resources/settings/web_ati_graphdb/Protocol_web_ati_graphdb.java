/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
* Copyright (c) 2017 Open Universiteit - www.ou.nl
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


/*
 * A generic desktop protocol
 *
 * @author Urko Rueda Molina, Govert Buijs
 */

import es.upv.staq.testar.NativeLinker;
import es.upv.staq.testar.protocols.ClickFilterLayerProtocol;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;


public class Protocol_web_ati_graphdb extends ClickFilterLayerProtocol {
  // Each browser (and locale!) uses different names for standard elements
  private enum Browser {
    explorer("address", "UIAEdit", "back", "close"),
    firefox("voer zoekterm of adres in", "UIAEdit", "terug", null),
    chrome("adres- en zoekbalk", "UIAEdit", "vorige", "sluiten");

    String addressTitle;
    String addressRole;
    String backTitle;
    String closeTitle;

    Browser(String addressTitle, String addressRole,
            String backTitle, String closeTitle) {
      this.addressTitle = addressTitle;
      this.addressRole = addressRole;
      this.backTitle = backTitle;
      this.closeTitle = closeTitle;
    }
  }

  private static Role webText; // browser dependent
  private static double browser_toolbar_filter;
  private static Browser browser;

  private static double scrollArrowSize = 36; // sliding arrows (iexplorer)
  private static double scrollThick = 16; // scroll thickness (iexplorer)

  // If we encounter a login URL, determine the 'login button'and force click
  private static String loginTitle = "inloggen"; // lower case
  private static String loginUrl = "https://login.awo.ou.nl/sso/login";
  // Go back once we encounter certain files
  private static String[] deniedExtensions = new String[]{"pdf"};
  // If set to NULL, only the sut connector domain will be used
  private static String[] domainsAllowed = new String[]{
      "mijn.awo.ou.nl", "cws.awo.ou.nl", "ati.awo.ou.nl"};

  private String oldAddress = null;
  private String currentAddress = null;
  private Widget backWidget = null;
  private Widget closeWidget = null;
  private Widget loginWidget = null;

  /**
   * Called once during the life time of TESTAR
   * This method can be used to perform initial setup work
   *
   * @param settings the current TESTAR settings as specified by the user.
   */
  @Override
  protected void initialize(Settings settings) {
    super.initialize(settings);
    ensureDomainsAllowed();
    initBrowser();
  }

  // check browser
  private void initBrowser() {
    // just init with some value
    webText = NativeLinker.getNativeRole("UIAEdit");
    browser = Browser.explorer;

    String sutPath = settings().get(ConfigTags.SUTConnectorValue);
    if (sutPath.contains("iexplore.exe")) {
      webText = NativeLinker.getNativeRole("UIAEdit");
      browser = Browser.explorer;
    }
    else if (sutPath.contains("firefox")) {
      webText = NativeLinker.getNativeRole("UIAText");
      browser = Browser.firefox;
    }
    else if (sutPath.contains("chrome") || sutPath.contains("chromium")) {
      webText = NativeLinker.getNativeRole("UIAEdit");
      browser = Browser.chrome;
    }
  }

  /**
   * This method is invoked each time TESTAR starts to generate a new sequence
   */
  @Override
  protected void beginSequence(SUT system, State state) {
    super.beginSequence(system, state);
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
    SUT sut = super.startSystem();
    return sut;
  }

  /**
   * This method is called when TESTAR requests the state of the SUT.
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

    for (Widget w : state) {
      Role role = w.get(Tags.Role, Roles.Widget);
      if (Role.isOneOf(role, NativeLinker.getNativeRole("UIAToolBar"))) {
        browser_toolbar_filter = w.get(Tags.Shape, null).y() + w.get(Tags.Shape, null).height();
      }
    }

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
    Verdict verdict = super.getVerdict(state); // by urueda
    // system crashes, non-responsiveness and suspicious titles automatically detected!

    //-----------------------------------------------------------------------------
    // MORE SOPHISTICATED ORACLES CAN BE PROGRAMMED HERE (the sky is the limit ;-)
    //-----------------------------------------------------------------------------

    // ... YOU MAY WANT TO CHECK YOUR CUSTOM ORACLES HERE ...

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
  protected Set<Action> deriveActions(SUT system, State state)
      throws ActionBuildException {
    Set<Action> actions = super.deriveActions(system, state);

    // Ignore this protocol if Prolog is activated
    if (settings().get(ConfigTags.PrologActivated)) {
      return actions;
    }

    // Check if forced actions are needed to stay within allowed domains
    Set<Action> forcedActions = detectForcedActions(state);
    if (forcedActions != null && forcedActions.size() > 0) {
      return forcedActions;
    }

    // iterate through all (top) widgets
    StdActionCompiler ac = new AnnotatingActionCompiler();
    for (Widget widget : getTopWidgets(state)) {
      // only consider enabled and non-blocked widgets
      if (widget.get(Enabled, true) && !widget.get(Blocked, false)) {
        // do not build actions for tabu widgets
        if (blackListed(widget)) {
          continue;
        }

        // left clicks
        if (whiteListed(widget) || isClickable(widget)) {
          // Don't allow Testar to test outside domains
          if (isLinkDenied(widget)) {
            continue;
          }

          actions.add(ac.leftClickAt(widget));
        }

        // type into text boxes
        if (whiteListed(widget) || isTypeable(widget)) {
            actions.add(ac.clickTypeInto(widget, this.getRandomText(widget), true));
        }

        // slides
        addSlidingActions(actions, ac, scrollArrowSize, scrollThick, widget,state);
      }
    }

    return actions;
  }

  @Override
  protected boolean isClickable(Widget w) {
    if (!isAtBrowserCanvas(w)) {
      return false;
    }
    else {
      return super.isClickable(w);
    }
  }

  @Override
  protected boolean isTypeable(Widget w) {
    if (!isAtBrowserCanvas(w)) {
      return false;
    }

    Role role = w.get(Tags.Role, null);
    if (role != null && Role.isOneOf(role, webText)) {
      return isUnfiltered(w);
    }

    return false;
  }

  private boolean isAtBrowserCanvas(Widget w) {
    Shape shape = w.get(Tags.Shape, null);

    return shape != null && shape.y() > browser_toolbar_filter;
  }

  /**
   * Select one of the possible actions (e.g. at random)
   *
   * @param state   the SUT's current state
   * @param actions the set of available actions as computed by <code>buildActionsSet()</code>
   * @return the selected action (non-null!)
   */
  @Override
  protected Action selectAction(State state, Set<Action> actions) {
    return super.selectAction(state, actions);
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
    return super.executeAction(system, state, action);
  }

  /**
   * TESTAR uses this method to determine when to stop the generation of actions for the
   * current sequence. You could stop the sequence's generation after a given amount of executed
   * actions or after a specific time etc.
   *
   * @return if <code>true</code> continue generation, else stop
   */
  @Override
  protected boolean moreActions(State state) {
    return super.moreActions(state);
  }

  /**
   * This method is invoked each time after TESTAR finished the generation of a sequence.
   */
  @Override
  protected void finishSequence() {
    super.finishSequence();
  }

  /**
   * TESTAR uses this method to determine when to stop the entire test.
   * You could stop the test after a given amount of generated sequences or
   * after a specific time etc.
   *
   * @return if <code>true</code> continue test, else stop
   */
  @Override
  protected boolean moreSequences() {
    return super.moreSequences();
  }

  /*
   * Check the state if we need to force an action
   */
  private Set<Action> detectForcedActions(State state) {
    extractAddressWidgets(state);

    // SUT is probably in the background, let Testar handle it
    if (currentAddress == null) {
      return null;
    }

    StdActionCompiler ac = new AnnotatingActionCompiler();
    Set<Action> actions = new HashSet<>();

    // This assumes the user/pass is auto-filled by the browser
    if (currentAddress.startsWith(loginUrl) && loginWidget != null) {
      actions.add(ac.leftClickAt(loginWidget));
    }

    // Don't get caught in a PDFs etc. (no action back)
    else if (isExtensionDenied()) {
      actions.add(ac.leftClickAt(backWidget));
    }

    // Back action didn't succeed, probably opened in new tab
    else if (oldAddress != null &&
        isUrlDenied(oldAddress) &&
        isUrlDenied(currentAddress)) {
      actions.add(ac.leftClickAt(closeWidget));
    }

    // Try back action first, might have opened in same tab
    else if (isUrlDenied(currentAddress)) {
      actions.add(ac.leftClickAt(backWidget));
    }

    oldAddress = currentAddress;
    return actions;
  }

  /*
   * Get current address and the action-widgets
   */
  private void extractAddressWidgets(State state) {
    currentAddress = null;

    for (Widget widget : getTopWidgets(state)) {
      Shape shape = widget.get(Tags.Shape, null);
      String title = widget.get(Tags.Title, "").toLowerCase().trim();
      String value = widget.get(Tags.ValuePattern, "").toLowerCase().trim();
      String role = widget.get(Tags.Role).toString();

      // If not in the header, we only need to look for the loginWidget
      if (shape == null || shape.y() > browser_toolbar_filter) {
        if (title.contains(loginTitle) && role.equals("UIAButton")) {
          loginWidget = widget;
        }
        continue;
      }

      // Application / language setting specific
      if (title.equals(browser.addressTitle) &&
          role.equals(browser.addressRole)) {
        currentAddress = value;
      }
      else if ((browser == Browser.explorer || browser == Browser.chrome) &&
          (title.contains(browser.backTitle) || value.contains(browser.backTitle))) {
        backWidget = widget;
      }
      else if (title.contains(browser.closeTitle) ||
          value.contains(browser.closeTitle)) {
        closeWidget = widget;
      }
    }
  }

  /*
   * Get the domain from a full URL
   */
  private String getDomain(String url) {
    if (url == null) {
      return null;
    }

    url = url.replace("https://", "").replace("http://", "");
    return (url.split("/")[0]).split("\\?")[0];
  }

  /*
   * Check if the current address has a denied extension (PDF etc.)
   */
  private boolean isExtensionDenied() {
    // If the current page doesn't have an extsion, always allow
    if (!currentAddress.contains(".")) {
      return false;
    }

    // Deny if the extension is in the list
    String ext = currentAddress.substring(currentAddress.lastIndexOf("."));
    return Arrays.asList(deniedExtensions).contains(ext);
  }

  /*
   * Check if the widget has a denied URL as hyperlink
   */
  private boolean isLinkDenied(Widget widget) {
    return isUrlDenied(widget.get(Tags.ValuePattern, ""));
  }

  /*
   * Check if the URL is denied
   */
  private boolean isUrlDenied(String url) {
    // Not a link
    if (url == null ||
        !(url.startsWith("https://") || url.startsWith("https://"))) {
      return false;
    }

    // Only allow pre-approved domains
    String domain = getDomain(url);
    return !Arrays.asList(domainsAllowed).contains(domain);
  }

  /*
   * If domainsAllowed not set, allow the domain from the SUT Connector
   */
  private void ensureDomainsAllowed() {
    if (domainsAllowed != null && domainsAllowed.length > 0) {
      return;
    }

    String[] parts = settings().get(ConfigTags.SUTConnectorValue).split(" ");
    String url = parts[parts.length - 1].replace("\"", "");
    domainsAllowed = new String[]{getDomain(url)};
  }

  /*
   * Small convenience function
   */
  private static String clean(String field) {
    field = (field == null) ? "" : field;
    field = field.toLowerCase();
    field = field.replace(System.lineSeparator(), " ").replaceAll("\\s", " ");
    return field.substring(0, Math.min(35, field.length())).trim();
  }
}
