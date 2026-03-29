/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.alayer;

import org.testar.core.Assert;
import org.testar.core.state.State;
import org.testar.core.util.Util;

public final class OrthogonalPosition extends AbstractPosition {

    private static final long serialVersionUID = -5638599581798926650L;
    final Position pos1, pos2;
    final double relR, absR;

    public OrthogonalPosition(Position pos1, Position pos2, double relR, double absR) {
        Assert.notNull(pos1, pos2);
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.relR = relR;
        this.absR = absR;
    }

    public Point apply(State state) {
        Assert.notNull(state);
        Point p1 = pos1.apply(state);
        Point p2 = pos2.apply(state);
        double centerX = (p1.x() + p2.x()) * .5;
        double centerY = (p1.y() + p2.y()) * .5;
        double l = Util.length(p1.x(), p1.y(), p2.x(), p2.y());
        return Util.OrthogonalPoint(centerX, centerY, p2.x(), p2.y(), relR * l + absR);
    }
}
