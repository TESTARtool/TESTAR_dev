package formAnwb;

import nl.ou.testar.subroutine.FormProtocol;

/**
 * Class responsible for executing a FormProtocol with
 * a subroutine for completing a form on the ANWB site.
 *
 * @author Conny Hageluken
 * @Date January 2019
 */
public class ProtocolFormAnwb
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
 public ProtocolFormAnwb() {
   setMinNoOfEditWidgets(2);
   setNumberOfScreens(3);
   setPrintWidgets(false);
   setPrintBuild(true);
 } 
}
