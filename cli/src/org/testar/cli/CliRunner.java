/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.cli;

import java.io.PrintStream;

import org.testar.core.Assert;

final class CliRunner {

    private final PrintStream output;
    private final CliDaemonClient client;

    CliRunner(PrintStream output, CliDaemonClient client) {
        this.output = Assert.notNull(output);
        this.client = Assert.notNull(client);
    }

    int run(CliRequest request) {
        switch (request.getCommand()) {
            case DAEMON:
                return 0;
            case START_SESSION:
            case SESSION_STATUS:
            case GET_STATE:
            case GET_DERIVED_ACTIONS:
            case EXECUTE_ACTION:
            case STOP_SESSION:
                return sendToDaemon(request);
            case HELP:
            default:
                printHelp();
                return 0;
        }
    }

    private void printHelp() {
        output.println("TESTAR CLI");
        output.println("Usage:");
        output.println("  startSession windows <path>");
        output.println("  startSession webdriver <url>");
        output.println("  sessionStatus");
        output.println("  getState");
        output.println("  getDerivedActions");
        output.println("  executeAction click <semanticText>");
        output.println("  executeAction type <semanticText> <inputText>");
        output.println("  stopSession");
    }

    private int sendToDaemon(CliRequest request) {
        try {
            CliResponse response = client.send(request);
            for (String line : response.getLines()) {
                output.println(line);
            }
            return response.getExitCode();
        } catch (RuntimeException exception) {
            output.println("daemon failed: " + exception.getMessage());
            return 1;
        }
    }
}
