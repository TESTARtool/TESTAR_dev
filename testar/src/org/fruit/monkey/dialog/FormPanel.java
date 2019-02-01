package org.fruit.monkey.dialog;

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
import org.fruit.monkey.Settings;
import nl.ou.testar.subroutine.FormProtocol;

/**
 * Input table for subroutines settings.
 *
 */
public class FormPanel extends JPanel {

  private static final long serialVersionUID = -5878966626046293031L;

  private static final String FORM_DATA = "Form data";

  private static final String N_TEXT = "Record number";
  private static final String ROLE_TEXT = "Widget action ";
  private static final String TITLE_TEXT = "Widget title text ";
  private static final String INPUT_TEXT = "input (optional)";

  private static final int START_X = 20;
  private static final int START_Y = 80;
  private static final int TITLE_Y = 20;

  private static final int LABEL_WIDTH = 120;
  private static final int LABEL_HEIGHT = 30;
  private static final int TEXT_WIDTH = 300;
  private static final int N_WIDTH = 40;
  private static final int BUTTON_WIDTH = 60;
  private static final int BUTTON_WIDTH2 = 70;

  private static final int GAP_X = 5;
  private static final int GAP_X_L = GAP_X + LABEL_WIDTH;
  private static final int GAP_X_B = GAP_X + BUTTON_WIDTH2;
  private static final int GAP_Y = 10;
  private static final int GAP_Y_H = GAP_Y + LABEL_HEIGHT;

  private JTextField jtfN;
  private JTextField jtfRole;
  private JTextField jtfTitle;
  private JTextField jtfInput;

  private JButton btnPrevious = new JButton("<<");
  private JButton btnNext = new JButton(">>");
  private JButton btnNew = new JButton("New");
  private JButton btnDelete = new JButton("Delete");
  private JButton btnEdit = new JButton("Edit");
  private JButton btnCancel = new JButton("Cancel");
  private JButton btnSave = new JButton("Save");

  private HashMap<Integer,String[]> formData = new HashMap<>();
  private int currentIndexFormData = 0;
  private int maxIndexFormData = 0;

  private String protocolClass;
  private File formDataFile;
  private boolean formProtocol;
  private boolean active;

  // used with class loader
  private List<String> myClassPath;

  // line number within Form Panel
  private int lineNumber = 0;

  public FormPanel() {
    setLayout(null);
    addForm();
  }

  private void showButtons(
      boolean nnew, boolean ddelete, boolean eedit,
      boolean ccancel, boolean ssave) {
    btnNew.setEnabled(nnew);
    btnDelete.setEnabled(ddelete);
    btnEdit.setEnabled(eedit);
    btnEdit.setVisible(eedit);
    btnCancel.setEnabled(ccancel);
    btnCancel.setVisible(ccancel);
    btnSave.setEnabled(ssave);
    btnSave.setVisible(ssave);
  }

