/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.exceptions;

public class AbstractionException extends FruitException {

    private static final long serialVersionUID = -7797424127481093948L;

    public AbstractionException(String message) {
        this(message, null);
    }

    public AbstractionException(String message, Throwable cause) {
        super(message, cause);
    }

    public AbstractionException(Throwable cause) {
        super(cause);
    }
}
