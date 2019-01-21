package formRegistration;

import org.fruit.alayer.State;
import org.fruit.alayer.actions.CompoundAction;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.actions.UrlActionCompiler;
import org.fruit.alayer.actions.CompoundAction.Builder;
import nl.ou.testar.subroutine.FormProtocol;

/**
 * Class responsible for executing a FormProtocol with
 * a subroutine for completing a form on ATI registration form.
 *
 * @author Conny Hageluken
 * @Date January 2019
 */
public class ProtocolFormRegistration
    extends FormProtocol
     {
 
  /**
   * Constructor.
   * Including settings for print facilities
   * - Print additional information on widgets
   * - Print additional information on screen number where you want to start an action based on start state.
   * - Set number of editable widgets is used as a criterion to define a form
   * - Set maximum number of screens a form consists of.
   */
  public ProtocolFormRegistration() {
    setPrintWidgets(false);
    setPrintScreenNumber(true);
    setPrintBuild(true);
    setMinimumNumberOfEditWidgets(6);
    setMaximumNumberOfScreens(2);
  }  /**
   * This method is invoked each time TESTAR starts to generate a new form subroutine.
   * It creates a compound action based on the contents of formData
   * @param state the SUT's current state
   * @throws InterruptedException 
   */
  @Override
  public void startSubroutine(State state) {
    StdActionCompiler ac = new StdActionCompiler();
    UrlActionCompiler uc = new UrlActionCompiler();
    Builder builder = new CompoundAction.Builder();
    if (screenNumber == 1) {
      // add text fields using form data set
      builder = selectRoleSet(builder, "UIAEdit", uc, state);
      
      // page down
      builder = AddScrollPage(builder);
    } else if (screenNumber == 2) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        // add text fields using form data set   
        builder = selectRoleSet(builder, "UIAEdit", uc, state);
       
        // add buttons using form data set    
        builder = selectRoleSet(builder, "UIACheckBox", uc, state);
        
        // add buttons using form data set
        builder = selectRoleSet(builder, "UIAButton", uc, state);

        // page down
        builder = AddScrollPage(builder);
    } 
   
    // final compound action
    actionOnForm = builder.build();
  }
}
