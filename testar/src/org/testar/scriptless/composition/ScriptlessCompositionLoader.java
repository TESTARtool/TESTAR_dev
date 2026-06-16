/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.composition;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.Properties;

import org.testar.config.CompositionProfiles;
import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.config.settings.SettingsResourceResolver;
import org.testar.core.Assert;
import org.testar.scriptless.util.ExternalJavaClassSupport;

public final class ScriptlessCompositionLoader {

    private static final String PROPERTY_BASE_PROFILE = "baseProfile";
    private static final String PROPERTY_SETTINGS_CAPABILITY_CLASS = "settingsCapabilityClass";
    private static final String PROPERTY_TEST_SESSION_CAPABILITY_CLASS = "testSessionCapabilityClass";
    private static final String PROPERTY_TEST_SEQUENCE_CAPABILITY_CLASS = "testSequenceCapabilityClass";
    private static final String PROPERTY_STOP_CRITERIA_CAPABILITY_CLASS = "stopCriteriaCapabilityClass";

    private static final String PROPERTY_SYSTEM_SERVICE_CLASS = "systemServiceClass";
    private static final String PROPERTY_STATE_SERVICE_CLASS = "stateServiceClass";
    private static final String PROPERTY_STATE_IDENTIFIER_SERVICE_CLASS = "stateIdentifierServiceClass";
    private static final String PROPERTY_ACTION_DERIVATION_SERVICE_CLASS = "actionDerivationServiceClass";
    private static final String PROPERTY_ACTION_IDENTIFIER_SERVICE_CLASS = "actionIdentifierServiceClass";
    private static final String PROPERTY_ACTION_SELECTOR_SERVICE_CLASS = "actionSelectorServiceClass";
    private static final String PROPERTY_ACTION_EXECUTION_SERVICE_CLASS = "actionExecutionServiceClass";
    private static final String PROPERTY_ORACLE_COMPOSER_CLASS = "oracleComposerClass";

    private ScriptlessCompositionLoader() {
    }

    public static ScriptlessCompositionDescriptor loadDescriptor(Settings settings) {
        Assert.notNull(settings);

        String configuredProfile = settings.get(
                ConfigTags.CompositionProfile,
                CompositionProfiles.WINDOWS_COMPOSITION
        );
        String configuredResource = configuredCompositionResource(settings);
        Properties resourceProperties = loadResourceProperties(configuredResource);
        String resolvedProfile = resolveProfile(settings, configuredProfile, resourceProperties);

        return new ScriptlessCompositionDescriptor(
                resolvedProfile,
                optionalValue(configuredResource),
                optionalProperty(resourceProperties, PROPERTY_SETTINGS_CAPABILITY_CLASS),
                optionalProperty(resourceProperties, PROPERTY_TEST_SESSION_CAPABILITY_CLASS),
                optionalProperty(resourceProperties, PROPERTY_TEST_SEQUENCE_CAPABILITY_CLASS),
                optionalProperty(resourceProperties, PROPERTY_STOP_CRITERIA_CAPABILITY_CLASS),
                optionalProperty(resourceProperties, PROPERTY_SYSTEM_SERVICE_CLASS),
                optionalProperty(resourceProperties, PROPERTY_STATE_SERVICE_CLASS),
                optionalProperty(resourceProperties, PROPERTY_STATE_IDENTIFIER_SERVICE_CLASS),
                optionalProperty(resourceProperties, PROPERTY_ACTION_DERIVATION_SERVICE_CLASS),
                optionalProperty(resourceProperties, PROPERTY_ACTION_IDENTIFIER_SERVICE_CLASS),
                optionalProperty(resourceProperties, PROPERTY_ACTION_SELECTOR_SERVICE_CLASS),
                optionalProperty(resourceProperties, PROPERTY_ACTION_EXECUTION_SERVICE_CLASS),
                optionalProperty(resourceProperties, PROPERTY_ORACLE_COMPOSER_CLASS)
        );
    }

