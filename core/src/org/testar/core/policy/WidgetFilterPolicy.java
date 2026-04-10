/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.policy;

import org.testar.core.state.Widget;

@FunctionalInterface
public interface WidgetFilterPolicy {

    boolean allows(Widget widget);
}
