package subroutineNotepad;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;
import java.util.HashSet;
import java.util.Set;
import nl.ou.testar.subroutine.SubroutineProtocol;
import org.fruit.Assert;
import org.fruit.alayer.Action;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.actions.UrlActionCompiler;

/**
 * Class responsible for executing a SubroutineProtocol with
 * a subroutine for a Notepad desktop application.
 *
 * @author Conny Hageluken
 * @Date January 2019
 */
public class ProtocolSubroutineNotepad
    extends SubroutineProtocol {

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
          System.out.println(
              "ProtocolSubroutineNotepad temp] " + "./" + getProtocolFolder() + "/"
                  + getSubData().get(index)[1]
          );

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

        // do not build actions for tab widget
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
      addSlidingActions(actions, ac, getScrollarrowsize(), getScrollthick(), widget, state);
    }
    return actions;
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
