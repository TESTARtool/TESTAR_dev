/***************************************************************************************************
 *
 * Copyright (c) 2023 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2023 Open Universiteit - www.ou.nl
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

package org.testar.monkey.alayer.yolo;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Canvas;
import org.testar.monkey.alayer.Pen;

public class YoloCanvas implements Canvas {
	private TransparentPainter transparentPainter;
	private double x, y, width, height;
	private Pen defaultPen;

	public YoloCanvas(Pen defaultPen) {
		this.defaultPen = defaultPen;
		this.transparentPainter = new TransparentPainter();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.x = 0;
		this.y= 0;
		this.width = screenSize.getWidth();
		this.height = screenSize.getHeight();
	}

	@Override
	public double width() { return width; }

	@Override
	public double height() { return height; }

	@Override
	public double x() { return x; }

	@Override
	public double y() { return y; }

	@Override
	public Pen defaultPen() { return defaultPen; }

	@Override
	public void begin() {
		this.transparentPainter.setVisible(false);
		SwingUtilities.invokeLater(() -> {
			this.transparentPainter.repaint();
			this.transparentPainter.setVisible(true);
		});
	}

	@Override
	public void end() {
		this.transparentPainter.clearDotPositions();
	}

	@Override
	public void release() {
		this.transparentPainter.clearDotPositions();
		this.transparentPainter.setVisible(false);
	}

	@Override
	public void line(Pen pen, double x1, double y1, double x2, double y2) { }

	@Override
	public void text(Pen pen, double x, double y, double angle, String text) { }

	@Override
	public Pair<Double, Double> textMetrics(Pen pen, String text) { return null; }

	@Override
	public void clear(double x, double y, double width, double height) { }

	@Override
	public void triangle(Pen pen, double x1, double y1, double x2, double y2, double x3, double y3) { }

	@Override
	public void image(Pen pen, double x, double y, double width, double height, int[] image, int imageWidth,
			int imageHeight) { }

	@Override
	public void ellipse(Pen pen, double x, double y, double width, double height) {
		this.transparentPainter.addDotPosition(new Point2D.Double(x, y));
	}

	@Override
	public void rect(Pen pen, double x, double y, double width, double height) { }

}
