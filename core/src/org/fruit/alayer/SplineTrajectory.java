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

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.fruit.Assert;
import org.fruit.UnFunc;

public final class SplineTrajectory implements UnFunc<State, Iterable<Point>>, Serializable {

	private static final long serialVersionUID = -7833747078043023184L;
	final Position[] positions;
	final int smoothness;
	
	private final class PointIterable implements Iterable<Point>{
		final Point[] points;
		public PointIterable(Point[] points){ this.points = points; }
		public Iterator<Point> iterator() { return new Iter(points); }		
	}
	
	private final class Iter implements Iterator<Point>{
		List<Point> intermediatePoints;
		Iterator<Point> iter;
		
		public Iter(Point[] points){
			intermediatePoints = Spline.evaluate(points, smoothness + 1); //TODO: only create points on demand!!
			iter = intermediatePoints.iterator();
		}
		
		public boolean hasNext() { return iter.hasNext(); }
		public Point next() { return iter.next(); }
		public void remove() { throw new UnsupportedOperationException(); }
	}
	
	public SplineTrajectory(int smoothness, Position... positions){
		Assert.notNull(positions);
		Assert.isTrue(smoothness >= 0 && positions.length > 1);
		this.positions = positions;
		this.smoothness = smoothness;
	}
	
	public Iterable<Point> apply(State s) {
		Assert.notNull(s);
		Point[] points = new Point[positions.length];
		for(int i = 0; i < positions.length; i++)
			points[i] = positions[i].apply(s);
		return new PointIterable(points);
	}
}
