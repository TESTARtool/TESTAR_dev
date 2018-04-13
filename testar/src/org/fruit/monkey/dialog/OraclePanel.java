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

import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import javax.swing.*;

import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;
import static javax.swing.LayoutStyle.ComponentPlacement.RELATED;
import static org.fruit.monkey.dialog.ToolTipTexts.suspiciousTitlesTTT;

public class OraclePanel extends JPanel {
  private JTextArea txtSuspTitles;
  private JSpinner spnFreezeTime;

  public OraclePanel() {
    txtSuspTitles = new JTextArea();
    txtSuspTitles.setLineWrap(true);

    spnFreezeTime = new JSpinner();
    spnFreezeTime.setModel(new SpinnerNumberModel(1.0d, 1.0d, null, 1.0d));

    JScrollPane scrollPane_1 = new JScrollPane();
    scrollPane_1.setViewportView(txtSuspTitles);

    JLabel jLabel10 = new JLabel("Suspicious Titles:");
    jLabel10.setToolTipText(suspiciousTitlesTTT);
    JLabel jLabel13 = new JLabel("Freeze Time:");
    JLabel jLabel14 = new JLabel("seconds");

    GroupLayout gl_jPanelOracles = new GroupLayout(this);
    this.setLayout(gl_jPanelOracles);
    gl_jPanelOracles.setHorizontalGroup(
        gl_jPanelOracles.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(gl_jPanelOracles.createSequentialGroup()
                .addGap(10)
                .addGroup(gl_jPanelOracles.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addGroup(gl_jPanelOracles.createSequentialGroup()
                        .addGroup(gl_jPanelOracles.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(gl_jPanelOracles.createSequentialGroup()
                                .addGap(98)
                                .addComponent(jLabel13, PREFERRED_SIZE, 92, PREFERRED_SIZE)
                                .addPreferredGap(RELATED)
                                .addComponent(spnFreezeTime, PREFERRED_SIZE, 95, PREFERRED_SIZE)
                                .addGap(18)
                                .addComponent(jLabel14)
                                .addPreferredGap(RELATED, 121, Short.MAX_VALUE))
                            .addGroup(GroupLayout.Alignment.TRAILING, gl_jPanelOracles.createSequentialGroup()
                                .addComponent(scrollPane_1, PREFERRED_SIZE, 461, PREFERRED_SIZE)
                                .addPreferredGap(RELATED)))
                        .addGap(23))
                    .addGroup(gl_jPanelOracles.createSequentialGroup()
                        .addPreferredGap(RELATED)
                        .addComponent(jLabel10, PREFERRED_SIZE, 142, PREFERRED_SIZE)
                        .addContainerGap(348, Short.MAX_VALUE))))
    );
    gl_jPanelOracles.setVerticalGroup(
        gl_jPanelOracles.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(gl_jPanelOracles.createSequentialGroup()
                .addGap(18)
                .addComponent(jLabel10)
                .addGap(226)
                .addGroup(gl_jPanelOracles.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(spnFreezeTime, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel13))
                .addContainerGap(120, Short.MAX_VALUE))
            .addGroup(gl_jPanelOracles.createSequentialGroup()
                .addGap(50)
                .addComponent(scrollPane_1, PREFERRED_SIZE, 192, PREFERRED_SIZE)
                .addContainerGap(156, Short.MAX_VALUE))
    );
  }

  /**
   * Populate Oracle Fields from Settings structure.
   * @param settings The settings to load.
   */
  public void populateFrom(final Settings settings) {
    txtSuspTitles.setText(settings.get(ConfigTags.SuspiciousTitles));
    spnFreezeTime.setValue(settings.get(ConfigTags.TimeToFreeze));
  }

  /**
   * Retrieve information from the Oracle GUI.
   * @param settings reference to the object where the settings will be stored.
   */
  public void extractInformation(final Settings settings) {
    settings.set(ConfigTags.SuspiciousTitles, txtSuspTitles.getText());
    settings.set(ConfigTags.TimeToFreeze, (Double) spnFreezeTime.getValue());
  }
}
