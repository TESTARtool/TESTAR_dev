/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017, 2018 Universitat Politecnica de Valencia - www.upv.es
* Copyright (c) 2018 Open Universiteit - www.ou.nl
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* 1. Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
* 2. Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* 3. Neither the name of the copyright holder nor the names of its
* contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
* ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************************************/


package org.fruit.monkey.dialog;

import org.fruit.monkey.*;

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

  private static final long serialVersionUID = -7401834140061189752L;

  private Settings settings;
  private JComboBox<String> cboxSUTconnector;
  private JTextArea txtSutPath;
  private JSpinner spnNumSequences;
  private JSpinner spnSequenceLength;
  private JSpinner esiSpinner;
  private JComboBox<String> comboboxVerbosity;
  private JCheckBox checkStopOnFault;
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
    cboxSUTconnector.setBounds(114, 12, 171, 25);
    cboxSUTconnector.setToolTipText(sutConnectorTTT);
    cboxSUTconnector.setMaximumRowCount(3);
    add(cboxSUTconnector);

    txtSutPath = new JTextArea();
    txtSutPath.setLineWrap(true);
    txtSutPath.setToolTipText(sutPathTTT);

    spnNumSequences = new JSpinner();
    spnNumSequences.setBounds(160, 161, 71, 25);
    spnNumSequences.setModel(new SpinnerNumberModel(1, 1, null, 1));
    spnNumSequences.setToolTipText(nofSequencesTTT);
    add(spnNumSequences);

    spnSequenceLength = new JSpinner();
    spnSequenceLength.setBounds(160, 199, 71, 25);
    spnSequenceLength.setModel(new SpinnerNumberModel(999, 1, null, 1));
    spnSequenceLength.setToolTipText(sequencesActionsTTT);
    add(spnSequenceLength);

    esiSpinner = new JSpinner();
    esiSpinner.setBounds(160, 275, 71, 25);
    esiSpinner.setValue(10);
    esiSpinner.setToolTipText(intervalTTT);
    add(esiSpinner);

    comboboxVerbosity = new JComboBox<>();
    comboboxVerbosity.setModel(new DefaultComboBoxModel<>(
        new String[]{"Critical", "Information", "Debug"}));
    comboboxVerbosity.setSelectedIndex(1);
    comboboxVerbosity.setBounds(160, 313, 107, 25);
    comboboxVerbosity.setMaximumRowCount(3);
    comboboxVerbosity.setToolTipText(loggingVerbosityTTT);
    add(comboboxVerbosity);

    comboBoxProtocol = new JComboBox<>();
    comboBoxProtocol.setBounds(350, 161, 260, 25);
 //   String[] sutSettings = new File("./settings/")
    String[] sutSettings = new File(Main.settingsDir)
        .list((current, name) -> new File(current, name).isDirectory());
    Arrays.sort(sutSettings);
    comboBoxProtocol.setModel(new DefaultComboBoxModel<>(sutSettings));
    comboBoxProtocol.setMaximumRowCount(sutSettings.length > 16 ? 16 : sutSettings.length);
    // Pass button click to settings dialog
    MyItemListener myItemListener = new MyItemListener();
    myItemListener.addObserver(settingsDialog);
    comboBoxProtocol.addItemListener(myItemListener);
    comboBoxProtocol.setToolTipText(comboBoxProtocolTTT);
    add(comboBoxProtocol);

    compileCheckBox = new JCheckBox("Always compile protocol");
    compileCheckBox.setBounds(286, 199, 192, 21);
    compileCheckBox.setToolTipText(lblCompileTTT);
    add(compileCheckBox);

    checkStopOnFault = new JCheckBox("Stop Test on Fault");
    checkStopOnFault.setBounds(10, 240, 192, 21);
    checkStopOnFault.setToolTipText(checkStopOnFaultTTT);
    add(checkStopOnFault);

  }

  private void addGeneralControlsLocal() {
    JButton btnSutPath = new JButton("Select SUT");
    btnSutPath.setBounds(520, 11, 90, 25);
    btnSutPath.addActionListener(this::btnSutPathActionPerformed);
    btnSutPath.setToolTipText(btnSelectSUTTTT);
    add(btnSutPath);

    JButton btnEditProtocol = new JButton("Edit Protocol");
    btnEditProtocol.setBounds(510, 199, 100, 25);
    btnEditProtocol.addActionListener(this::btnEditProtocolActionPerformed);
    btnEditProtocol.setToolTipText(btnEditProtocolTTT);
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
    lblSUTconnector.setToolTipText(sutConnectorTTT);
    add(lblSUTconnector);

    JLabel lblNofSequences = new JLabel("Number of Sequences:");
    lblNofSequences.setBounds(10, 164, 135, 14);
    lblNofSequences.setToolTipText(nofSequencesTTT);
    add(lblNofSequences);

    JLabel lblSamplingInterval = new JLabel("Sampling interval:");
    lblSamplingInterval.setBounds(10, 278, 100, 14);
    lblSamplingInterval.setToolTipText(intervalTTT);
    add(lblSamplingInterval);

    JLabel lblLoggingVerbosity = new JLabel("Logging Verbosity:");
    lblLoggingVerbosity.setBounds(10, 316, 120, 14);
    lblLoggingVerbosity.setToolTipText(lblLoggingVerbosityTTT);
    add(lblLoggingVerbosity);

    JLabel lblSequenceActions = new JLabel("Sequence actions:");
    lblSequenceActions.setBounds(10, 202, 148, 14);
    lblSequenceActions.setToolTipText(sequencesActionsTTT);
    add(lblSequenceActions);

    JLabel lblProtocol = new JLabel("Protocol:");
    lblProtocol.setBounds(286, 164, 64, 14);
    lblProtocol.setToolTipText(comboBoxProtocolTTT);
    add(lblProtocol);
  }

  private void btnSutPathActionPerformed(ActionEvent evt) {
    JFileChooser fd = new JFileChooser();
    fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fd.setCurrentDirectory(new File(settings.get(ConfigTags.SUTConnectorValue)).getParentFile());

    if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      String file = fd.getSelectedFile().getAbsolutePath();

      // Set the text from settings in txtSutPath
      txtSutPath.setText(file);
    }
  }

  private void btnEditProtocolActionPerformed(ActionEvent evt) {
    JDialog dialog = new ProtocolEditor(Main.settingsDir, settings.get(ConfigTags.ProtocolClass));
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
