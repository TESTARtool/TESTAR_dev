package org.fruit.monkey.dialog;

import es.upv.staq.testar.graph.Grapher;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import javax.swing.*;

import static org.fruit.monkey.dialog.ToolTipTexts.checkForceForegroundTTT;
import static org.fruit.monkey.dialog.ToolTipTexts.testGeneratorTTT;

public class WalkerPanel extends JPanel {
  private JComboBox<String> comboBoxTestGenerator;
  private JCheckBox checkForceForeground;
  private JCheckBox checkFormsFilling;
  private JCheckBox gaCheckbox;
  private JCheckBox garesumeCheckBox;

  public WalkerPanel() {
    setLayout(null);

    addWalkerControls();
    addWalkerLabels();
  }

  private void addWalkerControls() {
    comboBoxTestGenerator = new JComboBox<>();
    String[] algorithms = Grapher.getRegisteredAlgorithms();
    comboBoxTestGenerator.setModel(new DefaultComboBoxModel<>(algorithms));
    comboBoxTestGenerator.setSelectedIndex(0);
    comboBoxTestGenerator.setBounds(145, 23, 173, 23);
    comboBoxTestGenerator.setToolTipText(testGeneratorTTT);
    comboBoxTestGenerator.setMaximumRowCount(algorithms.length);
    add(comboBoxTestGenerator);

    checkForceForeground = new JCheckBox();
    checkForceForeground.setText("Force SUT to Foreground");
    checkForceForeground.setBounds(10, 77, 171, 21);
    checkForceForeground.setToolTipText(checkForceForegroundTTT);
    add(checkForceForeground);

    checkFormsFilling = new JCheckBox("Forms filling");
    checkFormsFilling.setBounds(10, 101, 171, 23);
    add(checkFormsFilling);

    gaCheckbox = new JCheckBox("Graphs activated");
    gaCheckbox.addActionListener(e -> garesumeCheckBox.setEnabled(gaCheckbox.isSelected()));
    gaCheckbox.setBounds(10, 127, 171, 21);
    add(gaCheckbox);

    garesumeCheckBox = new JCheckBox("Graphs resuming");
    garesumeCheckBox.setBounds(10, 151, 171, 23);
    add(garesumeCheckBox);
  }

  private void addWalkerLabels() {
    JLabel lblActionSelection = new JLabel("UI Actions selection:");
    lblActionSelection.setBounds(10, 27, 125, 14);
    add(lblActionSelection);
  }

  /**
   * Populate Walker Fields from Settings structure.
   * @param settings The settings to load.
   */
  public void populateFrom(final Settings settings) {
    comboBoxTestGenerator.setSelectedItem(settings.get(ConfigTags.TestGenerator));
    checkForceForeground.setSelected(settings.get(ConfigTags.ForceForeground));
    checkFormsFilling.setSelected(settings.get(ConfigTags.AlgorithmFormsFilling));
    gaCheckbox.setSelected(settings.get(ConfigTags.GraphsActivated));
    garesumeCheckBox.setSelected(settings.get(ConfigTags.GraphResuming));
    garesumeCheckBox.setEnabled(gaCheckbox.isSelected());
  }

  /**
   * Retrieve information from the Walker GUI.
   * @param settings reference to the object where the settings will be stored.
   */
  public void extractInformation(final Settings settings) {
    settings.set(ConfigTags.TestGenerator, (String) comboBoxTestGenerator.getSelectedItem());
    settings.set(ConfigTags.ForceForeground, checkForceForeground.isSelected());
    settings.set(ConfigTags.AlgorithmFormsFilling, checkFormsFilling.isSelected());
    settings.set(ConfigTags.GraphsActivated, gaCheckbox.isSelected());
    settings.set(ConfigTags.GraphResuming, garesumeCheckBox.isSelected());
  }
}
