/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.exceptions;

public class StateBuildException extends FruitException {

    private static final long serialVersionUID = 5741711528841953961L;

    public StateBuildException(String message) {
        super(message);
    }

    public StateBuildException(Throwable cause) {
        super(cause);
    }
}
