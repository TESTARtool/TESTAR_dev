/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.oracle;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Tracks whether an oracle was actually applied during execution.
 */
public interface OracleApplicationStatus {

    final class ApplicationStatusRegistry {

        private static final Map<OracleApplicationStatus, Boolean> statuses = Collections.synchronizedMap(new WeakHashMap<>());

        private ApplicationStatusRegistry() {
        }
    }

    default boolean wasApplied() {
        return Boolean.TRUE.equals(ApplicationStatusRegistry.statuses.get(this));
    }

    default boolean isVacuousPass() {
        return !wasApplied();
    }

    default void markAsNonVacuous() {
        ApplicationStatusRegistry.statuses.put(this, Boolean.TRUE);
    }

    default void resetApplicationStatus() {
        ApplicationStatusRegistry.statuses.remove(this);
    }
}
