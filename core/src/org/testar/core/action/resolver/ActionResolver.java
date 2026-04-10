/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.action.resolver;

import java.util.List;

import org.testar.core.action.Action;

public interface ActionResolver {

    ResolvedAction resolve(Iterable<Action> actions, List<String> arguments);
}
