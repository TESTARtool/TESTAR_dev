/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.cli;

public final class CliMain {

    private CliMain() {
    }

    public static void main(String[] args) {
        CliRequest request = CliRequest.parse(args);
        CliRunner runner = new CliRunner(System.out);
        int exitCode = runner.run(request);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }
}
