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

import java.util.List;

import org.fruit.Util;

public final class Spline {

	private static final class Polynom {
		private final double a;
		private final double b; 
		private final double c; 
		private final double d;

		public Polynom(double a, double b, double c, double d) {
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
		}

		public double evaluate(double x) {
			return a + x *(b + x * (c + x * d));
		}
	}

	private static Polynom[] calcPolynoms(int n, double[] x) {
		double[] D = new double[n + 1];
		double[] gamma = new double[n + 1];
		double[] delta = new double[n + 1];
		int i;

		gamma[0] = 1.0 / 2.0;
		for(i = 1; i < n; i++) {
			gamma[i] = 1 / (4 - gamma[i - 1]);
		}

		gamma[n] = 1 / (2 - gamma[n - 1]);

		delta[0] = 3 * (x[1] - x[0]) * gamma[0];
		for(i = 1; i < n; i++) {
			delta[i] = (3 * (x[i + 1] - x[i - 1]) - delta[i - 1]) * gamma[i];
		}
		
		delta[n] = (3 * (x[n] - x[n - 1]) - delta[n - 1]) * gamma[n];

		D[n] = delta[n];
		for(i = n - 1; i >= 0; i--) {
			D[i] = delta[i] - gamma[i] * D[i + 1];
		}

		Polynom[] ret = new Polynom[n];
		for(i = 0; i < n; i++) {
			ret[i] = new Polynom((double)x[i], D[i], 3 * (x[i + 1] - x[i]) - 2 * D[i] - D[i + 1],
					2 * (x[i] - x[i + 1]) + D[i] + D[i + 1]);
		}
		return ret;
	}

	public static List<Point> evaluate(Point[] points, int granularity) {

		List<Point> ret = Util.newArrayList();

		double[] xs = new double[points.length];
		double[] ys = new double[points.length];
		for(int i = 0; i < points.length; i++) {
			xs[i] = points[i].x();
			ys[i] = points[i].y();
		}

		Polynom[] X = calcPolynoms(points.length - 1, xs);
		Polynom[] Y = calcPolynoms(points.length - 1, ys);

		ret.add(Point.from(X[0].evaluate(0.0f), Y[0].evaluate(0.0f)));

		for(int i = 0; i < X.length; i++) {
			for(int j = 1; j <= granularity; j++) {
				double u = j / (double) granularity;
				ret.add(Point.from(X[i].evaluate(u), Y[i].evaluate(u)));
			}
		}
		return ret;
	}
}
