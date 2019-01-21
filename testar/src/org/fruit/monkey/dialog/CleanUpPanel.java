package org.fruit.monkey.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

public class CleanUpPanel extends JPanel {
  private static final String OUTPUT_TYPE_LOG = ".log";
  private static final String OUTPUT_TYPE_CSV = ".csv";

  private static final double BASICGAP_LINES = 3.5;
  private static final int GAP_X = 20;
  private static final int HOOGTE = 20;
  private static final int BREEDTE = 192;
  private static final int START_Y = 105;
  private static final int START_SETTINGS_X = 70;
  private static final int START_OUTPUT_X = 280;
  private static final int DELTA = 22;
  private static final long serialVersionUID = 1L;
  private String rootSettingsFolderName;
  private String[] settingsDirs;
  private int numSettingsDirs;
  private JCheckBox[] settingsBox;
  private String rootOutputFolderName;
  private String[] outputDirs;
  private int numOutputDirs;
  private JCheckBox[] outputBox;

  private JButton btnDelete;
  private JButton btnEmpty;

  public CleanUpPanel() {
  }

  private void setDirs() {
    settingsDirs = new File(rootSettingsFolderName).list(new FilenameFilter() {
      @Override
      public boolean accept(File current, String name) {
        return !name.toLowerCase().endsWith(OUTPUT_TYPE_LOG);
      }
    });
    this.numSettingsDirs = settingsDirs.length;

    outputDirs = new File(rootOutputFolderName).list(new FilenameFilter() {
      @Override
      public boolean accept(File current, String name) {
        return !name.toLowerCase().endsWith(OUTPUT_TYPE_CSV);
      }
    });
    this.numOutputDirs = outputDirs.length;
  }

  private void deleteButton() {
    // delete button
    btnDelete = new JButton("Confirm deletion");
    btnDelete.setBounds(
        START_SETTINGS_X + GAP_X + BREEDTE,
        START_Y + (numSettingsDirs - 1) * DELTA, BREEDTE,
        2 * HOOGTE);
    btnEmpty = new JButton("No files to delete");
    btnEmpty.setBounds(
        START_SETTINGS_X + GAP_X + BREEDTE,
        START_Y + (numSettingsDirs - 1) * DELTA, BREEDTE,
        2 * HOOGTE);
  }

  private void setEnabledAndVisible() {

    boolean settingsBool = setupSettingsDirs();
    boolean outputBool = setupOutputDirs();

    if (settingsBool && outputBool) {
      btnDelete.setEnabled(false);
      btnDelete.setVisible(false);
      btnEmpty.setEnabled(true);
      btnEmpty.setVisible(true);
    } else {
      btnDelete.setEnabled(true);
      btnDelete.setVisible(true);
      btnEmpty.setEnabled(false);
      btnEmpty.setVisible(false);
    }
  }