    public static <T> T loadDelegateWrapper(Optional<String> wrapperClassName,
                                            Optional<String> customCompositionResourcePath,
                                            Class<T> expectedType,
                                            T delegate,
                                            Object[]... extraArgumentOptions) {
        Assert.notNull(wrapperClassName);
        Assert.notNull(customCompositionResourcePath);
        Assert.notNull(expectedType);
        Assert.notNull(delegate);
        Assert.notNull(extraArgumentOptions);

        if (wrapperClassName.isEmpty()) {
            return delegate;
        }

        String className = wrapperClassName.get();
        try {
            // Wrapper classes are resolved from the configured settings workspace first,
            // then from the packaged runtime classpath.
            Class<?> wrapperClass = ExternalJavaClassSupport.loadClass(className, customCompositionResourcePath);

            for (Object[] extraArguments : extraArgumentOptions) {
                Object[] constructorArguments = prepend(delegate, extraArguments);
                Constructor<?> constructor = findMatchingConstructor(wrapperClass, constructorArguments);
                if (constructor != null) {
                    return instantiateWrapper(
                            wrapperClass,
                            constructor,
                            expectedType,
                            className,
                            constructorArguments
                    );
                }
            }

            throw new IllegalStateException(
                    "No supported constructor found for custom wrapper: " + className
            );
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to load custom wrapper class: " + className, exception);
        }
    }

    private static String configuredCompositionResource(Settings settings) {
        return settings.get(ConfigTags.CustomCompositionResource, "").trim();
    }

    private static String resolveProfile(Settings settings,
                                         String configuredProfile,
                                         Properties resourceProperties) {
        String resourceProfile = resourceProperties.getProperty(PROPERTY_BASE_PROFILE, "").trim();
        String selectedProfile = resourceProfile.isBlank() ? configuredProfile : resourceProfile;
        if (!CompositionProfiles.isSupported(selectedProfile)) {
            throw new IllegalStateException("Unsupported composition profile: " + selectedProfile);
        }

        return CompositionProfiles.resolve(
                selectedProfile,
                settings.get(ConfigTags.SUTConnector, "")
        );
    }

    private static Optional<String> optionalProperty(Properties properties, String propertyName) {
        return optionalValue(properties.getProperty(propertyName, "").trim());
    }

    private static <T> T instantiateWrapper(Class<?> wrapperClass,
                                            Constructor<?> constructor,
                                            Class<T> expectedType,
                                            String className,
                                            Object[] constructorArguments)
            throws ReflectiveOperationException {
        Object instance = constructor.newInstance(constructorArguments);
        if (!expectedType.isInstance(instance)) {
            throw new IllegalStateException(
                    "Custom wrapper must implement " + expectedType.getName() + ": " + className
            );
        }
        return expectedType.cast(instance);
    }

    private static Properties loadResourceProperties(String customCompositionResource) {
        Properties properties = new Properties();

        if (customCompositionResource == null || customCompositionResource.isBlank()) {
            return properties;
        }

        File resourceFile = SettingsResourceResolver.resolve(customCompositionResource);
        if (!resourceFile.exists()) {
            return properties;
        }

        try (FileInputStream inputStream = new FileInputStream(resourceFile)) {
            // Resource files are optional. When present, they override or extend
            // the baseline composition selected through the profile.
            properties.load(inputStream);
            return properties;
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to read custom composition resource: " + customCompositionResource,
                    exception
            );
        }
    }

    private static Optional<String> optionalValue(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(value);
    }

    private static Object[] prepend(Object delegate, Object[] extraArguments) {
        Object[] constructorArguments = new Object[extraArguments.length + 1];
        constructorArguments[0] = delegate;
        System.arraycopy(extraArguments, 0, constructorArguments, 1, extraArguments.length);
        return constructorArguments;
    }

    private static Constructor<?> findMatchingConstructor(Class<?> wrapperClass, Object[] arguments) {
        for (Constructor<?> constructor : wrapperClass.getConstructors()) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length != arguments.length) {
                continue;
            }

            boolean matches = true;
            for (int i = 0; i < parameterTypes.length; i++) {
                if (!isAssignable(parameterTypes[i], arguments[i])) {
                    matches = false;
                    break;
                }
            }

            if (matches) {
                return constructor;
            }
        }

        return null;
    }

    private static boolean isAssignable(Class<?> parameterType, Object argument) {
        if (argument == null) {
            return !parameterType.isPrimitive();
        }

        Class<?> normalizedParameterType = normalize(parameterType);
        return normalizedParameterType.isAssignableFrom(argument.getClass());
    }

    private static Class<?> normalize(Class<?> parameterType) {
        if (!parameterType.isPrimitive()) {
            return parameterType;
        }

        if (parameterType == boolean.class) {
            return Boolean.class;
        }
        if (parameterType == byte.class) {
            return Byte.class;
        }
        if (parameterType == short.class) {
            return Short.class;
        }
        if (parameterType == int.class) {
            return Integer.class;
        }
        if (parameterType == long.class) {
            return Long.class;
        }
        if (parameterType == float.class) {
            return Float.class;
        }
        if (parameterType == double.class) {
            return Double.class;
        }
        if (parameterType == char.class) {
            return Character.class;
        }

        return parameterType;
    }
}
