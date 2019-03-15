package nl.ou.testar.subroutine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.fruit.UnProc;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.DefaultProtocol;
import org.fruit.monkey.Settings;

/**
 * Input table for subroutines settings.
 *
 */
public class InputDataPanel extends JPanel {

  private static final long serialVersionUID = -5878966626046293031L;

  private static final String N_TEXT = "Record number";
  private static String STRING_A = "a";
  private static String STRING_B = "b";
  private static String STRING_C = "c";
  private static String TOOL_A = "a";
  private static String TOOL_B = "b";
  private static String TOOL_C = "c";
  
  protected static final int START_X = 20;
  protected static final int START_Y = 40;
  protected static final int TITLE_Y = 20;

  protected static final int LABEL_WIDTH = 120;
  protected static final int LABEL_HEIGHT = 30;
  protected static final int TEXT_WIDTH = 300;
  protected static final int N_WIDTH = 40;
  protected static final int BUTTON_WIDTH = 60;
  protected static final int BUTTON_WIDTH2 = 70;

  protected static final int GAP_X = 5;
  protected static final int GAP_X_L = GAP_X + LABEL_WIDTH;
  protected static final int GAP_X_B = GAP_X + BUTTON_WIDTH2;
  protected static final int GAP_Y = 10;
  protected static final int GAP_Y_H = GAP_Y + LABEL_HEIGHT;

  protected JTextField jtfN;
  protected JTextField jtfStringA;
  protected JTextField jtfStringB;
  protected JTextField jtfStringC;

  protected JButton btnPrevious = new JButton("<<");
  protected JButton btnNext = new JButton(">>");
  protected JButton btnNew = new JButton("New");
  protected JButton btnDelete = new JButton("Delete");
  protected JButton btnEdit = new JButton("Edit");
  protected JButton btnCancel = new JButton("Cancel");
  protected JButton btnSave = new JButton("Save");

  protected HashMap<Integer,String[]> input_data = new HashMap<>();
  protected int currentIndexInputData = 0;
  protected int maxIndexInputData = 0;

  protected boolean classBoolean;
  protected boolean active;

  protected String protocolClass;

  /**
   * Data input file.
   * with format (.csv) Integer|String a|String b|String c
   * Contents can be adapted using input data panel from main TESTAR menu
   */
  private File dataInputFile;

  public void setDataInputFile(File dataInputFile) {
    this.dataInputFile = dataInputFile;
  }

  // used with class loader
  protected List<String> myClassPath;

  public InputDataPanel() {
    setLayout(null);
  }

  protected void showButtons(
      boolean nnew, boolean ddelete, boolean eedit,
      boolean ccancel, boolean ssave) {
    btnNew.setEnabled(nnew);
    btnNew.setVisible(nnew);
    btnDelete.setEnabled(ddelete);
    btnDelete.setVisible(ddelete);
    btnEdit.setEnabled(eedit);
    btnEdit.setVisible(eedit);
    btnCancel.setEnabled(ccancel);
    btnCancel.setVisible(ccancel);
    btnSave.setEnabled(ssave);
    btnSave.setVisible(ssave);
  }
  
  protected void addIntro(int line) {
    //label
    JLabel jlabelIntro = new JLabel("Input Data");
    jlabelIntro.setBounds(
        START_X,
        TITLE_Y + line * GAP_Y_H,
        TEXT_WIDTH,
        LABEL_HEIGHT);
    add(jlabelIntro);
  }
  
