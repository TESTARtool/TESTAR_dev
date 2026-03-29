package org.testar.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.testar.core.alayer.Canvas;
import org.testar.core.alayer.Rect;
import org.testar.core.alayer.Shape;

public class VisualizationUtilTest {

    @Test
    public void repositionShape_InsideCanvas() {
        Canvas canvas = mock(Canvas.class);
        Shape shape = Rect.from(10, 10, 20, 20);

        when(canvas.x()).thenReturn(0.0);
        when(canvas.y()).thenReturn(0.0);
        when(canvas.width()).thenReturn(200.0);
        when(canvas.height()).thenReturn(200.0);

        Shape result = VisualizationUtil.repositionShape(canvas, shape);

        assertSame(shape, result);
    }

    @Test
    public void repositionShape_OverflowingCanvas() {
        Canvas canvas = mock(Canvas.class);
        Shape shape = Rect.from(90, 80, 20, 30);

        when(canvas.x()).thenReturn(0.0);
        when(canvas.y()).thenReturn(0.0);
        when(canvas.width()).thenReturn(100.0);
        when(canvas.height()).thenReturn(100.0);

        Shape result = VisualizationUtil.repositionShape(canvas, shape);

        assertEquals(80.0, result.x(), 0.0);
        assertEquals(70.0, result.y(), 0.0);
        assertEquals(20.0, result.width(), 0.0);
        assertEquals(30.0, result.height(), 0.0);
    }

    @Test
    public void calculateWidgetInfoShape_NoRepositionNeeded() {
        Canvas canvas = mock(Canvas.class);
        Shape currentWidgetShape = Rect.from(50, 50, 20, 20);

        when(canvas.x()).thenReturn(0.0);
        when(canvas.y()).thenReturn(0.0);
        when(canvas.width()).thenReturn(500.0);
        when(canvas.height()).thenReturn(500.0);

        Shape result = VisualizationUtil.calculateWidgetInfoShape(canvas, currentWidgetShape, 100.0, 60.0);

        assertSame(currentWidgetShape, result);
    }

    @Test
    public void calculateWidgetInfoShape_NegativeYToZero() {
        Canvas canvas = mock(Canvas.class);
        Shape currentWidgetShape = Rect.from(50, -5, 20, 20);

        when(canvas.x()).thenReturn(0.0);
        when(canvas.y()).thenReturn(0.0);
        when(canvas.width()).thenReturn(200.0);
        when(canvas.height()).thenReturn(200.0);

        Shape result = VisualizationUtil.calculateWidgetInfoShape(canvas, currentWidgetShape, 40.0, 20.0);

        assertEquals(0.0, result.y(), 0.0);
    }

    @Test
    public void repositionShape_NullCanvas() {
        Shape shape = Rect.from(10, 10, 20, 20);
        try {
            VisualizationUtil.repositionShape(null, shape);
        } catch (NullPointerException expected) {
            assertEquals("Canvas cannot be null", expected.getMessage());
            return;
        }
        throw new AssertionError("Expected NullPointerException");
    }

    @Test
    public void repositionShape_NullShape() {
        Canvas canvas = mock(Canvas.class);
        try {
            VisualizationUtil.repositionShape(canvas, null);
        } catch (NullPointerException expected) {
            assertEquals("Shape cannot be null", expected.getMessage());
            return;
        }
        throw new AssertionError("Expected NullPointerException");
    }

    @Test
    public void calculateWidgetInfoShape_NullCanvas() {
        Shape currentWidgetShape = Rect.from(50, 50, 20, 20);
        try {
            VisualizationUtil.calculateWidgetInfoShape(null, currentWidgetShape, 100.0, 60.0);
        } catch (NullPointerException expected) {
            assertEquals("Canvas cannot be null", expected.getMessage());
            return;
        }
        throw new AssertionError("Expected NullPointerException");
    }

    @Test
    public void calculateWidgetInfoShape_NullCurrentShape() {
        Canvas canvas = mock(Canvas.class);
        try {
            VisualizationUtil.calculateWidgetInfoShape(canvas, null, 100.0, 60.0);
        } catch (NullPointerException expected) {
            assertEquals("Current widget shape cannot be null", expected.getMessage());
            return;
        }
        throw new AssertionError("Expected NullPointerException");
    }

    @Test
    public void calculateWidgetInfoShape_InvalidWidgetInfo() {
        Canvas canvas = mock(Canvas.class);
        Shape currentWidgetShape = Rect.from(50, 50, 20, 20);
        Shape result = VisualizationUtil.calculateWidgetInfoShape(canvas, currentWidgetShape, 0.0, 60.0);
        assertSame(currentWidgetShape, result);
    }

}
