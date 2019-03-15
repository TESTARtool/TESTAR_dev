/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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
*******************************************************************************************************/

/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.alayer;

import org.fruit.Assert;
import org.fruit.Pair;

public final class OverrideCanvas implements Canvas {

  Canvas canvas;
  Pen overridePen;

  public OverrideCanvas(Canvas canvas) {
    this(canvas, Pen.PEN_DEFAULT);
  }

  public OverrideCanvas(Canvas canvas, Pen pen) {
    Assert.notNull(canvas, pen);
    this.canvas = canvas;
    this.overridePen = pen;
  }

  public void setOverridePen(Pen pen) {
    Assert.notNull(pen);
    this.overridePen = pen;
  }

  public double width() {
    return canvas.width();
  }
  public double height() {
    return canvas.height();
  }
  public double x() {
    return canvas.x();
  }
  public double y() {
    return canvas.y();
  }
  public void begin() {
    canvas.begin();
  }
  public void end() {
    canvas.end();
  }
  public Pen defaultPen() {
    return Pen.merge(overridePen, canvas.defaultPen());
  }
  public void release() {
    canvas.release();
  }

  public void line(Pen pen, double x1, double y1, double x2, double y2) {
    canvas.line(Pen.merge(overridePen, pen), x1, y1, x2, y2);
  }

  public void text(Pen pen, double x, double y, double angle, String text) {
    canvas.text(Pen.merge(overridePen, pen), x, y, angle, text);
  }

  public Pair<Double, Double> textMetrics(Pen pen, String text) {
    return canvas.textMetrics(Pen.merge(overridePen, pen), text);
  }

  public void clear(double x, double y, double width, double height) {
    canvas.clear(x, y, width, height);
  }

  public void triangle(Pen pen, double x1, double y1, double x2, double y2,
      double x3, double y3) {
    canvas.triangle(Pen.merge(overridePen, pen), x1, y1, x2, y2, x3, y3);
  }

  public void image(Pen pen, double x, double y, double width,
      double height, int[] image, int imageWidth, int imageHeight) {
    canvas.image(Pen.merge(overridePen, pen), x, y, width, height, image, imageWidth, imageHeight);
  }

  public void ellipse(Pen pen, double x, double y, double width,
      double height) {
    canvas.ellipse(Pen.merge(overridePen, pen), x, y, width, height);
  }

  public void rect(Pen pen, double x, double y, double width, double height) {
    canvas.rect(Pen.merge(overridePen, pen), x, y, width, height);
  }
}
