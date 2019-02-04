package formDtg;

import java.util.HashSet;
import java.util.Set;
import org.fruit.alayer.Action;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.UrlActionCompiler;
import nl.ou.testar.subroutine.FormProtocol;

/**
 * Class responsible for executing a FormProtocol with
 * a subroutine for completing a form on deTelefoongids site.
 *
 * @author Conny Hageluken
 * @Date January 2019
 */
public class ProtocolFormDtg
    extends FormProtocol {

  /**
   * Constructor.
   * Settings for form parameters
   * - Set number of editable widgets that is used as a criterion to define a form
   * - Set the number of screens a form consists of.
   * Settings for print facilities
   * - Print additional information on widgets
   *   => helps to define the contents of input data file
   * - Print additional information on building compound action for current screen.
   *   => show the actual building step for each predefined widget
   */
  public ProtocolFormDtg() {
    setMinNoOfEditWidgets(3);
    setNumberOfScreens(1);
    setPrintWidgets(false);
    setPrintBuild(true);
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
}
