/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.util;

import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import org.testar.core.action.Action;
import org.testar.core.alayer.Canvas;
import org.testar.core.alayer.Color;
import org.testar.core.alayer.FillPattern;
import org.testar.core.alayer.Pen;
import org.testar.core.alayer.Point;
import org.testar.core.alayer.Rect;
import org.testar.core.alayer.Roles;
import org.testar.core.alayer.Shape;
import org.testar.core.devices.Mouse;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.core.visualizers.Visualizer;

public class VisualizationUtil {

    private VisualizationUtil() {
    }

    public static synchronized void visualizeState(boolean showExtendedWidgetInfo, Mouse mouse, Canvas canvas, State state, Predicate<Tag<?>> visibleTagPredicate){
        Point cursor = mouse.cursor();
        Widget cursorWidget = Util.widgetFromPoint(state, cursor.x(), cursor.y(), null);

        if(cursorWidget != null){
            Widget rootW = cursorWidget;
            while (rootW.parent() != null && rootW.parent() != rootW) {
                rootW = rootW.parent();
            }
            Shape cwShape = cursorWidget.get(Tags.Shape, null);

            if(cwShape != null){
                cwShape.paint(canvas, Pen.PEN_MARK_ALPHA);
                cwShape.paint(canvas, Pen.PEN_MARK_BORDER);
                if (!showExtendedWidgetInfo){
                	// Widget properties we are going to show in Spy mode when the information is not extended
                	String rootText = "StateID: " + rootW.get(Tags.AbstractID, "");
                	String widgetText = "WidgetID: " + cursorWidget.get(Tags.AbstractID, "");
                	String titleText = "Title: " + cursorWidget.get(Tags.Title, "");
                	String roleText = "Role: " + cursorWidget.get(Tags.Role, Roles.Widget).toString();
                	String enabledText = "Enabled: " + cursorWidget.get(Tags.Enabled, false);
                	String shapeText = "Shape: " + cursorWidget.get(Tags.Shape, Rect.from(0, 0, 0, 0));
                	String pathText = "Path: " + cursorWidget.get(Tags.Path, "");

                	// Calculate the maximum length of the widget properties strings
                	String[] textArray = {rootText, widgetText, titleText, roleText, enabledText, shapeText, pathText};
                	int maxLength = 0;
                	for (String text : textArray) {
                		maxLength = Math.max(maxLength, text.length());
                	}
                	double miniwidgetInfoW = maxLength * 8.0;
                	if (miniwidgetInfoW < 256) miniwidgetInfoW = 256;
                	double miniwidgetInfoH = 20 * textArray.length; // Each property uses a height of 20

                	// Create the Shape of the visual rectangles we draw in Spy mode
                	Shape minicwShape = Rect.from(cwShape.x() + cwShape.width()/2 + 32,
                			cwShape.y() + cwShape.height()/2 + 32,
                			miniwidgetInfoW, miniwidgetInfoH);
                	Shape repositionShape = VisualizationUtil.calculateWidgetInfoShape(canvas,minicwShape, miniwidgetInfoW, miniwidgetInfoH);
                	if (repositionShape != minicwShape){
                		double x = repositionShape.x() - repositionShape.width() - 32,
                				y = repositionShape.y() - repositionShape.height() - 32;
                		if (x < 0) x = 0; if (y < 0) y = 0;
                		minicwShape = Rect.from(x,y,repositionShape.width(), repositionShape.height());
                	}

                	// Draw the rectangle and widget properties in the screen
                	canvas.rect(Pen.PEN_WHITE_ALPHA, minicwShape.x(), minicwShape.y(), miniwidgetInfoW, miniwidgetInfoH);
                	canvas.rect(Pen.PEN_BLACK, minicwShape.x(), minicwShape.y(), miniwidgetInfoW, miniwidgetInfoH);
                	canvas.text(Pen.PEN_RED, minicwShape.x(), minicwShape.y(), 0, rootText);
                	canvas.text(Pen.PEN_BLUE, minicwShape.x(), minicwShape.y() + 20, 0, widgetText);
                	canvas.text(Pen.PEN_BLUE, minicwShape.x(), minicwShape.y() + 40, 0, titleText);
                	canvas.text(Pen.PEN_BLUE, minicwShape.x(), minicwShape.y() + 60, 0, roleText);
                	canvas.text(Pen.PEN_BLUE, minicwShape.x(), minicwShape.y() + 80, 0, enabledText);
                	canvas.text(Pen.PEN_BLUE, minicwShape.x(), minicwShape.y() + 100, 0, shapeText);
                	canvas.text(Pen.PEN_BLUE, minicwShape.x(), minicwShape.y() + 120, 0, pathText);
                }

                int MAX_ANCESTORS_PERLINE = 6;
                double widgetInfoW = canvas.width() / 2;
                double widgetInfoH = (1 + calculateNumberOfTagsToShow(cursorWidget, visibleTagPredicate) + Util.size(Util.ancestors(cursorWidget)) / MAX_ANCESTORS_PERLINE) * 20;
                cwShape = VisualizationUtil.calculateWidgetInfoShape(canvas, cwShape, widgetInfoW, widgetInfoH);

                if(showExtendedWidgetInfo) {
                    canvas.rect(Pen.PEN_WHITE_ALPHA, cwShape.x(), cwShape.y(), widgetInfoW, widgetInfoH);
                    canvas.rect(Pen.PEN_BLACK, cwShape.x(), cwShape.y(), widgetInfoW, widgetInfoH);

                    int pos = -20;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Ancestors: ");

                    int i=0;
                    for(Widget p : Util.ancestors(cursorWidget)){
                        sb.append("::").append(p.get(Tags.Role, Roles.Widget));
                        i++;
                        if (i >= MAX_ANCESTORS_PERLINE){
                            canvas.text(Pen.PEN_BLACK, cwShape.x(), cwShape.y() + (pos+=20), 0, sb.toString());
                            i=0;
                            sb = new StringBuilder();
                            sb.append("\t");
                        }
                    }
                    if (i > 0) {
                        canvas.text(Pen.PEN_BLACK, cwShape.x(), cwShape.y() + (pos+=20), 0, sb.toString());
                    }

                    for (Tag<?> t : cursorWidget.tags()) {
                        if (shouldVisualizeTag(visibleTagPredicate, t)) {
                            canvas.text((t.isOneOf(Tags.Role, Tags.Title, Tags.Shape, Tags.Enabled, Tags.Path,
                                    Tags.ConcreteID)) ? Pen.PEN_RED : Pen.PEN_BLACK, cwShape.x(), cwShape.y() + (pos += 20),
                                    0, t.name() + ":   " + Util.abbreviate(Util.toString(cursorWidget.get(t)), 50, "..."));
                        }
                    }
                }
            }
        }
    }

