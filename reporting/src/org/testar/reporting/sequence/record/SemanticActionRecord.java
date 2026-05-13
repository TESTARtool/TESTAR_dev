/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.reporting.sequence.record;

public final class SemanticActionRecord {

    private final String role;
    private final String description;
    private final String input;

    public SemanticActionRecord(String role, String description, String input) {
        this.role = valueOrEmpty(role);
        this.description = valueOrEmpty(description);
        this.input = valueOrEmpty(input);
    }

    public String getRole() {
        return role;
    }

    public String getDescription() {
        return description;
    }

    public String getInput() {
        return input;
    }

    private static String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
