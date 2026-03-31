/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.dialog;

import org.testar.dialog.components.IgnoredVerdictsDialog;
import org.testar.dialog.components.UndoTextArea;
import org.testar.config.ConfigTags;
import org.testar.config.TestarDirectories;
import org.testar.config.settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

public class GeneralPanel extends SettingsPanel implements Observer {

  private static final long serialVersionUID = -7401834140061189752L;

  private Settings settings;
  private JComboBox<String> cboxSUTconnector;
  private UndoTextArea txtSutPath = new UndoTextArea();
  private JSpinner spnNumSequences;
  private JSpinner spnSequenceLength;
  //private JCheckBox checkStopOnFault;
  private JComboBox<String> comboBoxProtocol;
  private JCheckBox compileCheckBox, checkActionVisualization, checkIgnoreDuplicatedVerdict;
  private JButton btnManageIgnoredVerdicts;
  
  private JLabel labelAppName = new JLabel("Application name");
  private JLabel labelAppVersion = new JLabel("Application version");

  private JTextField applicationNameField = new JTextField();
  private JTextField applicationVersionField = new JTextField();

  private JLabel labelOverrideWebDriverDisplayScale = new JLabel("Override display scale");
  private JTextField overrideWebDriverDisplayScaleField = new JTextField();

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
        Settings.SUT_CONNECTOR_WINDOW_TITLE,
        Settings.SUT_CONNECTOR_WEBDRIVER
    }));
    cboxSUTconnector.setSelectedIndex(0);
    cboxSUTconnector.setBounds(114, 12, 171, 25);
    cboxSUTconnector.setToolTipText(ToolTipTexts.sutConnectorTTT);
    cboxSUTconnector.setMaximumRowCount(3);
    add(cboxSUTconnector);

    txtSutPath.setLineWrap(true);
    txtSutPath.setToolTipText(ToolTipTexts.sutPathTTT);

    spnNumSequences = new JSpinner();
    spnNumSequences.setBounds(160, 161, 71, 25);
    spnNumSequences.setModel(new SpinnerNumberModel(1, 1, null, 1));
    spnNumSequences.setToolTipText(ToolTipTexts.nofSequencesTTT);
    add(spnNumSequences);

    spnSequenceLength = new JSpinner();
    spnSequenceLength.setBounds(160, 199, 71, 25);
    spnSequenceLength.setModel(new SpinnerNumberModel(999, 1, null, 1));
    spnSequenceLength.setToolTipText(ToolTipTexts.sequencesActionsTTT);
    add(spnSequenceLength);

    comboBoxProtocol = new JComboBox<>();
    comboBoxProtocol.setBounds(350, 161, 260, 25);
    String[] sutSettings = new File(TestarDirectories.getSettingsDir())
        .list((current, name) -> new File(current, name).isDirectory());
    Arrays.sort(sutSettings);
    comboBoxProtocol.setModel(new DefaultComboBoxModel<>(sutSettings));
    comboBoxProtocol.setMaximumRowCount(sutSettings.length > 16 ? 16 : sutSettings.length);
    
    // Pass button click to settings dialog
    MyItemListener myItemListener = new MyItemListener();
    myItemListener.addObserver(settingsDialog);
    myItemListener.addObserver(this);
    comboBoxProtocol.addItemListener(myItemListener);
    comboBoxProtocol.setToolTipText(ToolTipTexts.comboBoxProtocolTTT);
    add(comboBoxProtocol);

    compileCheckBox = new JCheckBox("Always compile protocol");
    compileCheckBox.setBounds(286, 199, 192, 21);
    compileCheckBox.setToolTipText(ToolTipTexts.lblCompileTTT);
    add(compileCheckBox);

    /*checkStopOnFault = new JCheckBox("Stop Test on Fault");
    checkStopOnFault.setBounds(10, 240, 192, 21);
    checkStopOnFault.setToolTipText(checkStopOnFaultTTT);
    add(checkStopOnFault);*/

    checkActionVisualization = new JCheckBox("Visualize actions on GUI");
    checkActionVisualization.setBounds(10, 240, 192, 21);
    //checkActionVisualization.setToolTipText(checkStopOnFaultTTT);
    add(checkActionVisualization);

    checkIgnoreDuplicatedVerdict = new JCheckBox("Ignore duplicated verdicts");
    checkIgnoreDuplicatedVerdict.setBounds(10, 262, 192, 21);
    checkIgnoreDuplicatedVerdict.setToolTipText(ConfigTags.IgnoreDuplicatedVerdicts.getDescription());
    add(checkIgnoreDuplicatedVerdict);

    btnManageIgnoredVerdicts = new JButton("Manage ignored verdicts");
    btnManageIgnoredVerdicts.setBounds(10, 286, 192, 25);
    btnManageIgnoredVerdicts.setToolTipText("Open a modal dialog to check or clear existing ignored verdicts");
    btnManageIgnoredVerdicts.addActionListener(this::btnManageIgnoredVerdictsActionPerformed);
    add(btnManageIgnoredVerdicts);

    labelAppName.setBounds(330, 242, 150, 27);
    labelAppName.setToolTipText(ToolTipTexts.applicationNameTTT);
    add(labelAppName);
    applicationNameField.setBounds(480, 242, 125, 27);
    applicationNameField.setToolTipText(ToolTipTexts.applicationNameTTT);
    add(applicationNameField);

    labelAppVersion.setBounds(330, 280, 150, 27);
    labelAppVersion.setToolTipText(ToolTipTexts.applicationVersionTTT);
    add(labelAppVersion);
    applicationVersionField.setBounds(480, 280, 125, 27);
    applicationVersionField.setToolTipText(ToolTipTexts.applicationVersionTTT);
    add(applicationVersionField);

    // Hide the override webdriver display scale fields by default, only show them when a webdriver protocol is selected.
    setOverrideWebDriverDisplayScaleVisibility(false);
    labelOverrideWebDriverDisplayScale.setBounds(330, 320, 150, 27);
    labelOverrideWebDriverDisplayScale.setToolTipText(ToolTipTexts.overrideWebDriverDisplayScaleTTT);
    add(labelOverrideWebDriverDisplayScale);
    overrideWebDriverDisplayScaleField.setBounds(480, 320, 125, 27);
    overrideWebDriverDisplayScaleField.setToolTipText(ToolTipTexts.overrideWebDriverDisplayScaleTTT);
    add(overrideWebDriverDisplayScaleField);
  }

  private void setOverrideWebDriverDisplayScaleVisibility(boolean isVisible){
    labelOverrideWebDriverDisplayScale.setVisible(isVisible);
    overrideWebDriverDisplayScaleField.setVisible(isVisible);
  }

  @Override
  public void update(Observable o, Object arg) {
    boolean showWidgets = arg.toString().contains("webdriver");
    setOverrideWebDriverDisplayScaleVisibility(showWidgets);
  }

  private void addGeneralControlsLocal() {
    JButton btnSutPath = new JButton("Select SUT");
    btnSutPath.setBounds(520, 11, 90, 25);
    btnSutPath.addActionListener(this::btnSutPathActionPerformed);
    btnSutPath.setToolTipText(ToolTipTexts.btnSelectSUTTTT);
    add(btnSutPath);

    JButton btnEditProtocol = new JButton("Edit Protocol");
    btnEditProtocol.setBounds(510, 199, 100, 25);
    btnEditProtocol.addActionListener(this::btnEditProtocolActionPerformed);
    btnEditProtocol.setToolTipText(ToolTipTexts.btnEditProtocolTTT);
    btnEditProtocol.setMaximumSize(new Dimension(160, 35));
    btnEditProtocol.setMinimumSize(new Dimension(160, 35));
    btnEditProtocol.setPreferredSize(new Dimension(160, 35));
    add(btnEditProtocol);

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBounds(10, 42, 600, 108);
    add(scrollPane);
    scrollPane.setViewportView(txtSutPath);
  }

  private void addGeneralLabels() {
    JLabel lblSUTconnector = new JLabel("SUT connector:");
    lblSUTconnector.setBounds(10, 11, 97, 20);
    lblSUTconnector.setToolTipText(ToolTipTexts.sutConnectorTTT);
    add(lblSUTconnector);

    JLabel lblNofSequences = new JLabel("Number of Sequences:");
    lblNofSequences.setBounds(10, 164, 135, 14);
    lblNofSequences.setToolTipText(ToolTipTexts.nofSequencesTTT);
    add(lblNofSequences);

    JLabel lblSequenceActions = new JLabel("Sequence actions:");
    lblSequenceActions.setBounds(10, 202, 148, 14);
    lblSequenceActions.setToolTipText(ToolTipTexts.sequencesActionsTTT);
    add(lblSequenceActions);

    JLabel lblProtocol = new JLabel("Protocol:");
    lblProtocol.setBounds(286, 164, 64, 14);
    lblProtocol.setToolTipText(ToolTipTexts.comboBoxProtocolTTT);
    add(lblProtocol);
  }

  private void btnSutPathActionPerformed(ActionEvent evt) {
    JFileChooser fd = new JFileChooser();
    fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fd.setCurrentDirectory(new File(settings.get(ConfigTags.SUTConnectorValue)).getParentFile());

    if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      String file = fd.getSelectedFile().getAbsolutePath();

      if (settings.get(ConfigTags.SUTConnector)
          .equals(Settings.SUT_CONNECTOR_WEBDRIVER)) {
        // When using the WEB_DRIVER connector, only replace webdriver path
        String[] orgSettingParts = txtSutPath.getText().split(" ");
        orgSettingParts[0] = "\"" + file + "\"";
        txtSutPath.setText(String.join(" ", orgSettingParts));
      }
      else {
        // Set the text from settings in txtSutPath
        txtSutPath.setText(file);
      }
    }
  }

  private void btnEditProtocolActionPerformed(ActionEvent evt) {
    JDialog dialog = new ProtocolEditor(TestarDirectories.getSettingsDir(), settings.get(ConfigTags.ProtocolClass));
    dialog.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
    dialog.setVisible(true);
  }

  private void btnManageIgnoredVerdictsActionPerformed(ActionEvent evt) {
    JDialog dialog = new IgnoredVerdictsDialog();
    dialog.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
    dialog.setVisible(true);
  }

  /**
   * Populate JPanelGeneral from Settings structure.
   *
   * @param settings The settings to load.
   */
  @Override
  public void populateFrom(final Settings settings) {
    this.settings = settings;

    cboxSUTconnector.setSelectedItem(settings.get(ConfigTags.SUTConnector));
    //checkStopOnFault.setSelected(settings.get(ConfigTags.StopGenerationOnFault));
    checkActionVisualization.setSelected(settings.get(ConfigTags.VisualizeActions));
    checkIgnoreDuplicatedVerdict.setSelected(settings.get(ConfigTags.IgnoreDuplicatedVerdicts));
    txtSutPath.setInitialText(settings.get(ConfigTags.SUTConnectorValue));
    comboBoxProtocol.setSelectedItem(settings.get(ConfigTags.ProtocolClass).split("/")[0]);
    spnNumSequences.setValue(settings.get(ConfigTags.Sequences));
    spnSequenceLength.setValue(settings.get(ConfigTags.SequenceLength));
    compileCheckBox.setSelected(settings.get(ConfigTags.AlwaysCompile));
    applicationNameField.setText(settings.get(ConfigTags.ApplicationName));
    applicationVersionField.setText(settings.get(ConfigTags.ApplicationVersion));
    overrideWebDriverDisplayScaleField.setText(settings.get(ConfigTags.OverrideWebDriverDisplayScale));
  }

  /**
   * Retrieve information from the JPanelGeneral GUI.
   *
   * @param settings reference to the object where the settings will be stored.
   */
  @Override
  public void extractInformation(final Settings settings) {
    settings.set(ConfigTags.SUTConnector, (String) cboxSUTconnector.getSelectedItem());
    settings.set(ConfigTags.SUTConnectorValue, txtSutPath.getText());
    //settings.set(ConfigTags.StopGenerationOnFault, checkStopOnFault.isSelected());
    settings.set(ConfigTags.VisualizeActions, checkActionVisualization.isSelected());
    settings.set(ConfigTags.IgnoreDuplicatedVerdicts, checkIgnoreDuplicatedVerdict.isSelected());
    settings.set(ConfigTags.Sequences, (Integer) spnNumSequences.getValue());
    settings.set(ConfigTags.SequenceLength, (Integer) spnSequenceLength.getValue());
    settings.set(ConfigTags.AlwaysCompile, compileCheckBox.isSelected());
    settings.set(ConfigTags.ApplicationName, applicationNameField.getText());
    settings.set(ConfigTags.ApplicationVersion, applicationVersionField.getText());
    settings.set(ConfigTags.OverrideWebDriverDisplayScale, overrideWebDriverDisplayScaleField.getText());
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
