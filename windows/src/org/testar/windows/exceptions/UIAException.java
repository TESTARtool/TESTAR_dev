/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2017-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.exceptions;

public class UIAException extends WinApiException {
    private static final long serialVersionUID = 5815790395961162690L;
    public UIAException(String message) { super(message); }
}
