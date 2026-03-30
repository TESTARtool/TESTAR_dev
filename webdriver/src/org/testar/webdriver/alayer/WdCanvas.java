/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.alayer;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.core.Assert;
import org.testar.core.Pair;
import org.testar.core.alayer.Canvas;
import org.testar.core.alayer.Color;
import org.testar.core.alayer.FillPattern;
import org.testar.core.alayer.Pen;
import org.testar.webdriver.state.WdDriver;


/**
 * Represents a webdriver canvas on which can be painted.
 */
public class WdCanvas implements Canvas {
  protected static final Logger logger = LogManager.getLogger();

  private static final int textOffsetX = 5;
  private static final int textOffsetY = 15;

  private Pen defaultPen;
  private double fontSize, strokeWidth;
  private String font;
  private FillPattern fillPattern;
  private Color color;

  private final List<Object[]> drawBatch = new ArrayList<>();

  public WdCanvas(Pen defaultPen) {
    this.defaultPen = defaultPen;
  }

  @Override
  public double width() {
    return WdCanvasDimensions.getCanvasWidth();
  }

  @Override
  public double height() {
    return WdCanvasDimensions.getCanvasHeight();
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

  }

  @Override
  public void end() {

  }

  @Override
  public void release() {
  }

  @Override
  public void clear(double x, double y, double width, double height) {
    Object[] args = new Object[]{x, y, width, height};
    executeCanvasScript("clearCanvasTestar(arguments)", args);
  }

  @Override
  public void text(Pen pen, double x, double y, double angle, String text) {
    adjustPen(pen);

    Object[] args = new Object[]{cssColor(), Math.round(fontSize), font, text,
        x + textOffsetX, y + textOffsetY};

    drawBatch.add(new Object[]{"text", args});
  }

  @Override
  public Pair<Double, Double> textMetrics(Pen pen, String text) {
    Assert.notNull(pen, text);
    return Pair.from(text.length() * 2., 20.);
  }

  @Override
  public void line(Pen pen, double x1, double y1, double x2, double y2) {
    final int viewW = WdCanvasDimensions.getCanvasWidth();
    final int viewH = WdCanvasDimensions.getCanvasHeight();
    
    // Wrap X when it runs past the right browser canvas edge
    if (x1 > viewW) x1 = 2 * viewW - x1;
    if (x2 > viewW) x2 = 2 * viewW - x2;
    if (x1 < 0) x1 = 0;
    if (x2 < 0) x2 = 0;

    // Skip only if the whole line is outside vertically
    if ((y1 < 0 && y2 < 0) || (y1 > viewH && y2 > viewH)) {
      return;
    }

    adjustPen(pen);

    Object[] args = new Object[]{cssColor(), strokeWidth, x1, y1, x2, y2};

    drawBatch.add(new Object[]{"line", args});
  }

  @Override
  public void triangle(Pen pen, double x1, double y1, double x2, double y2, double x3, double y3) {
    adjustPen(pen);

    String fillStroke = fillPattern == FillPattern.Solid ? "fill" : "stroke";
    Object[] args = new Object[]{cssColor(), strokeWidth, fillStroke,
        x1, y1, x2, y2, x3, y3};

    drawBatch.add(new Object[]{"triangle", args});
  }

  @Override
  public void image(Pen pen, double x, double y, double width, double height,
                    int[] image, int imageWidth, int imageHeight) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void ellipse(Pen pen, double x, double y, double width, double height) {
    adjustPen(pen);

    String fillStroke = fillPattern == FillPattern.Solid ? "fill" : "stroke";
    Object[] args = new Object[]{cssColor(), strokeWidth, fillStroke,
        x + width / 2, y + height / 2, width / 2, height / 2};

    drawBatch.add(new Object[]{"ellipse", args});
  }

  @Override
  public void rect(Pen pen, double x, double y, double width, double height) {
    adjustPen(pen);

    String fillStroke = fillPattern == FillPattern.Solid ? "fillRect" : "strokeRect";
    Object[] args = new Object[]{
        cssColor(), strokeWidth, fillStroke, x, y, width, height};

    drawBatch.add(new Object[]{"rect", args});
  }

  @Override
  public void paintBatch() {
	  if (!drawBatch.isEmpty()) {
		  executeCanvasScript("drawBatchTestar(arguments[0])", drawBatch);
		  drawBatch.clear();
	  }
  }

  @Override
  public Pen defaultPen() {
    return defaultPen;
  }

  private void adjustPen(Pen pen) {
    strokeWidth = pen.strokeWidth() != null ?
        pen.strokeWidth() : defaultPen.strokeWidth();

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

  @Override
  public String toString() {
    return "x: " + x() + ", y: " + y() + ", width: " + width() + ", height: " + height() ;
  }
  
  private void executeCanvasScript(String script, Object... args) {
    try {
      RemoteWebDriver remoteWebDriver = WdDriver.getRemoteWebDriver();

      if(remoteWebDriver == null) return;
        
      // Add the canvas if the page doesn't have one
      remoteWebDriver.executeScript("addCanvasTestar()");

      remoteWebDriver.executeScript(script, args);
    } catch (NullPointerException | WebDriverException ignored) {

    }
  }
}
