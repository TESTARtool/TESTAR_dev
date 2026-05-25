/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.composition;

import java.util.Optional;

import org.testar.core.Assert;

public final class ScriptlessCompositionDescriptor {

    private final String compositionProfile;
    private final Optional<String> customCompositionResource;

    private final Optional<String> settingsCapabilityClass;
    private final Optional<String> testSessionCapabilityClass;
    private final Optional<String> testSequenceCapabilityClass;
    private final Optional<String> stopCriteriaCapabilityClass;

    private final Optional<String> systemServiceClass;
    private final Optional<String> stateServiceClass;
    private final Optional<String> stateIdentifierServiceClass;
    private final Optional<String> actionDerivationServiceClass;
    private final Optional<String> actionIdentifierServiceClass;
    private final Optional<String> actionSelectorServiceClass;
    private final Optional<String> actionExecutionServiceClass;
    private final Optional<String> oracleComposerClass;

    public ScriptlessCompositionDescriptor(String compositionProfile,
                                          Optional<String> customCompositionResource,
                                          Optional<String> settingsCapabilityClass,
                                          Optional<String> testSessionCapabilityClass,
                                          Optional<String> testSequenceCapabilityClass,
                                          Optional<String> stopCriteriaCapabilityClass,
                                          Optional<String> systemServiceClass,
                                          Optional<String> stateServiceClass,
                                          Optional<String> stateIdentifierServiceClass,
                                          Optional<String> actionDerivationServiceClass,
                                          Optional<String> actionIdentifierServiceClass,
                                          Optional<String> actionSelectorServiceClass,
                                          Optional<String> actionExecutionServiceClass,
                                          Optional<String> oracleComposerClass) {
        this.compositionProfile = Assert.notNull(compositionProfile);
        this.customCompositionResource = Assert.notNull(customCompositionResource);
        this.settingsCapabilityClass = Assert.notNull(settingsCapabilityClass);
        this.testSessionCapabilityClass = Assert.notNull(testSessionCapabilityClass);
        this.testSequenceCapabilityClass = Assert.notNull(testSequenceCapabilityClass);
        this.stopCriteriaCapabilityClass = Assert.notNull(stopCriteriaCapabilityClass);
        this.systemServiceClass = Assert.notNull(systemServiceClass);
        this.stateServiceClass = Assert.notNull(stateServiceClass);
        this.stateIdentifierServiceClass = Assert.notNull(stateIdentifierServiceClass);
        this.actionDerivationServiceClass = Assert.notNull(actionDerivationServiceClass);
        this.actionIdentifierServiceClass = Assert.notNull(actionIdentifierServiceClass);
        this.actionSelectorServiceClass = Assert.notNull(actionSelectorServiceClass);
        this.actionExecutionServiceClass = Assert.notNull(actionExecutionServiceClass);
        this.oracleComposerClass = Assert.notNull(oracleComposerClass);
    }

    public String compositionProfile() {
        return compositionProfile;
    }

    public Optional<String> customCompositionResource() {
        return customCompositionResource;
    }

    public Optional<String> settingsCapabilityClass() {
        return settingsCapabilityClass;
    }

    public Optional<String> testSessionCapabilityClass() {
        return testSessionCapabilityClass;
    }

    public Optional<String> testSequenceCapabilityClass() {
        return testSequenceCapabilityClass;
    }

    public Optional<String> stopCriteriaCapabilityClass() {
        return stopCriteriaCapabilityClass;
    }

    public Optional<String> systemServiceClass() {
        return systemServiceClass;
    }

    public Optional<String> stateServiceClass() {
        return stateServiceClass;
    }

    public Optional<String> stateIdentifierServiceClass() {
        return stateIdentifierServiceClass;
    }

    public Optional<String> actionDerivationServiceClass() {
        return actionDerivationServiceClass;
    }

    public Optional<String> actionIdentifierServiceClass() {
        return actionIdentifierServiceClass;
    }

    public Optional<String> actionSelectorServiceClass() {
        return actionSelectorServiceClass;
    }

    public Optional<String> actionExecutionServiceClass() {
        return actionExecutionServiceClass;
    }

    public Optional<String> oracleComposerClass() {
        return oracleComposerClass;
    }
}
