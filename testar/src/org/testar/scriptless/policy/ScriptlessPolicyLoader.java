/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.policy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.core.Assert;
import org.testar.core.policy.Policy;
import org.testar.scriptless.util.ExternalJavaClassSupport;

public final class ScriptlessPolicyLoader {

    private static final String PROPERTY_CLICKABLE_POLICIES = "clickablePolicies";
    private static final String PROPERTY_REPLACE_CLICKABLE_POLICIES = "replaceClickablePolicies";
    private static final String PROPERTY_TYPEABLE_POLICIES = "typeablePolicies";
    private static final String PROPERTY_REPLACE_TYPEABLE_POLICIES = "replaceTypeablePolicies";
    private static final String PROPERTY_SCROLLABLE_POLICIES = "scrollablePolicies";
    private static final String PROPERTY_REPLACE_SCROLLABLE_POLICIES = "replaceScrollablePolicies";
    private static final String PROPERTY_SELECTABLE_POLICIES = "selectablePolicies";
    private static final String PROPERTY_REPLACE_SELECTABLE_POLICIES = "replaceSelectablePolicies";
    private static final String PROPERTY_ENABLED_POLICIES = "enabledPolicies";
    private static final String PROPERTY_REPLACE_ENABLED_POLICIES = "replaceEnabledPolicies";
    private static final String PROPERTY_BLOCKED_POLICIES = "blockedPolicies";
    private static final String PROPERTY_REPLACE_BLOCKED_POLICIES = "replaceBlockedPolicies";
    private static final String PROPERTY_WIDGET_FILTER_POLICIES = "widgetFilterPolicies";
    private static final String PROPERTY_REPLACE_WIDGET_FILTER_POLICIES = "replaceWidgetFilterPolicies";
    private static final String PROPERTY_VISIBLE_POLICIES = "visiblePolicies";
    private static final String PROPERTY_REPLACE_VISIBLE_POLICIES = "replaceVisiblePolicies";
    private static final String PROPERTY_TOP_LEVEL_POLICIES = "topLevelPolicies";
    private static final String PROPERTY_REPLACE_TOP_LEVEL_POLICIES = "replaceTopLevelPolicies";

    private ScriptlessPolicyLoader() {
    }

    public static ScriptlessPolicyDescriptor loadDescriptor(Settings settings) {
        Assert.notNull(settings);

        String configuredResource = settings.get(ConfigTags.CustomPoliciesResource, "").trim();
        Properties resourceProperties = loadResourceProperties(configuredResource);

        return new ScriptlessPolicyDescriptor(
                optionalValue(configuredResource),
                parseBoolean(resourceProperties.getProperty(PROPERTY_REPLACE_CLICKABLE_POLICIES, "false")),
                parseBoolean(resourceProperties.getProperty(PROPERTY_REPLACE_TYPEABLE_POLICIES, "false")),
                parseBoolean(resourceProperties.getProperty(PROPERTY_REPLACE_SCROLLABLE_POLICIES, "false")),
                parseBoolean(resourceProperties.getProperty(PROPERTY_REPLACE_SELECTABLE_POLICIES, "false")),
                parseBoolean(resourceProperties.getProperty(PROPERTY_REPLACE_ENABLED_POLICIES, "false")),
                parseBoolean(resourceProperties.getProperty(PROPERTY_REPLACE_BLOCKED_POLICIES, "false")),
                parseBoolean(resourceProperties.getProperty(PROPERTY_REPLACE_WIDGET_FILTER_POLICIES, "false")),
                parseBoolean(resourceProperties.getProperty(PROPERTY_REPLACE_VISIBLE_POLICIES, "false")),
                parseBoolean(resourceProperties.getProperty(PROPERTY_REPLACE_TOP_LEVEL_POLICIES, "false")),
                parseClassList(resourceProperties.getProperty(PROPERTY_CLICKABLE_POLICIES, "")),
                parseClassList(resourceProperties.getProperty(PROPERTY_TYPEABLE_POLICIES, "")),
                parseClassList(resourceProperties.getProperty(PROPERTY_SCROLLABLE_POLICIES, "")),
                parseClassList(resourceProperties.getProperty(PROPERTY_SELECTABLE_POLICIES, "")),
                parseClassList(resourceProperties.getProperty(PROPERTY_ENABLED_POLICIES, "")),
                parseClassList(resourceProperties.getProperty(PROPERTY_BLOCKED_POLICIES, "")),
                parseClassList(resourceProperties.getProperty(PROPERTY_WIDGET_FILTER_POLICIES, "")),
                parseClassList(resourceProperties.getProperty(PROPERTY_VISIBLE_POLICIES, "")),
                parseClassList(resourceProperties.getProperty(PROPERTY_TOP_LEVEL_POLICIES, ""))
        );
    }

