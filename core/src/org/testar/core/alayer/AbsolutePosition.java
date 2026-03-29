/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.alayer;

import org.testar.core.Assert;
import org.testar.core.state.State;

public final class AbsolutePosition extends AbstractPosition {
    private static final long serialVersionUID = -6784500620656208720L;
    private final Point p;

    public AbsolutePosition(double x, double y) {
        p = Point.from(x, y);
    }

    public AbsolutePosition(Point point) {
        Assert.notNull(point);
        p = point;
    }

    public Point apply(State state) {
        return p;
    }

    public String toString() {
        return p.toString();
    }
}
