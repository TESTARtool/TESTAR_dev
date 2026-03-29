/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.alayer;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.testar.core.Assert;
import org.testar.core.state.State;

public final class SplineTrajectory implements Function<State, Iterable<Point>>, Serializable {

    private static final long serialVersionUID = -7833747078043023184L;
    final Position[] positions;
    final int smoothness;

    private final class PointIterable implements Iterable<Point> {
        final Point[] points;

        public PointIterable(Point[] points) {
            this.points = points;
        }

        public Iterator<Point> iterator() {
            return new Iter(points);
        }
    }

    private final class Iter implements Iterator<Point> {
        List<Point> intermediatePoints;
        Iterator<Point> iter;

        public Iter(Point[] points) {
            intermediatePoints = Spline.evaluate(points, smoothness + 1); //TODO: only create points on demand!!
            iter = intermediatePoints.iterator();
        }

        public boolean hasNext() {
            return iter.hasNext();
        }

        public Point next() {
            return iter.next();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public SplineTrajectory(int smoothness, Position... positions) {
        Assert.notNull(positions);
        Assert.isTrue(smoothness >= 0 && positions.length > 1);
        this.positions = positions;
        this.smoothness = smoothness;
    }

    public Iterable<Point> apply(State s) {
        Assert.notNull(s);
        Point[] points = new Point[positions.length];
        for (int i = 0; i < positions.length; i++) {
            points[i] = positions[i].apply(s);
        }
        return new PointIterable(points);
    }
}
