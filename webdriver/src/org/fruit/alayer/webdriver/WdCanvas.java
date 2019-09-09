/**
 * Copyright (c) 2018, 2019 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.fruit.alayer.webdriver;

import org.fruit.Assert;
import org.fruit.Pair;
import org.fruit.alayer.Canvas;
import org.fruit.alayer.Color;
import org.fruit.alayer.FillPattern;
import org.fruit.alayer.Pen;


/**
 * Represents a webdriver canvas on which can be painted.
 */
public class WdCanvas implements Canvas {
  private static final int textOffsetX = 5;
  private static final int textOffsetY = 15;

  private Pen defaultPen;
  private double fontSize, strokeWidth;
  private String font;
  private FillPattern fillPattern;
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
    WdDriver.executeCanvasScript("clearCanvasTestar(arguments)", args);
  }

  @Override
  public void text(Pen pen, double x, double y, double angle, String text) {
    adjustPen(pen);

    Object[] args = new Object[]{cssColor(), Math.round(fontSize), font, text,
        x + textOffsetX, y + textOffsetY};
    WdDriver.executeCanvasScript("drawTextTestar(arguments)", args);
  }

  @Override
  public Pair<Double, Double> textMetrics(Pen pen, String text) {
    Assert.notNull(pen, text);
    return Pair.from(text.length() * 2., 20.);
  }

  @Override
  public void line(Pen pen, double x1, double y1, double x2, double y2) {
    if (!needToDraw(x1, y1, x2, y2)) {
      return;
    }
    adjustPen(pen);

    Object[] args = new Object[]{cssColor(), strokeWidth, x1, y1, x2, y2};
    WdDriver.executeCanvasScript("drawLineTestar(arguments)", args);
  }

  @Override
  public void triangle(Pen pen, double x1, double y1, double x2, double y2, double x3, double y3) {
    adjustPen(pen);

    String fillStroke = fillPattern == FillPattern.Solid ? "fill" : "stroke";
    Object[] args = new Object[]{cssColor(), strokeWidth, fillStroke,
        x1, y1, x2, y2, x3, y3};
    WdDriver.executeCanvasScript("drawTriangleTestar(arguments)", args);
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
    WdDriver.executeCanvasScript("drawEllipseTestar(arguments)", args);
  }

  @Override
  public void rect(Pen pen, double x, double y, double width, double height) {
    adjustPen(pen);

    String fillStroke = fillPattern == FillPattern.Solid ? "fillRect" : "strokeRect";
    Object[] args = new Object[]{
        cssColor(), strokeWidth, fillStroke, x, y, width, height};
    WdDriver.executeCanvasScript("drawRectTestar(arguments)", args);
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

  // Check if we actually need to draw the element, i.e. inside viewport
  private boolean needToDraw(double x1, double y1, double x2, double y2) {
    int width = CanvasDimensions.getCanvasWidth();
    int height = CanvasDimensions.getCanvasHeight();

    if (x1 < 0 || x2 < 0 || x1 > width || x2 > width) {
      return false;
    }

    if (y1 < 0 || y2 < 0 || y1 > height || y2 > height) {
      return false;
    }

    return true;
  }
}
