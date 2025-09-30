/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
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
import org.testar.rascal.DslOracleStudio;
import org.testar.settings.Settings;
import org.testar.settings.dialog.components.RegexButton;
import org.testar.settings.dialog.components.RestoreButton;
import org.testar.settings.dialog.components.UndoTextArea;
import org.testar.settings.extended.ExtendedSettingsFactory;
import org.testar.visualvalidation.VisualValidationSettings;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

public class OraclePanel extends SettingsPanel {

    private static final long serialVersionUID = -8633257917450402330L;

    private JLabel suspiciousTagsRegexLabel = new JLabel("Suspicious Tags values (regular expression)");
    private JLabel applySuspiciousTagsLabel = new JLabel("Tags to apply the Suspicious Tags values (semicolon to customize multiple Tags)");
    private JLabel freezeTimeLabel = new JLabel("Freeze Time:");
    private JLabel secondsLabel = new JLabel("seconds");

    private UndoTextArea txtSuspTagsRegex = new UndoTextArea();
    private UndoTextArea txtApplySuspTags = new UndoTextArea();
    private UndoTextArea txtSuspProccesRegex = new UndoTextArea();

    private RestoreButton restoreButtonSuspTags = new RestoreButton(txtSuspTagsRegex);
    private RegexButton regexButtonSuspTags = new RegexButton(txtSuspTagsRegex);
    private RestoreButton restoreButtonSuspProcces= new RestoreButton(txtSuspProccesRegex);
    private RegexButton regexButtonSuspProcces = new RegexButton(txtSuspProccesRegex);

    private JCheckBox suspiciousProcessCheckBox = new JCheckBox("Enable Suspicious Process Output (regular expression)");
    private JSpinner spnFreezeTime;

    private JCheckBox enableWebConsoleErrorOracle;
    private JTextArea txtWebConsoleErrorPattern = new JTextArea();
    private JCheckBox enableWebConsoleWarningOracle;
    private JTextArea txtWebConsoleWarningPattern = new JTextArea();

    private String extendedOracles = "";
    private JButton extendedOraclesButton = new JButton("ExtendedOracles");
    private ExtendedOraclesDialog extendedOraclesDialog;

    private String externalOracles = "";
    private JButton externalOraclesButton = new JButton("ExternalOracles");
    private ExternalOraclesDialog externalOraclesDialog;

    private JButton dslOracleStudioButton = new JButton("DslOracleStudio");
    
    private JCheckBox enableVisualValidationCheckBox;

