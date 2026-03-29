/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.devices;

public enum MouseButtons {

    BUTTON1(16), BUTTON2(8), BUTTON3(4);

    private final int code;

    private MouseButtons(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
