/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.cli;

import org.testar.core.Assert;
import org.testar.plugin.OperatingSystems;

final class CliSessionTarget {

    private final OperatingSystems operatingSystem;
    private final String target;

    CliSessionTarget(OperatingSystems operatingSystem, String target) {
        this.operatingSystem = Assert.notNull(operatingSystem);
        this.target = Assert.notNull(target);
    }

    OperatingSystems operatingSystem() {
        return operatingSystem;
    }

    String target() {
        return target;
    }
}
