package nl.ou.testar.subroutine;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import org.fruit.alayer.Action;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.StdActionCompiler;
import org.fruit.monkey.Settings;
import es.upv.staq.testar.protocols.ClickFilterLayerProtocol;

/**
 * Abstract class responsible for executing a protocol enriched with a subroutine framework, 
 * Core methods.
 *
 * @author Conny Hageluken
 * @Date January 2019
 */
public abstract class SubroutineProtocol
    extends ClickFilterLayerProtocol 
    implements SubroutineProxy{

  public SubroutineProtocol() {
  }
  
  /**
   * The text of address- and search bar in the Dutch version (see ENUM Browser).
   */
  private static String addressTitle = Browser.chromeNL.getAddressTitle();

  /**
   * data input file.
   * with format (.csv) Integer|String a|String b|String c
   * Contents can be adapted using input data panel from main TESTAR menu
   */
  private static File dataInputFile;

  /**
   * Data set with options for widgets on a form.
   */
  private static HashMap<Integer,String[]> inputData = new HashMap<Integer,String[]>();

  /**
   * Index of the current data line in the data input file.
   */
  private static int actualIndex = 0;

  private static final double SCROLLARROWSIZE = 36; // sliding arrows (iexplorer)
  private static final double SCROLLTHICK = 16; // scroll thickness (iexplorer)

  /**
   *  Each browser (and locale!) uses different names for standard elements.
   */
  public enum Browser {

    // Dutch version
    explorerNL("address", "UIAEdit", "back", "close"),
    firefoxNL("voer zoekterm of adres in", "UIAEdit", "terug",null),
    chromeNL("Adres- en zoekbalk", "UIAEdit", "vorige", "sluiten"),

    // English version
    explorerEN("address", "UIAEdit", "back", "close"),
    firefoxEN("voer zoekterm of adres in", "UIAEdit", "terug",null),
    chromeEN("address and search bar", "UIAEdit", "back", "close");

    public String getAddressTitle() {
      return addressTitle;
    }

    private String addressTitle;
    
    @SuppressWarnings ("unused")
    private String addressRole;
    
    @SuppressWarnings ("unused")
    private String backTitle;
    
    @SuppressWarnings ("unused")
    private String closeTitle;

    Browser(String addressTitle, String addressRole, String backTitle, String closeTitle) {
      this.addressTitle = addressTitle;
      this.addressRole = addressRole;
      this.backTitle = backTitle;
      this.closeTitle = closeTitle;
    }
  }
  
  /**
   * This method is used to perform initial setup work.
   * Called once during the lifetime of TESTAR
   * @param settings the current TESTAR settings as specified by the user.
   */
  @Override
  protected void initialize(Settings settings) {
    super.initialize(settings);
    initInputData(settings);
  }
  
  /**
   * Initialize input data file and input data set
   */
  protected void initInputData(Settings settings) {
  }

  /**
   * Read data from the data input file.
   * Contents of the file is stored in input data set
   * with format (.csv) Integer|String a|String b|String c
   * Contents can be adapted using input data panel from main TESTAR menu
   * @return input data set
   */
  protected HashMap<Integer, String[]> readDataInputfile() {
    HashMap<Integer, String[]> outputData = new HashMap<Integer, String[]>();
    int index = 0;
    String a, b, c;
    try (Scanner in = new Scanner(dataInputFile)) {
      while (in.hasNextLine()) {
        String instring = in.nextLine();
        int n = instring.indexOf("|");
        a = instring.substring(0, n);
        b = instring.substring(n + 1);
        instring = b;
        n = instring.indexOf("|");
        if (n < 0) {
          b = instring;
          c = "";
        } else {
          b = instring.substring(0, n);
          c = instring.substring(n + 1);
        }
        if (a != null && b != null && c != null) {
          index++;
          String[] r = { a, b, c };
          outputData.put(index, r);
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return outputData;
  }
  
  /**
   * Default derivation of action steps.
   * @param sut the system under test
   * @param state the SUT's current state
   * @return actions the actions to be taken
   */
  protected Set<Action> defaultDeriveActions(SUT sut, State state) {
    Set<Action> actions = super.deriveActions(sut, state);

    // iterate through all (top) widgets
    StdActionCompiler uc = new StdActionCompiler();

    for (Widget widget: getTopWidgets(state)) {
      // only consider enabled and non-blocked widgets
      if (widget.get(Enabled, true) && !widget.get(Blocked, false)) {

        // do not build actions for tab widget
        if (blackListed(widget)) {
          continue;
        }

        // left clicks
        if (whiteListed(widget) || isClickable(widget)) {
          continue;
        }

        storeWidget(state.get(Tags.ConcreteID), widget);
        actions.add(uc.leftClickAt(widget));
      }

      // type into text boxes
      if (whiteListed(widget) || isTypeable(widget)) {
        storeWidget(state.get(Tags.ConcreteID), widget);
        actions.add(uc.clickTypeInto(widget, this.getRandomText(widget), true));
      }

      // slides
      addSlidingActions(actions, uc, SCROLLARROWSIZE, SCROLLTHICK, widget, state);
    }
    return actions;
  }
   
  // getters and setters

  /**
   * Retrieve addressTitle.
   * @return addressTitle
   */
  public static String getAddressTitle() {
    return addressTitle;
  }
  
  /**
   * @return the dataInputFile
   */
  public static File getDataInputFile() {
    return dataInputFile;
  }

  /**
   * Set data input file.
   * @param filename name of data input file
   */
  public static void setDataInputFile(File filename) {
    dataInputFile = filename;
  }
 
  /**
   * @return the inputData
   */
  public static HashMap<Integer, String[]> getInputData() {
    return inputData;
  }

  /**
   * @param inptData the inputData to set
   */
  public static void setInputData(HashMap<Integer, String[]> inptData) {
    inputData = inptData;
  }

  /**
   * @return the actualIndexSubD
   */
  public static int getActualIndex() {
    return actualIndex;
  }

  /**
   * @param actualIndex Index of the currently running subroutine
   */
  public static void setActualIndex(int index) {
    actualIndex = index;
  }

  /**
   * @return the scrollarrowsize
   */
  public static double getScrollarrowsize() {
    return SCROLLARROWSIZE;
  }

  /**
   * @return the scrollthick
   */
  public static double getScrollthick() {
    return SCROLLTHICK;
  }
}
