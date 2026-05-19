/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.dialog.helper;

import org.testar.config.TestarDirectories;
import org.testar.config.settings.Settings;
import org.testar.core.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class PolicyWorkspaceHelper {

    public static final PolicyDefinition[] POLICY_DEFINITIONS = new PolicyDefinition[]{
            new PolicyDefinition("clickablePolicies", "Clickable policies", "ClickablePolicy",
                    "org.testar.core.policy.ClickablePolicy", "isClickable", "return false;"),
            new PolicyDefinition("typeablePolicies", "Typeable policies", "TypeablePolicy",
                    "org.testar.core.policy.TypeablePolicy", "isTypeable", "return false;"),
            new PolicyDefinition("scrollablePolicies", "Scrollable policies", "ScrollablePolicy",
                    "org.testar.core.policy.ScrollablePolicy", "isScrollable", "return false;"),
            new PolicyDefinition("selectablePolicies", "Selectable policies", "SelectablePolicy",
                    "org.testar.core.policy.SelectablePolicy", "isSelectable", "return false;"),
            new PolicyDefinition("enabledPolicies", "Enabled policies", "EnabledPolicy",
                    "org.testar.core.policy.EnabledPolicy", "isEnabled", "return true;"),
            new PolicyDefinition("blockedPolicies", "Blocked policies", "BlockedPolicy",
                    "org.testar.core.policy.BlockedPolicy", "isBlocked", "return false;"),
            new PolicyDefinition("widgetFilterPolicies", "Widget filter policies", "WidgetFilterPolicy",
                    "org.testar.core.policy.WidgetFilterPolicy", "allows", "return true;"),
            new PolicyDefinition("visiblePolicies", "Visible policies", "VisiblePolicy",
                    "org.testar.core.policy.VisiblePolicy", "isVisible", "return true;"),
            new PolicyDefinition("atCanvasPolicies", "At-canvas policies", "AtCanvasPolicy",
                    "org.testar.core.policy.AtCanvasPolicy", "isAtCanvas", "return true;"),
            new PolicyDefinition("topLevelPolicies", "Top-level policies", "TopLevelPolicy",
                    "org.testar.core.policy.TopLevelPolicy", "isTopLevel", "return true;")
    };

    private PolicyWorkspaceHelper() {
    }

    public static Properties loadPoliciesProperties(String resourcePath) {
        Properties properties = new Properties();

        if (resourcePath == null || resourcePath.isBlank()) {
            return properties;
        }

        File resourceFile = new File(resourcePath);
        if (!resourceFile.exists()) {
            return properties;
        }

        try (FileInputStream inputStream = new FileInputStream(resourceFile)) {
            properties.load(inputStream);
            return properties;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read policies resource: " + resourcePath, exception);
        }
    }

    public static void savePoliciesProperties(String resourcePath, Properties properties) {
        StringBuilder builder = new StringBuilder();
        builder.append("# Scriptless custom policies resource").append(System.lineSeparator());
        builder.append("# Additive policy composition. Multiple classes can be declared with ';'").append(System.lineSeparator());
        builder.append("# Set replace...=true to ignore the built-in policies for that seam.").append(System.lineSeparator());
        builder.append(System.lineSeparator());
        for (PolicyDefinition policyDefinition : POLICY_DEFINITIONS) {
            builder.append(policyDefinition.replacePropertyKey)
                    .append("=")
                    .append(properties.getProperty(policyDefinition.replacePropertyKey, "false"))
                    .append(System.lineSeparator());
            builder.append(policyDefinition.propertyKey)
                    .append("=")
                    .append(properties.getProperty(policyDefinition.propertyKey, ""))
                    .append(System.lineSeparator());
        }

        try {
            Util.saveToFile(builder.toString(), resourcePath);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to save policies resource: " + resourcePath, exception);
        }
    }

    public static String ensureCustomPoliciesResourcePath(String resourcePath, Settings settings) {
        if (resourcePath == null || resourcePath.isBlank()) {
            return defaultPoliciesResource(settings);
        }
        return resourcePath.trim();
    }

    public static String defaultPoliciesResource(Settings settings) {
        String selectedSse = TestarDirectories.getSelectedSse();
        if (selectedSse == null || selectedSse.isBlank()) {
            selectedSse = "sut";
        }

        return TestarDirectories.getSettingsDir()
                + selectedSse
                + File.separator
                + "policies.properties";
    }

    public static String buildDefaultClassName(String resourcePath,
                                               PolicyDefinition policyDefinition,
                                               int existingPolicyCount) {
        File resourceFile = new File(resourcePath);
        File parentDirectory = resourceFile.getParentFile();
        String folderName = parentDirectory == null ? "custom_policy" : parentDirectory.getName();
        String baseName = toPascalCase(folderName) + policyDefinition.classSuffix;
        if (existingPolicyCount == 0) {
            return baseName;
        }
        return baseName + (existingPolicyCount + 1);
    }

    public static File ensurePolicySourceFile(String resourcePath,
                                              String className,
                                              PolicyDefinition policyDefinition) {
        File resourceFile = new File(resourcePath);
        File baseDirectory = resourceFile.getParentFile();
        if (baseDirectory == null) {
            throw new IllegalStateException("Unable to resolve policies directory from: " + resourcePath);
        }

        File sourceFile = resolveSourceFile(baseDirectory, className);
        File parentDirectory = sourceFile.getParentFile();
        if (parentDirectory != null && !parentDirectory.exists()) {
            parentDirectory.mkdirs();
        }

        if (!sourceFile.exists()) {
            try {
                Util.saveToFile(defaultPolicyTemplate(className, policyDefinition), sourceFile.getAbsolutePath());
            } catch (IOException exception) {
                throw new IllegalStateException("Unable to create policy source file: " + sourceFile, exception);
            }
        }

        return sourceFile;
    }

    public static String defaultPolicyTemplate(String className, PolicyDefinition policyDefinition) {
        String simpleClassName = className;
        String packageName = "";
        int separatorIndex = className.lastIndexOf('.');
        if (separatorIndex >= 0) {
            packageName = className.substring(0, separatorIndex);
            simpleClassName = className.substring(separatorIndex + 1);
        }

        StringBuilder builder = new StringBuilder();
        if (!packageName.isEmpty()) {
            builder.append("package ")
                    .append(packageName)
                    .append(";")
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());
        }

        builder.append("import org.testar.core.state.Widget;")
                .append(System.lineSeparator())
                .append("import ")
                .append(policyDefinition.interfaceImport)
                .append(";")
                .append(System.lineSeparator())
                .append(System.lineSeparator())
                .append("public final class ")
                .append(simpleClassName)
                .append(" implements ")
                .append(policyDefinition.interfaceSimpleName)
                .append(" {")
                .append(System.lineSeparator())
                .append(System.lineSeparator())
                .append("    @Override")
                .append(System.lineSeparator())
                .append("    public boolean ")
                .append(policyDefinition.methodName)
                .append("(Widget widget) {")
                .append(System.lineSeparator())
                .append("        ")
                .append(policyDefinition.defaultReturn)
                .append(System.lineSeparator())
                .append("    }")
                .append(System.lineSeparator())
                .append("}")
                .append(System.lineSeparator());

        return builder.toString();
    }

    public static List<String> parseClassList(String value) {
        List<String> classNames = new ArrayList<String>();
        if (value == null || value.isBlank()) {
            return classNames;
        }

        for (String token : value.split(";")) {
            String trimmed = token.trim();
            if (!trimmed.isEmpty()) {
                classNames.add(trimmed);
            }
        }

        return classNames;
    }

    public static String joinClassNames(List<String> classNames) {
        return String.join("; ", classNames);
    }

    private static File resolveSourceFile(File baseDirectory, String className) {
        String relativePath = className.replace('.', File.separatorChar) + ".java";
        return new File(baseDirectory, relativePath);
    }

    private static String toPascalCase(String value) {
        StringBuilder builder = new StringBuilder();
        for (String token : value.split("[^A-Za-z0-9]+")) {
            if (token.isEmpty()) {
                continue;
            }
            builder.append(Character.toUpperCase(token.charAt(0)));
            if (token.length() > 1) {
                builder.append(token.substring(1));
            }
        }
        return builder.length() == 0 ? "CustomPolicy" : builder.toString();
    }

    public static final class PolicyDefinition {

        public final String propertyKey;
        public final String replacePropertyKey;
        public final String label;
        public final String classSuffix;
        public final String interfaceImport;
        public final String interfaceSimpleName;
        public final String methodName;
        public final String defaultReturn;

        public PolicyDefinition(String propertyKey,
                                String label,
                                String classSuffix,
                                String interfaceImport,
                                String methodName,
                                String defaultReturn) {
            this.propertyKey = propertyKey;
            this.replacePropertyKey = "replace" + Character.toUpperCase(propertyKey.charAt(0)) + propertyKey.substring(1);
            this.label = label;
            this.classSuffix = classSuffix;
            this.interfaceImport = interfaceImport;
            this.interfaceSimpleName = interfaceImport.substring(interfaceImport.lastIndexOf('.') + 1);
            this.methodName = methodName;
            this.defaultReturn = defaultReturn;
        }
    }
}
