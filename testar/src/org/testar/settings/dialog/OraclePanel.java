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


package org.testar.settings.dialog;

import org.testar.monkey.ConfigTags;
import org.testar.settings.Settings;
import org.testar.settings.extended.ExtendedSettingsFactory;
import org.testar.visualvalidation.VisualValidationSettings;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;

import java.util.Arrays;

public class OraclePanel extends SettingsPanel {

    private static final long serialVersionUID = -8633257917450402330L;

    private JLabel suspiciousTagsRegexLabel = new JLabel("Suspicious Tags values (regular expression)");
    private JLabel applySuspiciousTagsLabel = new JLabel("Tags to apply the Suspicious Tags values (semicolon to customize multiple Tags)");
    private JLabel suspiciousProcessRegexLabel = new JLabel("Suspicious Process Output (regular expression)");
    private JLabel freezeTimeLabel = new JLabel("Freeze Time:");
    private JLabel secondsLabel = new JLabel("seconds");

    private JTextArea txtSuspTagsRegex = new JTextArea();
    private JTextArea txtApplySuspTags = new JTextArea();
    private JTextArea txtSuspProccesRegex = new JTextArea();

    private RegexButton regexButtonSuspTags = new RegexButton(txtSuspTagsRegex);
    private RegexButton regexButtonSuspProcces = new RegexButton(txtSuspProccesRegex);

    private JCheckBox processCheckBox;
    private JSpinner spnFreezeTime;

    private JCheckBox enableWebConsoleErrorOracle;
    private JTextArea txtWebConsoleErrorPattern = new JTextArea();
    private JCheckBox enableWebConsoleWarningOracle;
    private JTextArea txtWebConsoleWarningPattern = new JTextArea();

    private JCheckBox enableVisualValidationCheckBox;

    public OraclePanel() {
        setLayout(null);

        suspiciousTagsRegexLabel.setBounds(10, 5, 450, 27);
        suspiciousTagsRegexLabel.setToolTipText(ToolTipTexts.suspiciousTagsTTT);
        add(suspiciousTagsRegexLabel);

        regexButtonSuspTags.setBounds(500, 5, 110, 27);
        add(regexButtonSuspTags);

        txtSuspTagsRegex.setLineWrap(true);
        JScrollPane suspTagsRegexPane = new JScrollPane(txtSuspTagsRegex);
        suspTagsRegexPane.setBounds(10, 35, 600, 100);
        suspTagsRegexPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        suspTagsRegexPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(suspTagsRegexPane);

        applySuspiciousTagsLabel.setBounds(10, 130, 600, 27);
        add(applySuspiciousTagsLabel);

        txtApplySuspTags.setLineWrap(true);
        JScrollPane tagsApplySuspTagsPane = new JScrollPane(txtApplySuspTags);
        tagsApplySuspTagsPane.setBounds(10, 160, 600, 50);
        tagsApplySuspTagsPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        tagsApplySuspTagsPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(tagsApplySuspTagsPane);

        suspiciousProcessRegexLabel.setBounds(10, 220, 280, 27);
        add(suspiciousProcessRegexLabel);

        processCheckBox = new JCheckBox("Enable Process Listener");
        processCheckBox.setBounds(300, 220, 200, 27);
        add(processCheckBox);
        
        regexButtonSuspProcces.setBounds(500, 220, 110, 27);
        add(regexButtonSuspProcces);

        txtSuspProccesRegex.setLineWrap(true);
        JScrollPane suspProcessRegexPane = new JScrollPane(txtSuspProccesRegex);
        suspProcessRegexPane.setBounds(10, 250, 600, 50);
        suspProcessRegexPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        suspProcessRegexPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(suspProcessRegexPane);

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
    	txtSuspTagsRegex.setText(settings.get(ConfigTags.SuspiciousTags));
        txtApplySuspTags.setText(StringUtils.join(settings.get(ConfigTags.TagsForSuspiciousOracle), ";"));
        processCheckBox.setSelected(settings.get(ConfigTags.ProcessListenerEnabled));
        txtSuspProccesRegex.setText(settings.get(ConfigTags.SuspiciousProcessOutput));
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
        settings.set(ConfigTags.SuspiciousTags, txtSuspTagsRegex.getText());
        settings.set(ConfigTags.TagsForSuspiciousOracle, Arrays.asList(txtApplySuspTags.getText().split(";")));
        settings.set(ConfigTags.ProcessListenerEnabled, processCheckBox.isSelected());
        settings.set(ConfigTags.SuspiciousProcessOutput, txtSuspProccesRegex.getText());
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
