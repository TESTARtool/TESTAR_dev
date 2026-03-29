/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.exceptions;

public class WidgetNotFoundException extends FruitException {

    private static final long serialVersionUID = 7813610847340484828L;

    public WidgetNotFoundException() {
        super("");
    }

    public WidgetNotFoundException(String message) {
        this(message, null);
    }

    public WidgetNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public WidgetNotFoundException(Throwable cause) {
        super(cause);
    }
}
