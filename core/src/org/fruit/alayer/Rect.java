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

public final class Rect implements Shape {
	private static final long serialVersionUID = 678389946638512272L;
	private final double x, y, width, height;

	public static boolean intersect(Rect r1, Rect r2) {
		Assert.notNull(r1, r2);
		return !(r1.x() + r1.width() < r2.x() ||
				r1.y() + r1.height() < r2.y() ||
				r2.x() + r2.width() < r1.x() ||
				r2.y() + r2.height() < r1.y()); 
	}

	public static boolean contains(Rect r1, Rect r2) {
		Assert.notNull(r1, r2);
		return r2.x() >= r1.x() && r2.x() + r2.width() <= r1.x() + r1.width() && 
				r2.y() >= r1.y() && r2.y() + r2.height() <= r1.y() + r1.height();
	}
	
	public static double area(Rect rect){
		Assert.notNull(rect);
		return rect.width() * rect.height();
	}

	public static Rect intersection(Rect r1, Rect r2){
		if(r2 == null) return r1;
		if(r1 == null) return null;
		double x1 = Math.max(r1.x(), r2.x());
		double x2 = Math.min(r1.x() + r1.width(),r2.x() + r2.width());
		double y1 = Math.max(r1.y(), r2.y());
		double y2 = Math.min(r1.y() + r1.height(), r2.y() + r2.height());
		if(y2 < y1 || x2 < x1) return null;
		return Rect.fromCoordinates(x1, y1, x2, y2);
	}


	public static Rect from(double x, double y, double width, double height){ return new Rect(x, y, width, height); }
	public static Rect fromCoordinates(double x1, double y1, double x2, double y2){ return new Rect(x1, y1, x2 - x1, y2 - y1); }

	private Rect(double x, double y, double width, double height){
		Assert.isTrue(width >= 0 && height >= 0,
					  "The width and height of a rectangle cannot be negative!: " +
					  "WIDTH=" + width + " x HEIGHT=" + height);
		this.x = x;
		this.width = width;
		this.y = y;
		this.height = height;
	}

	public double x(){	return this.x; }
	public double y() { return this.y; }
	public double width() { return this.width; }
	public double height() { return this.height; }

	public boolean contains(double x, double y) {
		if(x < this.x || y < this.y) return false;
		if(x > this.x + width || y > this.y + height) return false;
		return true;
	}

	public String toString(){
		return "Rect [x:" + x() + " y:" + y() + " w:" + width() + " h:" + height() + "]";
	}

	public void paint(Canvas canvas, Pen pen) {
		canvas.rect(pen, x, y, width, height);
	}
	
	// by urueda
	@Override
	public boolean equals(Object o){
		if (o == this) return true;
		if (o == null) return false;
		if (!(o instanceof Rect)) return false;
		Rect r = (Rect) o;
		return r.x == this.x && r.y == this.y && r.width == this.width && r.height == this.height;
	}
	
}