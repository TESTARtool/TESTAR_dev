/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.visualizers;

import java.util.Iterator;
import java.util.function.Function;

import org.testar.core.Assert;
import org.testar.core.alayer.Canvas;
import org.testar.core.alayer.Pen;
import org.testar.core.alayer.Point;
import org.testar.core.alayer.Position;
import org.testar.core.alayer.SplineTrajectory;
import org.testar.core.state.State;
import org.testar.core.alayer.StrokeCaps;
import org.testar.core.util.Util;

public class TrajectoryVisualizer implements Visualizer {

    private static final long serialVersionUID = 1107281202398264314L;
    final Function<State, Iterable<Point>> trajectory;
    final Pen pen;

    public TrajectoryVisualizer(Pen pen, Position... positions) {
        this(new SplineTrajectory(10, positions), pen);
    }
    
    public TrajectoryVisualizer(Function<State, Iterable<Point>> trajectory, Pen pen) {
        Assert.notNull(trajectory, pen);
        Assert.isTrue(pen.strokeWidth() != null);
        this.trajectory = trajectory;
        this.pen = pen;
    }
    
    public void run(State state, Canvas canvas, Pen pen) {
        Assert.notNull(state, canvas, pen);
        pen = Pen.merge(pen, this.pen);
        Iterator<Point> iter = trajectory.apply(state).iterator();
        Point last = iter.next();
        
        while (iter.hasNext()) {
            Point current = iter.next();
            
            if (!iter.hasNext() && (pen.strokeCaps() == StrokeCaps._Arrow || pen.strokeCaps() == StrokeCaps.Arrow_)) {
                Util.arrow(canvas, pen, last.x(), last.y(), current.x(), current.y(), 5 * pen.strokeWidth(), 5 * pen.strokeWidth());
            } else {
                canvas.line(pen, last.x(), last.y(), current.x(), current.y());
            }
            
            last = current;
        }
    }
}
