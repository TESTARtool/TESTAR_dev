package subroutineFormDtg;

import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import nl.ou.testar.subroutine.FormProtocol;

/**
 * Class responsible for executing a FormProtocol with
 * a subroutine for completing a form on deTelefoongids site.
 *
 * @author Conny Hageluken
 * @Date January 2019
 */
public class ProtocolSubroutineFormDtg
    extends FormProtocol {

  private String searchTitle = "/personen/6-1/";
 /**
   * Constructor.
   * Including settings for print facilities
   * - Print additional information on widgets
   * - Set number of editable widgets is used as a criterion to define a form
   * - Set maximum number of screens a form consists of.
   */
  public ProtocolSubroutineFormDtg() {
    setPrintWidgets(false);
    setPrintBuild(true);
    setMinimumNumberOfEditWidgets(5);
    setMaximumNumberOfScreens(1);
  }
  /**
   * State is fulfilling criterion for running a form subroutine.
   * @param state     the SUT's current state
   * @return state    is ready for form subroutine action
   */
  @Override
  public boolean startState(State state) {
    setStartBoolean(false);
    // Set actual index of widget condition to 1 if you are using the Form Data tab on
    // the TESTAR panel
    String subStr = searchTitle;
    for (Widget widget: getTopWidgets(state)) {
      String title = widget.get(Tags.Title, null).toString();
      if (title.equalsIgnoreCase(getAddressTitle())) {
        String value = widget.get(Tags.ValuePattern, null);

        if (value != null && value.contains(subStr)) {
          setStartBoolean(true);
        }
      }
    }

    // Printing additional information, optional (setPrintScreenNumber(true))
    if (isPrintWidgets()) {
       System.out.print("[FP/startState] form available, create compound action\n");
    }
    return isStartBoolean();
  }
 }
