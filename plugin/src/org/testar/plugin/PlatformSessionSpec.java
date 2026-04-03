/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.plugin;

import org.testar.config.settings.Settings;
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
    private final Settings settings;

    private PlatformSessionSpec(Builder builder) {
        this.operatingSystem = Assert.notNull(builder.operatingSystem);
        this.targetType = Assert.notNull(builder.targetType);
        this.target = Assert.notNull(builder.target);
        this.settings = Assert.notNull(builder.settings);
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

    public Settings getSettings() {
        return settings;
    }

    public static Builder builder(OperatingSystems operatingSystem, TargetType targetType, String target, Settings settings) {
        return new Builder(operatingSystem, targetType, target, settings);
    }

    public static final class Builder {

        private final OperatingSystems operatingSystem;
        private final TargetType targetType;
        private final String target;
        private final Settings settings;

        private Builder(OperatingSystems operatingSystem, TargetType targetType, String target, Settings settings) {
            this.operatingSystem = Assert.notNull(operatingSystem);
            this.targetType = Assert.notNull(targetType);
            this.target = Assert.notNull(target);
            this.settings = Assert.notNull(settings);
        }

        public PlatformSessionSpec build() {
            return new PlatformSessionSpec(this);
        }
    }
}
