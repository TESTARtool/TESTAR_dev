/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.cli;

enum CliCommand {
    HELP,
    START_SESSION,
    GET_STATE,
    GET_DERIVED_ACTIONS,
    EXECUTE_ACTION,
    STOP_SYSTEM;

    static CliCommand fromToken(String token) {
        if (token == null || token.isEmpty()) {
            return HELP;
        }
        if ("startSession".equalsIgnoreCase(token)) {
            return START_SESSION;
        }
        if ("getState".equalsIgnoreCase(token)) {
            return GET_STATE;
        }
        if ("getDerivedActions".equalsIgnoreCase(token)) {
            return GET_DERIVED_ACTIONS;
        }
        if ("executeAction".equalsIgnoreCase(token)) {
            return EXECUTE_ACTION;
        }
        if ("stopSession".equalsIgnoreCase(token) || "stopSystem".equalsIgnoreCase(token)) {
            return STOP_SYSTEM;
        }
        return HELP;
    }
}
