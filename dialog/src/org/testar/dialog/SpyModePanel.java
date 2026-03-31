/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2023-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2023-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.dialog;

import org.testar.dialog.tagsvisualization.DefaultTagFilter;
import org.testar.dialog.tagsvisualization.TagFilter;
import org.testar.webdriver.tag.WdTags;
import org.testar.windows.tag.UIATags;
import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.config.settings.SettingsDefaults;
import org.testar.core.Pair;
import org.testar.core.tag.Tag;

import javax.swing.*;

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
            if (pair.left().equals(ConfigTags.SpyTagAttributes)) {
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
