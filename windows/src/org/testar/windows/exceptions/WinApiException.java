/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2017-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.exceptions;

import org.testar.core.exceptions.FruitException;

public class WinApiException extends FruitException {
    private static final long serialVersionUID = 4344611676441793933L;
    public WinApiException(String message) { super(message); }
}
