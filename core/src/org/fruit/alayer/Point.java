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

public final class Point implements Shape {
	private static final long serialVersionUID = -8319702986528318327L;
	private final double x, y;
		
	public static Point from(double x, double y){ return new Point(x, y); }
	
	private Point(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public double x(){ return x; }
	public double y(){ return y; }
	public double width(){ return 0; }
	public double height(){	return 0; }

	public boolean contains(double x, double y) {
		return this.x == x && this.y == y;
	}

	public void paint(Canvas canvas, Pen pen) {
		Assert.notNull(canvas, pen);
		double d = canvas.defaultPen().strokeWidth();
		canvas.ellipse(Pen.merge(pen, Pen.PEN_FILL), x - d * 0.5, y - d * 0.5, d, d);
	}

	public String toString(){ return "(" + x + ", " + y + ")"; }
	
	public boolean equals(Object o){
		if(this == o)
			return true;
		
		if(o instanceof Point){
			Point po = (Point) o;
			return x == po.x && y == po.y;
		}
		return false;
	}
	
	public int hashCode(){
		long ret = Double.doubleToLongBits(x);
		ret ^= Double.doubleToLongBits(y) * 31;
		return (((int) ret) ^ ((int) (ret >> 32)));
	}
}