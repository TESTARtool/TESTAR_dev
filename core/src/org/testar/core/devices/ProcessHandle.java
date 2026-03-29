/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.devices;

import org.testar.core.exceptions.SystemStopException;

public interface ProcessHandle {
    void kill() throws SystemStopException;
    boolean isRunning();
    String name();
    long pid();
}
