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
    STOP_SESSION;

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
        if ("stopSession".equalsIgnoreCase(token)) {
            return STOP_SESSION;
        }
        return HELP;
    }
}
