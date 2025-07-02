/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 - 2025 Open Universiteit - www.ou.nl
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

package org.testar.monkey.alayer.android;

import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Canvas;
import org.testar.monkey.alayer.Pen;

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
