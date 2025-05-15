/***************************************************************************************************
 *
 * Copyright (c) 2023 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2023 - 2025 Open Universiteit - www.ou.nl
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
import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.monkey.alayer.windows.UIATags;
import org.testar.settings.dialog.tagsvisualization.DefaultTagFilter;
import org.testar.settings.dialog.tagsvisualization.TagFilter;
import org.testar.settings.Settings;
import org.testar.settings.SettingsDefaults;

import javax.swing.*;

import static org.testar.monkey.ConfigTags.SpyTagAttributes;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class SpyModePanel extends SettingsPanel {

    public TreeSetListModel<Tag<?>> excludeTags = new TreeSetListModel<>(Comparator.comparing(Tag::name));
    public TreeSetListModel<Tag<?>> includeTags = new TreeSetListModel<>(Comparator.comparing(Tag::name));

    private final JList<Tag<?>> includeList = new JList<>(includeTags);
    private final JList<Tag<?>> excludeList = new JList<>(excludeTags);


    public SpyModePanel() {
        // Create two lists
        FlowLayout rootLayout = new FlowLayout();
        setLayout(rootLayout);

        Dimension listDimension = new Dimension(275,350);

        // Left Panel
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(new JLabel("Exclude:"));

        excludeList.setSelectionMode(
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JScrollPane leftScrollList = new JScrollPane(excludeList);
        leftScrollList.setPreferredSize(listDimension);
        left.add(leftScrollList);

        // Center Panel
        JPanel middle = new JPanel();
        middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));

        JButton allInclude = new JButton(">>");
        allInclude.addActionListener(e -> {
            includeTags.addAll(excludeTags.asSet());
            excludeTags.clear();
            excludeList.updateUI();
        });
        middle.add(allInclude);

        JButton selectedInclude = new JButton(">");
        selectedInclude.addActionListener(e -> { moveSelected(excludeList, excludeTags, includeTags);});
        middle.add(selectedInclude);

        JButton selectedExclude = new JButton("<");
        selectedExclude.addActionListener(e -> { moveSelected(includeList, includeTags, excludeTags);});
        middle.add(selectedExclude);

        JButton allExclude = new JButton("<<");
        allExclude.addActionListener(e -> {
            excludeTags.addAll(includeTags.asSet());
            includeTags.clear();
            includeList.updateUI();
        });
        middle.add(allExclude);

        JButton restoreTags = new JButton("Defaults");
        restoreTags.addActionListener(e -> { setDefaults(excludeTags, includeTags);});
        middle.add(restoreTags);    

        JButton uiaTags = new JButton("UIATags");
        uiaTags.addActionListener(e -> { setAllSystemTags(UIATags.getUIATags(), excludeTags, includeTags);});
        middle.add(uiaTags);  

        JButton webTags = new JButton("WdTags");
        webTags.addActionListener(e -> { setAllSystemTags(WdTags.getWdTags(), excludeTags, includeTags);});
        middle.add(webTags);  

        // Right Panel
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.add(new JLabel("Include:"));


        includeList.setSelectionMode(
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane rightScrollList = new JScrollPane(includeList);
        rightScrollList.setPreferredSize(listDimension);
        right.add(rightScrollList);

        add(left);
        add(middle);
        add(right);
    }

    private void moveSelected(JList<Tag<?>> source, TreeSetListModel<Tag<?>> sourceModel, TreeSetListModel<Tag<?>> targetModel) {
        for (Tag<?> tag : source.getSelectedValuesList()) {
            sourceModel.remove(tag);
            targetModel.add(tag);
        }
        source.clearSelection();
    }

    @SuppressWarnings("unchecked")
    private void setDefaults(TreeSetListModel<Tag<?>> excludeModel, TreeSetListModel<Tag<?>> includeModel) {
        // Use the SettingsDefaults configuration
        Set<String> defaultTagNames = new HashSet<>();
        for (Pair<?, ?> pair : SettingsDefaults.getSettingsDefaults()) {
            if (pair.left().equals(SpyTagAttributes)) {
                defaultTagNames.addAll((List<String>) pair.right());
                break;
            }
        }

        // Combine both include and exclude tags into a working set
        Set<Tag<?>> allTags = new HashSet<>();
        allTags.addAll(includeModel.asSet());
        allTags.addAll(excludeModel.asSet());

        // Clear, re-add tags based on the default list, and update the UI
        includeModel.clear();
        excludeModel.clear();

        for (Tag<?> tag : allTags) {
            if (defaultTagNames.contains(tag.name())) {
                includeModel.add(tag);
            } else {
                excludeModel.add(tag);
            }
        }

        includeList.updateUI();
        excludeList.updateUI();
    }

    private void setAllSystemTags(Set<Tag<?>> tags, TreeSetListModel<Tag<?>> excludeModel, TreeSetListModel<Tag<?>> includeModel) {
        // First, clear lists and add the defaults
        setDefaults(excludeTags, includeTags);

        // Second, add all selected system Tags
        for (Tag<?> tag : tags) {
            if (excludeModel.contains(tag)) {
                excludeModel.remove(tag);
            }
            if (!includeModel.contains(tag)) {
                includeModel.add(tag);
            }
        }

        includeList.updateUI();
        excludeList.updateUI();
    }

    @Override
    public void extractInformation(final Settings settings) {
        // TODO store the information in the settings file.

        List<String> filter = includeTags.asSet().stream()
                .map(tag -> tag.name())
                .collect(Collectors.toList());
        settings.set(ConfigTags.SpyTagAttributes, filter);

        TagFilter.getInstance().setFilter(includeTags.asSet());

    }

    @Override
    public void populateFrom(final Settings settings) {
        // TODO read the information from the settings file.
        includeTags.clear();
        excludeTags.clear();
        List<String> storedTags = settings.get(ConfigTags.SpyTagAttributes);
        if (!storedTags.isEmpty()) {
            storedTags.stream()
                    .map(DefaultTagFilter::findTagByName)
                    .filter(Objects::nonNull)
                    .forEach(includeTags::add);
        }

        DefaultTagFilter.getSet().forEach(i -> {
            if (!includeTags.contains(i)) {
                excludeTags.add(i);
            }
        });

        TagFilter.getInstance().setFilter(includeTags.asSet());
    }
}