  // Record number selection
  protected void addRecord(int line) {
    // label
    JLabel jlabelN = new JLabel(N_TEXT);
    jlabelN.setBounds(
        START_X,
        START_Y + line * GAP_Y_H,
        LABEL_WIDTH,
        LABEL_HEIGHT);
    add(jlabelN);
    
    // actual number
    jtfN = new JTextField();
    jtfN.setBounds(
        START_X + GAP_X_L + BUTTON_WIDTH + GAP_X,
        START_Y + line * GAP_Y_H,
        N_WIDTH,
        LABEL_HEIGHT);
    add(jtfN);
    
    // previous button
    btnPrevious.setBounds(
        START_X + GAP_X_L,
        START_Y + line * GAP_Y_H,
        BUTTON_WIDTH,
        LABEL_HEIGHT);
    btnPrevious.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          actionPreviousBtn();
        }
      });
    add(btnPrevious);
    
    // next button
    btnNext.setBounds(
        START_X + GAP_X_L + BUTTON_WIDTH + GAP_X + N_WIDTH + GAP_X,
        START_Y + line * GAP_Y_H,
        BUTTON_WIDTH,
        LABEL_HEIGHT);
    btnNext.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        actionNextBtn();
      }
    });
    add(btnNext);
  }
  
  protected JLabel addLabelX(int line, String string_x, String string_y){ 
    // label string x
    JLabel jlabel = new JLabel(string_x);
    jlabel.setToolTipText(string_y); 

    jlabel.setBounds(
        START_X,
        START_Y + line * GAP_Y_H,
        LABEL_WIDTH,
        LABEL_HEIGHT);
    return jlabel;
  }
  
  protected JTextField addTextFieldX(int line){
    // actual value string a
    JTextField jtf = new JTextField();
    jtf.setBounds(
        START_X + GAP_X_L,
        START_Y + line * GAP_Y_H,
        TEXT_WIDTH,
        LABEL_HEIGHT);
    jtf.setEditable(false);
    return jtf;
   }
  
  protected void addButtons(int line) {
    int column = 0;
    btnNew.setBounds(
        START_X + GAP_X_L + column * GAP_X_B,
        START_Y + line * GAP_Y_H,
        BUTTON_WIDTH2,
        LABEL_HEIGHT);
    btnNew.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        actionNewBtn();
      }
    });
    add(btnNew);
    
    column++;
    btnDelete.setBounds(
        START_X + GAP_X_L + column * GAP_X_B,
        START_Y + line * GAP_Y_H,
        BUTTON_WIDTH2,
        LABEL_HEIGHT);
    btnDelete.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        actionDeleteBtn();
      }
    });
    add(btnDelete);
    
    column++;
    btnEdit.setBounds(
        START_X + GAP_X_L +  column * GAP_X_B,
        START_Y + line * GAP_Y_H,
        BUTTON_WIDTH2,
        LABEL_HEIGHT);
    btnEdit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        actionEditBtn();
      }
    });
    add(btnEdit);

    column++;
    btnCancel.setBounds(
        START_X + GAP_X_L + column * GAP_X_B,
        START_Y + line * GAP_Y_H,
        BUTTON_WIDTH2,
        LABEL_HEIGHT);

    btnCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        actionCancelBtn();
      }
    });
    add(btnCancel);

    column++;
    btnSave.setBounds(
        START_X + GAP_X_L + column * GAP_X_B,
        START_Y + line * GAP_Y_H,
        BUTTON_WIDTH2,
        LABEL_HEIGHT);
    btnSave.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        actionSaveBtn();
      }
    });
    btnSave.setEnabled(false);
    add(btnSave);
  }
  
  protected void addSubroutine() {
    int lineNumber = 1; 
    
 // addIntro(lineNumber);
    addRecord(lineNumber++); 
    add(addLabelX(lineNumber, STRING_A, TOOL_A));
    jtfStringA = addTextFieldX(lineNumber++); add(jtfStringA);
    add(addLabelX(lineNumber, STRING_B, TOOL_B));
    jtfStringB = addTextFieldX(lineNumber++); add(jtfStringB);
    add(addLabelX(lineNumber, STRING_C, TOOL_C));
    jtfStringC = addTextFieldX(lineNumber++); add(jtfStringC);
    addButtons(lineNumber);
    showButtons(true, true, true, true, false);
  }

  // Button actions
  protected void actionPreviousBtn() {
    currentIndexInputData--;
    jtfStringB.setEnabled(true);
    jtfStringB.setVisible(true);
    showInputData(currentIndexInputData);
    adjustRanges();
  }

  private void actionNextBtn() {
    currentIndexInputData++;
    showInputData(currentIndexInputData);
    adjustRanges();
  }

  protected void actionNewBtn() {
    currentIndexInputData = input_data.size() + 1;
    jtfN.setText(currentIndexInputData + "");
    jtfStringA.setText("");
    jtfStringA.setEditable(true);
    jtfStringB.setText("");
    jtfStringB.setEditable(true);
    jtfStringC.setText("");
    jtfStringC.setEditable(true);

    adjustRanges();
    showButtons(false, false, false, true, true);
  }

  private void actionDeleteBtn() {
    String[] dummy = new String[2];

    if (currentIndexInputData == maxIndexInputData) {
      if (currentIndexInputData > 1) {
        dummy = input_data.get(currentIndexInputData - 1);
        jtfN.setText(currentIndexInputData - 1 + "");
      } else {
        jtfN.setText("");
      }
      input_data.remove(currentIndexInputData);
      jtfStringA.setText(dummy[0]);
      jtfStringB.setText(dummy[1]);
      jtfStringC.setText(dummy[2]);
      currentIndexInputData--;
      maxIndexInputData--;

    } else {

      for (Integer index: input_data.keySet()) {
        if (currentIndexInputData <= index && index < maxIndexInputData) {

          dummy = input_data.get(index + 1);
          input_data.put(index, dummy);
          if (index == currentIndexInputData) {
            jtfStringA.setText(dummy[0]);
            jtfStringB.setText(dummy[1]);
            jtfStringC.setText(dummy[2]);
          }
        }
        if (index == maxIndexInputData) {
          input_data.remove(index);
          maxIndexInputData--;
        }
      }
    }
    adjustRanges();
    writeInputData();
  }

  protected void actionEditBtn() {
    jtfStringA.setEditable(true);
    jtfStringB.setVisible(false);

    showButtons(false, false, false, true, true);
  }

  protected void actionCancelBtn() {
    if (currentIndexInputData > maxIndexInputData) {
      currentIndexInputData--;
    }
    showInputData(currentIndexInputData);

    jtfStringA.setEditable(false);
    jtfStringB.setVisible(true);

    adjustRanges();
    showButtons(true, true, true, false, false);
  }

  protected void actionSaveBtn() {
    String[] invoer = {jtfStringA.getText(),jtfStringB.getText(),jtfStringC.getText()};
    input_data.put(currentIndexInputData, invoer);
    maxIndexInputData = input_data.size();
    writeInputData();

    jtfStringA.setEditable(false);
    jtfStringB.setEditable(false);
    jtfStringB.setVisible(true);
    jtfStringC.setEditable(false);

    adjustRanges();
    showButtons(true, true, true, false, false);
  }

  protected void showInputData(int index) {
    String StringA = input_data.get(index)[0];
    String StringB = input_data.get(index)[1];
    jtfN.setText(index + "");
    jtfStringA.setText(StringA);
    jtfStringB.setText(StringB);
    String StringC = input_data.get(index)[2];
    jtfStringC.setText(StringC);
  }

  protected void adjustRanges() {
    maxIndexInputData = input_data.size();

    if (currentIndexInputData == maxIndexInputData) {
      btnNext.setEnabled(false);
    } else {
      btnNext.setEnabled(true);
    }
    if (currentIndexInputData <= 1) {
      btnPrevious.setEnabled(false);
    } else {
      btnPrevious.setEnabled(true);
    }
  }

  /**
   * Populate Timing Fields from Settings structure.
   *
   * @param settings  The settings to load
   */
  public void populateFrom(final Settings settings) {
    protocolClass = settings.get(ConfigTags.ProtocolClass);
    myClassPath = settings.get(ConfigTags.MyClassPath);

    dataInputFile = new File(settings.get(ConfigTags.TgherkinFileData));
  
    checkClassProtocol();
    if (classBoolean) {
      readDataInputfile(); // if exist, read data from input file
      maxIndexInputData = input_data.size();
      addSubroutine();
      if (maxIndexInputData > 0) {
        currentIndexInputData = 1; // start with first record
        showInputData(currentIndexInputData);
      }
      adjustRanges();
    }
  }

  /**
   * Read data from the input data file
   * with format (.csv) Integer|String a|String b|String c.
   * Contents of the file is stored in input data file
   */
  protected HashMap<Integer,String[]> readDataInputfile() {
    HashMap<Integer,String[]> outputData = new HashMap<Integer,String[]>();
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
          String[] r = {a, b, c};
          outputData.put(index, r);
        }
      }
      maxIndexInputData = index;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return outputData;
  }

  /**
   * Write data to the input data file
   * with format (.csv) Integer|String a|String b|String c.
   * Contents of the file is stored in input data file
   */
  protected void writeInputData() {

    try {
      String fileName = dataInputFile.getAbsolutePath();
      File file = new File(fileName);
      file.delete();
      String a = "";
      String b = "";
      String c = "";
      FileWriter writer = new FileWriter(fileName, true);
      for (Integer index: input_data.keySet()) {
        String[] invoer = input_data.get(index);
        if (invoer[0] != null) {
          a = invoer[0];
        } 
        if (invoer[1] != null) {
          b = invoer[1];
        }
        if (invoer[2] != null) {
          c = invoer[2];
        }
        String line = a + "|" + b + "|" + c;
        writer.write(line);
        writer.write(System.lineSeparator());
      }
      if (writer != null) {
        writer.flush();
        writer.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

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
      if (DefaultProtocol.class.isAssignableFrom(protocol.getClass())) {
        classBoolean = true;
      }
      System.out.println(protocolClass + " == "  + classBoolean + "DefaultProtocol") ;

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

  /**
   * Retrieve information from the Timing GUI.
   *
   * @param settings  reference to the object where the settings are stored
   */
  public void extractInformation(final Settings settings) {
  }

  /**
   * Retrieve protocol class indicator.
   * @return protocol class indicator
   */
  public boolean isClassBoolean() {
    return classBoolean;
  }

  /**
   * Retrieve active indicator.
   * @return active indicator
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Set active indicator.
   * @param active the active indicator
   */
  public void setActive(boolean active) {
    this.active = active;
  }

  public static String getSTRING_A() {
    return STRING_A;
  }

  public static String getSTRING_B() {
    return STRING_B;
  }

  public static String getSTRING_C() {
    return STRING_C;
  }

  public static String getTOOL_A() {
    return TOOL_A;
  }

  public static String getTOOL_B() {
    return TOOL_B;
  }

  public static String getTOOL_C() {
    return TOOL_C;
  }

  public static void setSTRING_A(String sTRING_A) {
    STRING_A = sTRING_A;
  }

  public static void setSTRING_B(String sTRING_B) {
    STRING_B = sTRING_B;
  }

  public static void setSTRING_C(String sTRING_C) {
    STRING_C = sTRING_C;
  }

  public static void setTOOL_A(String tOOL_A) {
    TOOL_A = tOOL_A;
  }

  public static void setTOOL_B(String tOOL_B) {
    TOOL_B = tOOL_B;
  }

  public static void setTOOL_C(String tOOL_C) {
    TOOL_C = tOOL_C;
  }

}
