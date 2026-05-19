/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2023-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2023-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.dialog;

import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.dialog.editor.CustomCompositionEditor;
import org.testar.dialog.editor.ModuleSourceEditor;
import org.testar.dialog.helper.ModuleWorkspaceHelper;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class ModulesPanel extends SettingsPanel {

    private static final long serialVersionUID = -8923907072510225148L;

    private static final String DEFAULT_VALUE = "<default built-in>";

    private final JLabel labelCustomCompositionResource = new JLabel("Composition properties");
    private final JTextField customCompositionResourceField = new JTextField();
    private final JButton selectCustomCompositionResourceButton = new JButton("Select");
    private final JButton editCustomCompositionResourceButton = new JButton("Edit");

    private final Map<String, JTextField> moduleFields = new LinkedHashMap<String, JTextField>();
    private final Map<String, JButton> createButtons = new LinkedHashMap<String, JButton>();
    private final Map<String, JButton> editButtons = new LinkedHashMap<String, JButton>();

    private Settings settings;

    public ModulesPanel() {
        setLayout(null);

        labelCustomCompositionResource.setBounds(10, 12, 180, 27);
        labelCustomCompositionResource.setToolTipText(ConfigTags.CustomCompositionResource.getDescription());
        add(labelCustomCompositionResource);

        customCompositionResourceField.setBounds(190, 12, 240, 27);
        customCompositionResourceField.setToolTipText(ConfigTags.CustomCompositionResource.getDescription());
        add(customCompositionResourceField);

        selectCustomCompositionResourceButton.setBounds(440, 12, 80, 27);
        selectCustomCompositionResourceButton.addActionListener(this::selectCustomCompositionResourceActionPerformed);
        add(selectCustomCompositionResourceButton);

        editCustomCompositionResourceButton.setBounds(530, 12, 80, 27);
        editCustomCompositionResourceButton.addActionListener(this::editCustomCompositionResourceActionPerformed);
        add(editCustomCompositionResourceButton);

        int rowY = 45;
        for (ModuleWorkspaceHelper.ModuleDefinition moduleDefinition : ModuleWorkspaceHelper.MODULE_DEFINITIONS) {
            JLabel label = new JLabel(moduleDefinition.label);
            label.setBounds(10, rowY, 180, 27);
            add(label);

            JTextField moduleField = new JTextField();
            moduleField.setBounds(190, rowY, 240, 27);
            moduleField.setEditable(false);
            add(moduleField);
            moduleFields.put(moduleDefinition.propertyKey, moduleField);

            JButton createButton = new JButton("Create");
            createButton.setBounds(440, rowY, 80, 27);
            createButton.addActionListener(event -> createModuleActionPerformed(moduleDefinition));
            add(createButton);
            createButtons.put(moduleDefinition.propertyKey, createButton);

            JButton editButton = new JButton("Edit");
            editButton.setBounds(530, rowY, 80, 27);
            editButton.addActionListener(event -> editModuleActionPerformed(moduleDefinition));
            add(editButton);
            editButtons.put(moduleDefinition.propertyKey, editButton);

            rowY += 33;
        }
    }

    @Override
    public void populateFrom(Settings settings) {
        this.settings = settings;

        String customCompositionResource = settings.get(ConfigTags.CustomCompositionResource);
        if (customCompositionResource == null || customCompositionResource.isBlank()) {
            customCompositionResource = ModuleWorkspaceHelper.defaultCustomCompositionResource(settings);
        }

        customCompositionResourceField.setText(customCompositionResource);
        refreshModuleFields();
    }

    @Override
    public void extractInformation(Settings settings) {
        settings.set(ConfigTags.CustomCompositionResource, customCompositionResourceField.getText().trim());
    }

    private void selectCustomCompositionResourceActionPerformed(ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        File currentFile = new File(customCompositionResourceField.getText());
        File currentDirectory = currentFile.getParentFile();
        if (currentDirectory != null && currentDirectory.exists()) {
            fileChooser.setCurrentDirectory(currentDirectory);
        }

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            customCompositionResourceField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            refreshModuleFields();
        }
    }

    private void editCustomCompositionResourceActionPerformed(ActionEvent evt) {
        try {
            String resourcePath = ensureCustomCompositionResourcePath();
            File resourceFile = new File(resourcePath);
            File parentDirectory = resourceFile.getParentFile();
            if (parentDirectory != null && !parentDirectory.exists()) {
                parentDirectory.mkdirs();
            }

            CustomCompositionEditor dialog = new CustomCompositionEditor(resourcePath);
            dialog.setModalityType(javax.swing.JDialog.ModalityType.APPLICATION_MODAL);
            dialog.setVisible(true);
            refreshModuleFields();
        } catch (RuntimeException exception) {
            exception.printStackTrace();
        }
    }

    private void createModuleActionPerformed(ModuleWorkspaceHelper.ModuleDefinition moduleDefinition) {
        try {
            String resourcePath = ensureCustomCompositionResourcePath();
            Properties properties = ModuleWorkspaceHelper.loadCompositionProperties(resourcePath);
            String className = properties.getProperty(moduleDefinition.propertyKey, "").trim();

            if (className.isEmpty()) {
                className = ModuleWorkspaceHelper.buildDefaultClassName(resourcePath, moduleDefinition);
                properties.setProperty(moduleDefinition.propertyKey, className);
                ModuleWorkspaceHelper.saveCompositionProperties(resourcePath, properties, settings);
            }

            File sourceFile = ModuleWorkspaceHelper.ensureModuleSourceFile(resourcePath, className, moduleDefinition);
            openSourceEditor(sourceFile, className, moduleDefinition);
            refreshModuleFields();
        } catch (RuntimeException exception) {
            exception.printStackTrace();
        }
    }

    private void editModuleActionPerformed(ModuleWorkspaceHelper.ModuleDefinition moduleDefinition) {
        try {
            String resourcePath = ensureCustomCompositionResourcePath();
            Properties properties = ModuleWorkspaceHelper.loadCompositionProperties(resourcePath);
            String className = properties.getProperty(moduleDefinition.propertyKey, "").trim();

            if (className.isEmpty()) {
                createModuleActionPerformed(moduleDefinition);
                return;
            }

            File sourceFile = ModuleWorkspaceHelper.ensureModuleSourceFile(resourcePath, className, moduleDefinition);
            openSourceEditor(sourceFile, className, moduleDefinition);
            refreshModuleFields();
        } catch (RuntimeException exception) {
            exception.printStackTrace();
        }
    }

    private void openSourceEditor(File sourceFile,
                                  String className,
                                  ModuleWorkspaceHelper.ModuleDefinition moduleDefinition) {
        ModuleSourceEditor dialog = new ModuleSourceEditor(
                sourceFile.getAbsolutePath(),
                moduleDefinition.label,
                ModuleWorkspaceHelper.defaultModuleTemplate(className, moduleDefinition)
        );
        dialog.setModalityType(javax.swing.JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setVisible(true);
    }

    private void refreshModuleFields() {
        Properties properties = ModuleWorkspaceHelper.loadCompositionProperties(customCompositionResourceField.getText().trim());
        for (ModuleWorkspaceHelper.ModuleDefinition moduleDefinition : ModuleWorkspaceHelper.MODULE_DEFINITIONS) {
            JTextField moduleField = moduleFields.get(moduleDefinition.propertyKey);
            JButton createButton = createButtons.get(moduleDefinition.propertyKey);
            JButton editButton = editButtons.get(moduleDefinition.propertyKey);
            String value = properties.getProperty(moduleDefinition.propertyKey, "").trim();
            if (value.isEmpty()) {
                moduleField.setText(DEFAULT_VALUE);
                if (createButton != null) {
                    createButton.setEnabled(true);
                }
                if (editButton != null) {
                    editButton.setEnabled(false);
                }
            } else {
                moduleField.setText(value);
                if (createButton != null) {
                    createButton.setEnabled(false);
                }
                if (editButton != null) {
                    editButton.setEnabled(true);
                }
            }
        }
    }

    private String ensureCustomCompositionResourcePath() {
        String resourcePath = ModuleWorkspaceHelper.ensureCustomCompositionResourcePath(
                customCompositionResourceField.getText(),
                settings
        );
        customCompositionResourceField.setText(resourcePath);
        return resourcePath;
    }
}
