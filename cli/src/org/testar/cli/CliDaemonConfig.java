/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.cli;

final class CliDaemonConfig {

    static final String HOST = "127.0.0.1";
    static final int PORT = 47327;
    static final int START_TIMEOUT_MS = 15000;
    static final int CONNECT_TIMEOUT_MS = 1000;

    private CliDaemonConfig() {
    }
}
