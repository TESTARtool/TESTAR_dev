/******************************************************************************************
 * COPYRIGHT:                                                                             *
 * Universitat Politecnica de Valencia 2013                                               *
 * Camino de Vera, s/n                                                                    *
 * 46022 Valencia, Spain                                                                  *
 * www.upv.es                                                                             *
 *                                                                                        * 
 * D I S C L A I M E R:                                                                   *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)      *
 * in the context of the european funded FITTEST project (contract number ICT257574)      *
 * of which the UPV is the coordinator. As the sole developer of this source code,        *
 * following the signed FITTEST Consortium Agreement, the UPV should decide upon an       *
 * appropriate license under which the source code will be distributed after termination  *
 * of the project. Until this time, this code can be used by the partners of the          *
 * FITTEST project for executing the tasks that are outlined in the Description of Work   *
 * (DoW) that is annexed to the contract with the EU.                                     *
 *                                                                                        * 
 * Although it has already been decided that this code will be distributed under an open  *
 * source license, the exact license has not been decided upon and will be announced      *
 * before the end of the project. Beware of any restrictions regarding the use of this    *
 * work that might arise from the open source license it might fall under! It is the      *
 * UPV's intention to make this work accessible, free of any charge.                      *
 *****************************************************************************************/

/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.alayer;

import java.util.List;

import org.fruit.Util;

public final class Spline{

	private final static class Polynom {
		private final double a, b, c, d;

		public Polynom(double a, double b, double c, double d){
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
		for(i = 1; i < n; i++)
			gamma[i] = 1 / (4 - gamma[i - 1]);

		gamma[n] = 1 / (2 - gamma[n - 1]);

		delta[0] = 3 * (x[1] - x[0]) * gamma[0];
		for(i = 1; i < n; i++)
			delta[i] = (3 * (x[i + 1] - x[i - 1]) - delta[i - 1]) * gamma[i];

		delta[n] = (3 * (x[n] - x[n - 1]) - delta[n - 1]) * gamma[n];

		D[n] = delta[n];
		for(i = n - 1; i >= 0; i--){
			D[i] = delta[i] - gamma[i] * D[i + 1];
		}

		Polynom[] ret = new Polynom[n];
		for(i = 0; i < n; i++){
			ret[i] = new Polynom((double)x[i], D[i], 3 * (x[i + 1] - x[i]) - 2 * D[i] - D[i + 1],
					2 * (x[i] - x[i + 1]) + D[i] + D[i + 1]);
		}
		return ret;
	}

	public static List<Point> evaluate(Point[] points, int granularity){

		List<Point> ret = Util.newArrayList();

		double[] xs = new double[points.length];
		double[] ys = new double[points.length];
		for(int i = 0; i < points.length; i++){
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