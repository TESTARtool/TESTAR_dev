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