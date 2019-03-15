package subroutineCaTgDtg;

import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import nl.ou.testar.subroutine.CompoundActionProtocol;

/**
 * Class implementing the subroutine frame work with option CA 
 * for a web form on deTelefoongids.nl (DTG site).
 *
 * @author Conny Hageluken
 * @Date January 2019
 */
public class ProtocolCaTgDtg
    extends CompoundActionProtocol {

  private String searchTitle = "/personen/6-1/";
  /**
   * Constructor.
   * Settings for form parameters
   * - Set number of editable widgets that is used as a criterion to define a form
   * Remainder of settings default values
   */
  public ProtocolCaTgDtg() {
     setMinNoOfEditWidgets(5);
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
