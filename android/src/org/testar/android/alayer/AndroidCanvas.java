/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.android.alayer;

import org.testar.core.Pair;
import org.testar.core.alayer.Canvas;
import org.testar.core.alayer.Pen;

public class AndroidCanvas implements Canvas {

	private Pen defaultPen;

	public AndroidCanvas(Pen defaultPen) {
		this.defaultPen = defaultPen;
	}

	@Override
	public double width() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double height() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double x() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double y() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void begin() {
		// TODO Auto-generated method stub
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub

	}

	@Override
	public void line(Pen pen, double x1, double y1, double x2, double y2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void text(Pen pen, double x, double y, double angle, String text) {
		// TODO Auto-generated method stub

	}

	@Override
	public Pair<Double, Double> textMetrics(Pen pen, String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clear(double x, double y, double width, double height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void triangle(Pen pen, double x1, double y1, double x2, double y2, double x3, double y3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void image(Pen pen, double x, double y, double width, double height, int[] image, int imageWidth,
			int imageHeight) {
		// TODO Auto-generated method stub

	}

	@Override
	public void ellipse(Pen pen, double x, double y, double width, double height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rect(Pen pen, double x, double y, double width, double height) {
		// TODO Auto-generated method stub

	}

	@Override
	public Pen defaultPen() {
		return defaultPen;
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub

	}

	@Override
	public void paintBatch() {
		// TODO Auto-generated method stub
		
	}

}
