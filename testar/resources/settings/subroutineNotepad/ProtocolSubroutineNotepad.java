package subroutineNotepad;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;
import java.util.HashSet;
import java.util.Set;
import nl.ou.testar.subroutine.SubroutineProtocol;
import nl.ou.testar.subroutine.SubroutineProxy;
import org.fruit.Assert;
import org.fruit.alayer.Action;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.actions.UrlActionCompiler;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.monkey.Settings;

/**
 * Class responsible for executing a SubroutineProtocol with
 * a subroutine for a Notepad desktop application.
 *
 * @author Conny Hageluken
 * @Date January 2019
 */
public class ProtocolSubroutineNotepad
    extends SubroutineProtocol
    implements SubroutineProxy {
  
  /**
   * Start up the subroutine only once.
   */
  private static final double SCROLLARROWSIZE = 36; // sliding arrows (iexplorer)
  private static final double SCROLLTHICK = 16; // scroll thickness (iexplorer)

  /**
   * Default constructor.
   */
  public ProtocolSubroutineNotepad() {
    //default constructor
  }

  /**
   * State is fulfilling criterion for running a subroutine.
   * Important to define in this method
     * - setSourceFile: a valid Tgherkin subroutine filename
     * - setActualIndexSD: index of the actual subroutine
   * @param state the SUT's current state
     * @return state is ready for subroutine action
   */
  @Override
  public boolean startState(State state) {
    String lastAction = null;   
    String role = null;

    try {
    lastAction = getLastActionTitle(state);   
    role = getLastActionRole(state);
    } catch (Exception e) {
      return false;
    }
    if (role != null && "UIAMenuItem".equalsIgnoreCase(role)) {
      for (Integer index: getSubData().keySet()) {
        String selected = getSubData().get(index)[0];
        if (selected.equalsIgnoreCase(lastAction)) {
          System.out.println("ProtocolSubroutineNotepad temp] " + "./" + getProtocolFolder() + "/" + getSubData().get(index)[1] );
          
          setActualIndexSD(index);
          try {
            setSourceFile("./" + getProtocolFolder() + "/" + getSubData().get(index)[1]);
            return true;
          } catch (Exception e) {
            e.printStackTrace();
          }
          
        } 
      }
    }
    return false;
  }

  /**
   * This method is invoked each time TESTAR starts to generate a new subroutine.
   * - start a subroutine with this document (initializeDocument())
   * @param state the SUT's current state
   */
  @Override
  public void startSubroutine(State state) {
    super.initializeDocument();
    super.startSubroutine(state);
  }

  /** Define action to be taken when switching from subroutine to .
   * @param sut the system under test
   * @param state the SUT's current state
   * @return the action to be taken
   */
  @Override
  public Set<Action> finishState(SUT sut, State state) {
    return super.finishState(sut, state);
  }

  /**
   * This method is invoked each time after TESTAR finishes the generation of a subroutine.
   */
  @Override
  public void finishSubroutine(State state) {
    super.finishSubroutine(state);
  }

  /**
   * Called once during the life time of TESTAR.
   * This method can be used to perform initial setup work
   * @param   settings   the current TESTAR settings as specified by the user.
   */
  @Override
  protected void initialize(Settings settings) {
    super.initialize(settings);
    readSubroutineDataInputfile();
  }

  /**
   * This method is used by TESTAR to determine the set of currently available actions.
   * You can use the SUT's current state, analyze the widgets and their properties to create
   * a set of sensible actions, such as: "Click every Button which is enabled" etc.
   * The return value is supposed to be non-null. If the returned set is empty, TESTAR
   * will stop generation of the current action and continue with the next one.
   * @param sut the system under test
   * @param state the SUT's current state
   * @return  a set of actions
   */
  @Override
  protected Set<Action> deriveActions(SUT sut, State state) throws ActionBuildException {
    return super.deriveActions(sut, state);
  }

  /**
   * Default derivation of action steps.

   * @param state the SUT's current state
   * @return actions the actions to be taken
   */
  @Override
  protected Set<Action> defaultDeriveActions(SUT system, State state) {
    Assert.notNull(state);
    Set<Action> actions = new HashSet<Action>();
    
    // iterate through all (top) widgets
    StdActionCompiler ac = new UrlActionCompiler();

    for (Widget widget: getTopWidgets(state)) {
      // only consider enabled and non-blocked widgets
      if (widget.get(Enabled, true) && !widget.get(Blocked, false)) {

        // do not build actions for tabu widget
        if (blackListed(widget)) {
          continue;
        }

       // left clicks
        if (whiteListed(widget) || isClickable(widget)) {
          actions.add(ac.leftClickAt(widget));
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
      addSlidingActions(actions, ac, SCROLLARROWSIZE, SCROLLTHICK, widget, state);
    }
    return actions;
  }
  
  /**
   * This is a helper method used by the default implementation of <code>buildState()</code>.
   * It examines the SUT's current state and the subroutine verdict
   * @return oracle verdict, which determines whether the state is erroneous and why.
   */
  @Override
  protected Verdict getVerdict(State state) {
    return super.getVerdict(state);
  }

  /**
   * Select one of the possible actions (e.g. at random).
   * @param state the SUT's current state
   * @param actions the set of available actions as computed by <code>buildActionsSet()</code>
   * @return  the selected action (non-null!)
   */
  @Override
  protected Action selectAction(State state, Set<Action> actions) {
    return super.selectAction(state, actions);
  }

  /**
   * Execute the selected action.

   * @param state the SUT's current state
   * @param action the action to execute
   * @return whether or not the execution succeeded
   */
  @Override
  protected boolean executeAction(SUT sut, State state, Action action) {
    return super.executeAction(sut, state, action);
  }

  /**
   * TESTAR uses this method to determine when to stop the generation of actions for the
   * current sequence. You could stop the sequence's generation after a given amount of executed
   * actions or after a specific time etc.
   * @return  if <code>true</code> continue generation, else stop
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
   * @return  if <code>true</code> continue test, else stop   */
  @Override
  protected boolean moreSequences() {
    return super.moreSequences();
  }
  
  protected boolean blackListed(Widget w) {
    String title = w.get(Tags.Title);
    if ("Maximaliseren".equals(title)) {
      return false;
    }
    
    if ("Minimaliseren".equals(title)) {
      return false;
    }
 
    if ("Systeem".equals(title)) {
      return false;
    }
    
    if ("Vorig formaat".equals(title)) {
      return false;
    }
    
    if ("Verplaatsen".equals(title)) {
      return false;
    }
    
    if ("Formaat wijzigen".equals(title)) {
      return false;
    }
    
    if ("Windows (CRLF)".equals(title)) {
      return false;
    }

    if ("100%".equals(title)) {
      return false;
    }
    
    if ("Regel omhoog".equals(title)) {
      return false;
    }
    
    if ("Regel omlaag".equals(title)) {
      return false;
    }
    
    if ("Help weergeven".equals(title)) {
      return false;
    }

    if (title.contains("Ln") && title.contains("Col")) {
      return false;
    }

    return filteringManager.blackListed(w);
  }

}
