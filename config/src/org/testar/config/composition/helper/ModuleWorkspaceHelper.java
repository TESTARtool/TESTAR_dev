/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.config.composition.helper;

import org.testar.config.CompositionProfiles;
import org.testar.config.ConfigTags;
import org.testar.config.TestarDirectories;
import org.testar.config.settings.Settings;
import org.testar.core.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class ModuleWorkspaceHelper {

    public static final ModuleDefinition[] MODULE_DEFINITIONS = new ModuleDefinition[]{
            new ModuleDefinition("settingsCapabilityClass", "Settings capability", "SettingsCapability"),
            new ModuleDefinition("testSessionCapabilityClass", "Test session capability", "TestSessionCapability"),
            new ModuleDefinition("testSequenceCapabilityClass", "Test sequence capability", "TestSequenceCapability"),
            new ModuleDefinition("stopCriteriaCapabilityClass", "Stop criteria capability", "StopCriteriaCapability"),
            new ModuleDefinition("systemServiceClass", "System service", "SystemService"),
            new ModuleDefinition("stateServiceClass", "State service", "StateService"),
            new ModuleDefinition("stateIdentifierServiceClass", "State identifier service", "StateIdentifierService"),
            new ModuleDefinition("actionDerivationServiceClass", "Action derivation service", "ActionDerivationService"),
            new ModuleDefinition("actionIdentifierServiceClass", "Action identifier service", "ActionIdentifierService"),
            new ModuleDefinition("actionSelectorServiceClass", "Action selector service", "ActionSelectorService"),
            new ModuleDefinition("actionExecutionServiceClass", "Action execution service", "ActionExecutionService"),
            new ModuleDefinition("oracleComposerClass", "Oracle composer", "OracleComposer")
    };

    private ModuleWorkspaceHelper() {
    }

    public static Properties loadCompositionProperties(String resourcePath) {
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
            throw new IllegalStateException("Unable to read composition resource: " + resourcePath, exception);
        }
    }

    public static void saveCompositionProperties(String resourcePath, Properties properties, Settings settings) {
        StringBuilder builder = new StringBuilder();
        builder.append("# Scriptless custom composition resource").append(System.lineSeparator());
        builder.append("# Select one of: windows_composition, webdriver_composition, android_composition").append(System.lineSeparator());
        builder.append("baseProfile=").append(resolveBaseProfile(properties, settings)).append(System.lineSeparator());
        builder.append(System.lineSeparator());
        builder.append("# Optional capability wrappers.").append(System.lineSeparator());
        builder.append("settingsCapabilityClass=").append(properties.getProperty("settingsCapabilityClass", "")).append(System.lineSeparator());
        builder.append("testSessionCapabilityClass=").append(properties.getProperty("testSessionCapabilityClass", "")).append(System.lineSeparator());
        builder.append("testSequenceCapabilityClass=").append(properties.getProperty("testSequenceCapabilityClass", "")).append(System.lineSeparator());
        builder.append("stopCriteriaCapabilityClass=").append(properties.getProperty("stopCriteriaCapabilityClass", "")).append(System.lineSeparator());
        builder.append(System.lineSeparator());
        builder.append("# Optional service wrappers.").append(System.lineSeparator());
        builder.append("systemServiceClass=").append(properties.getProperty("systemServiceClass", "")).append(System.lineSeparator());
        builder.append("stateServiceClass=").append(properties.getProperty("stateServiceClass", "")).append(System.lineSeparator());
        builder.append("stateIdentifierServiceClass=").append(properties.getProperty("stateIdentifierServiceClass", "")).append(System.lineSeparator());
        builder.append("actionDerivationServiceClass=").append(properties.getProperty("actionDerivationServiceClass", "")).append(System.lineSeparator());
        builder.append("actionIdentifierServiceClass=").append(properties.getProperty("actionIdentifierServiceClass", "")).append(System.lineSeparator());
        builder.append("actionSelectorServiceClass=").append(properties.getProperty("actionSelectorServiceClass", "")).append(System.lineSeparator());
        builder.append("actionExecutionServiceClass=").append(properties.getProperty("actionExecutionServiceClass", "")).append(System.lineSeparator());
        builder.append("oracleComposerClass=").append(properties.getProperty("oracleComposerClass", "")).append(System.lineSeparator());

        try {
            Util.saveToFile(builder.toString(), resourcePath);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to save composition resource: " + resourcePath, exception);
        }
    }

    public static String defaultCustomCompositionResource(Settings settings) {
        String selectedSse = TestarDirectories.getSelectedSse();
        if (selectedSse == null || selectedSse.isBlank()) {
            selectedSse = "sut";
        }

        String compositionProfile = CompositionProfiles.resolve(
                settings.get(ConfigTags.CompositionProfile, CompositionProfiles.WINDOWS_COMPOSITION),
                settings.get(ConfigTags.SUTConnector, "")
        );

        String folderName = compositionProfile + "_" + selectedSse;
        return TestarDirectories.getSettingsDir()
                + folderName
                + File.separator
                + "composition.properties";
    }

    public static String ensureCustomCompositionResourcePath(String resourcePath, Settings settings) {
        if (resourcePath == null || resourcePath.isBlank()) {
            return defaultCustomCompositionResource(settings);
        }
        return resourcePath.trim();
    }

    public static String buildDefaultClassName(String resourcePath, ModuleDefinition moduleDefinition) {
        File resourceFile = new File(resourcePath);
        File parentDirectory = resourceFile.getParentFile();
        String folderName = parentDirectory == null ? "custom_module" : parentDirectory.getName();
        return toPascalCase(folderName) + moduleDefinition.classSuffix;
    }

    public static File ensureModuleSourceFile(String resourcePath,
                                              String className,
                                              ModuleDefinition moduleDefinition) {
        File resourceFile = new File(resourcePath);
        File baseDirectory = resourceFile.getParentFile();
        if (baseDirectory == null) {
            throw new IllegalStateException("Unable to resolve composition directory from: " + resourcePath);
        }

        File sourceFile = resolveSourceFile(baseDirectory, className);
        File parentDirectory = sourceFile.getParentFile();
        if (parentDirectory != null && !parentDirectory.exists()) {
            parentDirectory.mkdirs();
        }

        if (!sourceFile.exists()) {
            try {
                Util.saveToFile(defaultModuleTemplate(className, moduleDefinition), sourceFile.getAbsolutePath());
            } catch (IOException exception) {
                throw new IllegalStateException("Unable to create module source file: " + sourceFile, exception);
            }
        }

        return sourceFile;
    }

    public static String defaultModuleTemplate(String className, ModuleDefinition moduleDefinition) {
        String simpleClassName = className;
        String packageName = "";
        int separatorIndex = className.lastIndexOf('.');
        if (separatorIndex >= 0) {
            packageName = className.substring(0, separatorIndex);
            simpleClassName = className.substring(separatorIndex + 1);
        }

        StringBuilder builder = new StringBuilder();
        if (!packageName.isEmpty()) {
            builder.append("package ").append(packageName).append(";").append(System.lineSeparator()).append(System.lineSeparator());
        }

        builder.append(moduleDefinition.template(simpleClassName));
        return builder.toString();
    }

    private static File resolveSourceFile(File baseDirectory, String className) {
        String relativePath = className.replace('.', File.separatorChar) + ".java";
        return new File(baseDirectory, relativePath);
    }

    private static String resolveBaseProfile(Properties properties, Settings settings) {
        String baseProfile = properties.getProperty("baseProfile", "").trim();
        if (!baseProfile.isEmpty()) {
            return baseProfile;
        }

        return CompositionProfiles.resolve(
                settings.get(ConfigTags.CompositionProfile, CompositionProfiles.WINDOWS_COMPOSITION),
                settings.get(ConfigTags.SUTConnector, "")
        );
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
        return builder.length() == 0 ? "CustomComposition" : builder.toString();
    }

    public static final class ModuleDefinition {

        public final String propertyKey;
        public final String label;
        public final String classSuffix;

        public ModuleDefinition(String propertyKey, String label, String classSuffix) {
            this.propertyKey = propertyKey;
            this.label = label;
            this.classSuffix = classSuffix;
        }

        public String template(String simpleClassName) {
            switch (propertyKey) {
                case "settingsCapabilityClass":
                    return String.join(System.lineSeparator(),
                            "import org.testar.config.settings.Settings;",
                            "import org.testar.scriptless.capability.SettingsCapability;",
                            "",
                            "public final class " + simpleClassName + " extends SettingsCapability {",
                            "",
                            "    private final SettingsCapability delegate;",
                            "",
                            "    public " + simpleClassName + "(SettingsCapability delegate) {",
                            "        this.delegate = delegate;",
                            "    }",
                            "",
                            "    @Override",
                            "    public Settings initializeSettings(Settings settings) {",
                            "        return delegate.initializeSettings(settings);",
                            "    }",
                            "}",
                            ""
                    );
                case "testSessionCapabilityClass":
                    return String.join(System.lineSeparator(),
                            "import org.testar.scriptless.RuntimeContext;",
                            "import org.testar.scriptless.capability.TestSessionCapability;",
                            "",
                            "public final class " + simpleClassName + " extends TestSessionCapability {",
                            "",
                            "    private final TestSessionCapability delegate;",
                            "",
                            "    public " + simpleClassName + "(TestSessionCapability delegate) {",
                            "        this.delegate = delegate;",
                            "    }",
                            "",
                            "    @Override",
                            "    public void initializeTestSession(RuntimeContext runtimeContext) {",
                            "        delegate.initializeTestSession(runtimeContext);",
                            "    }",
                            "",
                            "    @Override",
                            "    public void closeTestSession(RuntimeContext runtimeContext) {",
                            "        delegate.closeTestSession(runtimeContext);",
                            "    }",
                            "}",
                            ""
                    );
                case "testSequenceCapabilityClass":
                    return String.join(System.lineSeparator(),
                            "import java.util.List;",
                            "",
                            "import org.testar.core.state.SUT;",
                            "import org.testar.core.state.State;",
                            "import org.testar.core.verdict.Verdict;",
                            "import org.testar.scriptless.RuntimeContext;",
                            "import org.testar.scriptless.capability.TestSequenceCapability;",
                            "",
                            "public final class " + simpleClassName + " extends TestSequenceCapability {",
                            "",
                            "    private final TestSequenceCapability delegate;",
                            "",
                            "    public " + simpleClassName + "(TestSequenceCapability delegate) {",
                            "        this.delegate = delegate;",
                            "    }",
                            "",
                            "    @Override",
                            "    public void startTestSequence(RuntimeContext runtimeContext) {",
                            "        delegate.startTestSequence(runtimeContext);",
                            "    }",
                            "",
                            "    @Override",
                            "    public void beginSequence(RuntimeContext runtimeContext, SUT system, State initialState) {",
                            "        delegate.beginSequence(runtimeContext, system, initialState);",
                            "    }",
                            "",
                            "    @Override",
                            "    public void finishTestSequence(RuntimeContext runtimeContext, List<Verdict> verdicts) {",
                            "        delegate.finishTestSequence(runtimeContext, verdicts);",
                            "    }",
                            "}",
                            ""
                    );
                case "stopCriteriaCapabilityClass":
                    return String.join(System.lineSeparator(),
                            "import org.testar.core.state.State;",
                            "import org.testar.scriptless.RuntimeContext;",
                            "import org.testar.scriptless.capability.StopCriteriaCapability;",
                            "",
                            "public final class " + simpleClassName + " extends StopCriteriaCapability {",
                            "",
                            "    private final StopCriteriaCapability delegate;",
                            "",
                            "    public " + simpleClassName + "(StopCriteriaCapability delegate) {",
                            "        this.delegate = delegate;",
                            "    }",
                            "",
                            "    @Override",
                            "    public boolean stopTestSequence(RuntimeContext runtimeContext, State state) {",
                            "        return delegate.stopTestSequence(runtimeContext, state);",
                            "    }",
                            "",
                            "    @Override",
                            "    public boolean stopTestSession(RuntimeContext runtimeContext) {",
                            "        return delegate.stopTestSession(runtimeContext);",
                            "    }",
                            "}",
                            ""
                    );
                case "systemServiceClass":
                    return String.join(System.lineSeparator(),
                            "import org.testar.core.exceptions.SystemStartException;",
                            "import org.testar.core.service.SystemService;",
                            "import org.testar.core.state.SUT;",
                            "",
                            "public final class " + simpleClassName + " implements SystemService {",
                            "",
                            "    private final SystemService delegate;",
                            "",
                            "    public " + simpleClassName + "(SystemService delegate) {",
                            "        this.delegate = delegate;",
                            "    }",
                            "",
                            "    @Override",
                            "    public SUT startSystem() throws SystemStartException {",
                            "        return delegate.startSystem();",
                            "    }",
                            "",
                            "    @Override",
                            "    public void stopSystem(SUT system) {",
                            "        delegate.stopSystem(system);",
                            "    }",
                            "}",
                            ""
                    );
                case "stateServiceClass":
                    return String.join(System.lineSeparator(),
                            "import org.testar.core.exceptions.StateBuildException;",
                            "import org.testar.core.service.StateService;",
                            "import org.testar.core.state.SUT;",
                            "import org.testar.core.state.State;",
                            "",
                            "public final class " + simpleClassName + " implements StateService {",
                            "",
                            "    private final StateService delegate;",
                            "",
                            "    public " + simpleClassName + "(StateService delegate) {",
                            "        this.delegate = delegate;",
                            "    }",
                            "",
                            "    @Override",
                            "    public State getState(SUT system) throws StateBuildException {",
                            "        return delegate.getState(system);",
                            "    }",
                            "}",
                            ""
                    );
                case "stateIdentifierServiceClass":
                    return String.join(System.lineSeparator(),
                            "import org.testar.core.service.StateIdentifierService;",
                            "import org.testar.core.state.State;",
                            "",
                            "public final class " + simpleClassName + " implements StateIdentifierService {",
                            "",
                            "    private final StateIdentifierService delegate;",
                            "",
                            "    public " + simpleClassName + "(StateIdentifierService delegate) {",
                            "        this.delegate = delegate;",
                            "    }",
                            "",
                            "    @Override",
                            "    public State identifyState(State state) {",
                            "        return delegate.identifyState(state);",
                            "    }",
                            "}",
                            ""
                    );
                case "actionDerivationServiceClass":
                    return String.join(System.lineSeparator(),
                            "import java.util.Set;",
                            "",
                            "import org.testar.core.action.Action;",
                            "import org.testar.core.service.ActionDerivationService;",
                            "import org.testar.core.state.SUT;",
                            "import org.testar.core.state.State;",
                            "",
                            "public final class " + simpleClassName + " implements ActionDerivationService {",
                            "",
                            "    private final ActionDerivationService delegate;",
                            "",
                            "    public " + simpleClassName + "(ActionDerivationService delegate) {",
                            "        this.delegate = delegate;",
                            "    }",
                            "",
                            "    @Override",
                            "    public Set<Action> deriveActions(SUT system, State state) {",
                            "        return delegate.deriveActions(system, state);",
                            "    }",
                            "}",
                            ""
                    );
                case "actionIdentifierServiceClass":
                    return String.join(System.lineSeparator(),
                            "import java.util.Set;",
                            "",
                            "import org.testar.core.action.Action;",
                            "import org.testar.core.service.ActionIdentifierService;",
                            "import org.testar.core.state.State;",
                            "",
                            "public final class " + simpleClassName + " implements ActionIdentifierService {",
                            "",
                            "    private final ActionIdentifierService delegate;",
                            "",
                            "    public " + simpleClassName + "(ActionIdentifierService delegate) {",
                            "        this.delegate = delegate;",
                            "    }",
                            "",
                            "    @Override",
                            "    public Set<Action> identifyActions(State state, Set<Action> actions) {",
                            "        return delegate.identifyActions(state, actions);",
                            "    }",
                            "",
                            "    @Override",
                            "    public Action identifyEnvironmentAction(State state, Action action) {",
                            "        return delegate.identifyEnvironmentAction(state, action);",
                            "    }",
                            "}",
                            ""
                    );
                case "actionSelectorServiceClass":
                    return String.join(System.lineSeparator(),
                            "import java.util.Set;",
                            "",
                            "import org.testar.core.action.Action;",
                            "import org.testar.core.service.ActionSelectorService;",
                            "import org.testar.core.state.State;",
                            "",
                            "public final class " + simpleClassName + " implements ActionSelectorService {",
                            "",
                            "    private final ActionSelectorService delegate;",
                            "",
                            "    public " + simpleClassName + "(ActionSelectorService delegate) {",
                            "        this.delegate = delegate;",
                            "    }",
                            "",
                            "    @Override",
                            "    public Action selectAction(State state, Set<Action> actions) {",
                            "        return delegate.selectAction(state, actions);",
                            "    }",
                            "}",
                            ""
                    );
                case "actionExecutionServiceClass":
                    return String.join(System.lineSeparator(),
                            "import org.testar.core.action.Action;",
                            "import org.testar.core.service.ActionExecutionService;",
                            "import org.testar.core.state.SUT;",
                            "import org.testar.core.state.State;",
                            "",
                            "public final class " + simpleClassName + " implements ActionExecutionService {",
                            "",
                            "    private final ActionExecutionService delegate;",
                            "",
                            "    public " + simpleClassName + "(ActionExecutionService delegate) {",
                            "        this.delegate = delegate;",
                            "    }",
                            "",
                            "    @Override",
                            "    public boolean executeAction(SUT system, State state, Action action) {",
                            "        return delegate.executeAction(system, state, action);",
                            "    }",
                            "}",
                            ""
                    );
                case "oracleComposerClass":
                    return String.join(System.lineSeparator(),
                            "import java.util.List;",
                            "",
                            "import org.testar.core.state.SUT;",
                            "import org.testar.core.state.State;",
                            "import org.testar.core.verdict.Verdict;",
                            "import org.testar.scriptless.RuntimeContext;",
                            "import org.testar.scriptless.service.ScriptlessOracleComposer;",
                            "",
                            "public final class " + simpleClassName + " extends ScriptlessOracleComposer {",
                            "",
                            "    private final ScriptlessOracleComposer delegate;",
                            "",
                            "    public " + simpleClassName + "(ScriptlessOracleComposer delegate) {",
                            "        this.delegate = delegate;",
                            "    }",
                            "",
                            "    @Override",
                            "    public List<Verdict> composeVerdicts(RuntimeContext runtimeContext,",
                            "                                         SUT system,",
                            "                                         State state,",
                            "                                         List<Verdict> verdicts) {",
                            "        return delegate.composeVerdicts(runtimeContext, system, state, verdicts);",
                            "    }",
                            "}",
                            ""
                    );
                default:
                    throw new IllegalStateException("Unsupported module definition: " + propertyKey);
            }
        }
    }
}
