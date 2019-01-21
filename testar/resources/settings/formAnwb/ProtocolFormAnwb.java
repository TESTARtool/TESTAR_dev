package formAnwb;

import org.fruit.alayer.State;
import org.fruit.alayer.actions.CompoundAction;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.actions.UrlActionCompiler;
import org.fruit.alayer.actions.CompoundAction.Builder;
import nl.ou.testar.subroutine.FormProtocol;

/**
 * Class responsible for executing a FormProtocol with
 * a subroutine for completing a form on the ANWB site.
 *
 * @author Conny Hageluken
 * @Date January 2019
 */
public class ProtocolFormAnwb
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
  public ProtocolFormAnwb() {
    setPrintWidgets(false);
    setPrintScreenNumber(true);
    setPrintBuild(true);
    setMinimumNumberOfEditWidgets(2);
    setMaximumNumberOfScreens(2);
  }
  
  /**
   * This method is invoked each time TESTAR starts to generate a new form subroutine.
   * It creates a compound action based on the contents of formData
   * @param state the SUT's current state
   */
  @Override
  public void startSubroutine(State state) {
    StdActionCompiler ac = new StdActionCompiler();
    UrlActionCompiler uc = new UrlActionCompiler();
    Builder builder = new CompoundAction.Builder();
    if (screenNumber == 1) {
      // add text fields using form data set
      builder = selectRoleSet(builder, "UIAEdit", uc, state);
  
      // add radiobutton using form data set
      builder = selectRoleSet(builder, "UIARadioButton", ac, state);
      
      // page down
      builder = AddScrollPage(builder);
    } else if (screenNumber == 2) {
        
        // add text fields using form data set   
        builder = selectRoleSet(builder, "UIAEdit", uc, state);
    
        // add radio buttons using form data set
        builder = selectRoleSet(builder, "UIARadioButton", ac, state);
    
        // add buttons using form data set
        builder = selectRoleSet(builder, "UIAButton", uc, state);
        // page down
        builder = AddScrollPage(builder);
    } 
    /*
    else if (screenNumber == 3) {
        
        // add text fields using form data set   
        builder = selectRoleSet(builder, "UIASpinner", uc, state);
    
        // add radio buttons using form data set
        builder = selectRoleSet(builder, "UIACustomControl", ac, state);
    
        // add buttons using form data set
        builder = selectRoleSet(builder, "UIAButton", uc, state);
    }
    */
    
    // final compound action
    actionOnForm = builder.build();
  }
 }
