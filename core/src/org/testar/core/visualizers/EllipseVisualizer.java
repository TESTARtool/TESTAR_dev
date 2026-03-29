/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.visualizers;

import org.testar.core.Assert;
import org.testar.core.alayer.Canvas;
import org.testar.core.alayer.Pen;
import org.testar.core.alayer.Point;
import org.testar.core.alayer.Position;
import org.testar.core.state.State;
import org.testar.core.exceptions.PositionException;

public final class EllipseVisualizer implements Visualizer {

    private static final long serialVersionUID = -6006402344810634504L;
    private final double width, height;
    private final Pen pen;
    private final Position position;
    
    public EllipseVisualizer(Position position, Pen pen, double width, double height) {
        Assert.notNull(position, pen);
        this.width = width;
        this.height = height;
        this.pen = pen;
        this.position = position;
    }
    
    public void run(State state, Canvas canvas, Pen pen) {
        Assert.notNull(state, canvas, pen);
        pen = Pen.merge(pen, this.pen);
        try { // by urueda
            Point p = position.apply(state);
            canvas.ellipse(pen, p.x() - width * .5, p.y() - height * .5, width, height);
        } catch (PositionException pe) {
        }
    }
}
