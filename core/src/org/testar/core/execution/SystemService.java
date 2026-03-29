/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.execution;

import org.testar.core.exceptions.SystemStartException;
import org.testar.core.state.SUT;

public interface SystemService {

    SUT startSystem() throws SystemStartException;

    void stopSystem(SUT system);
}
