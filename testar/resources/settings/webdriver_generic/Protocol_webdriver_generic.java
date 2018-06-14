/*
 * *
 * COPYRIGHT (2017):                                                                     *
 * Universitat Politecnica de Valencia                                                   *
 * Camino de Vera, s/n                                                                   *
 * 46022 Valencia, Spain                                                                 *
 * www.upv.es                                                                            *
 * *
 * D I S C L A I M E R:                                                                  *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)     *
 * in the context of the TESTAR Proof of Concept project:                                *
 * "UPV, Programa de Prueba de Concepto 2014, SP20141402"                  *
 * This software is distributed FREE of charge under the TESTAR license, as an open      *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *                                                                                        *
 * *
 *
 */

/*
 *  @author (base) Sebastian Bauersfeld
 *  @author Govert Buijs
 */

import es.upv.staq.testar.NativeLinker;
import es.upv.staq.testar.protocols.ClickFilterLayerProtocol;
import org.fruit.Pair;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.webdriver.CanvasDimensions;
import org.fruit.alayer.webdriver.WdElement;
import org.fruit.alayer.webdriver.WdWidget;
import org.fruit.alayer.webdriver.enums.WdRoles;
import org.fruit.alayer.webdriver.enums.WdTags;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import java.io.File;
import java.util.*;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;
import static org.fruit.alayer.webdriver.Constants.scrollArrowSize;
import static org.fruit.alayer.webdriver.Constants.scrollThick;


public class Protocol_webdriver_generic extends ClickFilterLayerProtocol {
  // Classes that are deemed clickable by the web framework
  private static List<String> clickableClasses =
      Arrays.asList("list__item__title");

  /**
   * Called once during the life time of TESTAR
   * This method can be used to perform initial setup work
   *
   * @param settings the current TESTAR settings as specified by the user.
   */
  protected void initialize(Settings settings) {
    super.initialize(settings);
  }

  /**
   * This method is invoked each time TESTAR starts to generate a new sequence
   */
  protected void beginSequence(SUT system, State state) {
    super.beginSequence(system, state);
  }

  /**
   * This method is called when TESTAR starts the System Under Test (SUT). The method should
   * take care of
   *   1) starting the SUT (you can use TESTAR's settings obtainable from <code>settings()</code> to find
   *      out what executable to run)
   *   2) bringing the system into a specific start state which is identical on each start (e.g. one has to delete or restore
   *      the SUT's configuratio files etc.)
   *   3) waiting until the system is fully loaded and ready to be tested (with large systems, you might have to wait several
   *      seconds until they have finished loading)
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
   * This method is called when TESTAR requests the state of the SUT.
   * Here you can add additional information to the SUT's state or write your
   * own state fetching routine. The state should have attached an oracle
   * (TagName: <code>Tags.OracleVerdict</code>) which describes whether the
   * state is erroneous and if so why.
   *
   * @return the current state of the SUT with attached oracle.
   */
  protected State getState(SUT system) throws StateBuildException {
    State state = super.getState(system);

    return state;
  }

  /**
   * This is a helper method used by the default implementation of <code>buildState()</code>
   * It examines the SUT's current state and returns an oracle verdict.
   *
   * @return oracle verdict, which determines whether the state is erroneous and why.
   */
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
  protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
    Set<Action> actions = super.deriveActions(system, state); // by urueda
    // unwanted processes, force SUT to foreground, ... actions automatically derived!

    //----------------------
    // BUILD CUSTOM ACTIONS
    //----------------------

    // Don't add actions if prolog is activated
    if (settings().get(ConfigTags.PrologActivated)) {
      return actions;
    }

    // create an action compiler, which helps us create actions
    // such as clicks, drag&drop, typing ...
    StdActionCompiler ac = new AnnotatingActionCompiler();

    // iterate through all widgets
    for (Widget w : state) {
      // only consider enabled, non-blocked widgets and non-tabu widgets
      if (!w.get(Enabled, true) || w.get(Blocked, false) || blackListed(w)) {
        continue;
      }

      // left clicks
      if (isAtBrowserCanvas(w) && (whiteListed(w) || isClickable(w))) {
        actions.add(ac.leftClickAt(w));
      }

      // type into text boxes
      if (isAtBrowserCanvas(w) && (whiteListed(w) || isTypeable(w))) {
        actions.add(ac.clickTypeInto(w, this.getRandomText(w)));
      }

      // slides
      addSlidingActions(actions, ac, scrollArrowSize, scrollThick, w);
    }

    return actions;
  }

  /*
   * We need to check if click position is within the canvas
   */
  private boolean isAtBrowserCanvas(Widget widget) {
    Shape shape = widget.get(Tags.Shape, null);
    if (shape == null) {
      return false;
    }

    double x = shape.x() + shape.width() / 2;
    double y = shape.y() + shape.height() / 2;
    return x > 0 && x < CanvasDimensions.getCanvasWidth() &&
        y > 0 && y < CanvasDimensions.getInnerWidth();
  }

  protected boolean isClickable(Widget w) {
    Role role = w.get(Tags.Role, Roles.Widget);
    if (Role.isOneOf(role, NativeLinker.getNativeClickableRoles())) {
      // Input type are special...
      if (!role.equals(WdRoles.WdINPUT)) {
        String type = ((WdWidget) w).element.type;
        if (WdRoles.clickableInputTypes().contains(type)) {
          return true;
        }
      }
      return true;
    }

    WdElement element = ((WdWidget) w).element;
    if (element.isClickable) {
      return true;
    }

    Set<String> clickSet = new HashSet<>(clickableClasses);
    clickSet.retainAll(element.cssClasses);
    return clickSet.size() > 0;
  }

  protected boolean isTypeable(Widget w) {
    Role role = w.get(Tags.Role, Roles.Widget);
    if (Role.isOneOf(role, NativeLinker.getNativeTypeableRoles())) {
      // Input type are special...
      if (role.equals(WdRoles.WdINPUT)) {
        String type = ((WdWidget) w).element.type;
        return WdRoles.typeableInputTypes().contains(type);
      }
      return true;
    }

    return false;
  }

  /**
   * Select one of the possible actions (e.g. at random)
   *
   * @param state   the SUT's current state
   * @param actions the set of available actions as computed by <code>buildActionsSet()</code>
   * @return the selected action (non-null!)
   */
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
  protected boolean moreActions(State state) {
    return super.moreActions(state);
  }


  /**
   * This method is invoked each time after TESTAR finished the generation of a sequence.
   */
  protected void finishSequence(File recordedSequence) {
    super.finishSequence(recordedSequence);
  }


  /**
   * TESTAR uses this method to determine when to stop the entire test.
   * You could stop the test after a given amount of generated sequences or
   * after a specific time etc.
   *
   * @return if <code>true</code> continue test, else stop
   */
  protected boolean moreSequences() {
    return super.moreSequences();
  }
}