    public static void visualizeActions(Canvas canvas, State state, Set<Action> actions){
        try {
            for(Action a : actions){
                Pen vp = Pen.PEN_IGNORE;
                a.get(Tags.Visualizer, Util.NullVisualizer).run(state, canvas, vp);
            }
        } catch(IllegalStateException ise) {
            System.out.println("visualizeActions : canvas visualization not available!");
            if(ise.getMessage()!=null) { System.out.println(ise.getMessage()); }
        }
    }

    public static void visualizeSelectedAction(Canvas canvas, State state, Action action, double actionDuration){
        Pen redPen = Pen.newPen().setColor(Color.Red).setFillPattern(FillPattern.Solid).setStrokeWidth(20).build();
        try {
            Visualizer visualizer = action.get(Tags.Visualizer, Util.NullVisualizer);
            final int BLINK_COUNT = 3;
            final double BLINK_DELAY = actionDuration / BLINK_COUNT;
            for(int i = 0; i < BLINK_COUNT; i++){
                Util.pause(BLINK_DELAY);
                canvas.begin();
                visualizer.run(state, canvas, Pen.PEN_IGNORE);
                canvas.end();
                Util.pause(BLINK_DELAY);
                canvas.begin();
                visualizer.run(state, canvas, redPen);
                canvas.end();
            }
        } catch(IllegalStateException ise) {
            System.out.println("visualizeSelectedAction : canvas visualization not available!");
            if(ise.getMessage()!=null) { System.out.println(ise.getMessage()); }
        }
    }

    /**
     * Calculate how many tags we need to show .
     * @param cursorWidget The widget pointed by the cursor.
     * @return The number of tags we need to show.
     */
    private static int calculateNumberOfTagsToShow(Widget cursorWidget, Predicate<Tag<?>> visibleTagPredicate) {
        int tagsToShow = 0;
        for (Tag<?> t : cursorWidget.tags()) {
            if (shouldVisualizeTag(visibleTagPredicate, t)) {
                tagsToShow++;
            }
        }
        return tagsToShow;
    }

    private static boolean shouldVisualizeTag(Predicate<Tag<?>> visibleTagPredicate, Tag<?> tag) {
        return visibleTagPredicate.test(tag);
    }

    // Build the info-panel shape near the current widget and keep it inside the visible canvas.
    static Shape calculateWidgetInfoShape(Canvas canvas, Shape cwShape, double widgetInfoW, double widgetInfoH) {
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
    static Shape repositionShape(Canvas canvas, Shape shape) {
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
