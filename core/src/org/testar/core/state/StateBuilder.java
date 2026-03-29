/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.state;

import java.io.Serializable;

import org.testar.core.exceptions.StateBuildException;

/**
 * A StateBuilder fetches an <code>SUT</code>'s current state.
 */
public interface StateBuilder extends Serializable {

    State apply(SUT system) throws StateBuildException;

}
