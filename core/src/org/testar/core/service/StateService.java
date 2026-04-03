/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.service;

import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.exceptions.StateBuildException;

public interface StateService {
    State getState(SUT system) throws StateBuildException;
}
