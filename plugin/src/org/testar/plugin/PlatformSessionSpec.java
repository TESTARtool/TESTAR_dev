/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin;

import org.testar.core.Assert;

/**
 * Minimal platform session specification used by CLI and future composition
 * layers to request platform services for a concrete startup/attach strategy.
 */
public final class PlatformSessionSpec {

    public enum TargetType {
        EXECUTABLE,
        PROCESS_NAME,
        PROCESS_ID,
        UWP
    }

    private final OperatingSystems operatingSystem;
    private final TargetType targetType;
    private final String target;
    private final boolean processListenerEnabled;
    private final String sutProcesses;
    private final double stateTimeoutSeconds;
    private final boolean accessBridgeEnabled;

    private PlatformSessionSpec(Builder builder) {
        this.operatingSystem = Assert.notNull(builder.operatingSystem);
        this.targetType = Assert.notNull(builder.targetType);
        this.target = Assert.notNull(builder.target);
        this.processListenerEnabled = builder.processListenerEnabled;
        this.sutProcesses = builder.sutProcesses;
        this.stateTimeoutSeconds = builder.stateTimeoutSeconds;
        this.accessBridgeEnabled = builder.accessBridgeEnabled;
    }

    public OperatingSystems getOperatingSystem() {
        return operatingSystem;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public String getTarget() {
        return target;
    }

    public boolean isProcessListenerEnabled() {
        return processListenerEnabled;
    }

    public String getSutProcesses() {
        return sutProcesses;
    }

    public double getStateTimeoutSeconds() {
        return stateTimeoutSeconds;
    }

    public boolean isAccessBridgeEnabled() {
        return accessBridgeEnabled;
    }

    public static Builder builder(OperatingSystems operatingSystem, TargetType targetType, String target) {
        return new Builder(operatingSystem, targetType, target);
    }

    public static final class Builder {

        private final OperatingSystems operatingSystem;
        private final TargetType targetType;
        private final String target;
        private boolean processListenerEnabled;
        private String sutProcesses = "";
        private double stateTimeoutSeconds = 10.0;
        private boolean accessBridgeEnabled;

        private Builder(OperatingSystems operatingSystem, TargetType targetType, String target) {
            this.operatingSystem = Assert.notNull(operatingSystem);
            this.targetType = Assert.notNull(targetType);
            this.target = Assert.notNull(target);
        }

        public Builder withProcessListenerEnabled(boolean processListenerEnabled) {
            this.processListenerEnabled = processListenerEnabled;
            return this;
        }

        public Builder withSutProcesses(String sutProcesses) {
            this.sutProcesses = sutProcesses == null ? "" : sutProcesses;
            return this;
        }

        public Builder withStateTimeoutSeconds(double stateTimeoutSeconds) {
            this.stateTimeoutSeconds = stateTimeoutSeconds;
            return this;
        }

        public Builder withAccessBridgeEnabled(boolean accessBridgeEnabled) {
            this.accessBridgeEnabled = accessBridgeEnabled;
            return this;
        }

        public PlatformSessionSpec build() {
            return new PlatformSessionSpec(this);
        }
    }
}
