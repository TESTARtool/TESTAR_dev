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