    public static <T extends Policy> List<T> loadPolicies(List<String> policyClassNames,
                                                          Class<T> expectedType,
                                                          Settings settings) {
        Assert.notNull(policyClassNames);
        Assert.notNull(expectedType);
        Assert.notNull(settings);

        if (policyClassNames.isEmpty()) {
            return Collections.emptyList();
        }

        List<T> policies = new ArrayList<>();
        Optional<String> resourcePath = optionalValue(settings.get(ConfigTags.CustomPoliciesResource, "").trim());
        for (String className : policyClassNames) {
            policies.add(loadPolicy(className, expectedType, settings, resourcePath));
        }
        return Collections.unmodifiableList(policies);
    }

    private static <T extends Policy> T loadPolicy(String className,
                                                   Class<T> expectedType,
                                                   Settings settings,
                                                   Optional<String> resourcePath) {
        try {
            Class<?> policyClass = ExternalJavaClassSupport.loadClass(className, resourcePath);

            if (!expectedType.isAssignableFrom(policyClass)) {
                throw new IllegalStateException(
                        "Configured policy class " + className
                                + " does not implement " + expectedType.getName()
                );
            }

            Constructor<?> settingsConstructor = findConstructor(policyClass, Settings.class);
            if (settingsConstructor != null) {
                Object instance = settingsConstructor.newInstance(settings);
                return expectedType.cast(instance);
            }

            Constructor<?> emptyConstructor = findConstructor(policyClass);
            if (emptyConstructor != null) {
                Object instance = emptyConstructor.newInstance();
                return expectedType.cast(instance);
            }

            throw new IllegalStateException(
                    "Configured policy class " + className
                            + " must expose either a no-args constructor or a Settings constructor"
            );
        }
        catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to instantiate policy class " + className, exception);
        }
    }

    private static Constructor<?> findConstructor(Class<?> type, Class<?>... parameterTypes) {
        try {
            return type.getConstructor(parameterTypes);
        }
        catch (NoSuchMethodException exception) {
            return null;
        }
    }

    private static List<String> parseClassList(String value) {
        if (value == null || value.isBlank()) {
            return Collections.emptyList();
        }

        List<String> classNames = new ArrayList<>();
        for (String token : value.split(";")) {
            String trimmed = token.trim();
            if (!trimmed.isEmpty()) {
                classNames.add(trimmed);
            }
        }
        return Collections.unmodifiableList(classNames);
    }

    private static Properties loadResourceProperties(String configuredResource) {
        Properties properties = new Properties();
        if (configuredResource == null || configuredResource.isBlank()) {
            return properties;
        }

        File resourceFile = new File(configuredResource);
        if (!resourceFile.exists()) {
            return properties;
        }

        try (FileInputStream inputStream = new FileInputStream(resourceFile)) {
            properties.load(inputStream);
            return properties;
        }
        catch (IOException exception) {
            throw new IllegalStateException("Unable to read policies resource: " + configuredResource, exception);
        }
    }

    private static Optional<String> optionalValue(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(value);
    }

    private static boolean parseBoolean(String value) {
        return Boolean.parseBoolean(value == null ? "false" : value.trim());
    }
}
