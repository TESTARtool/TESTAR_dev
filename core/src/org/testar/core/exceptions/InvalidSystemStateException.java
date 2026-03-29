/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.exceptions;

public class InvalidSystemStateException extends FruitException {

    private static final long serialVersionUID = 1554849408074075595L;

    public InvalidSystemStateException(String message) {
        this(message, null);
    }

    public InvalidSystemStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSystemStateException(Throwable cause) {
        super(cause);
    }
}
