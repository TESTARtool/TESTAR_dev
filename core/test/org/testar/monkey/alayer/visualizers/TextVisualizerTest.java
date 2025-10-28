package org.testar.monkey.alayer.visualizers;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mockito.Mockito;
import org.testar.monkey.Pair;
import org.testar.monkey.alayer.AbsolutePosition;
import org.testar.monkey.alayer.Canvas;
import org.testar.monkey.alayer.Pen;
import org.testar.stub.StateStub;

public class TextVisualizerTest {

    private final String originalText = "original_text";
    private final String updatedText = "updated_text";

    private final Pen originalPen = Pen.PEN_BLUE;
    private final Pen updatedPen = Pen.PEN_RED;

    @Test
    public void test_correct_original_textVisualizer() {
        TextVisualizer textVisualizer = new TextVisualizer(new AbsolutePosition(1, 1), originalText, originalPen);
        assertTrue(textVisualizer.getText().equals(originalText));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_original_null_pos() {
        new TextVisualizer(null, originalText, originalPen);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_original_null_text() {
        new TextVisualizer(new AbsolutePosition(1, 1), null, originalPen);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_original_null_pen() {
        new TextVisualizer(new AbsolutePosition(1, 1), originalText, null);
    }

    @Test
    public void test_correct_updated_textVisualizer() {
        TextVisualizer textVisualizer = new TextVisualizer(new AbsolutePosition(1, 1), originalText, originalPen);
        textVisualizer = textVisualizer.withText(updatedText, updatedPen);
        assertTrue(textVisualizer.getText().equals(updatedText));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_updated_null_text() {
        TextVisualizer textVisualizer = new TextVisualizer(new AbsolutePosition(1, 1), originalText, originalPen);
        textVisualizer.withText(null, updatedPen);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_updated_null_pen() {
        TextVisualizer textVisualizer = new TextVisualizer(new AbsolutePosition(1, 1), originalText, originalPen);
        textVisualizer.withText(updatedText, null);
    }

    @Test
    public void test_run_visualizer() {
        TextVisualizer textVisualizer = new TextVisualizer(new AbsolutePosition(1, 1), originalText, originalPen);
        Canvas mockCanvas = Mockito.mock(Canvas.class);
        Mockito.when(mockCanvas.textMetrics(Mockito.any(Pen.class), Mockito.anyString())).thenReturn(new Pair<Double, Double>(2.0, 2.0));
        textVisualizer.run(new StateStub(), mockCanvas, updatedPen);
    }

    public void test_run_catched_null_position() {
        TextVisualizer textVisualizer = new TextVisualizer(new AbsolutePosition(1, 1), originalText, originalPen);
        Canvas mockCanvas = Mockito.mock(Canvas.class);
        Mockito.when(mockCanvas.textMetrics(Mockito.any(Pen.class), Mockito.anyString())).thenReturn(null);
        textVisualizer.run(new StateStub(), mockCanvas, updatedPen);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_run_null_state() {
        TextVisualizer textVisualizer = new TextVisualizer(new AbsolutePosition(1, 1), originalText, originalPen);
        textVisualizer.run(null, Mockito.mock(Canvas.class), updatedPen);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_run_null_canvas() {
        TextVisualizer textVisualizer = new TextVisualizer(new AbsolutePosition(1, 1), originalText, originalPen);
        textVisualizer.run(new StateStub(), null, updatedPen);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_run_null_pen() {
        TextVisualizer textVisualizer = new TextVisualizer(new AbsolutePosition(1, 1), originalText, originalPen);
        textVisualizer.run(new StateStub(), Mockito.mock(Canvas.class), null);
    }

}
