/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.dialog;

import org.apache.commons.lang3.StringUtils;
import org.testar.dialog.components.RegexButton;
import org.testar.dialog.components.UndoTextArea;
import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import javax.swing.*;

import java.util.Arrays;

public class FilterPanel extends SettingsPanel {

    private static final long serialVersionUID = 1572649050808020748L;

    private JLabel filterLabel = new JLabel("Filter actions on the widgets based on Tags values (regular expression):");
    private JLabel filterTags = new JLabel("Tags to apply the filters (semicolon to customize multiple Tags)");
    private JLabel killProcessLabel = new JLabel("Kill processes by name (regular expression):");

    private UndoTextArea txtClickFilter = new UndoTextArea();
    private UndoTextArea txtTagsToFilter = new UndoTextArea();
    private UndoTextArea txtProcessFilter = new UndoTextArea();
    
    private RegexButton regexButtonClickFilter = new RegexButton(txtClickFilter);
    private RegexButton regexButtonProcFilter = new RegexButton(txtProcessFilter);

    public FilterPanel() {
        filterLabel.setToolTipText(ToolTipTexts.label1TTT);
        killProcessLabel.setToolTipText(ToolTipTexts.label2TTT);

        setLayout(null);
        filterLabel.setBounds(10, 5, 400, 27);
        add(filterLabel);

        regexButtonClickFilter.setPosition(505, 5);
        add(regexButtonClickFilter);

        txtClickFilter.setLineWrap(true);
        JScrollPane clickFilterPane = new JScrollPane(txtClickFilter);
        clickFilterPane.setBounds(10, 35, 600, 100);
        clickFilterPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        clickFilterPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(clickFilterPane);

        filterTags.setBounds(10, 130, 600, 27);
        add(filterTags);

        txtTagsToFilter.setLineWrap(true);
        JScrollPane tagsToFilterPane = new JScrollPane(txtTagsToFilter);
        tagsToFilterPane.setBounds(10, 160, 600, 50);
        tagsToFilterPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        tagsToFilterPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(tagsToFilterPane);

        killProcessLabel.setBounds(10, 210, 400, 27);
        add(killProcessLabel);
        
        regexButtonProcFilter.setPosition(505, 210);
        add(regexButtonProcFilter);

        txtProcessFilter.setLineWrap(true);
        JScrollPane processFilterPane = new JScrollPane(txtProcessFilter);
        processFilterPane.setBounds(10, 240, 600, 100);
        processFilterPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        processFilterPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(processFilterPane);
    }

    /**
     * Populate Filter Fields from Settings structure.
     * @param settings The settings to load.
     */
    @Override
    public void populateFrom(final Settings settings) {
        txtClickFilter.setInitialText(settings.get(ConfigTags.ClickFilter));
        txtTagsToFilter.setInitialText(StringUtils.join(settings.get(ConfigTags.TagsToFilter), ";"));
        txtProcessFilter.setInitialText(settings.get(ConfigTags.ProcessesToKillDuringTest));
    }

    /**
     * Retrieve information from the Filter  GUI.
     * @param settings reference to the object where the settings will be stored.
     */
    @Override
    public void extractInformation(final Settings settings) {
        settings.set(ConfigTags.ClickFilter, txtClickFilter.getText());
        settings.set(ConfigTags.TagsToFilter, Arrays.asList(txtTagsToFilter.getText().split(";")));
        settings.set(ConfigTags.ProcessesToKillDuringTest, txtProcessFilter.getText());
    }
}