  /**
   * Populate CleanUp Fields from Settings structure.
   *
   * @param settings
   *            The settings to load.
   */
  public void populateFrom(final Settings settings) {
    setLayout(null);

    // instruction
    JLabel titel = new JLabel("Check the folder(s) you want to empty");
    titel.setBounds(
        START_SETTINGS_X - GAP_X,
        START_Y - (int) (BASICGAP_LINES  * DELTA),
        (int) (1.5 * BREEDTE),
        HOOGTE);
    add(titel);

    // root folder, sub folder and all folders
    this.rootSettingsFolderName = settings.get(ConfigTags.OutputDir) + "/";
    this.rootOutputFolderName = "./output/";

    JLabel settingsLabel = new JLabel(rootSettingsFolderName);
    settingsLabel.setBounds(START_SETTINGS_X - GAP_X, START_Y - 2 * DELTA, BREEDTE, HOOGTE);
    add(settingsLabel);

    JLabel outputLabel = new JLabel(rootOutputFolderName);
    outputLabel.setBounds(START_OUTPUT_X - 2 * GAP_X, START_Y - 2 * DELTA, BREEDTE, HOOGTE);
    add(outputLabel);

    setDirs();
    deleteButton();
    setEnabledAndVisible();

    btnDelete.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        actionButton();
      }
    });
    add(btnEmpty);
    add(btnDelete);
  }

  private boolean setupSettingsDirs() {
    int emptyAll = 0;
    settingsBox = new JCheckBox[numSettingsDirs + 2];
    // root folder
    settingsBox[0] = new JCheckBox("root");
    settingsBox[0].setBounds(START_SETTINGS_X, START_Y, BREEDTE, HOOGTE);
    if (isEmptySettingsRoot()) {
      settingsBox[0].setEnabled(false);
      emptyAll++;
    } else {
      settingsBox[0].setEnabled(true);
    }
    // sub folders
    for (int i = 1; i < numSettingsDirs + 1; i++) {
      settingsBox[i] = new JCheckBox(settingsDirs[i - 1]);
      settingsBox[i].setBounds(START_SETTINGS_X, START_Y + i * DELTA, BREEDTE, HOOGTE);

      if (isEmptySettingsDir(i)) {
        settingsBox[i].setEnabled(false);
        emptyAll++;
      } else {
        settingsBox[i].setEnabled(true);
      }
    }
    // all folders
    settingsBox[numSettingsDirs + 1] = new JCheckBox("Select all");
    settingsBox[numSettingsDirs + 1]
       .setBounds(START_SETTINGS_X - GAP_X, START_Y - DELTA, BREEDTE, HOOGTE);

    boolean empty = (emptyAll == numSettingsDirs + 1);
    if (empty) {
      settingsBox[numSettingsDirs + 1].setEnabled(false);
    } else {
      settingsBox[numSettingsDirs + 1].setEnabled(true);
      settingsBox[numSettingsDirs + 1].setSelected(true);
    }

    // action listeners
    for (int i = 0; i <= numSettingsDirs + 1; i++) {
      int m = i;
      settingsBox[i].addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          actionSettingsCheckBox(m);
        }
      });
      add(settingsBox[i]);
    }
    return empty;
  }

  private boolean setupOutputDirs() {
    int emptyAll = 0;
    outputBox = new JCheckBox[numOutputDirs + 2];

    // root folder
    outputBox[0] = new JCheckBox("root");
    outputBox[0].setBounds(START_OUTPUT_X, START_Y, BREEDTE, HOOGTE);
    if (isEmptyOutputRoot()) {
      outputBox[0].setEnabled(false);
      emptyAll++;
    } else {
      outputBox[0].setEnabled(true);
    }

    // sub folders
    for (int i = 1; i < numOutputDirs + 1; i++) {
      outputBox[i] = new JCheckBox(outputDirs[i - 1]);
      outputBox[i].setBounds(START_OUTPUT_X, START_Y + i * DELTA, BREEDTE, HOOGTE);
      if (isEmptyOutputDir(i)) {
        outputBox[i].setEnabled(false);
        emptyAll++;
      } else {
        outputBox[i].setEnabled(true);
      }
    }

    // all folders
    outputBox[numOutputDirs + 1] = new JCheckBox("Select all");
    outputBox[numOutputDirs + 1]
        .setBounds(START_OUTPUT_X - GAP_X, START_Y - DELTA, BREEDTE, HOOGTE);
    boolean empty = (emptyAll == numOutputDirs + 1);
    if (empty) {
      outputBox[numOutputDirs + 1].setEnabled(false);
    } else {
      outputBox[numOutputDirs + 1].setEnabled(true);
      outputBox[numOutputDirs + 1].setSelected(true);
    }

    // action listeners
    for (int i = 0; i <= numOutputDirs + 1; i++) {
      int m = i;
      outputBox[i].addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          actionOutputCheckBox(m);
        }
      });
      add(outputBox[i]);
    }
    return empty;
  }

  private void actionSettingsCheckBox(int i) {
    boolean allChecked = settingsBox[numSettingsDirs + 1].isSelected();
    if (i == numSettingsDirs + 1 && allChecked) {
      for (int j = 0; j < numSettingsDirs + 1; j++) {
        settingsBox[j].setSelected(false);
      }
    }
    if (i != (numSettingsDirs + 1)) {
      outputBox[numOutputDirs + 1].setSelected(false);
      settingsBox[numSettingsDirs + 1].setSelected(false);
    }
  }

  private void actionOutputCheckBox(int i) {
    boolean allChecked = outputBox[numOutputDirs + 1].isSelected();
    if (i == numOutputDirs + 1 && allChecked) {
      for (int j = 0; j < numOutputDirs + 1; j++) {
        outputBox[j].setSelected(false);
      }
    }
    if (i != (numOutputDirs + 1)) {
      outputBox[numOutputDirs + 1].setSelected(false);
      settingsBox[numSettingsDirs + 1].setSelected(false);
    }
  }

  private void actionButton() {
    // all folders is selected
    boolean allSettingsChecked = settingsBox[numSettingsDirs + 1].isSelected();
    for (int i = 0; i < numSettingsDirs + 1; i++) {
      // all folders is selected or the specified folder is selected
      if (settingsBox[i].isSelected() || allSettingsChecked) {
        cleanSettingsDir(i);
        settingsBox[i].setEnabled(false);
        settingsBox[i].setSelected(false);
      }
    }

    boolean allOutputChecked = outputBox[numOutputDirs + 1].isSelected();
    for (int i = 0; i < numOutputDirs + 1; i++) {
      // all folders is selected or the specified folder is selected
      if (outputBox[i].isSelected() || allOutputChecked) {
        cleanOutputDir(i);
        outputBox[i].setEnabled(false);
        outputBox[i].setSelected(false);
      }
    }

    int allSettingsEmpty = 0;
    for (int i = 0; i < numSettingsDirs + 1; i++) {
      if (!settingsBox[i].isEnabled()) {
        allSettingsEmpty++;
      }
    }

    int allOutputEmpty = 0;
    for (int i = 0; i < numOutputDirs + 1; i++) {
      if (!outputBox[i].isEnabled()) {
        allOutputEmpty++;
      }
    }

    if (allSettingsEmpty == numSettingsDirs + 1) {
      settingsBox[numSettingsDirs + 1].setEnabled(false);
    }
    if (allOutputEmpty == numOutputDirs + 1) {
      outputBox[numOutputDirs + 1].setEnabled(false);
    }
    if (allSettingsEmpty == numSettingsDirs + 1
        && allOutputEmpty == numOutputDirs + 1) {
      btnDelete.setEnabled(false);
      btnEmpty.setEnabled(true);
    }
  }

  private boolean isEmptySettingsDir(int i) {
    String folderName;
    if (i == 0) {
      folderName = rootSettingsFolderName;
    } else {
      folderName = rootSettingsFolderName + settingsDirs[i - 1];
    }
    File[] files =
        new File(folderName).listFiles(new FilenameFilter() {
          @Override
          public boolean accept(File current, String name) {
            return !name.contains("dummy");
          }
        });
    if (files == null || files.length == 0) {
      return true;
    }
    return false;
  }

  private boolean isEmptyOutputDir(int i) {
    String folderName;
    if (i == 0) {
      folderName = rootOutputFolderName;
    } else {
      folderName = rootOutputFolderName + outputDirs[i - 1];
    }
    File[] files =
        new File(folderName).listFiles(new FilenameFilter() {
          @Override
          public boolean accept(File current, String name) {
            return !name.contains("dummy");
          }
        });
    if (files.length == 0) {
      return true;
    }
    return false;
  }

  private boolean isEmptySettingsRoot() {
    File[] files =
        new File(rootSettingsFolderName).listFiles(new FilenameFilter() {
          @Override
          public boolean accept(File current, String name) {
            return name.toLowerCase().endsWith(OUTPUT_TYPE_LOG);
          }
        });

    if (files.length == 0) {
      return true;
    }
    return false;
  }

  private boolean isEmptyOutputRoot() {
    File[] files =
        new File(rootOutputFolderName).listFiles(new FilenameFilter() {
          @Override
          public boolean accept(File current, String name) {
            return name.toLowerCase().endsWith(OUTPUT_TYPE_CSV);
          }
        });

    if (files.length == 0) {
      return true;
    }
    return false;
  }

  private void printLine(String string) {
    System.out.println("++++ ");
    System.out.println("++++ " + string);
    int nf = string.length();
    String stringf = "";
    for (int j = 0; j < nf; j++) {
      stringf = stringf + "-";
    }
    System.out.println("++++ " + stringf);
  }

  private void printDelete(File f) {
    if (f.delete()) {
      System.out.println("++++ " + f.getName() + " deleted");
    }
  }

  private void cleanSettingsDir(int i) {
    String folderName;
    if (i == 0) {
      folderName = rootSettingsFolderName;
    } else {
      folderName = rootSettingsFolderName + settingsDirs[i - 1];
    }

    File[] files = new File(folderName).listFiles();

    if (files != null && files.length > 0) {
      printLine(folderName);

      for (File f: files) {
        if (!f.isDirectory() && !f.getName().toLowerCase().contains("dummy")) {
          printDelete(f);
        } else {
          if (f.isDirectory()) {
            String subFolderName = folderName + "/" + f.getName();
            File[] subFiles = new File(subFolderName).listFiles();
           if (subFiles != null && subFiles.length > 0) {
              printLine(subFolderName);
              for (File g: subFiles) {
                if (!g.isDirectory() && !g.getName().toLowerCase().contains("dummy")) {
                  printDelete(g);
                } 
                if (g.isDirectory()) {
                  printDelete(g);
                }
              }
            }
          }
        }
      }
    }
  }

  private void cleanOutputDir(int i) {
    String folderName;
    if (i == 0) {
      folderName = rootOutputFolderName;
    } else {
      folderName = rootOutputFolderName + outputDirs[i - 1];
    }

    File[] files = new File(folderName).listFiles();

    if (files != null && files.length > 0) {
      printLine(folderName);

      for (File f: files) {

        if (f.isDirectory()) {
          String fname = f.getName();
          printLine(folderName + "/" + fname);

          File subfiles = new File(folderName + "/" + fname);
          for (File g: subfiles.listFiles()) {
            printDelete(g);
          }
        }
        printDelete(f);
      }
    }
  }
}
