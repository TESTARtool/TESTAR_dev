/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.exceptions;

public class PositionException extends FruitException {

    private static final long serialVersionUID = -1132646035499653943L;

    public PositionException(String message) {
        this(message, null);
    }

    public PositionException(String message, Throwable cause) {
        super(message, cause);
    }

    public PositionException(Throwable cause) {
        super(cause);
    }
}
