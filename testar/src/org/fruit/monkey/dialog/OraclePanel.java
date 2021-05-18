/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2021 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2021 Open Universiteit - www.ou.nl
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

import nl.ou.testar.visualvalidation.VisualValidationSettings;

import org.apache.commons.lang3.StringUtils;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.fruit.monkey.SettingsPanel;
import org.testar.settings.ExtendedSettingsFactory;

import javax.swing.*;

import static org.fruit.monkey.dialog.ToolTipTexts.enableVisualValidationTTT;
import static org.fruit.monkey.dialog.ToolTipTexts.suspiciousTitlesTTT;

import java.util.Arrays;

public class OraclePanel extends SettingsPanel {

    private static final long serialVersionUID = -8633257917450402330L;

    private JLabel suspiciousTitleLabel = new JLabel("Suspicious Titles based on Tags values (regular expression)");
    private JLabel suspiciousTitleTagsLabel = new JLabel("Tags to apply the Suspicious Titles (semicolon to customize multiple Tags)");
    private JLabel suspiciousProcessLabel = new JLabel("Suspicious Process Output (regular expression)");
    private JLabel freezeTimeLabel = new JLabel("Freeze Time:");
    private JLabel secondsLabel = new JLabel("seconds");

    private JTextArea txtSuspTitles = new JTextArea();
    private JTextArea txtTagsForSuspTitles = new JTextArea();
    private JTextArea txtProcTitles = new JTextArea();

    private JCheckBox processCheckBox;
    private JSpinner spnFreezeTime;
    private JCheckBox enableVisualValidationCheckBox;

    public OraclePanel() {
        setLayout(null);

        suspiciousTitleLabel.setBounds(10, 5, 600, 27);
        suspiciousTitleLabel.setToolTipText(suspiciousTitlesTTT);
        add(suspiciousTitleLabel);

        txtSuspTitles.setLineWrap(true);
        JScrollPane suspTitlePane = new JScrollPane(txtSuspTitles);
        suspTitlePane.setBounds(10, 35, 600, 100);
        suspTitlePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        suspTitlePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(suspTitlePane);

        suspiciousTitleTagsLabel.setBounds(10, 130, 600, 27);
        add(suspiciousTitleTagsLabel);

        txtTagsForSuspTitles.setLineWrap(true);
        JScrollPane tagsForSuspTitlesPane = new JScrollPane(txtTagsForSuspTitles);
        tagsForSuspTitlesPane.setBounds(10, 160, 600, 50);
        tagsForSuspTitlesPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        tagsForSuspTitlesPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(tagsForSuspTitlesPane);

        suspiciousProcessLabel.setBounds(10, 220, 280, 27);
        add(suspiciousProcessLabel);

        processCheckBox = new JCheckBox("Enable Process Listener");
        processCheckBox.setBounds(300, 220, 200, 27);
        add(processCheckBox);

        txtProcTitles.setLineWrap(true);
        JScrollPane suspiciousProcessPane = new JScrollPane(txtProcTitles);
        suspiciousProcessPane.setBounds(10, 250, 600, 50);
        suspiciousProcessPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        suspiciousProcessPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(suspiciousProcessPane);

        enableVisualValidationCheckBox = new JCheckBox("Enable visual validation");
        enableVisualValidationCheckBox.setBounds(10, 330, 180, 27);
        enableVisualValidationCheckBox.setToolTipText(enableVisualValidationTTT);
        add(enableVisualValidationCheckBox);

        freezeTimeLabel.setBounds(300, 330, 80, 27);
        add(freezeTimeLabel);
        spnFreezeTime = new JSpinner();
        spnFreezeTime.setModel(new SpinnerNumberModel(1.0d, 1.0d, null, 1.0d));
        spnFreezeTime.setBounds(390, 330, 50, 27);
        add(spnFreezeTime);
        secondsLabel.setBounds(450, 330, 50, 27);
        add(secondsLabel);
    }

    /**
     * Populate Oracle Fields from Settings structure.
     * @param settings The settings to load.
     */
    @Override
    public void populateFrom(final Settings settings) {
        txtSuspTitles.setText(settings.get(ConfigTags.SuspiciousTitles));
        txtTagsForSuspTitles.setText(StringUtils.join(settings.get(ConfigTags.TagsForSuspiciousOracle), ";"));
        processCheckBox.setSelected(settings.get(ConfigTags.ProcessListenerEnabled));
        txtProcTitles.setText(settings.get(ConfigTags.SuspiciousProcessOutput));
        spnFreezeTime.setValue(settings.get(ConfigTags.TimeToFreeze));
        VisualValidationSettings visualSetting = ExtendedSettingsFactory.createVisualValidationSettings();
        enableVisualValidationCheckBox.setSelected(visualSetting.enabled);
    }

    /**
     * Retrieve information from the Oracle GUI.
     * @param settings reference to the object where the settings will be stored.
     */
    @Override
    public void extractInformation(final Settings settings) {
        settings.set(ConfigTags.SuspiciousTitles, txtSuspTitles.getText());
        settings.set(ConfigTags.TagsForSuspiciousOracle, Arrays.asList(txtTagsForSuspTitles.getText().split(";")));
        settings.set(ConfigTags.ProcessListenerEnabled, processCheckBox.isSelected());
        settings.set(ConfigTags.SuspiciousProcessOutput, txtProcTitles.getText());
        settings.set(ConfigTags.TimeToFreeze, (Double) spnFreezeTime.getValue());
        VisualValidationSettings visualSetting = ExtendedSettingsFactory.createVisualValidationSettings();
        visualSetting.enabled = enableVisualValidationCheckBox.isSelected();
    }
}
