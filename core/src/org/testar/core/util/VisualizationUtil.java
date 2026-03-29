/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.util;

import java.util.Objects;

import org.testar.core.alayer.Canvas;
import org.testar.core.alayer.Rect;
import org.testar.core.alayer.Shape;

public class VisualizationUtil {

    private VisualizationUtil() {
    }

    // Build the info-panel shape near the current widget and keep it inside the visible canvas.
    public static Shape calculateWidgetInfoShape(Canvas canvas, Shape cwShape, double widgetInfoW, double widgetInfoH) {
        Objects.requireNonNull(canvas, "Canvas cannot be null");
        Objects.requireNonNull(cwShape, "Current widget shape cannot be null");
        if (widgetInfoW <= 0.0 || widgetInfoH <= 0.0) {
            return cwShape;
        }

        Shape s = Rect.from(cwShape.x(), cwShape.y(), widgetInfoW, widgetInfoH);
        Shape rs = repositionShape(canvas, s);

        // If y-axis canvas is over the screen (negative value), set to 0.0
        if (s.y() < 0.0) {
            s = Rect.from(cwShape.x(), 0.0, widgetInfoW, widgetInfoH);
        }
        if (rs.y() < 0.0) {
            rs = Rect.from(rs.x(), 0.0, rs.width(), rs.height());
        }

        // If no repositioning was needed, keep the original widget shape.
        if (s == rs) {
            return cwShape;
        } else {
            return rs;
        }
    }

    // Reposition a shape only when it overflows the canvas bounds.
    public static Shape repositionShape(Canvas canvas, Shape shape) {
        Objects.requireNonNull(canvas, "Canvas cannot be null");
        Objects.requireNonNull(shape, "Shape cannot be null");
        double[] offset = calculateOffset(canvas, shape);
        return calculateInnerShape(shape, offset);
    }

    // Positive offset means the shape still fits on that axis; negative means overflow amount.
    private static double[] calculateOffset(Canvas canvas, Shape shape) {
        return new double[] {
                canvas.x() + canvas.width() - (shape.x() + shape.width()),
                canvas.y() + canvas.height() - (shape.y() + shape.height())
        };
    }

    // Shift the shape back by the overflow amount while preserving size.
    private static Shape calculateInnerShape(Shape shape, double[] offset) {
        if (offset[0] > 0 && offset[1] > 0) {
            return shape;
        } else {
            double offsetX = offset[0] > 0 ? 0 : offset[0];
            double offsetY = offset[1] > 0 ? 0 : offset[1];
            return Rect.from(shape.x() + offsetX,
                    shape.y() + offsetY,
                    shape.width(),
                    shape.height());
        }
    }
}
