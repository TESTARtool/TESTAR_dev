package nl.ou.testar.subroutine;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.fruit.alayer.Action;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.CompoundAction.Builder;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.actions.CompoundAction;
import org.fruit.alayer.actions.KeyDown;
import org.fruit.alayer.actions.KeyUp;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

/**
 * Abstract class responsible for executing a protocol enriched with a subroutine framework, 
 * where the subroutine is replaced by compound actions.
 *
 * @author Conny Hageluken
 * @Date January 2019
 */
public abstract class CompoundActionProtocol
    extends SubroutineProtocol {

  /**
   * Constructor.
   * Settings for form parameters
   * - Set the number of editable widgets that is used as the criterion to define a form
   * - Set the number of screens a form consists of.
   * Settings for print facilities
   * - Print additional information on widgets
   *   => helps to define the contents of input data file
   * - Print additional information on building compound action for current screen.
   *   => show the actual building step for each predefined widget
   */
  public CompoundActionProtocol() {
    setMinNoOfEditWidgets(5);
    setNumberOfScreens(1);
    setPrintWidgets(false);
    setPrintBuild(true);
  }
  // if true a subroutine can start
  private static boolean startBoolean = false;

  /**   * Compound action for widgets on a form.
   */
  private static Action caAction; 

  /**
   * The cumulative list of compound actions.
   */
  private static Set<Action> listOfCaActions = new HashSet<Action>();

   /**
   * Sequence number of previous action.
   */
  private static int previousSequence = 1;

  /**
   * Number of editable widgets is used as a criterion to define a form.
   * default value is 5
   */
  private int minNoOfEditWidgets = 5;

  /**
   * Form consists of one or more screens.
   * If size of listOfActionsOnForm
   * - = 0                   form has not yet been completed in this sequence
   * - <0, numberOfScreens>  continue search for new unique screen to complete form
   * - >= numberOfScreens    form has been completed in this sequence
   */
  private int numberOfScreens = 1;

  /**
   * Print additional information on widgets.
   * Either use this option or the Spy mode to retrieve characteristics 
   * of the widgets on a web page
  */  
  private boolean printWidgets = false;

  /**
   * Print additional information on building the compound action.
   */
  private boolean printBuild = true;

  /**
   * State is fulfilling criterion for running a form subroutine.
   * Number of editable widgets is at least equal to or larger than minNoOfEditWidgets
   * @param state the SUT's current state
   * @return state is ready for form subroutine action
   */
  @Override
  public boolean startState(State state) {
    
    // Set actual index of widget condition to 1 if you are using the Form Data tab on
    // the TESTAR panel
    setActualIndex(1);
    int numberOfEditWidgets = countEditWidgets(state);
    // Number of editable widgets is at least equal to or larger than minNoOfEditWidgets
    setStartBoolean(numberOfEditWidgets >= minNoOfEditWidgets);

    // Printing additional information, optional (setPrintScreenNumber(true))
    if (isPrintWidgets()) {
       System.out.print("[FP/startState] form available, create compound action\n");
       System.out.println("                number of editable widgets is "
                + numberOfEditWidgets + " (min = " + minNoOfEditWidgets + ")");
    }
    return isStartBoolean();
  }
  
  /**
   * This method is invoked each time TESTAR starts to generate a new subroutine for a form.
   * It creates a compound action based on the contents of ca_data
   * @param state the SUT's current state
   */
  @Override
  public void startSubroutine(State state) {

    System.out.println("Start action subroutine");
    Builder builder = new CompoundAction.Builder();

    // add text fields using form data set
    builder = selectRoleSet(builder, "UIAEdit", state);

    // add radio button using form data set
    builder = selectRoleSet(builder, "UIARadioButton", state);

    // add check box using form data set
    builder = selectRoleSet(builder, "UIACheckBox", state);

    // add text fields using form data set
    builder = selectRoleSet(builder, "UIASpinner", state);

    // add custom control using form data set
    builder = selectRoleSet(builder, "UIACustomControl", state);

    // add buttons using form data set
    builder = selectRoleSet(builder, "UIAButton", state);

    if (numberOfScreens > 1) {
      // add page down
      addPageDown(builder);
    }

    // final compound action
    caAction = builder.build();
  }

  /**
   * This method is invoked each time after TESTAR finishes the generation of a CA subroutine.
   * @param state the SUT's current state
   */
  @Override
  public void finishSubroutine(State state) {
    System.out.println("Finish action subroutine");
    setStartBoolean(false);
  }
  
  /** 
   * Define a set of actions to be taken when switching from the subroutine to TESTAR.
   * @param state the SUT's current state
   * @return the available actions
  */
  @Override
  public Set<Action> finishState(SUT sut, State state) {
    Set<Action> actions = defaultDeriveActions(sut, state);
    return actions;
  }

  /** Derive actions to run the subroutine.
   * @param state the SUT's current state
   * @param sut the system under testing
   * @param state the SUT's current state
   * @return action set
   */
  @Override
  public Set<Action> deriveActions(SUT sut, State state) throws ActionBuildException {
    Set<Action> actions = new HashSet<Action>();
    int n = listOfCaActions.size();
    if (n == 0) {
      if (startState(state)) {
        System.out.println("Start state subroutine");
        System.out.println("Start action subroutine");
      }
    }
    if (isStartBoolean() && n >= 0 && n < numberOfScreens) {
      // Create compound action for current screen
      startSubroutine(state);

      // Add each unique compound action (i.e. screen) to a list.
      // As such count the number of screens that have been completed
      if (!listOfCaActions.contains(caAction)) {
        listOfCaActions.add(caAction);
      }
      // Current action only contains the previously defined compound action
      actions.add(caAction);
    } else { 
      // Last screen is available
      if (isStartBoolean() && listOfCaActions.size() == numberOfScreens) {
        finishSubroutine(state);
        System.out.println("Finish state subroutine");
        actions = finishState(sut, state);
      } else {
        System.out.println("Continue TESTAR");
        actions = defaultDeriveActions(sut, state);
      }
    }
    return actions;
  }

  /**
   * Initialize input data file and input data set
   */
  protected void initInputData(Settings settings) {
    setDataInputFile(new File(settings.get(ConfigTags.CompoundActionData)));
    setInputData(readDataInputfile());
  }

  /**
   * Counting the number of editable widgets on a web page, Role = UIAEdit
   * @param state the SUT's current state
   * @return the number of editable widgets on a web page
   */
  protected int countEditWidgets(State state) {
    int numberOfEditWidgets = 0;
    int teller = 0;
    for (Widget widget: state) {
      teller++;
      String role = widget.get(Tags.Role, null).toString();
      String title = widget.get(Tags.Title, "");
      if ("UIAEdit".equalsIgnoreCase(role) && !title.isEmpty()) {
        numberOfEditWidgets++;
        if (isPrintWidgets()) {
          System.out.println("[CA Option] Editable widget no " + teller + " role: " + role + " title: " + title);
          System.out.println("[CA Option] Number of editable widgets " + numberOfEditWidgets);
        }
      }
      // optional: Print additional information on widgets
      if (isPrintWidgets() && !title.isEmpty()) {
        System.out.println("[CA Option] no " + teller + " role: " + role + " title: " + title);
      }
    }
    return numberOfEditWidgets;
  }

  /**
   * Create compound action on all available fields with same role using form data set.
   * @param builder  used to build the compound action
   * @param role     specific widget role
   * @param state    the SUT's current state
   * @return builder subset for the specified role
   */
  public Builder selectRoleSet(Builder builder, String role, State state) {
    StdActionCompiler uc = new StdActionCompiler();
    
    for (Integer index: getInputData().keySet()) {
      String searchRole = getInputData().get(index)[0];
      String searchLabel = getInputData().get(index)[1];
      String searchInput = getInputData().get(index)[2];
      for (Widget w: getTopWidgets(state)) {
        String textLabel = w.get(Tags.Title);
        String textRole = w.get(Tags.Role).toString();
        
        if (// item in form data set
            searchLabel.equals(textLabel) && searchRole.equals(textRole)
            // default role, input variable
            && searchRole.equals(role)) {

          // Printing additional information, optional (setPrintBuild(true))
          if (printBuild) {
            System.out.print("Compound action: " + searchRole + " => ");
          }

          // Role: UIAEdit or UIASpinner => clickTypeInto
          if ("UIAEdit".equals(role)
              || "UIAText".equals(role)
              || "UIASpinner".equals(role)) {
            if (!searchInput.isEmpty()) {

              // Printing additional information, optional (setPrintBuild(true))
              if (printBuild) {
                System.out.println("clickTypeInto(" + searchLabel + ": " + searchInput + ")");
              }
              builder.add(uc.clickTypeInto(w, searchInput, true), 5);
            }
          }

          // Role: UIAButton or UIARadioButton => leftClickAt
          if ( ("UIAButton".equals(role)
              || "UIARadioButton".equals(role)
              || "UIACustomControl".equals(role)
              || "UIACheckBox".equals(role))
              && searchRole.equals(textRole)) {

            // Printing additional information, optional (setPrintBuild(true))
            if (printBuild) {
              System.out.println("leftClickAt(" + searchLabel + ")");
            }
            builder.add(uc.leftClickAt(w), 5);
          }
          break;
        }
      }
    }
    return builder;
  }

  /**
   * Add page down action to the builder.
   * @param builder  used to build the compound action
   * @return builder builder for compound action
   */
  public Builder addPageDown(Builder builder) {
    if (printBuild) {
      System.out.println("Compound action: => page down");
    }
   // page down (if more screens are available)
    return builder.add(new KeyDown(KBKeys.VK_PAGE_DOWN), 5).add(new KeyUp(KBKeys.VK_PAGE_DOWN), 5);
  }

  /**
   * Execute the selected action.
   * Determine whether or not the execution succeeded
   * Update value of previous sequence and form action
   * @param state the SUT's current state
   * @param action the action to execute
   * @return whether or not the execution succeeded
   */
  @Override
  protected boolean executeAction(SUT system, State state, Action action) {
    // Execute current action on form
    boolean actionExecuted = super.executeAction(system, state, action);
    
    if (actionExecuted) {
      // Start a new sequence. 
      // Initializes parameters to start values/
      if (previousSequence < sequenceCount()) {
        previousSequence = sequenceCount();
        listOfCaActions = new HashSet<Action>();
      }
      // Reset action on form
      caAction = null;
    }
    return actionExecuted;
  }
  
  // getters and setters

  /**
   * @return the startBoolean
   */
  public static boolean isStartBoolean() {
    return startBoolean;
  }

  /**
   * @param startBoolean the startBoolean to set
   */
  public static void setStartBoolean(boolean strtBoolean) {
    startBoolean = strtBoolean;
  }
  
  /**
   * @return the minNoOfEditWidgets
   */
  public int getMinNoOfEditWidgets() {
    return minNoOfEditWidgets;
  }

  /**
   * @param minNoOfEditWidgets the minimum number of editable widgets to set
   */
  public void setMinNoOfEditWidgets(int minOfEdtWidgets) {
    minNoOfEditWidgets = minOfEdtWidgets;
  }
  
  /**
   * @return the numberOfScreens
   */
  public int getNumberOfScreens() {
    return numberOfScreens;
  }

  /**
   * @param numberOfScreens the maximum number of screens to set
   */
  public void setNumberOfScreens(int numberScreens) {
    numberOfScreens = numberScreens;
  }

  /**
   * @return the printWidgets
   */
  public boolean isPrintWidgets() {
    return printWidgets;
  }
  
  /**
   * @param printWidgets the printWidgets to set
   */
  public void setPrintWidgets(boolean prntWidgets) {
    printWidgets = prntWidgets;
  }
  
  /**
   * @return the printBuild
   */
  public boolean isPrintBuild() {
    return printBuild;
  }

  /**
   * @param printBuild the printBuild to set
   */
  public void setPrintBuild(boolean prntBuild) {
    printBuild = prntBuild;
  }

  /**
   * @return the ca_action
   */
  public static Action getCa_action() {
    return caAction;
  }

  /**
   * @param caAction the ca_action to set
   */
  public static void setCa_action(Action ca_acton) {
    caAction = ca_acton;
  }

  /**
   * @return the listOfCaActions
   */
  public static Set<Action> getListOfCaActions() {
    return listOfCaActions;
  }

  /**
   * @param listOfCaActions the listOfCaActions to set
   */
  public static void setListOfCaActions(Set<Action> listCaActions) {
    listOfCaActions = listCaActions;
  }
}
