/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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

import static org.fruit.monkey.dialog.ToolTipTexts.checkForceForegroundTTT;
import static org.fruit.monkey.dialog.ToolTipTexts.testGeneratorTTT;

import es.upv.staq.testar.graph.Grapher;
import javax.swing.*;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

public class WalkerPanel extends JPanel {
  private static final long serialVersionUID = -6483480772900247792L;
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
