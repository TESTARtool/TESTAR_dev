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

import org.apache.commons.lang3.StringUtils;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.fruit.monkey.SettingsPanel;

import javax.swing.*;

import static org.fruit.monkey.dialog.ToolTipTexts.label1TTT;
import static org.fruit.monkey.dialog.ToolTipTexts.label2TTT;

import java.util.Arrays;

public class FilterPanel extends SettingsPanel {

    private static final long serialVersionUID = 1572649050808020748L;

    private JLabel filterLabel = new JLabel("Filter actions on the widgets based on Tags values (regular expression):");
    private JLabel filterTags = new JLabel("Tags to apply the filters (semicolon to customize multiple Tags)");
    private JLabel killProcessLabel = new JLabel("Kill processes by name (regular expression):");

    private JTextArea txtClickFilter = new JTextArea();
    private JTextArea txtTagsToFilter = new JTextArea();
    private JTextArea txtProcessFilter = new JTextArea();

    public FilterPanel() {
        filterLabel.setToolTipText(label1TTT);
        killProcessLabel.setToolTipText(label2TTT);

        setLayout(null);
        filterLabel.setBounds(10, 5, 600, 27);
        add(filterLabel);

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

        killProcessLabel.setBounds(10, 210, 600, 27);
        add(killProcessLabel);

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
        txtClickFilter.setText(settings.get(ConfigTags.ClickFilter));
        txtTagsToFilter.setText(StringUtils.join(settings.get(ConfigTags.TagsToFilter), ";"));
        txtProcessFilter.setText(settings.get(ConfigTags.ProcessesToKillDuringTest));
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
