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
package org.fruit.alayer.visualizers; // refactored by urueda

import java.util.Iterator;

import org.fruit.Assert;
import org.fruit.UnFunc;
import org.fruit.Util;
import org.fruit.alayer.Canvas;
import org.fruit.alayer.Pen;
import org.fruit.alayer.Point;
import org.fruit.alayer.Position;
import org.fruit.alayer.SplineTrajectory;
import org.fruit.alayer.State;
import org.fruit.alayer.StrokeCaps;
import org.fruit.alayer.Visualizer;

public class TrajectoryVisualizer implements Visualizer {

	private static final long serialVersionUID = 1107281202398264314L;
	final UnFunc<State, Iterable<Point>> trajectory;
	final Pen pen;

	public TrajectoryVisualizer(Pen pen, Position... positions){
		this(new SplineTrajectory(10, positions), pen);
	}
	
	public TrajectoryVisualizer(UnFunc<State, Iterable<Point>> trajectory, Pen pen){
		Assert.notNull(trajectory, pen);
		Assert.isTrue(pen.strokeWidth() != null);
		this.trajectory = trajectory;		
		this.pen = pen;
	}
	
	public void run(State s, Canvas c, Pen pen) {
		Assert.notNull(s, c, pen);
		pen = Pen.merge(pen, this.pen);
		Iterator<Point> iter = trajectory.apply(s).iterator();
		Point last = iter.next();
		
		while(iter.hasNext()){
			Point current = iter.next();
			
			if(!iter.hasNext() && (pen.strokeCaps() == StrokeCaps._Arrow || pen.strokeCaps() == StrokeCaps.Arrow_))
				Util.arrow(c, pen, last.x(), last.y(), current.x(), current.y(), 5 * pen.strokeWidth(), 5 * pen.strokeWidth());
			else
				c.line(pen, last.x(), last.y(), current.x(), current.y());
			
			last = current;
		}
	}
}