package subroutineCaDtg;

import java.util.HashSet;
import java.util.Set;
import org.fruit.alayer.Action;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.StdActionCompiler;
import nl.ou.testar.subroutine.CompoundActionProtocol;

/**
 * Class implementing the subroutine frame work with option CA 
 * for a web form on the DTG site (dutch telephone guide).
*
 * @author Conny Hageluken
 * @Date January 2019
 */
public class ProtocolCaDtg
    extends CompoundActionProtocol {

  /**
   * Constructor.
   * Settings for form parameters
   * - Set number of editable widgets that is used as a criterion to define a form
   * Remainder of settings default values
   */
  public ProtocolCaDtg() {
    setMinNoOfEditWidgets(3);
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
    StdActionCompiler ac = new StdActionCompiler();

    for (Widget widget: getTopWidgets(state)) {
      String title = widget.get(Tags.Title, null);
      if ("feedback".equalsIgnoreCase(title)) {
        action = ac.leftClickAt(widget);
      }
    }
    if (action != null) {
      actions.add(action);
    }
    return actions;
  }
}
