package org.fruit.monkey.dialog;

import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.ProtocolEditor;
import org.fruit.monkey.Settings;
import org.fruit.monkey.SettingsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Arrays;
import java.util.Observable;

import static org.fruit.monkey.dialog.ToolTipTexts.*;

public class GeneralPanel extends JPanel {
  private Settings settings;
  private JComboBox<String> cboxSUTconnector;
  private JTextArea txtSutPath;
  private JSpinner spnNumSequences;
  private JSpinner spnSequenceLength;
  private JSpinner esiSpinner;
  private JComboBox<String> comboboxVerbosity;
  private JCheckBox f2slCheckBox;
  private JCheckBox checkStopOnFault;
  private JCheckBox paCheckbox;
  private JCheckBox offlineGraphConversionCheckBox;
  private JComboBox<String> comboBoxProtocol;
  private JCheckBox compileCheckBox;

  public GeneralPanel(SettingsDialog settingsDialog) {
    setLayout(null);

    addGeneralLabels();
    addGeneralControlsGlobal(settingsDialog);
    addGeneralControlsLocal();
  }

  private void addGeneralControlsGlobal(SettingsDialog settingsDialog) {
    cboxSUTconnector = new JComboBox<>();
    cboxSUTconnector.setModel(new DefaultComboBoxModel<>(new String[]{
        Settings.SUT_CONNECTOR_CMDLINE,
        Settings.SUT_CONNECTOR_PROCESS_NAME,
        Settings.SUT_CONNECTOR_WINDOW_TITLE
    }));
    cboxSUTconnector.setSelectedIndex(0);
    cboxSUTconnector.setBounds(114, 12, 171, 18);
    cboxSUTconnector.setToolTipText(sutConnectorTTT);
    cboxSUTconnector.setMaximumRowCount(3);
    add(cboxSUTconnector);

    txtSutPath = new JTextArea();
    txtSutPath.setLineWrap(true);
    txtSutPath.setToolTipText(suthPathTTT);

    spnNumSequences = new JSpinner();
    spnNumSequences.setBounds(160, 161, 81, 30);
    spnNumSequences.setModel(new SpinnerNumberModel(1, 1, null, 1));
    spnNumSequences.setToolTipText(nofSequencesTTT);
    add(spnNumSequences);

    spnSequenceLength = new JSpinner();
    spnSequenceLength.setBounds(160, 199, 81, 31);
    spnSequenceLength.setModel(new SpinnerNumberModel(999, 1, null, 1));
    spnSequenceLength.setToolTipText(sequencesActionsTTT);
    add(spnSequenceLength);

    esiSpinner = new JSpinner();
    esiSpinner.setBounds(417, 161, 63, 30);
    esiSpinner.setValue(10);
    add(esiSpinner);

    comboboxVerbosity = new JComboBox<>();
    comboboxVerbosity.setModel(new DefaultComboBoxModel<>(
        new String[]{"Critical", "Information", "Debug"}));
    comboboxVerbosity.setSelectedIndex(1);
    comboboxVerbosity.setBounds(373, 222, 107, 20);
    comboboxVerbosity.setMaximumRowCount(3);
    comboboxVerbosity.setToolTipText(loggingVerbosityTTT);
    add(comboboxVerbosity);

    comboBoxProtocol = new JComboBox<>();
    comboBoxProtocol.setBounds(286, 248, 194, 20);
    String[] sutSettings = new File("./settings/")
        .list((current, name) -> new File(current, name).isDirectory());
    Arrays.sort(sutSettings);
    comboBoxProtocol.setModel(new DefaultComboBoxModel<>(sutSettings));
    comboBoxProtocol.setMaximumRowCount(sutSettings.length > 16 ? 16 : sutSettings.length);
    // Pass button click to settings dialog
    MyItemListener myItemListener = new MyItemListener();
    myItemListener.addObserver(settingsDialog);
    comboBoxProtocol.addItemListener(myItemListener);
    add(comboBoxProtocol);

    compileCheckBox = new JCheckBox("Always compile protocol");
    compileCheckBox.setBounds(286, 272, 192, 21);
    compileCheckBox.setToolTipText(lblCompileTTT);
    add(compileCheckBox);

    f2slCheckBox = new JCheckBox("Force to sequence length");
    f2slCheckBox.setBounds(10, 235, 192, 20);
    add(f2slCheckBox);

    checkStopOnFault = new JCheckBox("Stop Test on Fault");
    checkStopOnFault.setBounds(10, 258, 192, 21);
    checkStopOnFault.setToolTipText(checkStopOnFaultTTT);
    add(checkStopOnFault);

    paCheckbox = new JCheckBox("Prolog activated");
    paCheckbox.setBounds(10, 282, 192, 21);
    add(paCheckbox);

    offlineGraphConversionCheckBox = new JCheckBox("Offline graph conversion");
    offlineGraphConversionCheckBox.setBounds(10, 304, 192, 23);
    add(offlineGraphConversionCheckBox);
  }

