/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2015-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.plugin;


public class UnsupportedPlatformException extends RuntimeException {

    private static final long serialVersionUID = -763558210570361617L;

    public UnsupportedPlatformException() {
        super();
    }

    public UnsupportedPlatformException(String message) {
        super(message);
    }

    public UnsupportedPlatformException(Throwable cause) {
        super(cause);
    }

    public UnsupportedPlatformException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedPlatformException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
