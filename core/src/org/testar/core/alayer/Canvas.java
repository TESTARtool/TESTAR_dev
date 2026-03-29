/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.alayer;

import org.testar.core.Pair;
import org.testar.core.visualizers.Visualizer;

/**
 * A Canvas is a surface onto which you can draw e.g. lines, rectangles and circles.
 * It serves as a way to visualize e.g. <code>Action</code>'s and the <code>Shape</code> of
 * <code>Widget</code>'s.
 * 
 * @see Pen
 * @see Visualizer
 */
public interface Canvas {
    double width();
    double height();
    double x();
    double y();
    void begin();
    void end();
    void line(Pen pen, double x1, double y1, double x2, double y2);
    void text(Pen pen, double x, double y, double angle, String text);
    Pair<Double, Double> textMetrics(Pen pen, String text);
    void clear(double x, double y, double width, double height);
    void triangle(Pen pen, double x1, double y1, double x2, double y2, double x3, double y3);
    void image(Pen pen, double x, double y, double width, double height, int[] image, int imageWidth, int imageHeight);
    void ellipse(Pen pen, double x, double y, double width, double height);
    void rect(Pen pen, double x, double y, double width, double height);
    Pen defaultPen();
    void release();
    void paintBatch();
}