  private void addGeneralControlsLocal() {
    JButton btnSutPath = new JButton("...");
    btnSutPath.setBounds(456, 11, 24, 20);
    btnSutPath.addActionListener(this::btnSutPathActionPerformed);
    add(btnSutPath);

    JButton btnEditProtocol = new JButton("Edit Protocol");
    btnEditProtocol.setBounds(286, 298, 194, 35);
    btnEditProtocol.addActionListener(this::btnEditProtocolActionPerformed);
    btnEditProtocol.setToolTipText("Edit the protocol");
    btnEditProtocol.setMaximumSize(new Dimension(160, 35));
    btnEditProtocol.setMinimumSize(new Dimension(160, 35));
    btnEditProtocol.setPreferredSize(new Dimension(160, 35));
    add(btnEditProtocol);

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBounds(10, 42, 470, 108);
    add(scrollPane);
    scrollPane.setViewportView(txtSutPath);
  }

  private void addGeneralLabels() {
    JLabel lblSUTconnector = new JLabel("SUT connector:");
    lblSUTconnector.setBounds(10, 11, 97, 20);
    // TODO Create TTT
    add(lblSUTconnector);

    JLabel lblNofSequences = new JLabel("Number of Sequences:");
    lblNofSequences.setBounds(10, 164, 135, 14);
    lblNofSequences.setToolTipText(nofSequencesTTT);
    add(lblNofSequences);

    JLabel lblSamplingInterval = new JLabel("Sampling interval:");
    lblSamplingInterval.setBounds(286, 161, 121, 17);
    // TODO Create TTT
    add(lblSamplingInterval);

    JLabel lblLoggingVerbosity = new JLabel("Logging Verbosity:");
    lblLoggingVerbosity.setBounds(286, 197, 120, 14);
    lblLoggingVerbosity.setToolTipText(lblLoggingVerbosityTTT);
    add(lblLoggingVerbosity);

    JLabel lblSequenceActions = new JLabel("Sequence actions:");
    lblSequenceActions.setBounds(10, 202, 148, 14);
    lblSequenceActions.setToolTipText(sequencesActionsTTT);
    add(lblSequenceActions);

    JLabel lblProtocol = new JLabel("Protocol:");
    lblProtocol.setBounds(229, 248, 64, 20);
    // TODO Create TTT
    add(lblProtocol);
  }

  private void btnSutPathActionPerformed(ActionEvent evt) {
    JFileChooser fd = new JFileChooser();
    fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fd.setCurrentDirectory(new File(settings.get(ConfigTags.SUTConnectorValue)).getParentFile());

    if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      String file = fd.getSelectedFile().getAbsolutePath();
      txtSutPath.setText(file);
    }
  }

  private void btnEditProtocolActionPerformed(ActionEvent evt) {
    JDialog dialog = new ProtocolEditor(settings.get(ConfigTags.ProtocolClass));
    dialog.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
    dialog.setVisible(true);
  }

  /**
   * Populate JPanelGeneral from Settings structure.
   *
   * @param settings The settings to load.
   */
  public void populateFrom(final Settings settings) {
    this.settings = settings;

    cboxSUTconnector.setSelectedItem(settings.get(ConfigTags.SUTConnector));
    checkStopOnFault.setSelected(settings.get(ConfigTags.StopGenerationOnFault));
    txtSutPath.setText(settings.get(ConfigTags.SUTConnectorValue));
    comboBoxProtocol.setSelectedItem(settings.get(ConfigTags.ProtocolClass).split("/")[0]);
    esiSpinner.setValue(settings.get(ConfigTags.ExplorationSampleInterval));
    offlineGraphConversionCheckBox.setSelected(settings.get(ConfigTags.OfflineGraphConversion));
    f2slCheckBox.setSelected(settings.get(ConfigTags.ForceToSequenceLength));
    paCheckbox.setSelected(settings.get(ConfigTags.PrologActivated));
    spnNumSequences.setValue(settings.get(ConfigTags.Sequences));
    spnSequenceLength.setValue(settings.get(ConfigTags.SequenceLength));
    comboboxVerbosity.setSelectedIndex(settings.get(ConfigTags.LogLevel));
    compileCheckBox.setSelected(settings.get(ConfigTags.AlwaysCompile));
  }

  /**
   * Retrieve information from the JPanelGeneral GUI.
   *
   * @param settings reference to the object where the settings will be stored.
   */
  public void extractInformation(final Settings settings) {
    settings.set(ConfigTags.SUTConnector, (String) cboxSUTconnector.getSelectedItem());
    settings.set(ConfigTags.SUTConnectorValue, txtSutPath.getText());
    settings.set(ConfigTags.StopGenerationOnFault, checkStopOnFault.isSelected());
    settings.set(ConfigTags.SUTConnectorValue, txtSutPath.getText());
    settings.set(ConfigTags.ExplorationSampleInterval, (Integer) esiSpinner.getValue());
    settings.set(ConfigTags.ForceToSequenceLength, f2slCheckBox.isSelected());
    settings.set(ConfigTags.PrologActivated, paCheckbox.isSelected());
    settings.set(ConfigTags.OfflineGraphConversion, offlineGraphConversionCheckBox.isSelected());
    settings.set(ConfigTags.Sequences, (Integer) spnNumSequences.getValue());
    settings.set(ConfigTags.LogLevel, comboboxVerbosity.getSelectedIndex());
    settings.set(ConfigTags.SequenceLength, (Integer) spnSequenceLength.getValue());
    settings.set(ConfigTags.AlwaysCompile, compileCheckBox.isSelected());
  }

  public class MyItemListener extends Observable implements ItemListener {
    @Override
    public void itemStateChanged(ItemEvent e) {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        setChanged();
        notifyObservers(e.getItem());
      }
    }
  }
}
