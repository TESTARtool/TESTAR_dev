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

import org.fruit.Assert;
import org.fruit.Pair;

public final class OverrideCanvas implements Canvas {

	Canvas canvas;
	Pen overridePen;
	
	public OverrideCanvas(Canvas canvas){
		this(canvas, Pen.PEN_DEFAULT);
	}
	
	public OverrideCanvas(Canvas canvas, Pen pen){
		Assert.notNull(canvas, pen);
		this.canvas = canvas;
		this.overridePen = pen;
	}
	
	public void setOverridePen(Pen pen){
		Assert.notNull(pen);
		this.overridePen = pen; 
	}
	
	public double width() { return canvas.width(); }
	public double height() {	return canvas.height(); }
	public double x(){ return canvas.x(); }
	public double y(){ return canvas.y(); }
	public void begin() { canvas.begin(); }
	public void end() { canvas.end(); }
	public Pen defaultPen() { return Pen.merge(overridePen, canvas.defaultPen()); }
	public void release() { canvas.release(); }

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