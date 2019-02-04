package subroutineDtg;

import java.util.HashSet;
import java.util.Set;
import nl.ou.testar.subroutine.SubroutineProtocol;
import org.fruit.alayer.Action;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.UrlActionCompiler;

/**
 * Class responsible for executing a SubroutineProtocol with
 * a subroutine for web form on the DTG site (dutch telephone guide).
 *
 * @author Conny Hageluken
 * @Date October 2018
 */
public class ProtocolSubroutineDtg
    extends SubroutineProtocol {

  /**
   * Start up the subroutine only once.
   */
  private int onlyOnce = 1;

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
    if (onlyOnce == 1) {
      for (Widget widget: getTopWidgets(state)) {
        String title = widget.get(Tags.Title, null).toString();
        if (title.equalsIgnoreCase(getAddressTitle())) {
          String value = widget.get(Tags.ValuePattern, null);
          for (Integer index: getSubData().keySet()) {
            String subStr = getSubData().get(index)[0];
            if (value != null && value.contains(subStr)) {
              setActualIndexSD(index);
              try {
                setSourceFile("./" + getProtocolFolder() + "/" + getSubData().get(index)[1]);
              } catch (Exception e) {
                e.printStackTrace();
              }
              onlyOnce++;
              return true;
            }
          }
          break;
        }
      }
    }
    return false;
  }

  /** Define action to be taken when switching from subroutine to TESTAR.
   * @param sut the system under test
   * @param state the SUT's current state
   * @return the action to be taken
   */
  @Override
  public Set<Action> finishState(SUT sut, State state) {
    Set<Action> actions = new HashSet<Action>();

    Action action = null;
    UrlActionCompiler ac = new UrlActionCompiler();

    for (Widget widget: getTopWidgets(state)) {
      String title = widget.get(Tags.Title, null);
      if ("feedback".equalsIgnoreCase(title)) {
        action = ac.leftClickAt(widget);
      }
    }
    actions.add(action);
    return actions;
  }

  /**
   * This method is invoked each time after TESTAR finished the generation of a sequence.
   */
  @Override
  protected void finishSequence() {
    onlyOnce = 1;
    super.finishSequence();
  }
}
