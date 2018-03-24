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

import org.fruit.Assert;
import org.fruit.alayer.Canvas;
import org.fruit.alayer.Pen;
import org.fruit.alayer.Point;
import org.fruit.alayer.Position;
import org.fruit.alayer.State;
import org.fruit.alayer.Visualizer;
import org.fruit.alayer.exceptions.PositionException;

public final class EllipseVisualizer implements Visualizer {

	private static final long serialVersionUID = -6006402344810634504L;
	private final double width, height;
	private final Pen pen;
	private final Position position;
	
	public EllipseVisualizer(Position position, Pen pen, double width, double height){
		Assert.notNull(position, pen);
		this.width = width;
		this.height = height;
		this.pen = pen; 
		this.position = position;
	}
	
	public void run(State state, Canvas canvas, Pen pen) {
		Assert.notNull(state, canvas, pen);
		pen = Pen.merge(pen, this.pen);
		try { // by urueda
			Point p = position.apply(state);
			canvas.ellipse(pen, p.x() - width * .5, p.y() - height * .5, width, height);
		} catch (PositionException pe) {}
	}
}