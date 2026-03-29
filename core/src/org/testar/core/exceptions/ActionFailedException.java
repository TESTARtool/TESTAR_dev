/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.exceptions;

public class ActionFailedException extends FruitException {

    private static final long serialVersionUID = 1996197001243858386L;

    public ActionFailedException(String message) {
        this(message, null);
    }

    public ActionFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActionFailedException(Throwable cause) {
        super(cause);
    }
}
