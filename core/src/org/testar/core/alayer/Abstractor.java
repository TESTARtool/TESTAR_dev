/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.alayer;

import org.testar.core.state.Widget;
import org.testar.core.exceptions.AbstractionException;

/**
 * An Abstractor takes a Widget and creates a <code>Finder</code> object, which can
 * be used to find this widget in arbitrary <code>State</code>'s.
 * @see Finder
 */
public interface Abstractor {
    Finder apply(Widget widget) throws AbstractionException;
}
