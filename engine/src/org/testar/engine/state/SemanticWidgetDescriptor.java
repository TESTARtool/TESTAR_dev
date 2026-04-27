/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.state;

import org.testar.core.state.Widget;

/**
 * Platform-specific semantic widget policy used by engine projections.
 */
public interface SemanticWidgetDescriptor {

    boolean shouldInclude(Widget widget);

    String roleOf(Widget widget);

    String labelOf(Widget widget);

    String valueOf(Widget widget);
}
