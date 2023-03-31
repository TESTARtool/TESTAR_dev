/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2023 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2023 Open Universiteit - www.ou.nl
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


package org.testar.settingsdialog.dialog;

import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;
import org.testar.settingsdialog.SettingsPanel;
import org.testar.visualvalidation.VisualValidationSettings;

import org.apache.commons.lang3.StringUtils;
import org.testar.extendedsettings.ExtendedSettingsFactory;

import javax.swing.*;

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

    private RegexButton regexButtonSuspTitle = new RegexButton(txtSuspTitles);
    private RegexButton regexButtonProcTitle = new RegexButton(txtProcTitles);

    private JCheckBox processCheckBox;
    private JSpinner spnFreezeTime;

    private JCheckBox enableWebConsoleErrorOracle;
    private JTextArea txtWebConsoleErrorPattern = new JTextArea();
    private JCheckBox enableWebConsoleWarningOracle;
    private JTextArea txtWebConsoleWarningPattern = new JTextArea();

    private JCheckBox enableVisualValidationCheckBox;

    public OraclePanel() {
        setLayout(null);

        suspiciousTitleLabel.setBounds(10, 5, 450, 27);
        suspiciousTitleLabel.setToolTipText(ToolTipTexts.suspiciousTitlesTTT);
        add(suspiciousTitleLabel);

        regexButtonSuspTitle.setBounds(500, 5, 110, 27);
        add(regexButtonSuspTitle);

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
        
        regexButtonProcTitle.setBounds(500, 220, 110, 27);
        add(regexButtonProcTitle);

        txtProcTitles.setLineWrap(true);
        JScrollPane suspiciousProcessPane = new JScrollPane(txtProcTitles);
        suspiciousProcessPane.setBounds(10, 250, 600, 50);
        suspiciousProcessPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        suspiciousProcessPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(suspiciousProcessPane);

        enableWebConsoleErrorOracle = new JCheckBox("Enable Web Console Error Oracle");
        enableWebConsoleErrorOracle.setBounds(10, 300, 300, 27);
        enableWebConsoleErrorOracle.setToolTipText("Enable Web Console Error Oracle");
        add(enableWebConsoleErrorOracle);

        enableWebConsoleWarningOracle = new JCheckBox("Enable Web Console Warning Oracle");
        enableWebConsoleWarningOracle.setBounds(10, 330, 300, 27);
        enableWebConsoleWarningOracle.setToolTipText("Enable Web Console Warning Oracle");
        add(enableWebConsoleWarningOracle);

        // Disable the visualization until the implementation is ready
        //enableVisualValidationCheckBox = new JCheckBox("Enable visual validation");
        //enableVisualValidationCheckBox.setBounds(10, 330, 180, 27);
        //enableVisualValidationCheckBox.setToolTipText(ToolTipTexts.enableVisualValidationTTT);
        //add(enableVisualValidationCheckBox);

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
        // Web Browser Console Oracles elements
        enableWebConsoleErrorOracle.setSelected(settings.get(ConfigTags.WebConsoleErrorOracle));
        txtWebConsoleErrorPattern.setText(settings.get(ConfigTags.WebConsoleErrorPattern));
        enableWebConsoleWarningOracle.setSelected(settings.get(ConfigTags.WebConsoleWarningOracle));
        txtWebConsoleWarningPattern.setText(settings.get(ConfigTags.WebConsoleWarningPattern));
        // Visual validation elements
        VisualValidationSettings visualSetting = ExtendedSettingsFactory.createVisualValidationSettings();
        // Disable the visualization until the implementation is ready
        //enableVisualValidationCheckBox.setSelected(visualSetting.enabled);
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
        // Web Browser Console Oracles elements
        settings.set(ConfigTags.WebConsoleErrorOracle, enableWebConsoleErrorOracle.isSelected());
        settings.set(ConfigTags.WebConsoleErrorPattern, txtWebConsoleErrorPattern.getText());
        settings.set(ConfigTags.WebConsoleWarningOracle, enableWebConsoleWarningOracle.isSelected());
        settings.set(ConfigTags.WebConsoleWarningPattern, txtWebConsoleWarningPattern.getText());
        // Visual validation elements
        VisualValidationSettings visualSetting = ExtendedSettingsFactory.createVisualValidationSettings();
        // Disable the visualization until the implementation is ready
        //visualSetting.enabled = enableVisualValidationCheckBox.isSelected();
    }
}
