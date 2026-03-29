/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.exceptions;

public class ActionBuildException extends FruitException {

    private static final long serialVersionUID = -4992879959880705549L;

    public ActionBuildException(String message) {
        this(message, null);
    }

    public ActionBuildException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActionBuildException(Throwable cause) {
        super(cause);
    }
}
