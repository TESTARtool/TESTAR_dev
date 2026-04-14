/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.capability;

import org.testar.core.Assert;

/**
 * Configurable set of scriptless capabilities.
 */
public final class ScriptlessCapabilities {

    private final SequenceLifecycleCapability sequenceLifecycleCapability;
    private final StateVerdictCapability stateVerdictCapability;
    private final StopCriteriaCapability stopCriteriaCapability;

    private ScriptlessCapabilities(Builder builder) {
        this.sequenceLifecycleCapability = Assert.notNull(builder.sequenceLifecycleCapability);
        this.stateVerdictCapability = Assert.notNull(builder.stateVerdictCapability);
        this.stopCriteriaCapability = Assert.notNull(builder.stopCriteriaCapability);
    }

    public static ScriptlessCapabilities defaults() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public SequenceLifecycleCapability sequenceLifecycleCapability() {
        return sequenceLifecycleCapability;
    }

    public StateVerdictCapability stateVerdictCapability() {
        return stateVerdictCapability;
    }

    public StopCriteriaCapability stopCriteriaCapability() {
        return stopCriteriaCapability;
    }

    public static final class Builder {

        private SequenceLifecycleCapability sequenceLifecycleCapability = new SequenceLifecycleCapability();
        private StateVerdictCapability stateVerdictCapability = new StateVerdictCapability();
        private StopCriteriaCapability stopCriteriaCapability = new StopCriteriaCapability();

        public Builder withSequenceLifecycleCapability(SequenceLifecycleCapability sequenceLifecycleCapability) {
            this.sequenceLifecycleCapability = sequenceLifecycleCapability;
            return this;
        }

        public Builder withStateVerdictCapability(StateVerdictCapability stateVerdictCapability) {
            this.stateVerdictCapability = stateVerdictCapability;
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
