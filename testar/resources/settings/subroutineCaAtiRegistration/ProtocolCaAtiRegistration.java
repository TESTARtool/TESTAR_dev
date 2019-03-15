package subroutineCaAtiRegistration;

import nl.ou.testar.subroutine.CompoundActionProtocol;

/**
 * Class implementing the subroutine frame work with option TG 
 * for a web form on the ATI site, registration of a new user.
 *
 * @author Conny Hageluken
 * @Date January 2019
 */
public class ProtocolCaAtiRegistration
    extends CompoundActionProtocol {

  /**
   * Constructor.
   * Settings for form parameters
   * - Set number of editable widgets that is used as a criterion to define a form
   * - Set the number of screens a form consists of.
   * Remainder of settings default values
   */
  public ProtocolCaAtiRegistration() {
    setMinNoOfEditWidgets(6);
    setNumberOfScreens(2);
  }
}
