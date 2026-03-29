/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.exceptions;

public class SystemStartException extends FruitException {

    private static final long serialVersionUID = 1554849408074075595L;

    public SystemStartException(String message) {
        this(message, null);
    }

    public SystemStartException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemStartException(Throwable cause) {
        super(cause);
    }
}
