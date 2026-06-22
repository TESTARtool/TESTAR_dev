/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.dialog;

import org.testar.config.ConfigTags;
import org.testar.config.policy.helper.PolicyWorkspaceHelper;
import org.testar.config.settings.Settings;
import org.testar.dialog.editor.CustomPoliciesEditor;
import org.testar.dialog.editor.ModuleSourceEditor;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class PoliciesPanel extends SettingsPanel {

    private static final long serialVersionUID = 2974327443313107498L;

    private static final String DEFAULT_VALUE = "<default built-in>";

    private final JLabel labelCustomPoliciesResource = new JLabel("Policies properties");
    private final JTextField customPoliciesResourceField = new JTextField();
    private final JButton selectCustomPoliciesResourceButton = new JButton("Select");
    private final JButton editCustomPoliciesResourceButton = new JButton("Edit");

    private final Map<String, JTextField> policyFields = new LinkedHashMap<String, JTextField>();
    private final Map<String, JButton> editButtons = new LinkedHashMap<String, JButton>();

    private Settings settings;

    public PoliciesPanel() {
        setLayout(null);

        labelCustomPoliciesResource.setBounds(10, 12, 180, 27);
        labelCustomPoliciesResource.setToolTipText(ConfigTags.CustomPoliciesResource.getDescription());
        add(labelCustomPoliciesResource);

        customPoliciesResourceField.setBounds(190, 12, 240, 27);
        customPoliciesResourceField.setToolTipText(ConfigTags.CustomPoliciesResource.getDescription());
        add(customPoliciesResourceField);

        selectCustomPoliciesResourceButton.setBounds(440, 12, 80, 27);
        selectCustomPoliciesResourceButton.addActionListener(this::selectCustomPoliciesResourceActionPerformed);
        add(selectCustomPoliciesResourceButton);

        editCustomPoliciesResourceButton.setBounds(530, 12, 80, 27);
        editCustomPoliciesResourceButton.addActionListener(this::editCustomPoliciesResourceActionPerformed);
        add(editCustomPoliciesResourceButton);

        int rowY = 45;
        for (PolicyWorkspaceHelper.PolicyDefinition policyDefinition : PolicyWorkspaceHelper.POLICY_DEFINITIONS) {
            JLabel label = new JLabel(policyDefinition.label);
            label.setBounds(10, rowY, 180, 27);
            add(label);

            JTextField policyField = new JTextField();
            policyField.setBounds(190, rowY, 240, 27);
            policyField.setEditable(false);
            add(policyField);
            policyFields.put(policyDefinition.propertyKey, policyField);

            JButton addButton = new JButton("Add");
            addButton.setBounds(440, rowY, 80, 27);
            addButton.addActionListener(event -> addPolicyActionPerformed(policyDefinition));
            add(addButton);

            JButton editButton = new JButton("Edit");
            editButton.setBounds(530, rowY, 80, 27);
            editButton.addActionListener(event -> editPolicyActionPerformed(policyDefinition));
            add(editButton);
            editButtons.put(policyDefinition.propertyKey, editButton);

            rowY += 33;
        }
    }

    @Override
    public void populateFrom(Settings settings) {
        this.settings = settings;

        String customPoliciesResource = settings.get(ConfigTags.CustomPoliciesResource);
        if (customPoliciesResource == null || customPoliciesResource.isBlank()) {
            customPoliciesResource = PolicyWorkspaceHelper.defaultPoliciesResource(settings);
        }

        customPoliciesResourceField.setText(customPoliciesResource);
        refreshPolicyFields();
    }

    @Override
    public void extractInformation(Settings settings) {
        settings.set(ConfigTags.CustomPoliciesResource, customPoliciesResourceField.getText().trim());
    }

    private void selectCustomPoliciesResourceActionPerformed(ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        File currentFile = new File(customPoliciesResourceField.getText());
        File currentDirectory = currentFile.getParentFile();
        if (currentDirectory != null && currentDirectory.exists()) {
            fileChooser.setCurrentDirectory(currentDirectory);
        }

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            customPoliciesResourceField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            refreshPolicyFields();
        }
    }

    private void editCustomPoliciesResourceActionPerformed(ActionEvent evt) {
        try {
            String resourcePath = ensureCustomPoliciesResourcePath();
            File resourceFile = new File(resourcePath);
            File parentDirectory = resourceFile.getParentFile();
            if (parentDirectory != null && !parentDirectory.exists()) {
                parentDirectory.mkdirs();
            }

            JDialog dialog = new CustomPoliciesEditor(resourcePath);
            dialog.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
            dialog.setVisible(true);
            refreshPolicyFields();
        } catch (RuntimeException exception) {
            exception.printStackTrace();
        }
    }

    private void addPolicyActionPerformed(PolicyWorkspaceHelper.PolicyDefinition policyDefinition) {
        try {
            String resourcePath = ensureCustomPoliciesResourcePath();
            Properties properties = PolicyWorkspaceHelper.loadPoliciesProperties(resourcePath);
            List<String> classNames = PolicyWorkspaceHelper.parseClassList(properties.getProperty(policyDefinition.propertyKey, ""));
            String className = PolicyWorkspaceHelper.buildDefaultClassName(resourcePath, policyDefinition, classNames.size());
            classNames.add(className);
            properties.setProperty(policyDefinition.propertyKey, PolicyWorkspaceHelper.joinClassNames(classNames));
            PolicyWorkspaceHelper.savePoliciesProperties(resourcePath, properties);

            File sourceFile = PolicyWorkspaceHelper.ensurePolicySourceFile(resourcePath, className, policyDefinition);
            openSourceEditor(sourceFile, className, policyDefinition);
            refreshPolicyFields();
        } catch (RuntimeException exception) {
            exception.printStackTrace();
        }
    }

    private void editPolicyActionPerformed(PolicyWorkspaceHelper.PolicyDefinition policyDefinition) {
        try {
            String resourcePath = ensureCustomPoliciesResourcePath();
            Properties properties = PolicyWorkspaceHelper.loadPoliciesProperties(resourcePath);
            List<String> classNames = PolicyWorkspaceHelper.parseClassList(properties.getProperty(policyDefinition.propertyKey, ""));

            if (classNames.isEmpty()) {
                return;
            }

            String className = classNames.get(0);
            if (classNames.size() > 1) {
                Object selected = JOptionPane.showInputDialog(
                        this,
                        "Select the policy class to edit:",
                        policyDefinition.label,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        classNames.toArray(),
                        classNames.get(0)
                );
                if (!(selected instanceof String)) {
                    return;
                }
                className = (String) selected;
            }

            File sourceFile = PolicyWorkspaceHelper.ensurePolicySourceFile(resourcePath, className, policyDefinition);
            openSourceEditor(sourceFile, className, policyDefinition);
            refreshPolicyFields();
        } catch (RuntimeException exception) {
            exception.printStackTrace();
        }
    }

    private void openSourceEditor(File sourceFile,
                                  String className,
                                  PolicyWorkspaceHelper.PolicyDefinition policyDefinition) {
        ModuleSourceEditor dialog = new ModuleSourceEditor(
                sourceFile.getAbsolutePath(),
                policyDefinition.label,
                PolicyWorkspaceHelper.defaultPolicyTemplate(className, policyDefinition)
        );
        dialog.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setVisible(true);
    }

    private void refreshPolicyFields() {
        Properties properties = PolicyWorkspaceHelper.loadPoliciesProperties(customPoliciesResourceField.getText().trim());
        for (PolicyWorkspaceHelper.PolicyDefinition policyDefinition : PolicyWorkspaceHelper.POLICY_DEFINITIONS) {
            JTextField policyField = policyFields.get(policyDefinition.propertyKey);
            JButton editButton = editButtons.get(policyDefinition.propertyKey);
            List<String> classNames = PolicyWorkspaceHelper.parseClassList(properties.getProperty(policyDefinition.propertyKey, ""));
            if (classNames.isEmpty()) {
                policyField.setText(DEFAULT_VALUE);
                if (editButton != null) {
                    editButton.setEnabled(false);
                }
            } else {
                policyField.setText(PolicyWorkspaceHelper.joinClassNames(classNames));
                if (editButton != null) {
                    editButton.setEnabled(true);
                }
            }
        }
    }

    private String ensureCustomPoliciesResourcePath() {
        String resourcePath = PolicyWorkspaceHelper.ensureCustomPoliciesResourcePath(
                customPoliciesResourceField.getText(),
                settings
        );
        customPoliciesResourceField.setText(resourcePath);
        return resourcePath;
    }
}
