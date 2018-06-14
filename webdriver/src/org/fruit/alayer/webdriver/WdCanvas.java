package org.fruit.alayer.webdriver;

import org.fruit.Assert;
import org.fruit.Pair;
import org.fruit.alayer.*;

import static org.fruit.Util.pause;


/**
 * Represents a canvas for a webdriver on which can be painted.
 */
public class WdCanvas implements Canvas {
  private static final int textOffsetX = 5;
  private static final int textOffsetY = 15;

  private Pen defaultPen;
  private double fontSize, strokeWidth;
  private String font;
  private StrokePattern strokePattern;
  private FillPattern fillPattern;
  private StrokeCaps strokeCaps;
  private Color color;

  public WdCanvas(Pen defaultPen) {
    this.defaultPen = defaultPen;
  }

  @Override
  public double width() {
    return CanvasDimensions.getCanvasWidth();
  }

  @Override
  public double height() {
    return CanvasDimensions.getCanvasHeight();
  }

  @Override
  public double x() {
    return 0;
  }

  @Override
  public double y() {
    return 0;
  }

  @Override
  public void begin() {
    check();
  }

  @Override
  public void end() {
    // TODO What ??
    pause(1);
  }

  @Override
  public void release() {
  }

  @Override
  public void clear(double x, double y, double width, double height) {
    check();

    Object[] args = new Object[]{x, y, width, height};
    WdDriver.executeScript("clearCanvasTestar(arguments)", args);
  }

  @Override
  public void text(Pen pen, double x, double y, double angle, String text) {
    check();
    adjustPen(pen);

    Object[] args = new Object[]{cssColor(), Math.round(fontSize), font, text,
        x + textOffsetX, y + textOffsetY};
    WdDriver.executeScript("drawTextTestar(arguments)", args);
  }

  @Override
  public Pair<Double, Double> textMetrics(Pen pen, String text) {
    check();
    // TODO Copied from GDIScreenCanvas, no idea if it works
    Assert.notNull(pen, text);
    return Pair.from(text.length() * 2., 20.);
  }

  @Override
  public void line(Pen pen, double x1, double y1, double x2, double y2) {
    check();
    adjustPen(pen);

    Object[] args = new Object[]{cssColor(), strokeWidth, x1, y1, x2, y2};
    WdDriver.executeScript("drawLineTestar(arguments)", args);
  }

  @Override
  public void triangle(Pen pen, double x1, double y1, double x2, double y2, double x3, double y3) {
    check();
    adjustPen(pen);

    String fillStroke = fillPattern == FillPattern.Solid ? "fill" : "stroke";
    Object[] args = new Object[]{cssColor(), strokeWidth, fillStroke,
        x1, y1, x2, y2, x3, y3};
    WdDriver.executeScript("drawTriangleTestar(arguments)", args);
  }

  @Override
  public void image(Pen pen, double x, double y, double width, double height,
                    int[] image, int imageWidth, int imageHeight) {
    // TODO
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void ellipse(Pen pen, double x, double y, double width, double height) {
    check();
    adjustPen(pen);

    String fillStroke = fillPattern == FillPattern.Solid ? "fill" : "stroke";
    Object[] args = new Object[]{cssColor(), strokeWidth, fillStroke,
        x + width / 2, y + height / 2, width / 2, height / 2};
    WdDriver.executeScript("drawEllipseTestar(arguments)", args);
  }

  @Override
  public void rect(Pen pen, double x, double y, double width, double height) {
    check();
    adjustPen(pen);

    String fillStroke = fillPattern == FillPattern.Solid ? "fillRect" : "strokeRect";
    Object[] args = new Object[]{
        cssColor(), strokeWidth, fillStroke, x, y, width, height};
    WdDriver.executeScript("drawRectTestar(arguments)", args);
  }

  @Override
  public Pen defaultPen() {
    return defaultPen;
  }

  private void adjustPen(Pen pen) {
    strokeWidth = pen.strokeWidth() != null ?
        pen.strokeWidth() : defaultPen.strokeWidth();
    strokePattern = pen.strokePattern() != null ?
        pen.strokePattern() : defaultPen.strokePattern();
    strokeCaps = pen.strokeCaps() != null ?
        pen.strokeCaps() : defaultPen.strokeCaps();

    color = pen.color() != null ?
        pen.color() : defaultPen.color();

    font = pen.font() != null ?
        pen.font() : defaultPen.font();
    fontSize = pen.fontSize() != null ?
        pen.fontSize() : defaultPen.fontSize();

    fillPattern = pen.fillPattern() != null ?
        pen.fillPattern() : defaultPen.fillPattern();
  }

  private String cssColor() {
    return String.format("rgba(%d, %d, %d, %.2f)",
        color.red(), color.green(), color.blue(), color.alpha() / 256.0);
  }

  // Add the canvas if the page doesn't have one
  private void check() {
    try {
      WdDriver.executeScript("addCanvasTestar()");
    } catch (Exception e) {
      // TODO
      System.out.println();
      e.printStackTrace();

      pause(1);
      check();
    }
  }
}