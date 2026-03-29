/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.exceptions;

public class FruitException extends RuntimeException {

    private static final long serialVersionUID = 8410136592237418299L;

    public FruitException(String message) {
        this(message, null);
    }

    public FruitException(String message, Throwable cause) {
        super(message, cause);
    }

    public FruitException(Throwable cause) {
        super(cause);
    }
}
