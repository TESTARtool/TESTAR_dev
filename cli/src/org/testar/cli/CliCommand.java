/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.cli;

enum CliCommand {
    HELP,
    DAEMON,
    START_SESSION,
    SESSION_STATUS,
    GET_STATE,
    GET_DERIVED_ACTIONS,
    EXECUTE_ACTION,
    STOP_SESSION;

    static CliCommand fromToken(String token) {
        if (token == null || token.isEmpty()) {
            return HELP;
        }
        if ("daemon".equalsIgnoreCase(token)) {
            return DAEMON;
        }
        if ("startSession".equalsIgnoreCase(token)) {
            return START_SESSION;
        }
        if ("sessionStatus".equalsIgnoreCase(token)) {
            return SESSION_STATUS;
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
        if ("stopSession".equalsIgnoreCase(token)) {
            return STOP_SESSION;
        }
        return HELP;
    }
}
