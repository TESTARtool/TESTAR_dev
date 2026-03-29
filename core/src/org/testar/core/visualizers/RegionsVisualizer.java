/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.visualizers;

import java.util.List;

import org.testar.core.Assert;
import org.testar.core.alayer.Canvas;
import org.testar.core.alayer.Pen;
import org.testar.core.alayer.Shape;
import org.testar.core.state.State;

public final class RegionsVisualizer implements Visualizer {
    private static final long serialVersionUID = 1L;
    private final List<Shape> shapes;
    private final String label;
    private final double labelX, labelY;
    private final Pen pen;

    public RegionsVisualizer(Pen pen, List<Shape> shapes, String label, double labelX, double labelY) {
        Assert.notNull(shapes, pen);
        this.shapes = shapes;
        this.pen = pen;
        this.label = label;
        this.labelX = labelX;
        this.labelY = labelY;
    }

    @Override
    public List<Shape> getShapes() {
        return this.shapes;
    }

    public void run(State state, Canvas c, Pen pen) {
        Assert.notNull(state, c, pen);
        pen = Pen.merge(pen, this.pen);
        
        for (Shape shape : shapes) {
            shape.paint(c, pen);
            if (label != null) {
                c.text(pen, shape.x() + shape.width() * labelX, shape.y() + shape.height() * labelY, 0, label);
            }
        }
    }
}