    public OraclePanel() {
        setLayout(null);

        suspiciousTagsRegexLabel.setBounds(10, 5, 260, 27);
        suspiciousTagsRegexLabel.setToolTipText(ToolTipTexts.suspiciousTagsTTT);
        add(suspiciousTagsRegexLabel);

        restoreButtonSuspTags.setPosition(430, 5);
        restoreButtonSuspTags.setToolTipText("Restore default suspicious regex if the text area is empty");
        add(restoreButtonSuspTags);
        regexButtonSuspTags.setPosition(505, 5);
        add(regexButtonSuspTags);

        txtSuspTagsRegex.setLineWrap(true);
        JScrollPane suspTagsRegexPane = new JScrollPane(txtSuspTagsRegex);
        suspTagsRegexPane.setBounds(10, 35, 600, 65);
        suspTagsRegexPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        suspTagsRegexPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(suspTagsRegexPane);

        applySuspiciousTagsLabel.setBounds(10, 100, 600, 27);
        add(applySuspiciousTagsLabel);

        txtApplySuspTags.setLineWrap(true);
        JScrollPane tagsApplySuspTagsPane = new JScrollPane(txtApplySuspTags);
        tagsApplySuspTagsPane.setBounds(10, 130, 600, 50);
        tagsApplySuspTagsPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        tagsApplySuspTagsPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(tagsApplySuspTagsPane);

        suspiciousProcessCheckBox.setBounds(10, 180, 350, 27);
        suspiciousProcessCheckBox.setToolTipText("Enable the process listener feature, which uses regular expression in the output and error buffer");
        add(suspiciousProcessCheckBox);

        restoreButtonSuspProcces.setPosition(430, 180);
        restoreButtonSuspProcces.setToolTipText("Restore default suspicious regex if the text area is empty");
        add(restoreButtonSuspProcces);
        regexButtonSuspProcces.setPosition(505, 180);
        add(regexButtonSuspProcces);

        txtSuspProccesRegex.setLineWrap(true);
        JScrollPane suspProcessRegexPane = new JScrollPane(txtSuspProccesRegex);
        suspProcessRegexPane.setBounds(10, 210, 600, 50);
        suspProcessRegexPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        suspProcessRegexPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(suspProcessRegexPane);

        enableWebConsoleErrorOracle = new JCheckBox("Enable Web Console Error Oracle");
        enableWebConsoleErrorOracle.setBounds(10, 260, 300, 27);
        enableWebConsoleErrorOracle.setToolTipText("Enable Web Console Error Oracle");
        add(enableWebConsoleErrorOracle);

        enableWebConsoleWarningOracle = new JCheckBox("Enable Web Console Warning Oracle");
        enableWebConsoleWarningOracle.setBounds(10, 290, 300, 27);
        enableWebConsoleWarningOracle.setToolTipText("Enable Web Console Warning Oracle");
        add(enableWebConsoleWarningOracle);

        // Disable the visualization until the implementation is ready
        //enableVisualValidationCheckBox = new JCheckBox("Enable visual validation");
        //enableVisualValidationCheckBox.setBounds(10, 330, 180, 27);
        //enableVisualValidationCheckBox.setToolTipText(ToolTipTexts.enableVisualValidationTTT);
        //add(enableVisualValidationCheckBox);

        freezeTimeLabel.setBounds(350, 275, 80, 27);
        add(freezeTimeLabel);
        spnFreezeTime = new JSpinner();
        spnFreezeTime.setModel(new SpinnerNumberModel(1.0d, 1.0d, null, 1.0d));
        spnFreezeTime.setBounds(440, 275, 50, 27);
        add(spnFreezeTime);
        secondsLabel.setBounds(500, 275, 50, 27);
        add(secondsLabel);

        extendedOraclesButton.setBounds(50, 330, 150, 27);
        extendedOraclesButton.setToolTipText("Open Extended Oracles dialog");
        extendedOraclesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	openExtendedOraclesDialog();
            }
        });
        add(extendedOraclesButton);

        externalOraclesButton.setBounds(250, 330, 150, 27);
        externalOraclesButton.setToolTipText("Open External Oracles dialog");
        externalOraclesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	openExternalOraclesDialog();
            }
        });
        add(externalOraclesButton);
        
        dslOracleStudioButton.setBounds(450, 330, 150, 27);
        dslOracleStudioButton.setToolTipText("Open the DSL Oracle studio");
        dslOracleStudioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDslOracleStudio();
            }
        });
        add(dslOracleStudioButton);
    }

    private void openExtendedOraclesDialog() {
    	extendedOraclesDialog = new ExtendedOraclesDialog(extendedOracles);
    	extendedOraclesDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // tell the manager to shut down its connection
            	extendedOracles = extendedOraclesDialog.getSavedExtendedOracles();
            }
        });
    }

    private void openExternalOraclesDialog() {
    	externalOraclesDialog = new ExternalOraclesDialog(externalOracles);
    	externalOraclesDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // tell the manager to shut down its connection
            	externalOracles = externalOraclesDialog.getSavedExternalOracles();
            }
        });
    }

    private void openDslOracleStudio() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                DslOracleStudio studio = new DslOracleStudio();
                studio.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
                studio.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Populate Oracle Fields from Settings structure.
     * @param settings The settings to load.
     */
    @Override
    public void populateFrom(final Settings settings) {
    	txtSuspTagsRegex.setInitialText(settings.get(ConfigTags.SuspiciousTags));
        txtApplySuspTags.setInitialText(StringUtils.join(settings.get(ConfigTags.TagsForSuspiciousOracle), ";"));
        suspiciousProcessCheckBox.setSelected(settings.get(ConfigTags.ProcessListenerEnabled));
        txtSuspProccesRegex.setInitialText(settings.get(ConfigTags.SuspiciousProcessOutput));
        spnFreezeTime.setValue(settings.get(ConfigTags.TimeToFreeze));
        // Web Browser Console Oracles elements
        enableWebConsoleErrorOracle.setSelected(settings.get(ConfigTags.WebConsoleErrorOracle));
        txtWebConsoleErrorPattern.setText(settings.get(ConfigTags.WebConsoleErrorPattern));
        enableWebConsoleWarningOracle.setSelected(settings.get(ConfigTags.WebConsoleWarningOracle));
        txtWebConsoleWarningPattern.setText(settings.get(ConfigTags.WebConsoleWarningPattern));
        // Advanced Oracles
        extendedOracles = settings.get(ConfigTags.ExtendedOracles);
        externalOracles = settings.get(ConfigTags.ExternalOracles);
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
        settings.set(ConfigTags.ProcessListenerEnabled, suspiciousProcessCheckBox.isSelected());
        settings.set(ConfigTags.SuspiciousProcessOutput, txtSuspProccesRegex.getText());
        settings.set(ConfigTags.TimeToFreeze, (Double) spnFreezeTime.getValue());
        // Web Browser Console Oracles elements
        settings.set(ConfigTags.WebConsoleErrorOracle, enableWebConsoleErrorOracle.isSelected());
        settings.set(ConfigTags.WebConsoleErrorPattern, txtWebConsoleErrorPattern.getText());
        settings.set(ConfigTags.WebConsoleWarningOracle, enableWebConsoleWarningOracle.isSelected());
        settings.set(ConfigTags.WebConsoleWarningPattern, txtWebConsoleWarningPattern.getText());
        // Advanced Oracles
        settings.set(ConfigTags.ExtendedOracles, extendedOracles);
        settings.set(ConfigTags.ExternalOracles, externalOracles);
        // Visual validation elements
        VisualValidationSettings visualSetting = ExtendedSettingsFactory.createVisualValidationSettings();
        // Disable the visualization until the implementation is ready
        //visualSetting.enabled = enableVisualValidationCheckBox.isSelected();
    }
}
