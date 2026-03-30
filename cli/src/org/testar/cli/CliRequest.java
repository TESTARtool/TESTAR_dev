/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.cli;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.testar.core.Assert;

final class CliRequest {

    private final CliCommand command;
    private final List<String> arguments;

    private CliRequest(CliCommand command, List<String> arguments) {
        this.command = Assert.notNull(command);
        this.arguments = Collections.unmodifiableList(Assert.notNull(arguments));
    }

    static CliRequest parse(String[] args) {
        if (args == null || args.length == 0) {
            return new CliRequest(CliCommand.HELP, Collections.emptyList());
        }
        CliCommand command = CliCommand.fromToken(args[0]);
        List<String> arguments = args.length > 1
                ? Arrays.asList(Arrays.copyOfRange(args, 1, args.length))
                : Collections.emptyList();
        return new CliRequest(command, arguments);
    }

    CliCommand getCommand() {
        return command;
    }

    List<String> getArguments() {
        return arguments;
    }

    String argumentAt(int index) {
        return index >= 0 && index < arguments.size() ? arguments.get(index) : null;
    }
}
