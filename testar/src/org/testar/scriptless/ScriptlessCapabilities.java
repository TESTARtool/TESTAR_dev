/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless;

import org.testar.core.Assert;
import org.testar.scriptless.capability.SettingsCapability;
import org.testar.scriptless.capability.StopCriteriaCapability;
import org.testar.scriptless.capability.TestSequenceCapability;
import org.testar.scriptless.capability.TestSessionCapability;
import org.testar.scriptless.service.ScriptlessOracleComposer;

public final class ScriptlessCapabilities {

    private final SettingsCapability settingsCapability;
    private final TestSessionCapability testSessionCapability;
    private final TestSequenceCapability testSequenceCapability;
    private final ScriptlessOracleComposer scriptlessOracleComposer;
    private final StopCriteriaCapability stopCriteriaCapability;

    private ScriptlessCapabilities(Builder builder) {
        this.settingsCapability = Assert.notNull(builder.settingsCapability); 
        this.testSessionCapability = Assert.notNull(builder.testSessionCapability);
        this.testSequenceCapability = Assert.notNull(builder.testSequenceCapability);
        this.scriptlessOracleComposer = Assert.notNull(builder.scriptlessOracleComposer);
        this.stopCriteriaCapability = Assert.notNull(builder.stopCriteriaCapability);
    }

    public static ScriptlessCapabilities defaults() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public SettingsCapability settingsCapability() {
        return settingsCapability;
    }

    public TestSessionCapability testSessionCapability() {
        return testSessionCapability;
    }

    public TestSequenceCapability testSequenceCapability() {
        return testSequenceCapability;
    }

    public ScriptlessOracleComposer scriptlessOracleComposer() {
        return scriptlessOracleComposer;
    }

    public StopCriteriaCapability stopCriteriaCapability() {
        return stopCriteriaCapability;
    }

    public static final class Builder {

        private SettingsCapability settingsCapability = new SettingsCapability();
        private TestSessionCapability testSessionCapability = new TestSessionCapability();
        private TestSequenceCapability testSequenceCapability = new TestSequenceCapability();
        private ScriptlessOracleComposer scriptlessOracleComposer = new ScriptlessOracleComposer();
        private StopCriteriaCapability stopCriteriaCapability = new StopCriteriaCapability();

        public Builder withSettingsCapability(SettingsCapability settingsCapability) {
            this.settingsCapability = settingsCapability;
            return this;
        }

        public Builder withTestSessionCapability(TestSessionCapability testSessionCapability) {
            this.testSessionCapability = testSessionCapability;
            return this;
        }

        public Builder withTestSequenceCapability(TestSequenceCapability testSequenceCapability) {
            this.testSequenceCapability = testSequenceCapability;
            return this;
        }

        public Builder withScriptlessOracleComposer(ScriptlessOracleComposer scriptlessOracleComposer) {
            this.scriptlessOracleComposer = scriptlessOracleComposer;
            return this;
        }

        public Builder withStopCriteriaCapability(StopCriteriaCapability stopCriteriaCapability) {
            this.stopCriteriaCapability = stopCriteriaCapability;
            return this;
        }

        public ScriptlessCapabilities build() {
            return new ScriptlessCapabilities(this);
        }
    }
}
