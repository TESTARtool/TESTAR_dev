/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2017-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.exceptions;

import org.testar.windows.Windows;

public final class GDIException extends WinApiException {
    private static final long serialVersionUID = 1L;
    public GDIException(String message) { super(message); }
    public GDIException(int statusCode) {
        super(Windows.Gdiplus_Status2String(statusCode));
    }
}
