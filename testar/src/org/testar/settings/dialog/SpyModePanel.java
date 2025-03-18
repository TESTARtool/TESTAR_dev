package org.testar.settings.dialog;

import org.testar.settings.dialog.tagsvisualization.DefaultTagFilter;
import org.testar.settings.dialog.tagsvisualization.TagFilter;
import org.testar.settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class SpyModePanel extends SettingsPanel {

    private TreeSetListModel<String> excludeTags = new TreeSetListModel<>();
    private TreeSetListModel<String> includeTags = new TreeSetListModel<>();

    JList excludeList = new JList();
    JList includeList = new JList();


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
        excludeList.setModel(excludeTags);

        JScrollPane leftScrollList = new JScrollPane(excludeList);
        leftScrollList.setPreferredSize(listDimension);
        left.add(leftScrollList);

        // Center Panel
        JPanel middle = new JPanel();
        middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));

        JButton allInclude = new JButton(">>");
        allInclude.addActionListener(e -> {
            int size = excludeTags.getSize();
            for (int i = 0 ; i < size; ++i){
                includeTags.add(excludeTags.getElementAt(i));
            }
            excludeList.clearSelection();
            excludeTags.clear();
            excludeList.updateUI();
        });
        middle.add(allInclude);

        JButton selectedInclude = new JButton(">");
        selectedInclude.addActionListener(e -> {
                    excludeList.getSelectedValuesList().forEach(i ->{
                        includeTags.add(i.toString());
                        excludeTags.remove(i.toString());
                    });
                    excludeList.clearSelection();
                }
        );
        middle.add(selectedInclude);

        JButton selectedExclude = new JButton("<");
        selectedExclude.addActionListener(e -> {
                    includeList.getSelectedValuesList().forEach(i -> {
                        excludeTags.add(i.toString());
                        includeTags.remove(i.toString());
                    });
                    includeList.clearSelection();
                }
        );
        middle.add(selectedExclude);

        JButton allExclude = new JButton("<<");
        allExclude.addActionListener(e -> {
            int size = includeTags.getSize();
            for (int i = 0 ; i < size; ++i){
                excludeTags.add(includeTags.getElementAt(i));
            }
            includeList.clearSelection();
            includeTags.clear();
            includeList.updateUI();
        });
        middle.add(allExclude);

        // Right Panel
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.add(new JLabel("Include:"));


        includeList.setSelectionMode(
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        includeList.setModel(includeTags);
        JScrollPane rightScrollList = new JScrollPane(includeList);
        rightScrollList.setPreferredSize(listDimension);
        right.add(rightScrollList);

        add(left);
        add(middle);
        add(right);
    }

    @Override
    public void extractInformation(final Settings settings) {
        // TODO store the information in the settings file.
    }

    @Override
    public void populateFrom(final Settings settings) {
        // TODO read the information from the settings file.

        DefaultTagFilter.getList().forEach(i -> includeTags.add(i));
        // Create and set the new tag filter.
        Set<String> filter = new HashSet<>();
        int size = includeTags.getSize();
        for (int i = 0 ; i < size; ++i){
            filter.add(includeTags.getElementAt(i));
        }
        TagFilter.getInstance().setFilter(filter);
    }
}
