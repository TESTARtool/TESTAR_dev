package subroutineCaAtiAccountDetails;

import org.fruit.alayer.State;
import nl.ou.testar.subroutine.CompoundActionProtocol;

/**
 * Class implementing the subroutine frame work with option TG 
 * for a web form on the ATI site, modifying account details of a user.
 *
 * @author Conny Hageluken
 * @Date January 2019
 */
public class ProtocolCaAtiAccountDetails
    extends CompoundActionProtocol {

  /**
   * Constructor.
   * Settings for form parameters
   * - Set number of editable widgets that is used as a criterion to define a form
   * - Set the number of screens a form consists of.
   * Remainder of settings default values
   */
  public ProtocolCaAtiAccountDetails() {
    setMinNoOfEditWidgets(2);
    setNumberOfScreens(3);
  }
  
  /**
   * This method is invoked each time TESTAR starts to generate a new subroutine for a form.
   * It creates a compound action based on the contents of ca_data
   * @param state the SUT's current state
   */
  @Override
  public void startSubroutine(State state) {
    int n = getListOfCaActions().size();
    
    if (n == 1) {
      try {
        Thread.sleep(4000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    super.startSubroutine(state);
    System.out.println(n);
  }
}
