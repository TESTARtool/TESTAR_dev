/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.visualizers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testar.core.state.State;
import org.testar.core.Assert;
import org.testar.core.alayer.Canvas;
import org.testar.core.alayer.Pen;
import org.testar.core.alayer.Rect;
import org.testar.core.alayer.Shape;
import org.testar.core.util.Util;

public interface Visualizer extends Serializable {

    void run(State state, Canvas canvas, Pen pen);

    default List<Shape> getShapes() {
        return Arrays.asList(Rect.from(0, 0, 0, 0));
    }

    static Visualizer join(Visualizer first, Visualizer second) {
        if (first == second) {
            return first;
        }
        if (first == Util.NullVisualizer) {
            return second;
        }
        if (second == Util.NullVisualizer) {
            return first;
        }

        return new Visualizer() {
            private static final long serialVersionUID = 1L;

            @Override
            public void run(State state, Canvas canvas, Pen pen) {
                Assert.notNull(state, canvas, pen);
                first.run(state, canvas, pen);
                second.run(state, canvas, pen);
            }

            @Override
            public List<Shape> getShapes() {
                ArrayList<Shape> merged = new ArrayList<>();
                merged.addAll(first.getShapes());
                merged.addAll(second.getShapes());
                return merged;
            }
        };
    }
}
