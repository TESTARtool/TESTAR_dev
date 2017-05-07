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
import org.fruit.alayer.Shape;
import org.fruit.alayer.State;
import org.fruit.alayer.Visualizer;

public final class ShapeVisualizer implements Visualizer {
	private static final long serialVersionUID = -1411595441118761574L;
	private final Shape shape;
	private final String label;
	private final double labelX, labelY;
	private final Pen pen;
	
	public ShapeVisualizer(Pen pen, Shape shape, String label, double labelX, double labelY){
		Assert.notNull(shape, pen);
		this.shape = shape;
		this.pen = pen;
		this.label = label;
		this.labelX = labelX;
		this.labelY = labelY;
	}

	public void run(State state, Canvas c, Pen pen) {
		Assert.notNull(state, c, pen);
		pen = Pen.merge(pen, this.pen);
		shape.paint(c, pen);
		if(label != null)
			c.text(pen, shape.x() + shape.width() * labelX, shape.y() + shape.height() * labelY, 0, label);
	}
}