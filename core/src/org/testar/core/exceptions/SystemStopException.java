/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.exceptions;

public class SystemStopException extends FruitException {

    private static final long serialVersionUID = -4938584367513438864L;

    public SystemStopException(String message) {
        this(message, null);
    }

    public SystemStopException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemStopException(Throwable cause) {
        super(cause);
    }
}
