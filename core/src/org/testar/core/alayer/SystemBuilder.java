/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.alayer;

import java.io.Serializable;

import org.testar.core.state.SUT;
import org.testar.core.exceptions.SystemStartException;

public interface SystemBuilder extends Serializable {
    SUT apply() throws SystemStartException;
}
