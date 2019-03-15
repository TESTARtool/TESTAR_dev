package nl.ou.testar.subroutine;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import org.fruit.UnProc;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

/**
 * Input table for Tgherkin file settings, combo box for Tgherkin files.
 *
 */
public class TgherkinFilePanel extends InputDataPanel {

  private static final long serialVersionUID = -5878966626046293031L;

  protected JComboBox<String> jcbStringB;

  protected JComboBox<String> addCombo(int line) {
    // String B selection
    JComboBox<String> jcb = new JComboBox<String>();
    jcb.setBounds(
        START_X + GAP_X_L,
        START_Y + line * GAP_Y_H,
        TEXT_WIDTH,
        LABEL_HEIGHT);
    jcb.setVisible(false);  
    jcb.setModel(new DefaultComboBoxModel<String>(listTgherkinDocuments()));

    return jcb;  
  }
  
  public TgherkinFilePanel() {
    super();
      
  }

  @Override
  protected void addSubroutine() {
    setSTRING_A("IF URL text contains:");
    setSTRING_B("use Tgherkin File hihi: ");
    setSTRING_C("exit URL: ");
    setTOOL_A(SubroutineTexts.getRoleTTT());
    setTOOL_B(SubroutineTexts.getTitleTTT());
    setTOOL_C(SubroutineTexts.getInputTTT());

    int lineNumber = 1;
  //  addIntro(lineNumber);
    addRecord(lineNumber++); 
    add(addLabelX(lineNumber, getSTRING_A(), getTOOL_A()));
    jtfStringA = addTextFieldX(lineNumber++); add(jtfStringA);
    
    // String B selection
    jcbStringB = addCombo(lineNumber);
    add(jcbStringB);    

    add(addLabelX(lineNumber, getSTRING_B(), getTOOL_B()));
    jtfStringB = addTextFieldX(lineNumber++); 
    add(jtfStringB);

    add(addLabelX(lineNumber, getSTRING_C(), getTOOL_C()));
    jtfStringC = addTextFieldX(lineNumber++); add(jtfStringC);
    addButtons(lineNumber);
    showButtons(true, true, true, true, false);
   }
  
  // Button actions
  @Override
  protected void actionPreviousBtn() {
    super.actionPreviousBtn();
    jcbStringB.setVisible(false);
    showInputData(currentIndexInputData);
    adjustRanges();
  }
  
  @Override
  protected void actionNewBtn() {
    super.actionNewBtn();
    jcbStringB.setVisible(true);
    adjustRanges();
    showButtons(false, false, false, true, true);
  }
  
  protected void actionEditBtn() {
    jtfStringA.setEditable(true);
    jtfStringB.setVisible(false);
    jcbStringB.setVisible(true);
    String selected = input_data.get(currentIndexInputData)[1];
    jcbStringB.setSelectedItem(selected);
    jtfStringC.setEditable(true);

    showButtons(false, false, false, true, true);
  }

  protected void actionCancelBtn() {
    super.actionCancelBtn();
    jcbStringB.setVisible(false);

    adjustRanges();
    showButtons(true, true, true, false, false);
  }
  
  @Override
  protected void actionSaveBtn() {
    String stringC = (String) jcbStringB.getSelectedItem();
    String[] invoer = {jtfStringA.getText(),stringC,jtfStringC.getText()};
    input_data.put(currentIndexInputData, invoer);
    maxIndexInputData = input_data.size();
    writeInputData();

    jtfStringA.setEditable(false);
    jtfStringB.setEditable(false);
    jtfStringB.setVisible(true);
    jcbStringB.setVisible(false);
    jtfStringC.setEditable(false);

    adjustRanges();
    showButtons(true, true, true, false, false);
  }
  
  /**
   * List available Tgherkin documents in protocol specific settings folder.
   */
  private String [] listTgherkinDocuments() {
    String[] tgherkinDocuments =
        new File("./resources/settings/" + protocolClass.split("/")[0] + "/")
        .list(new FilenameFilter() {
          @Override
          public boolean accept(File current, String name) {
            return name.toLowerCase().endsWith(".tgherkin");
          }
        });
    Arrays.sort(tgherkinDocuments);
    return tgherkinDocuments;
  }

  /**
   * Populate Timing Fields from Settings structure.
   *
   * @param settings  The settings to load
   */
  @Override
  public void populateFrom(final Settings settings) {
    protocolClass = settings.get(ConfigTags.ProtocolClass);
    myClassPath = settings.get(ConfigTags.MyClassPath);
    checkClassProtocol();
    if (classBoolean) {
      // if exist, read data from input file
      setDataInputFile(new File(settings.get(ConfigTags.TgherkinFileData)));
      input_data = readDataInputfile(); 
      maxIndexInputData = input_data.size();
      addSubroutine();
      if (maxIndexInputData > 0) {
        currentIndexInputData = 1; // start with first record
        showInputData(currentIndexInputData);
      }   
      adjustRanges();
    }
  }
  
  @Override
  protected void checkClassProtocol() {
    classBoolean = false;
    URLClassLoader loader = null;
    try {

      URL[] classPath = new URL[myClassPath.size()];
      for (int i = 0; i < myClassPath.size(); i++) {
        classPath[i] = new File(myClassPath.get(i)).toURI().toURL();
      }

      // URL[] classPath = {new File("bin/oracle/").toURI().toURL()};
      loader = new URLClassLoader(classPath);

      @SuppressWarnings ("unchecked")
      UnProc<Settings> protocol = (UnProc<Settings>) loader
          .loadClass(protocolClass.replace("/", "."))
          .getConstructor().newInstance();
      if (TgherkinFileProtocol.class.isAssignableFrom(protocol.getClass())) {
        classBoolean = true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (loader != null) {
        try {
          loader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}