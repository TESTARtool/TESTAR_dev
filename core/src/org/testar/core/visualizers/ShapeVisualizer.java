/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.visualizers;

import java.util.Arrays;
import java.util.List;

import org.testar.core.Assert;
import org.testar.core.alayer.Canvas;
import org.testar.core.alayer.Pen;
import org.testar.core.alayer.Shape;
import org.testar.core.state.State;

public final class ShapeVisualizer implements Visualizer {
    private static final long serialVersionUID = -1411595441118761574L;
    private final Shape shape;
    private final String label;
    private final double labelX, labelY;
    private final Pen pen;
    
    public ShapeVisualizer(Pen pen, Shape shape, String label, double labelX, double labelY) {
        Assert.notNull(shape, pen);
        this.shape = shape;
        this.pen = pen;
        this.label = label;
        this.labelX = labelX;
        this.labelY = labelY;
    }

    @Override
    public List<Shape> getShapes() {
        return Arrays.asList(this.shape);
    }

    public void run(State state, Canvas c, Pen pen) {
        Assert.notNull(state, c, pen);
        pen = Pen.merge(pen, this.pen);
        shape.paint(c, pen);
        if (label != null) {
            c.text(pen, shape.x() + shape.width() * labelX, shape.y() + shape.height() * labelY, 0, label);
        }
    }
}