  private void addForm() {

    JLabel jlabelIntro = new JLabel(FORM_DATA);
    jlabelIntro.setBounds(
        START_X,
        TITLE_Y,
        TEXT_WIDTH,
        LABEL_HEIGHT);
    add(jlabelIntro);

    // Record number selection
    JLabel jlabelN = new JLabel(N_TEXT);
    jlabelN.setBounds(
        START_X,
        START_Y + lineNumber * GAP_Y_H,
        LABEL_WIDTH,
        LABEL_HEIGHT);
    add(jlabelN);

    btnNext.setBounds(
        START_X + GAP_X_L + BUTTON_WIDTH + GAP_X + N_WIDTH + GAP_X,
        START_Y + lineNumber * GAP_Y_H,
        BUTTON_WIDTH,
        LABEL_HEIGHT);
    btnNext.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        actionNextBtn();
      }
    });
    add(btnNext);

    jtfN = new JTextField();
    jtfN.setBounds(
        START_X + GAP_X_L + BUTTON_WIDTH + GAP_X,
        START_Y + lineNumber * GAP_Y_H, N_WIDTH,
        LABEL_HEIGHT);
    add(jtfN);

    btnPrevious.setBounds(
        START_X + GAP_X_L,
        START_Y + lineNumber * GAP_Y_H,
        BUTTON_WIDTH,
        LABEL_HEIGHT);
    btnPrevious.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          actionPreviousBtn();
        }
      });
    add(btnPrevious);

    lineNumber++;

    // Role component
    JLabel jlabelRole = new JLabel(ROLE_TEXT);
    jlabelRole.setBounds(
        START_X,
        START_Y + lineNumber * GAP_Y_H,
        LABEL_WIDTH,
        LABEL_HEIGHT);
    jlabelRole.setToolTipText(ToolTipTexts.getRoleTTT());
    add(jlabelRole);

    jtfRole = new JTextField();
    jtfRole.setBounds(
        START_X + GAP_X_L,
        START_Y + lineNumber * GAP_Y_H,
        TEXT_WIDTH,
        LABEL_HEIGHT);
    jtfRole.setEditable(false);
    add(jtfRole);

    lineNumber++;

    // Title component
    JLabel jlabelTitle = new JLabel(TITLE_TEXT);
    jlabelTitle.setBounds(
        START_X,
        START_Y + lineNumber * GAP_Y_H,
        LABEL_WIDTH,
        LABEL_HEIGHT);
    jlabelTitle.setToolTipText(ToolTipTexts.getTitleTTT());
    add(jlabelTitle);

    jtfTitle = new JTextField();
    jtfTitle.setBounds(
        START_X + GAP_X_L,
        START_Y + lineNumber * GAP_Y_H,
        TEXT_WIDTH,
        LABEL_HEIGHT);
    jtfTitle.setVisible(true);
    jtfTitle.setEditable(false);
    add(jtfTitle);

    lineNumber++;

    // Input component
    JLabel jlabelInput = new JLabel(INPUT_TEXT);
    jlabelInput.setBounds(
        START_X,
        START_Y + lineNumber * GAP_Y_H,
        LABEL_WIDTH,
        LABEL_HEIGHT);
    jlabelInput.setToolTipText(ToolTipTexts.getInputTTT());

    add(jlabelInput);

    jtfInput = new JTextField();
    jtfInput.setBounds(
        START_X + GAP_X_L,
        START_Y + lineNumber * GAP_Y_H,
        TEXT_WIDTH,
        LABEL_HEIGHT);
    jtfInput.setVisible(true);
    jtfInput.setEditable(false);
    add(jtfInput);

    lineNumber++;

    //Action buttons
    btnNew.setBounds(
        START_X + GAP_X_L,
        START_Y + lineNumber * GAP_Y_H,
        BUTTON_WIDTH2,
        LABEL_HEIGHT);
    btnNew.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        actionNewBtn();
      }
    });
    add(btnNew);

    btnDelete.setBounds(
        START_X + GAP_X_L + GAP_X_B,
        START_Y + lineNumber * GAP_Y_H,
        BUTTON_WIDTH2,
        LABEL_HEIGHT);
    btnDelete.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        actionDeleteBtn();
      }
    });
    add(btnDelete);

    btnEdit.setBounds(
        START_X + GAP_X_L + 2 * GAP_X_B,
        START_Y + lineNumber * GAP_Y_H,
        BUTTON_WIDTH2,
        LABEL_HEIGHT);
    btnEdit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        actionEditBtn();
      }
    });
    add(btnEdit);

    btnCancel.setBounds(
        START_X + GAP_X_L + 2 * GAP_X_B,
        START_Y + lineNumber * GAP_Y_H,
        BUTTON_WIDTH2,
        LABEL_HEIGHT);

    btnCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        actionCancelBtn();
      }
    });
    add(btnCancel);

    btnSave.setBounds(
        START_X + GAP_X_L + 3 * GAP_X_B,
        START_Y + lineNumber * GAP_Y_H,
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

  // Button actions
  private void actionPreviousBtn() {
    currentIndexFormData--;
    jtfTitle.setEnabled(true);
    jtfTitle.setVisible(true);
    showFormData(currentIndexFormData);
    adjustRanges();
  }

  private void actionNextBtn() {
    currentIndexFormData++;
    showFormData(currentIndexFormData);
    adjustRanges();
  }

  private void actionNewBtn() {
    currentIndexFormData = formData.size() + 1;
    jtfN.setText(currentIndexFormData + "");
    jtfRole.setText("");
    jtfRole.setEditable(true);

    jtfTitle.setEnabled(false);
    jtfTitle.setVisible(false);

    jtfInput.setText("");
    jtfInput.setEditable(true);

    adjustRanges();
    showButtons(false, false, false, true, true);
  }

  private void actionDeleteBtn() {
    String[] dummy = new String[2];

    if (currentIndexFormData == maxIndexFormData) {
      if (currentIndexFormData > 1) {
        dummy = formData.get(currentIndexFormData - 1);
        jtfN.setText(currentIndexFormData - 1 + "");
      } else {
        jtfN.setText("");
      }
      formData.remove(currentIndexFormData);
      jtfRole.setText(dummy[0]);
      jtfTitle.setText(dummy[1]);
      jtfInput.setText(dummy[2]);
      currentIndexFormData--;
      maxIndexFormData--;

    } else {

      for (Integer index: formData.keySet()) {
        if (currentIndexFormData <= index && index < maxIndexFormData) {

          dummy = formData.get(index + 1);
          formData.put(index, dummy);
          if (index == currentIndexFormData) {
            jtfRole.setText(dummy[0]);
            jtfTitle.setText(dummy[1]);
            jtfInput.setText(dummy[2]);
          }
        }
        if (index == maxIndexFormData) {
          formData.remove(index);
          maxIndexFormData--;
        }
      }
    }
    adjustRanges();
    writeFormData();
  }

  private void actionEditBtn() {
    jtfRole.setEditable(true);
    jtfTitle.setVisible(false);
    showButtons(false, false, false, true, true);
  }

  private void actionCancelBtn() {
    if (currentIndexFormData > maxIndexFormData) {
      currentIndexFormData--;
    }
    showFormData(currentIndexFormData);

    jtfRole.setEditable(false);
    jtfTitle.setVisible(true);

    adjustRanges();
    showButtons(true, true, true, false, false);
  }

  private void actionSaveBtn() {
    String[] invoer = {jtfRole.getText(),jtfTitle.getText(),jtfInput.getText()};
    formData.put(currentIndexFormData, invoer);
    maxIndexFormData = formData.size();
    writeFormData();

    jtfRole.setEditable(false);
    jtfTitle.setEditable(false);
    jtfTitle.setVisible(true);
    jtfInput.setEditable(false);

    adjustRanges();
    showButtons(true, true, true, false, false);
  }

  private void showFormData(int index) {
    String searchRole = formData.get(index)[0];
    String searchTitle = formData.get(index)[1];
    String searchText = formData.get(index)[2];
    jtfN.setText(index + "");
    jtfRole.setText(searchRole);
    jtfTitle.setText(searchTitle);
    jtfInput.setText(searchText);
  }

  private void adjustRanges() {
    maxIndexFormData = formData.size();

    if (currentIndexFormData == maxIndexFormData) {
      btnNext.setEnabled(false);
    } else {
      btnNext.setEnabled(true);
    }
    if (currentIndexFormData <= 1) {
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

    formDataFile = new File(settings.get(ConfigTags.FormData));
    checkFormProtocol();
    if (formProtocol) {
      readFormDataInputfile(); // if exist, read data from input file
      maxIndexFormData = formData.size();
      if (maxIndexFormData > 0) {
        currentIndexFormData = 1; // start with first record
        showFormData(currentIndexFormData);
      }
      adjustRanges();
    }
  }

  /**
   * Read data from the form data input file.
   * Name of the input file is stored in ConfigTag subroutineData (see settings)
   * Data format (.csv) textLabel | textStr
   * Contents of the file will be stored in formData HashMap
   * Contents can be adapted using panel Form Data from main TESTAR menu
   */
  protected void readFormDataInputfile() {
    int index = 0;
    String searchRole = "";
    String searchLabel = "";
    String searchInput = "";

    try (Scanner in = new Scanner(formDataFile)) {
      while (in.hasNextLine()) {
        String instring = in.nextLine();
        int n = instring.indexOf("|");
        if (n > 0) {
          searchRole = instring.substring(0, n);
          searchLabel = instring.substring(n + 1);
          instring = searchLabel;
          n = instring.indexOf("|");
          if (n > 0) {
            searchLabel =  instring.substring(0, n);
            searchInput = instring.substring(n + 1);
          }
        }
        index++;
        String[] r = {searchRole, searchLabel, searchInput};
        formData.put(index, r);
      }
      maxIndexFormData = index;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Write form data to the input file.
   * Name of the input file is stored in ConfigTag SubroutineData (see settings)
   * Data format (.csv) role | title | input
   * Contents of the file will be stored in subroutineData
   */
  private void writeFormData() {

    try {
      String fileName = formDataFile.getAbsolutePath();
      File file = new File(fileName);
      file.delete();

      FileWriter writer = new FileWriter(fileName, true);
      for (Integer index: formData.keySet()) {
        String[] invoer = formData.get(index);

        String line = invoer[0] + "|" + invoer[1];
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

  private void checkFormProtocol() {
    formProtocol = false;
    URLClassLoader loader = null;
    try {

      URL[] classPath = new URL[myClassPath.size()];
      for (int i = 0; i < myClassPath.size(); i++) {
        classPath[i] = new File(myClassPath.get(i)).toURI().toURL();
      }

      loader = new URLClassLoader(classPath);

      @SuppressWarnings ("unchecked")
      UnProc<Settings> protocol = (UnProc<Settings>) loader
          .loadClass(protocolClass.replace("/", "."))
          .getConstructor().newInstance();
      if (FormProtocol.class.isAssignableFrom(protocol.getClass())) {
        formProtocol = true;
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

  /**
   * Retrieve information from the Timing GUI.
   *
   * @param settings  reference to the object where the settings will be stored
   */
  public void extractInformation(final Settings settings) {
  }

  /**
   * Retrieve form protocol indicator.
   * @return form protocol indicator
   */
  public boolean isFormProtocol() {
    return formProtocol;
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
}
