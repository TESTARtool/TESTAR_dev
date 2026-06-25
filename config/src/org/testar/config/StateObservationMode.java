/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.config;

/**
 * Consumer-facing state views used by CLI/agents.
 * These modes do not change the canonical captured TESTAR state.
 */
public enum StateObservationMode {
    FULL_STATE,
    LEAF_WIDGETS,
    SEMANTIC_WIDGETS,
    INTERACTIVE_WIDGETS,
    INTERACTIVE_SEMANTIC_WIDGETS,
    ACTIONABLE_WIDGETS,
    ACTIONABLE_SEMANTIC_WIDGETS,
    TEXTUAL_CONTEXT
}
